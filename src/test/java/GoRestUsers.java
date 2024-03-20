import POJOClasses.User;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class GoRestUsers {

    public String randomName() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    public String randomEmail() {
        return RandomStringUtils.randomAlphanumeric(7) + "@techno.com";
    }

    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification;

    @BeforeClass
    public void setUp() {
        baseURI = "https://gorest.co.in/public/v2/users/";

        requestSpecification = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer 1352035115bdf297fee05d2110140e048fa57732bcdf430aa119426721d3f505")
                .setContentType(ContentType.JSON)
                .build();

        responseSpecification = new ResponseSpecBuilder()
                .log(LogDetail.BODY)
                .expectContentType(ContentType.JSON)
                .build();
    }

    @Test(priority = 1)
    void getUsersList() {
        given()
                .when()
                .get()// Since the entire url is our baseURI we don't need to use anything in request method
                .then()
                //.log().body()
                .statusCode(200)
                //.contentType(ContentType.JSON)
                .spec(responseSpecification)
                .body("", hasSize(10));
    }

//    @Test
//    void createNewUser() {
//        given()
//                //.header("Authorization","Bearer 1352035115bdf297fee05d2110140e048fa57732bcdf430aa119426721d3f505")
//                .body("{\"name\":\"" + randomName() + "\",\"gender\":\"male\",\"email\":\"" + randomEmail() + "\",\"status\":\"active\"}")
//                //.contentType(ContentType.JSON) // To tell the API our body is in JSON format
//                .spec(requestSpecification)
//                .when()
//                .post()
//                .then()
//                //.log().body()
//                .statusCode(201)
//                //.contentType(ContentType.JSON); // To test the type of the response
//                .spec(responseSpecification);
//
//
////        {
////            "name":"{{$randomFullName}}",
////                "gender":"male",
////                "email":"{{$randomEmail}}",
////                "status":"active"
////        }
//    }

    @Test(priority = 2)
    void createNewUserWithMaps() {
        Map<String, String> user = new HashMap<>();
        user.put("name", randomName());
        user.put("gender", "male");
        user.put("email", randomEmail());
        user.put("status", "active");

        given()
                .spec(requestSpecification)
                .body(user)
                .when()
                .post()
                .then()
                .statusCode(201)
                .spec(responseSpecification)
                .body("email", equalTo(user.get("email")))
                .body("name", equalTo(user.get("name")));
    }

    User user;
    User userFromResponse;

    @Test(priority = 3)
    void createNewUserWithObject() {

        user = new User(randomName(), randomEmail(), "female", "active");

//        User user = new User();
//        user.setName(randomName());
//        user.setEmail(randomEmail());
//        user.setGender("female");
//        user.setStatus("active");

        userFromResponse = given()
                .spec(requestSpecification)
                .body(user)
                .when()
                .post()
                .then()
                .spec(responseSpecification)
                .statusCode(201)
                .body("email", equalTo(user.getEmail()))
                .body("name", equalTo(user.getName()))
                .extract().as(User.class);
    }

    @Test(dependsOnMethods = "createNewUserWithObject", priority = 4)
    void createUserNegativeTest() {

        User userNegative = new User(randomName(), user.getEmail(), "female", "active");

        given()
                .spec(requestSpecification)
                .body(userNegative)
                .when()
                .post()
                .then()
                .spec(responseSpecification)
                .statusCode(422)
                .body("[0].message", equalTo("has already been taken"));
    }

    /**
     * get the user you created in createNewUserWithObject test
     **/

    @Test(dependsOnMethods = "createNewUserWithObject", priority = 5)
    void getUserById() {

        given()
                .pathParam("userId", userFromResponse.getId())
                .spec(requestSpecification)
                .when()
                .get("{userId}")
                .then()
                .spec(responseSpecification)
                .statusCode(200)
                .body("id", equalTo(userFromResponse.getId()))
                .body("name", equalTo(userFromResponse.getName()))
                .body("email", equalTo(userFromResponse.getEmail()));
    }

    /**
     * Update the user you created in createNewUserWithObject
     **/

    @Test(dependsOnMethods = "createNewUserWithObject", priority = 6)
    void updateUser() {
        User updatedUser = new User(randomName(), randomEmail(), "male", "active");

//        userFromResponse.setName(randomName());
//        userFromResponse.setEmail(randomEmail());

        given()
                .spec(requestSpecification)
                .pathParam("userId", userFromResponse.getId())
                .body(updatedUser)
//                .body(userFromResponse)
                .when()
                .put("{userId}")
                .then()
                .spec(responseSpecification)
                .statusCode(200)
                .body("id", equalTo(userFromResponse.getId()))
//                .body("email",equalTo(userFromResponse.getEmail()))
//                .body("name",equalTo(userFromResponse.getName()));
                .body("email", equalTo(updatedUser.getEmail()))
                .body("name", equalTo(updatedUser.getName()));
    }

    /**
     * Delete the user you created in createNewUserWithObject
     **/
    @Test(dependsOnMethods = "createNewUserWithObject", priority = 7)
    void deleteUser() {
        given()
                .spec(requestSpecification)
                .pathParam("userId", userFromResponse.getId())
                .when()
                .delete("{userId}")
                .then()
                .statusCode(204);
    }

    /**
     * create delete user negative test
     **/

    @Test(dependsOnMethods = {"createNewUserWithObject", "deleteUser"}, priority = 8)
    void deleteUserNegativeTest() {
        given()
                .spec(requestSpecification)
                .pathParam("userId", userFromResponse.getId())
                .when()
                .delete("{userId}")
                .then()
                .statusCode(404);
    }

    @Test(dependsOnMethods = {"createNewUserWithObject", "deleteUser"}, priority = 9)
    void getUserByIdNegativeTest() {
        given()
                .pathParam("userId", userFromResponse.getId())
                .spec(requestSpecification)
                .when()
                .get("{userId}")
                .then()
                .statusCode(404);
    }
}
