package org.ayfaar.app.controllers;

import com.jayway.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.ayfaar.app.Application;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
public class TopicControllerTest {
    @Test
    public void testGet() {
        addChild("1", "2");
        addChild("1", "10");
        addChild("5", "1");

        given().param("name", "1").
        when().get("/api/topic").
        then().
            log().all().
            statusCode(HttpStatus.SC_OK).
            body("name", Matchers.is("1")).
            body("uri", Matchers.is("тема:1")).
            body("children", Matchers.hasItems("2", "10")).
            body("parents", Matchers.hasItem("5"))
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
        addChild("1", "2");
    }

    @Test(expected = AssertionError.class)
    public void testAddChildWrong() {
        addChild("1", "2");
        //Expected RuntimeException: The parent has a child for the given name
        addChild("2", "1");
    }

    @Test
    public void testUnLink() {
        // сначала прилинковываем
        addChild("1", "2");

        given().
                params("name", "1", "linked", "2").
        when().
            get("/api/topic/unlink").
        then().
            log().all().
            statusCode(HttpStatus.SC_OK)
        ;

        given().param("name", "1").
                when().get("/api/topic").
                then().
                log().all().
                statusCode(HttpStatus.SC_OK).
                body("name", Matchers.is("1")).
                body("uri", Matchers.is("тема:1")).
                body("children", Matchers.hasItems("10")).//"2" does not exist in children after unlink
                body("parents", Matchers.hasItem("5"))
        ;
    }

    @Test(expected = AssertionError.class)
    public void testMerge() {
        addChild("1011", "2012");
        addChild("101", "2011");
        addChild("4", "101");
        given().
                params("main", "101", "mergeWith", "1011").
                when().
                get("/api/topic/merge").
                then().
                log().all().
                statusCode(HttpStatus.SC_OK)
        ;

        given().param("name", "101").
                when().get("/api/topic").
                then().
                log().all().
                statusCode(HttpStatus.SC_OK).
                body("name", Matchers.is("101")).
                body("uri", Matchers.is("тема:101")).
                body("children", Matchers.hasItems("2011", "2012")).
                body("parents", Matchers.hasItem("4"))
        ;

        //Expected RuntimeException: Topic for 1011 not found, INTERNAL_SERVER_ERROR
        given().param("name", "1011").
                when().get("/api/topic").
                then().
                log().all().
                statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                body("status", Matchers.is("error")).
                body("code",Matchers.is("UNDEFINED")).
                body("message", Matchers.is("java.lang.RuntimeException: Topic for 1011 not found"))
        ;
    }

    @Test(expected = AssertionError.class)
    public void testMerge2() {
        addChild("1011", "2012");
        addChild("1", "1011");
        addChild("101", "2011");
        addChild("4", "101");
        given().
                params("main", "101", "mergeWith", "1011").
                when().
                get("/api/topic/merge").
                then().
                log().all().
                statusCode(HttpStatus.SC_OK)
        ;

        given().param("name", "101").
                when().get("/api/topic").
                then().
                log().all().
                statusCode(HttpStatus.SC_OK).
                body("name", Matchers.is("101")).
                body("uri", Matchers.is("тема:101")).
                body("children", Matchers.hasItems("2011", "2012")).
                body("parents", Matchers.hasItems("4","1"))
        ;

        //Expected RuntimeException: Topic for 1011 not found, INTERNAL_SERVER_ERROR
        given().param("name", "1011").
                when().get("/api/topic").
                then().
                log().all().
                statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                body("status", Matchers.is("error")).
                body("code", Matchers.is("UNDEFINED")).
                body("message", Matchers.is("java.lang.RuntimeException: Topic for 1011 not found"))
        ;
    }
}