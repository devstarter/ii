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
        addChild("156", "2");
        addChild("156", "1001");
        addChild("400", "156");

        // todo добавить ещё чайлда "3" и парента "4"

        given().param("name", "156").
        when().get("/api/topic").
        then().
            log().all().
            statusCode(HttpStatus.SC_OK).
            body("name", Matchers.is("156")).
            body("uri", Matchers.is("тема:156")).
            body("children", Matchers.hasItems("2", "1001")).
            body("parents", Matchers.hasItem("400"))
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

    @Test(expected = AssertionError.class)
    public void testAddChildWrong() {
        addChild("1", "4");
        addChild("4", "1");
    }

    @Test
    public void testUnLink() {
        // сначала прилинковываем
        addChild("1001", "2015");

        given().
                params("name", "1001", "linked", "2015").
        when().
            get("/api/topic/unlink").
        then().
            log().all().
            statusCode(HttpStatus.SC_OK)
        ;
    }
}