package specs;

import io.qameta.allure.restassured.AllureRestAssured;

public class AllureFilter {

    public static final AllureRestAssured ALLURE_FILTER = new AllureRestAssured()
            .setRequestTemplate("request.ftl")    // ← без tpl/
            .setResponseTemplate("response.ftl"); // ← без tpl/
}