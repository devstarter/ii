package org.ayfaar.app.controllers;

import org.apache.http.HttpStatus;
import org.ayfaar.app.Application;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.jayway.restassured.RestAssured.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
public class TopicControllerTest {
    @Test
    public void testGet() {

        when().
                get("/api/topic/{name}", "1").
                then().
                log().all().
                statusCode(HttpStatus.SC_OK).

                body("name", Matchers.is("1")).
                body("uri", Matchers.is("тема:1")).
                body("children", Matchers.hasItems("3", "2")).
                body("parents", Matchers.hasItem("4"))
        ;
    }
}