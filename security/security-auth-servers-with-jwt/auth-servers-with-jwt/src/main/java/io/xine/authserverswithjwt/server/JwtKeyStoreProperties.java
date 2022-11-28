package io.xine.authserverswithjwt.server;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
@Component
@ConfigurationProperties("io.xine.auth-servers-with-jwt.keystore")
public class JwtKeyStoreProperties {

    @NotBlank
    private String path;

    @NotBlank
    private String password;

    @NotBlank
    private String keypairAlias;

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getKeypairAlias() {
        return keypairAlias;
    }

    public void setKeypairAlias(final String keypairAlias) {
        this.keypairAlias = keypairAlias;
    }
}
