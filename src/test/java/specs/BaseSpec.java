package specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

import static io.restassured.http.ContentType.JSON;
import static specs.AllureFilter.ALLURE_FILTER;

public class BaseSpec {

    public static final RequestSpecification baseRequestSpec = new RequestSpecBuilder()
            .setBaseUri("https://book-club.qa.guru")
            .setBasePath("/api/v1")
            .setContentType(JSON)
            .addFilter(ALLURE_FILTER)
            .build();
}