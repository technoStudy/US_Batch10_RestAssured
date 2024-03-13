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

    @Test
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

    @Test
    void createNewUser() {
        given()
                //.header("Authorization","Bearer 1352035115bdf297fee05d2110140e048fa57732bcdf430aa119426721d3f505")
                .body("{\"name\":\"" + randomName() + "\",\"gender\":\"male\",\"email\":\"" + randomEmail() + "\",\"status\":\"active\"}")
                //.contentType(ContentType.JSON) // To tell the API our body is in JSON format
                .spec(requestSpecification)
                .when()
                .post()
                .then()
                //.log().body()
                .statusCode(201)
                //.contentType(ContentType.JSON); // To test the type of the response
                .spec(responseSpecification);


//        {
//            "name":"{{$randomFullName}}",
//                "gender":"male",
//                "email":"{{$randomEmail}}",
//                "status":"active"
//        }
    }

    @Test
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

    @Test
    void createNewUserWithObject() {

        user = new User(randomName(), randomEmail(), "female", "active");

//        User user = new User();
//        user.setName(randomName());
//        user.setEmail(randomEmail());
//        user.setGender("female");
//        user.setStatus("active");

        given()
                .spec(requestSpecification)
                .body(user)
                .when()
                .post()
                .then()
                .spec(responseSpecification)
                .statusCode(201)
                .body("email", equalTo(user.getEmail()))
                .body("name", equalTo(user.getName()));
    }

    @Test(dependsOnMethods = "createNewUserWithObject")
    void createUserNegativeTest() {

        User userNegative = new User(randomName(), user.getEmail(), "female", "active");

        given()
                .spec(requestSpecification)
                .body(userNegative)
                .when()
                .post()
                .then()
                .spec(responseSpecification)
                .statusCode(422);
    }
}
