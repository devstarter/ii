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

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
public class TopicControllerTest {
    @Test
    public void testGet() {
        when().
                get("/api/topic/{name}/add-child/{child}", "1", "2").
        then()
                .log().all().
                statusCode(HttpStatus.SC_OK);

        // todo добавить ещё чайлда "3" и парента "4"

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

    @Test
    public void testAddFor() {
        given().
            contentType("application/x-www-form-urlencoded; charset=UTF-8"). // это чтобы русский понимал в параметрах
            param("name", "1"). // имя темы
            param("uri", "видео:youtube:_8vYBLrOq-w"). // uri объекта к которому прилинковать тему
        when().
            post("/api/topic/for").
        then().
                log().all().
                statusCode(HttpStatus.SC_OK)
        ;
    }

    @Test
    public void testAddChild() {
        when().
            get("/api/topic/{name}/add-child/{child}", "1", "2").
        then().
            log().all().
            statusCode(HttpStatus.SC_OK)
        ;
    }



    @Test
    public void testUnLink() {
        // сначала прилинковываем
        when().get("/api/topic/{name}/add-child/{child}", "1", "2").then().statusCode(HttpStatus.SC_OK);

        when().
            get("/api/topic/{name}/unlink/{linked}", "1", "2").
        then().
            log().all().
            statusCode(HttpStatus.SC_OK)
        ;
    }
}