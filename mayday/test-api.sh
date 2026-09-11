#!/usr/bin/env bash
#
# Demo script for the mayday-hls-provider GET /encounters endpoint.
#
# Prerequisites:
#   1. docker compose up -d          (from the repo root -- starts WireMock on :9090)
#   2. cd mayday-hls-provider && ./mvnw spring-boot:run   (starts the app on :8080)
#
# Usage: ./test-api.sh
set -uo pipefail

APP_URL="http://localhost:8080"
WIREMOCK_URL="http://localhost:9090"

BOLD="\033[1m"
GREEN="\033[32m"
RED="\033[31m"
YELLOW="\033[33m"
RESET="\033[0m"

PASS_COUNT=0
FAIL_COUNT=0

print_header() {
    echo
    echo -e "${BOLD}==> $1${RESET}"
}

has_jq() { command -v jq >/dev/null 2>&1; }

print_body() {
    if has_jq && echo "$1" | jq -e . >/dev/null 2>&1; then
        echo "$1" | jq .
    else
        echo "$1"
    fi
}

# check <description> <expected_status> <method> <url> [extra curl args...]
check() {
    local description=$1 expected_status=$2 method=$3 url=$4
    shift 4
    local start_s elapsed_s
    start_s=$SECONDS
    local response
    response=$(curl -s -o /tmp/mayday-demo-body -w "%{http_code}" -X "$method" "$url" "$@")
    elapsed_s=$((SECONDS - start_s))
    local body
    body=$(cat /tmp/mayday-demo-body)

    echo
    echo -e "${BOLD}$description${RESET}"
    echo "  $method $url"
    echo "  elapsed: ~${elapsed_s}s"
    print_body "$body" | sed 's/^/  /'

    if [ "$response" = "$expected_status" ]; then
        echo -e "  ${GREEN}PASS${RESET} (status $response, expected $expected_status)"
        PASS_COUNT=$((PASS_COUNT + 1))
    else
        echo -e "  ${RED}FAIL${RESET} (status $response, expected $expected_status)"
        FAIL_COUNT=$((FAIL_COUNT + 1))
    fi
}

echo -e "${BOLD}mayday-hls-provider -- API demo${RESET}"
echo "App:      $APP_URL"
echo "WireMock: $WIREMOCK_URL"

if ! curl -s -o /dev/null "$APP_URL/actuator/health"; then
    echo -e "${RED}App is not reachable at $APP_URL -- start it with: cd mayday-hls-provider && ./mvnw spring-boot:run${RESET}"
    exit 1
fi
if ! curl -s -o /dev/null "$WIREMOCK_URL/__admin/mappings"; then
    echo -e "${RED}WireMock is not reachable at $WIREMOCK_URL -- start it with: docker compose up -d (from the repo root)${RESET}"
    exit 1
fi

print_header "Resetting WireMock scenarios (so this script is repeatable)"
curl -s -X POST "$WIREMOCK_URL/__admin/scenarios/reset" -o /dev/null
echo "  done"

print_header "1. Match found -- account-123, 2026-09-01..2026-09-10, FOLLOW_UP"
echo "   9 daily windows are searched concurrently; one (2026-09-05) matches, one"
echo "   (2026-09-03) is a deliberately slow (5s) stub. The response should come back"
echo "   in well under 5 seconds, proving the slow window's request is cancelled once"
echo "   the match is found -- not waited on."
check "Definition-of-Done curl" 200 GET \
    "$APP_URL/encounters?startDate=2026-09-01T00:00:00Z&endDate=2026-09-10T00:00:00Z&visitType=FOLLOW_UP" \
    -H "Authorization: Bearer account-123"

print_header "2. No match -- account-123, a single empty day"
check "Empty window search" 200 GET \
    "$APP_URL/encounters?startDate=2026-01-01T00:00:00Z&endDate=2026-01-02T00:00:00Z&visitType=FOLLOW_UP" \
    -H "Authorization: Bearer account-123"

print_header "3. Missing Authorization header"
check "No Authorization header" 401 GET \
    "$APP_URL/encounters?startDate=2026-09-01T00:00:00Z&endDate=2026-09-02T00:00:00Z&visitType=FOLLOW_UP"

print_header "4. Malformed Authorization header"
check "Authorization without Bearer prefix" 401 GET \
    "$APP_URL/encounters?startDate=2026-09-01T00:00:00Z&endDate=2026-09-02T00:00:00Z&visitType=FOLLOW_UP" \
    -H "Authorization: account-123"

print_header "5. Missing required query parameter (visitType)"
check "visitType omitted" 400 GET \
    "$APP_URL/encounters?startDate=2026-09-01T00:00:00Z&endDate=2026-09-02T00:00:00Z" \
    -H "Authorization: Bearer account-123"

print_header "6. Invalid date format"
check "startDate is not ISO-8601" 400 GET \
    "$APP_URL/encounters?startDate=not-a-date&endDate=2026-09-02T00:00:00Z&visitType=FOLLOW_UP" \
    -H "Authorization: Bearer account-123"

print_header "7. Provider-driven rate limiting -- account-A (independent per account)"
echo "   The WireMock stub allows 2 requests for account-A, then always returns 429"
echo "   with Retry-After. Direct calls to the provider first:"
check "account-A request 1 (provider, direct)" 200 GET \
    "$WIREMOCK_URL/provider/encounters?startDate=2026-01-01T00:00:00Z&endDate=2026-01-02T00:00:00Z" \
    -H "X-Account-Id: account-A"
check "account-A request 2 (provider, direct)" 200 GET \
    "$WIREMOCK_URL/provider/encounters?startDate=2026-01-01T00:00:00Z&endDate=2026-01-02T00:00:00Z" \
    -H "X-Account-Id: account-A"
check "account-A request 3 (provider, direct, now rate limited)" 429 GET \
    "$WIREMOCK_URL/provider/encounters?startDate=2026-01-01T00:00:00Z&endDate=2026-01-02T00:00:00Z" \
    -H "X-Account-Id: account-A"

echo
echo "   Now searching through the app for account-A: every window it tries will hit"
echo "   the still-rate-limited provider, retry using Retry-After, exhaust its bounded"
echo "   retry budget, and the app surfaces this as 502 (an upstream failure, not the"
echo "   caller's fault)."
check "account-A search via the app (provider stays rate limited)" 502 GET \
    "$APP_URL/encounters?startDate=2026-01-01T00:00:00Z&endDate=2026-01-02T00:00:00Z&visitType=FOLLOW_UP" \
    -H "Authorization: Bearer account-A"

print_header "8. account-B is unaffected by account-A's rate limit"
check "account-B request 1 (provider, direct)" 200 GET \
    "$WIREMOCK_URL/provider/encounters?startDate=2026-01-01T00:00:00Z&endDate=2026-01-02T00:00:00Z" \
    -H "X-Account-Id: account-B"
check "account-B search via the app" 200 GET \
    "$APP_URL/encounters?startDate=2026-01-01T00:00:00Z&endDate=2026-01-02T00:00:00Z&visitType=FOLLOW_UP" \
    -H "Authorization: Bearer account-B"

echo
echo -e "${BOLD}Summary: ${GREEN}$PASS_COUNT passed${RESET}, ${RED}$FAIL_COUNT failed${RESET}"
rm -f /tmp/mayday-demo-body
[ "$FAIL_COUNT" -eq 0 ]
