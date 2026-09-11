package io.github.jlmc.maydayhlsprovider.adapters.in.web;

import io.github.jlmc.maydayhlsprovider.application.exception.UnauthorizedException;
import io.github.jlmc.maydayhlsprovider.domain.AccountId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BearerAccountResolverTest {

    private final BearerAccountResolver resolver = new BearerAccountResolver();

    @Test
    void validBearerHeader_returnsAccountId() {
        assertThat(resolver.resolve("Bearer account-123")).isEqualTo(new AccountId("account-123"));
    }

    @Test
    void missingHeader_throwsUnauthorized() {
        assertThatThrownBy(() -> resolver.resolve(null)).isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void malformedHeader_throwsUnauthorized() {
        assertThatThrownBy(() -> resolver.resolve("account-123")).isInstanceOf(UnauthorizedException.class);
        assertThatThrownBy(() -> resolver.resolve("Basic account-123")).isInstanceOf(UnauthorizedException.class);
        assertThatThrownBy(() -> resolver.resolve("Bearer ")).isInstanceOf(UnauthorizedException.class);
        assertThatThrownBy(() -> resolver.resolve("Bearer   ")).isInstanceOf(UnauthorizedException.class);
    }
}
