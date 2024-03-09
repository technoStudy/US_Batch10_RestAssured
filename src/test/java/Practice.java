import POJOClasses.ToDo;
import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class Practice {

    /**
     * Task 1
     * write a request to https://jsonplaceholder.typicode.com/todos/2
     * expect status 200
     * Convert Into POJO
     */

    @Test
    void task1() {

        ToDo toDo = given()
                .when()
                .get("https://jsonplaceholder.typicode.com/todos/2")
                .then()
                .statusCode(200)
                .extract().as(ToDo.class);

        System.out.println("toDo = " + toDo);

    }

    /**
     * Task 2
     * send a get request to https://jsonplaceholder.typicode.com/todos/2
     * expect status 200
     * expect content type JSON
     * expect title in response body to be "quis ut nam facilis et officia qui"
     */

    @Test
    void task2() {
//        given()
//                .when()
//                .get("https://jsonplaceholder.typicode.com/todos/2")
//                .then()
//                .statusCode(200)
//                .contentType(ContentType.JSON)
//                .body("title",equalTo("quis ut nam facilis et officia qui"));

//        String title = given()
//                .when()
//                .get("https://jsonplaceholder.typicode.com/todos/2")
//                .then()
//                .statusCode(200)
//                .contentType(ContentType.JSON)
//                .extract().path("title");
//
//        Assert.assertEquals(title, "quis ut nam facilis et officia qui");

        ToDo toDo = given()
                .when()
                .get("https://jsonplaceholder.typicode.com/todos/2")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().as(ToDo.class);

        Assert.assertEquals(toDo.getTitle(),"quis ut nam facilis et officia qui");
    }
}
