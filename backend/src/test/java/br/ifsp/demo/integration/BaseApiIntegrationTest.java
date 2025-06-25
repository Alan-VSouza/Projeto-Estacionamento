package br.ifsp.demo.integration;

import br.ifsp.demo.security.user.JpaUserRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseApiIntegrationTest {

    @Autowired
    protected JpaUserRepository repository;

    @BeforeEach
    public void generalSetup() {
        RestAssured.baseURI = "http://localhost:8080";
    }

    @AfterEach
    public void tearDown() {
        repository.deleteAll();
    }
}
