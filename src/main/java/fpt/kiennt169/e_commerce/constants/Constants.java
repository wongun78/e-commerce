package fpt.kiennt169.e_commerce.constants;

public final class Constants {

    private Constants() {
        throw new AssertionError("Cannot instantiate constants class");
    }

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    private static final String[] PUBLIC_ENDPOINTS_ARRAY = {
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/public/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api-docs/**",
            "/v3/api-docs/**",
            "/actuator/**"
    };

    private static final String[] PUBLIC_GET_ENDPOINTS_ARRAY = {
            "/api/v1/products/**"
    };

    private static final String[] GUEST_ENDPOINTS_ARRAY = {
            "/api/v1/cart/**",
            "/api/v1/checkout/**"
    };

    private static final String[] GUEST_POST_ENDPOINTS_ARRAY = {
            "/api/v1/orders"
    };

    private static final String[] ADMIN_ENDPOINTS_ARRAY = {
            "/api/v1/orders/admin/**",
            "/api/v1/admin/**"
    };

    public static final String[] PUBLIC_ENDPOINTS = PUBLIC_ENDPOINTS_ARRAY.clone();
    public static final String[] PUBLIC_GET_ENDPOINTS = PUBLIC_GET_ENDPOINTS_ARRAY.clone();
    public static final String[] GUEST_ENDPOINTS = GUEST_ENDPOINTS_ARRAY.clone();
    public static final String[] GUEST_POST_ENDPOINTS = GUEST_POST_ENDPOINTS_ARRAY.clone();
    public static final String[] ADMIN_ENDPOINTS = ADMIN_ENDPOINTS_ARRAY.clone();
    public static final String[] JWT_SKIP_PATHS = PUBLIC_ENDPOINTS_ARRAY.clone();

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final String DEFAULT_SORT_FIELD = "createdAt";
}
