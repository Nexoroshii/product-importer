package com.example.productimporter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "import.enabled=false",
    "spring.main.lazy-initialization=true"
})
class ProductImporterApplicationTests {

    @Test
    void contextLoads() {
    }

}
