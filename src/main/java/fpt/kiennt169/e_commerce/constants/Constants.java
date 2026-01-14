package fpt.kiennt169.e_commerce.constants;

public final class Constants {

    private Constants() {
    }

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String[] PUBLIC_ENDPOINTS = {
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/public/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api-docs/**",
            "/v3/api-docs/**",
            "/actuator/**"
    };

    public static final String[] PUBLIC_GET_ENDPOINTS = {
            "/api/v1/products/**"
    };

    public static final String[] GUEST_ENDPOINTS = {
            "/api/v1/cart/**",
            "/api/v1/checkout/**"
    };

    public static final String[] GUEST_POST_ENDPOINTS = {
            "/api/v1/orders"
    };

    public static final String[] ADMIN_ENDPOINTS = {
            "/api/v1/orders/admin/**",
            "/api/v1/admin/**"
    };

    public static final String[] JWT_SKIP_PATHS = PUBLIC_ENDPOINTS;
}
