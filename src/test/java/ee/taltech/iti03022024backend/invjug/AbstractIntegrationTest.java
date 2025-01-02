package ee.taltech.iti03022024backend.invjug;

import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class AbstractIntegrationTest {

    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER;
    protected static final String BASE_URL = "http://localhost:8080/api";

    static {
        POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:17.0-bookworm");
        POSTGRESQL_CONTAINER.start();
    }

    @DynamicPropertySource
    private static void addDataSourceProperties (DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
    }


}
