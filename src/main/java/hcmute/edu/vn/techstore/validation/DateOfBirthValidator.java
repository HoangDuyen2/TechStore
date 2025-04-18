package hcmute.edu.vn.techstore.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class DateOfBirthValidator implements ConstraintValidator<ValidDateOfBirth, LocalDate> {
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // để NotNull xử lý nếu cần
        }

        LocalDate now = LocalDate.now();

        // Ngày sinh phải trước ngày hiện tại
        if (!value.isBefore(now)) {
            return false;
        }

        int age = Period.between(value, now).getYears();

        // Tuổi phải từ 18 đến 100
        return age >= 18 && age <= 100;
    }
}
