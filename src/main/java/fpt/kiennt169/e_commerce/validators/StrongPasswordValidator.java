package fpt.kiennt169.e_commerce.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    private static final int MIN_LENGTH = 8;
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[@$!%*?&#^()_+=\\-{}\\[\\]|:;\"'<>,./ ].*");

    @Override
    public void initialize(StrongPassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        // Check minimum length
        if (password.length() < MIN_LENGTH) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Password must be at least " + MIN_LENGTH + " characters"
            ).addConstraintViolation();
            return false;
        }

        // Check all patterns
        boolean hasUppercase = UPPERCASE_PATTERN.matcher(password).matches();
        boolean hasLowercase = LOWERCASE_PATTERN.matcher(password).matches();
        boolean hasDigit = DIGIT_PATTERN.matcher(password).matches();
        boolean hasSpecialChar = SPECIAL_CHAR_PATTERN.matcher(password).matches();

        // Build custom error message
        if (!hasUppercase || !hasLowercase || !hasDigit || !hasSpecialChar) {
            context.disableDefaultConstraintViolation();
            
            StringBuilder message = new StringBuilder("Password must contain: ");
            if (!hasUppercase) message.append("uppercase letter, ");
            if (!hasLowercase) message.append("lowercase letter, ");
            if (!hasDigit) message.append("digit, ");
            if (!hasSpecialChar) message.append("special character, ");
            
            // Remove trailing comma and space
            String finalMessage = message.substring(0, message.length() - 2);
            
            context.buildConstraintViolationWithTemplate(finalMessage)
                   .addConstraintViolation();
            return false;
        }

        return true;
    }
}
