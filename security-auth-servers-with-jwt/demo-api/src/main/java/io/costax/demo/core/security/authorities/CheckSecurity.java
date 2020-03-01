package io.costax.demo.core.security.authorities;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

/**
 * We can create an individual file for each of the annotations, but this way we get a less verbose code.
 * In contrast this class is more sensitive to changes.
 */
public @interface CheckSecurity {

    @interface Users {

        @PreAuthorize("hasAnyAuthority('MANAGER_USERS')")
        @Target({ElementType.METHOD})
        @Retention(RetentionPolicy.RUNTIME)
        @Inherited
        @Documented
        @interface CanManage {}

    }

    @interface Books {

        @PreAuthorize("hasAuthority('SEE_BOOKS')")
        @Target({ElementType.METHOD})
        @Retention(RetentionPolicy.RUNTIME)
        @Inherited
        @Documented
        @interface CanSee {}

        @PreAuthorize("hasAuthority('EDIT_BOOKS')")
        @Target({ElementType.METHOD})
        @Retention(RetentionPolicy.RUNTIME)
        @Inherited
        @Documented
        @interface CanCreateOrEdit {}
    }

    @interface Orders {

        @PreAuthorize("hasAuthority('CREATE_ORDER')")
        @Target({ElementType.METHOD})
        @Retention(RetentionPolicy.RUNTIME)
        @Inherited
        @Documented
        @interface CanCreate {}
    }


    @PreAuthorize("isAuthenticated()")
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Documented
    @interface IsAuthenticated {}

    @PreAuthorize("permitAll()")
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Documented
    @interface PermitAll {}

}