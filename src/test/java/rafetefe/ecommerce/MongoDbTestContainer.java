package rafetefe.ecommerce;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

public abstract class MongoDbTestContainer {
    //Utilizing lib-testcontainers
    private static MongoDBContainer db = new MongoDBContainer("mongo:6.0.4");

    static {
        db.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.host", db::getContainerIpAddress);
        registry.add("spring.data.mongodb.port", () -> db.getMappedPort(27017));
        registry.add("spring.data.mongodb.database", () -> "test");
    }
}
