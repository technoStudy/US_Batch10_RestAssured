import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ZippoAPITest {

    @Test
    void test1() {
        given() // Preparation(Token, Request Body, parameters, cookies...)

                .when() // To send the request(Request method, request url)

                .then(); // Response(Response body, tests, extract data, set local or global variables)
    }

    @Test
    void statusCodeTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210") // Set up request method and url

                .then()
                .log().body() // prints the response body
                .log().status() // prints the status code
                .statusCode(200); // tests if the status code is 200
    }

    @Test
    void contentTypeTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .contentType(ContentType.JSON); // Tests if the response is in correct form(JSON).
    }

    @Test
    void countryInformationTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .body("country", equalTo("United States")); // Tests if the country value is correct
    }

    // Send a request to "http://api.zippopotam.us/us/90210"
    // and check if the state is "California"
    @Test
    void stateInformationTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .body("places[0].state", equalTo("California"));
    }

    // Send a request to "http://api.zippopotam.us/us/90210"
    // and check if the state abbreviation is "CA"
    @Test
    void stateAbbreviationTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .body("places[0].'state abbreviation'", equalTo("CA"));
    }

    // Send a request to "http://api.zippopotam.us/tr/01000"
    // and check if the body has "Büyükdikili Köyü"
    @Test
    void bodyHasItem() {
        given()
                .when()
                .get("http://api.zippopotam.us/tr/01000")

                .then()
                .log().body()
                .body("places.'place name'", hasItem("Büyükdikili Köyü"));
        // When we don't use index it gets all place names from the response and creates an array with them.
        // hasItem checks if that array contains "Büyükdikili Köyü" value in it
    }

    @Test
    void arrayHasSizeTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/tr/01000")

                .then()
                .log().body()
                .body("places.'place name'", hasSize(71)); //Tests if the size of the list is correct
    }

    @Test
    void multipleTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/tr/01000")

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("places", hasSize(71)) // Tests if places array's size is 71
                .body("places.'place name'", hasItem("Büyükdikili Köyü"))
                .body("country", equalTo("Turkey"));
        // If one test fails the entire @Test fails
    }

    // Parameters
    // There are 2 types of parameters
    //      1) Path parameters -> http://api.zippopotam.us/tr/01000 -> They are parts of the url
    //      2) Query Parameters -> https://gorest.co.in/public/v1/users?page=3 -> They are separated by a ? mark

    @Test
    void pathParametersTest1() {

        String countryCode = "us";
        String zipCode = "90210";

        given()
                .pathParam("country", countryCode)
                .pathParam("zip", zipCode)
                .log().uri()
                .when()
                .get("http://api.zippopotam.us/{country}/{zip}")
                .then()
                .log().body()
                .statusCode(200);
    }

    // send a get request for zipcodes between 90210 and 90213 and verify that in all responses the size
    // of the places array is 1

    @Test
    void pathParametersTest2() {
        for (int i = 90210; i <= 90213; i++) {
            given()
                    .pathParam("zip", i)

                    .when()
                    .get("http://api.zippopotam.us/us/{zip}")
                    .then()
                    .log().body()
                    .body("places", hasSize(1));
        }
    }
}
