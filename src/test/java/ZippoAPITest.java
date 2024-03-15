import POJOClasses.Location;
import POJOClasses.User;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

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

    @Test
    void queryParametersTest1() {
        given()
                .param("page", 2)
                .pathParam("apiName", "users")
                .pathParam("version", "v1")
                .log().uri()
                .when()
                .get("https://gorest.co.in/public/{version}/{apiName}")
                .then()
                .log().body()
                .statusCode(200);
    }

    // send the same request for the pages between 1-10 and check if
    // the page number we send from request and page number we get from response are the same

    @Test
    void queryParametersTest2() {
        for (int i = 1; i <= 10; i++) {
            given()
                    .param("page", i)
                    .pathParam("apiName", "users")
                    .pathParam("version", "v1")
                    .log().uri()
                    .when()
                    .get("https://gorest.co.in/public/{version}/{apiName}")
                    .then()
                    .log().body()
                    .statusCode(200)
                    .body("meta.pagination.page", equalTo(i));
        }
    }

    // Write the same test with Data Provider
    @Test(dataProvider = "parameters")
    void queryParametersTestWithDataProvider(int pageNumber, String apiName, String version) {
        given()
                .param("page", pageNumber)
                .pathParam("apiName", apiName)
                .pathParam("version", version)
                .log().uri()
                .when()
                .get("https://gorest.co.in/public/{version}/{apiName}")
                .then()
                .log().body()
                .statusCode(200)
                .body("meta.pagination.page", equalTo(pageNumber));
    }

    @DataProvider
    public Object[][] parameters() {
        Object[][] parametersList = {
                {1, "users", "v1"},
                {2, "users", "v1"},
                {3, "users", "v1"},
                {4, "users", "v1"},
                {5, "users", "v1"},
                {6, "users", "v1"},
                {7, "users", "v1"},
                {8, "users", "v1"},
                {9, "users", "v1"},
                {10, "users", "v1"},
        };
        return parametersList;
    }

    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification;

    @BeforeClass
    public void setUp() {
        baseURI = "https://gorest.co.in/public";
        // if the request url in the request method doesn't have http part
        // rest assured puts baseURI to the beginning of the url in the request method


        // If we are using the same things for our requests in our tests we can put them in request specification
        // so we don't have to write them again and again
        requestSpecification = new RequestSpecBuilder()
                .log(LogDetail.URI)
                .addPathParam("apiName", "users")
                .addPathParam("version", "v1")
                .addParam("page", 3)
                .build();

        // If we are using the same things for our response in our tests we can put them in response specification
        // so we don't have to write them again and again
        responseSpecification = new ResponseSpecBuilder()
                .log(LogDetail.BODY)
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .expectBody("meta.pagination.page", equalTo(3))
                .build();
    }

    @Test
    void baseURITest() {
        given()
                .param("page", 3)
                .log().uri()
                .when()
                .get("/v1/users")
                .then()
                .log().body()
                .statusCode(200);
    }

    @Test
    void specificationsTest() {
        given()
                .spec(requestSpecification)
                .when()
                .get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification);
    }

    @Test
    void extractStringTest() {
        String placeName = given()
                .pathParam("country", "us")
                .pathParam("zip", "90210")
                .when()
                .get("http://api.zippopotam.us/{country}/{zip}")
                .then()
                .log().body()
                .extract().path("places[0].'place name'");

        // with extract method our request returns a value(not an object).
        // extract returns only one part of the response(the part that we specified in the path method) or list of that value
        // we can assign it to a variable and use it however we want

        System.out.println("placeName = " + placeName);
    }

    @Test
    void extractIntTest() {
        int pageNumber = given()
                .spec(requestSpecification)
                .when()
                .get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().path("meta.pagination.page");

        System.out.println("pageNumber = " + pageNumber);
        Assert.assertTrue(pageNumber == 3);

        // We are not allowed to assign an int to a String(cannot assign a type to another type)
    }

    @Test
    void extractListTest1() {
        List<String> nameList = given()
                .spec(requestSpecification)
                .when()
                .get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().path("data.name");

        System.out.println("nameList.size() = " + nameList.size());
        System.out.println("nameList.get(4) = " + nameList.get(4));
        System.out.println("nameList.contains(\"Ravi Adiga\") = " + nameList.contains("Ravi Adiga"));

        Assert.assertTrue(nameList.contains("Ravi Adiga"));
    }

    // Send a request to https://gorest.co.in/public/v1/users?page=3
    // and extract email values from the response and check if they contain patel_atreyee_jr@gottlieb.test

    @Test
    void extractListTest2() {
        List<String> emailList = given()
                .spec(requestSpecification)
                .when()
                .get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().path("data.email");

        Assert.assertTrue(emailList.contains("patel_atreyee_jr@gottlieb.test"));
    }

    // Send a request to https://gorest.co.in/public/v1/users?page=3
    // and check if the next link value contains page=4

    @Test
    void nextLinkTest() {

        String nextLink = given()
                .spec(requestSpecification)
                .when()
                .get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().path("meta.pagination.links.next");

        System.out.println("nextLink = " + nextLink);
        Assert.assertTrue(nextLink.contains("page=4"));
    }

    @Test
    void extractResponse() {
        Response response = given()
                .spec(requestSpecification)
                .when()
                .get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().response();
        // The entire request returns the entire response as a Response object
        // By using this object we are able to extract multiple values with one request.

        int page = response.path("meta.pagination.page");
        System.out.println("page = " + page);

        String currentUrl = response.path("meta.pagination.links.current");
        System.out.println("currentUrl = " + currentUrl);

        String name = response.path("data[1].name");
        System.out.println("name = " + name);

        List<String> emailList = response.path("data.email");
        System.out.println("emailList = " + emailList);

        // extract.path           vs                 extract.response
        // extract.path() can only give us one part of the response. If you need different values from different parts of the response (names and page)
        // you need to write two different request.
        // extract.response() gives us the entire response as an object so if you need different values from different parts of the response (names and page)
        // you can get them with only one request
    }

    // POJO -> Plain Old Java Object
    @Test
    void extractJsonPOJO() {
        Location location = given()
                .pathParam("countryCode", "us")
                .pathParam("zipCode", "90210")
                .when()
                .get("http://api.zippopotam.us/{countryCode}/{zipCode}")
                .then()
                .log().body()
                .extract().as(Location.class);

        System.out.println("location.getPostCode() = " + location.getPostCode());
        System.out.println("location.getCountry() = " + location.getCountry());
        System.out.println("location.getPlaces().get(0).getPlaceName() = " + location.getPlaces().get(0).getPlaceName());
        System.out.println("location.getPlaces().get(0).getState() = " + location.getPlaces().get(0).getState());

        // This request extracts the entire response and assigns it to Location class as a Location object
        // We cannot extract the body partially (e.g. cannot extract place object separately)
    }

    // extract.path() => We can extract only one value (String, int...) or list of that value(List<String>, List<Integer>)
    //      String name = extract.path(data[0].name);
    //      List<String> nameList = extract.path(data.name);

    // extract.response => We can get the entire response as a Response object and get whatever we want from it.
    //      We don't need a class structure. But if you need to use an object for your next requests it is not useful

    // extract.as => We can extract the entire response body as POJO classes. But we cannot extract one part of the body separately.
    //      We need to create a class structure for the entire body
    //      extract.as(Location.class)
    //      extract.as(Place.class) is not allowed
    //      extract.as(User.class)

    // extract.jsonPath() => We can extract the entire body as POJO classes as well as only one part of the body. So if you need only one part
    //      of the body you don't need to create a class structure for the entire body. You only need class for that part of the body
    //      extract.jsonPath().getObject(Location.class)
    //      extract.jsonPath().getObject(Place.class)
    //      extract.jsonPath().getObject(User.class)

    @Test
    void extractWithJsonPath1() {
        User user = given()
                .spec(requestSpecification)
                .when()
                .get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().jsonPath().getObject("data[0]", User.class);

        System.out.println("user.getId() = " + user.getId());
        System.out.println("user.getName() = " + user.getName());
        System.out.println("user.getEmail() = " + user.getEmail());
    }

    @Test
    void extractWithJsonPath2() {

        List<User> userList = given()
                .spec(requestSpecification)
                .when()
                .get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().jsonPath().getList("data", User.class);

        System.out.println("userList.size() = " + userList.size());
        System.out.println("userList.get = " + userList.get(2).getName());
        System.out.println("userList.get(8).getId() = " + userList.get(8).getId());
    }

    @Test
    void extractWithJsonPath3() {

        String name = given()
                .spec(requestSpecification)
                .when()
                .get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().jsonPath().getString("data[1].name");

        System.out.println("name = " + name);
    }

    @Test
    void extractWithJsonPath4() {
        Response response = given()
                .spec(requestSpecification)
                .when()
                .get("/{version}/{apiName}")
                .then()
                .spec(responseSpecification)
                .extract().response();

        int page = response.jsonPath().getInt("meta.pagination.page");
        System.out.println("page = " + page);

        String currentLink = response.jsonPath().getString("meta.pagination.links.current");
        System.out.println("currentLink = " + currentLink);

        User user = response.jsonPath().getObject("data[2]",User.class);
        System.out.println("user.getName() = " + user.getName());
        
        List<User> userList = response.jsonPath().getList("data", User.class);
        System.out.println("userList.size() = " + userList.size());
        System.out.println("userList.get(3).getName() = " + userList.get(3).getName());
    }
}
