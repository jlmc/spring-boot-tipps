package io.costax.food4u.domain.services;

import io.costax.food4u.domain.model.Cooker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource("/application-it.properties")
public class RestaurantRegistrationServiceIT {

    @Autowired
    private CookerRegistrationService cookerRegistrationService;

    @Test
    @Rollback
    @Transactional
    public void should_register_a_cooker_with_success() {
        // scenario
        Cooker newcooker = new Cooker();
        newcooker.setName("Jack Lee ");

        // action
        Cooker cooker = cookerRegistrationService.add(newcooker);

        // validation
        assertThat(cooker).isNotNull();
        assertThat(cooker.getId()).isNotNull();
    }

    @Test(expected = ConstraintViolationException.class)
    public void should_register_a_cooker_when_an_cooker_do_not_contain_name() {
        Cooker cooker = new Cooker();
        cooker.setName(null);

        cookerRegistrationService.add(cooker);
    }
}
