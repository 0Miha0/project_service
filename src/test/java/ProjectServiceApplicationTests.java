import faang.school.projectservice.ProjectServiceApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

//@SpringBootTest(classes = ProjectServiceApplication.class)
//@Testcontainers
//@DirtiesContext
public class ProjectServiceApplicationTests {

//    @Container
//    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
//            new PostgreSQLContainer<>("postgres:13.3");
//
//    @DynamicPropertySource
//    static void postgresqlProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.driver-class-name", POSTGRESQL_CONTAINER::getDriverClassName);
//        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
//        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
//        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
//    }

//    @Test
//    @DisplayName("Test context loading")
//    void contextLoads() {
//
//    }
}