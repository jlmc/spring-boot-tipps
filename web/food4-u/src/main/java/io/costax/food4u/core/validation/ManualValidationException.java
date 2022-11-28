package io.costax.food4u.core.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.BindingResult;

@AllArgsConstructor
@Getter
public class ManualValidationException extends RuntimeException {

    BindingResult bindingResult;
}
