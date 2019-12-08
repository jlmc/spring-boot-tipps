package io.costax.examplesapi.configurationproperties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * This example requires some technical attentions:
 * <p>
 * 1. We should have the following dependency:
 * <p>
 * <p>
 * <p>
 * {@code
 *
 * <dependency>
 * <groupId>org.springframework.boot</groupId>
 * <artifactId>spring-boot-devtools</artifactId>
 * <scope>runtime</scope>
 * <optional>true</optional>
 * </dependency>
 * }
 * <p>
 * <p>
 * <p>
 * When the project is compiled, a file will be generated inside the output jar. This file will be very useful for us to be more productive, as IDEs will use your content to auto-complete.
 * <p>
 * <p>
 * 2. The Setters methods need to be defined
 */
@Component
@ConfigurationProperties("app.notifiers")
public class NotifiersConfigurationProperties {

    /**
     * smtp host server for notifications
     */
    private String smtpHost;
    private int smtpPort = 25;

    private String smsHost;
    private String smsUsername;
    private String smsPassword;

    private Others others;


    public String getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpHost(final String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(final int smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getSmsHost() {
        return smsHost;
    }

    public void setSmsHost(final String smsHost) {
        this.smsHost = smsHost;
    }

    public String getSmsUsername() {
        return smsUsername;
    }

    public void setSmsUsername(final String smsUsername) {
        this.smsUsername = smsUsername;
    }

    public String getSmsPassword() {
        return smsPassword;
    }

    public void setSmsPassword(final String smsPassword) {
        this.smsPassword = smsPassword;
    }

    public void setOthers(final Others others) {
        this.others = others;
    }

    public Others getOthers() {
        return others;
    }
}
