package io.github.jlmc.poc.it;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ClasspathFileSource;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.standalone.JsonFileMappingsSource;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class WireMockExampleIT {

    public static final WireMockServer WIRE_MOCK_SERVER = new
        WireMockServer(
                wireMockConfig()
                        .fileSource(new ClasspathFileSource("wiremock/__files"))
                        .mappingSource(new JsonFileMappingsSource(new ClasspathFileSource("wiremock/mappings")))
                        .notifier(new ConsoleNotifier(true))
                        .port(8089)
                        .extensions(
                                new ResponseTemplateTransformer(true)));

    @BeforeAll
    static void beforeAll() {
        WIRE_MOCK_SERVER.start();
    }

    @AfterAll
    static void afterAll() {
        WIRE_MOCK_SERVER.stop();
        WIRE_MOCK_SERVER.resetAll();
    }

    private HttpClient httpClient;

    @BeforeEach
    void setUp() {
        httpClient = HttpClient.newBuilder()
                //.executor(EXECUTOR)
                .connectTimeout(Duration.ofSeconds(10))
                .priority(1)
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .authenticator(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("x-u1", "x-p1".toCharArray());
                    }
                })
                .build();
        WireMock.configureFor("localhost", WIRE_MOCK_SERVER.port());
    }

    @AfterEach
    void tearDown() {
        MappingBuilder mappingBuilder = WireMock.get(WireMock.anyUrl())
                .atPriority(1)
                .withId(new UUID(0, 1));
        WireMock.removeStub(mappingBuilder);
    }

    @Test
    @Order(1)
    void test1() {
        WireMock.stubFor(WireMock.get("/api/products/9999")
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("{ \"id\": 9999 }")));

        HttpResponse response = getHttpResponse("/api/products/9999");

        Assertions.assertEquals(200, response.status());
        Assertions.assertEquals("{ \"id\": 9999 }", response.body());
    }

    @Test
    @Order(2)
    void test2() {

        WireMock.stubFor(
                WireMock.get(WireMock.urlEqualTo("/api/technicalsheets/000001-2024"))
                        //.withHeader("loadParticipantData", WireMock.matching("true"))
                        .atPriority(1)
                        .withId(new UUID(0, 1))
                .willReturn(
                        WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("mocked-1234")));

        HttpResponse response = getHttpResponse("/api/technicalsheets/000001-2024");

        Assertions.assertEquals(200, response.status());
        Assertions.assertEquals("mocked-1234", response.body());
    }

    @Order(3)
    @ParameterizedTest
    @ValueSource(strings = {
            "/api/technicalsheets/000001-2024?loadParticipantData=true",
            "/api/technicalsheets/000001-2024?loadParticipantData=false",
            "/api/technicalsheets/000001-2024?loadParticipantData=",
            "/api/technicalsheets/000001-2024",
    })
    void test3(String uri) {
        //displayMappings();

        WireMock.stubFor(
                WireMock.get(WireMock.urlPathMatching("/api/technicalsheets/000001-2024"))
                        .atPriority(1)
                        //.withQueryParam("loadParticipantData", matching("^(true|false)?$"))
                        //.withQueryParams(Map.of("loadParticipantData", matching("^(true|false)?$")))
                        //.withQueryParams(Map.of("loadParticipantData", matching("^(value)?$")))
                        .withId(new UUID(0, 1))
                        .willReturn(
                                WireMock.aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withStatus(200)
                                        .withBody("mocked-1234 -> {{request.path.[2]}} ")));

        HttpResponse response = getHttpResponse(uri);

        displayMappings();

        Assertions.assertEquals(200, response.status());
        Assertions.assertEquals("mocked-1234 -> 000001-2024 ", response.body());
    }

    @Test
    @Order(4)
    void test4() {

        var mappingBuilder = WireMock.get(WireMock.urlEqualTo("/api/technicalsheets/000001-2024"))
                //.withHeader("loadParticipantData", WireMock.matching("true"))
                .atPriority(1)
                .withId(new UUID(0, 1));
        WireMock.removeStub(mappingBuilder);

        HttpResponse response = getHttpResponse("/api/technicalsheets/000001-2024?loadParticipantData=false");

        Assertions.assertEquals(200, response.status());
        Assertions.assertNotEquals("mocked-1234", response.body());
    }

    private HttpResponse getHttpResponse(String url) {
        URI uri = URI.create("http://localhost:8089" + url);
        System.out.println(uri);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                //.POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        var response = httpClient.sendAsync(request, java.net.http.HttpResponse.BodyHandlers.ofString())
                //.thenApply(HttpResponse::body)
                //.thenAccept(System.out::println)
                .exceptionally(e -> {
                    System.out.println("Request failed: " + e.getMessage());
                    return null;
                })
                .join();

        return new HttpResponse(response.statusCode(), response.body());
    }

    record HttpResponse(int status, String body) {}

    void displayMappings() {
        HttpResponse response = getHttpResponse("/__admin");

        System.out.println("==================");
        System.out.println(response.body());
        System.out.println("==================");

    }
}
