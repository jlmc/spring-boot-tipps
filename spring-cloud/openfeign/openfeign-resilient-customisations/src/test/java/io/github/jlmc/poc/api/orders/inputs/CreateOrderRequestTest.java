package io.github.jlmc.poc.api.orders.inputs;

import io.github.jlmc.poc.domain.orders.commands.CreateOrderCommand;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class CreateOrderRequestTest {

    ValidatorFactory factory;
    Validator validator;

    @BeforeEach
    public void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterEach
    public void tearDown() {
        factory.close();
    }

    @Nested
    class ValidationTest {

        @Test
        void when_request_is_valid() {
            CreateOrderRequest request = new CreateOrderRequest(
                    List.of(new Item("1", 2), new Item("3", 4))
            );

            Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);
            assertTrue(violations.isEmpty());
        }

        @Test
        void when_request_items_is_empty_it_have_one_violation() {
            CreateOrderRequest request = new CreateOrderRequest(List.of());

            Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);
            assertEquals(1, violations.size());
        }

        @Test
        void when_request_items_is_null_it_have_one_violation() {
            CreateOrderRequest request = new CreateOrderRequest(null);

            Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);
            assertEquals(1, violations.size());
        }

        @Test
        void when_request_items_id_is_not_integer_it_have_one_violation() {
            CreateOrderRequest request = new CreateOrderRequest(List.of(new Item("not-integer", 2)));

            Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);
            assertEquals(1, violations.size());
        }

        @Test
        void when_request_items_quantity_is_not_positive_integer_it_have_one_violation() {
            CreateOrderRequest request = new CreateOrderRequest(List.of(new Item("1", -2)));

            Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);
            assertEquals(1, violations.size());
        }
    }

    @Nested
    class ToCommandTests {
        @Test
        void when_convert_to_command_with_null_items__it_throws_exception() {
            CreateOrderRequest createOrderRequest = new CreateOrderRequest(
                    null
            );

            assertThrows(IllegalArgumentException.class, createOrderRequest::toCommand);
        }

        @Test
        void when_convert_to_command_without_items__it_throws_exception() {
            CreateOrderRequest createOrderRequest = new CreateOrderRequest(
                    List.of()
            );

            assertThrows(IllegalArgumentException.class, createOrderRequest::toCommand);
        }

        @Test
        void when_convert_to_command__it_convert_all_fields_as_expected() {
            CreateOrderRequest createOrderRequest = new CreateOrderRequest(
                    List.of(new Item("1", 2), new Item("3", 4))
            );

            CreateOrderCommand command = createOrderRequest.toCommand();

            assertNotNull(command);
            assertEquals(
                    new CreateOrderCommand(
                            List.of(new CreateOrderCommand.Item("1", 2), new CreateOrderCommand.Item("3", 4))
                    ), command
            );
        }
    }
}