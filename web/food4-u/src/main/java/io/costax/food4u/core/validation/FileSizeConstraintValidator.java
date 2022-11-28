package io.costax.food4u.core.validation;

import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FileSizeConstraintValidator implements ConstraintValidator<FileSize, MultipartFile> {

   private DataSize maxSize;

   public void initialize(FileSize constraint) {
      this.maxSize = DataSize.parse(constraint.max());
   }

   @Override
   public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
      return value == null || value.getSize() <= this.maxSize.toBytes();
   }
}
