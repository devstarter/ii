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

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
public class TopicControllerTest {
    @Test
    public void testGet() {
        addChild("1001", "2015");
        addChild("1001", "3015");
        addChild("4", "1001");

        // todo добавить ещё чайлда "3" и парента "4"

        given().param("name", "1001").
        when().get("/api/topic").
        then().
            log().all().
            statusCode(HttpStatus.SC_OK).
            body("name", Matchers.is("1001")).
            body("uri", Matchers.is("тема:1001")).
            body("children", Matchers.hasItems("3015", "2015")).
            body("parents", Matchers.hasItem("4"))
        ;
    }


    private void addChild(String name, String child) {
        given().
                params("name", name, "child", child).
        when().
                get("/api/topic/add-child").
        then().
                statusCode(HttpStatus.SC_OK);
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
        addChild("1", "2012");
    }

    @Test//(expected = AssertionError.class)
    public void testAddChildWrong() {
        addChild("2012", "1");
    }

//    @Test
//    public void testUnLink() {
//        // сначала прилинковываем
//        addChild("1", "2011");
//
//        given().
//                param("name", "1", "linked", "2011").
//        when().
//            get("/api/topic/unlink").
//        then().
//            log().all().
//            statusCode(HttpStatus.SC_OK)
//        ;
//    }
}