package io.github.jlmc.firststep.config

import io.github.jlmc.firststep.domain.SystemAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.util.*

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
class AuditConfig {

    @Bean
    fun auditorProvider(): AuditorAware<SystemAware> {
        return AuditorAware<SystemAware> { Optional.of(SystemAware.createSystemAware("demos-graph-sql")) }
    }

    /*
     class SpringSecurityAuditorAware implements AuditorAware<User> {
         public User getCurrentAuditor() {
             Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
             if (authentication == null || !authentication.isAuthenticated()) {
               return null;
             }
             return ((MyUserDetails) authentication.getPrincipal()).getUser();
         }
     }
  */
}
