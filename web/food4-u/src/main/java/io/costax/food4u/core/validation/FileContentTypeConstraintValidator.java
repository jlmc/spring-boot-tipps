package io.costax.food4u.core.validation;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class FileContentTypeConstraintValidator implements ConstraintValidator<FileContentType, MultipartFile> {

   private Set<String> allowedContentTypes;

   public void initialize(FileContentType constraint) {
      this.allowedContentTypes = Arrays.stream(constraint.allowed()).collect(Collectors.toUnmodifiableSet());
   }

   public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
      return value == null
              || this.allowedContentTypes.contains(value.getContentType());
   }
}
