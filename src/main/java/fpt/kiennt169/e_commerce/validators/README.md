# Custom Validators Package

This package contains custom Jakarta Validation annotations and their implementations.

## 📋 Available Validators

### @StrongPassword

Custom validation annotation for strong password requirements.

**Files:**

- `StrongPassword.java` - Annotation interface
- `StrongPasswordValidator.java` - Validator implementation (ConstraintValidator)

**Requirements:**

- Minimum 8 characters
- At least 1 uppercase letter (A-Z)
- At least 1 lowercase letter (a-z)
- At least 1 digit (0-9)
- At least 1 special character (@$!%\*?&#^()\_+=-{}[]|:;"'<>,./)

**Usage:**

```java
import fpt.kiennt169.e_commerce.validators.StrongPassword;
import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {

    @NotBlank(message = "Password is required")
    @StrongPassword
    private String password;

    // getters/setters...
}
```

**Controller:**

```java
@PostMapping("/register")
public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
    // Validation happens automatically via @Valid
    // No need for manual password validation!
    return ResponseEntity.ok(authService.register(request));
}
```

**Valid Examples:**

- ✅ `Admin@123`
- ✅ `Customer@123`
- ✅ `MyP@ssw0rd`
- ✅ `Secure#2026`

**Invalid Examples:**

- ❌ `admin123` (no uppercase, no special)
- ❌ `Admin123` (no special)
- ❌ `Admin@` (too short, no digit)

## 🧪 Testing

Run the registration test script:

```bash
chmod +x test-registration.sh
./test-registration.sh
```

This will test:

- Valid strong passwords
- Weak password rejection (no uppercase, lowercase, digit, special char)
- Minimum length validation
- Auto-login after registration
- Duplicate email rejection

## 🏗️ Architecture

The implementation follows the Jakarta Validation (Bean Validation 2.0) specification:

1. **Annotation** (`@StrongPassword`) - Defines the constraint
2. **Validator** (`StrongPasswordValidator`) - Implements validation logic
3. **Usage** - Applied to fields with `@StrongPassword` annotation
4. **Trigger** - Validation runs when `@Valid` is used in controller

**Flow:**

```
Controller (@Valid)
  → Spring Validation Framework
    → @StrongPassword annotation detected
      → StrongPasswordValidator.isValid() executed
        → Return true/false
          → If false, MethodArgumentNotValidException thrown
            → GlobalExceptionHandler catches and returns 400 Bad Request
```

## 📚 References

- [Jakarta Validation Specification](https://jakarta.ee/specifications/bean-validation/)
- [Spring Boot Validation Guide](https://spring.io/guides/gs/validating-form-input/)
- [Custom Validation Tutorial](https://www.baeldung.com/spring-mvc-custom-validator)

## 🔮 Future Validators

Potential validators to add:

- `@ValidPhoneNumber` - Vietnamese phone number validation
- `@ValidVietnameseId` - CCCD/CMND validation
- `@ValidUrl` - URL format validation
- `@ValidSlug` - URL-friendly slug validation
- `@ValidCreditCard` - Credit card number validation (Luhn algorithm)
