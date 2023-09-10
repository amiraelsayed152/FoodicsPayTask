package APITests;

import common.User;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import static org.junit.Assert.assertNull;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertEquals;
import java.util.Properties;

public class UserLogin {
    User validUserCredential;
    User invalidUserCredential;
    Properties endPointproperties;

    @BeforeClass
    public void init() throws IOException, CsvException {
        validUserCredential = new User();
        invalidUserCredential = new User();
        endPointproperties = new Properties();
        endPointproperties.load(new FileInputStream("src/test/resources/Endpoints.properties"));
        readUserLoginDataFromCSVFile();
    }

    public void readUserLoginDataFromCSVFile() throws IOException, CsvException {
        CSVReader reader = new CSVReader(new FileReader("src/test/resources/UserCredential.csv"));

        List<String[]> data = reader.readAll();

        String[] validUserCredentialRecord = data.get(0);
        validUserCredential.setEmail(validUserCredentialRecord[0]);
        validUserCredential.setPassword(validUserCredentialRecord[1]);
        validUserCredential.setToken(validUserCredentialRecord[2]);

        String[] invalidUserCredentialRecord = data.get(1);
        invalidUserCredential.setEmail(invalidUserCredentialRecord[0]);
        invalidUserCredential.setPassword(invalidUserCredentialRecord[1]);
        invalidUserCredential.setToken(invalidUserCredentialRecord[2]);
    }

    @Test
    public void validateHTTPStatusCodeOfLoginAPIIsHTTP_OK() {
        RestAssured.baseURI = endPointproperties.getProperty("baseURL");

        Response response = RestAssured.given()
                .queryParam("email", validUserCredential.getEmail())
                .queryParam("password", validUserCredential.getPassword())
                .queryParam("token", validUserCredential.getToken())
                .post(endPointproperties.getProperty("loginEndpoint"));

        response.then().assertThat().statusCode(200);
    }

    @Test
    public void validateHTTPStatusCodeWhenGETUsedInsteadOfPOSTWithLoginAPIIsHTTP_MethodNotAllowed() {
        RestAssured.baseURI = endPointproperties.getProperty("baseURL");

        Response response = RestAssured.given()
                .queryParam("email", validUserCredential.getEmail())
                .queryParam("password", validUserCredential.getPassword())
                .queryParam("token", validUserCredential.getToken())
                .get(endPointproperties.getProperty("loginEndpoint"));

        response.then().assertThat().statusCode(405);
    }

    @Test
    public void validateTokenNotReceivedFromLoginAPIWhileUsingInvalidUserCredential() {
        String accessToken = null;

        RestAssured.baseURI =  endPointproperties.getProperty("baseURL");

        Response response = RestAssured.given()
                .queryParam("email", invalidUserCredential.getEmail())
                .queryParam("password", invalidUserCredential.getPassword())
                .queryParam("token", invalidUserCredential.getToken())
                .post(endPointproperties.getProperty("loginEndpoint"));

        try
        {
            accessToken = response.jsonPath().getString("token");
        }
        catch (Exception JsonPathException)
        {
            System.out.println("validateTokenNotReceivedFromLoginAPIWhileUsingInvalidUserCredential(): Exception While Trying To Parse Response To JSON");
        }

        assertNull(accessToken);
    }

    @Test(priority = 1)
    public void validateTokenFromLoginAPIIsNotNull() {
        String accessToken = null;

        RestAssured.baseURI =  endPointproperties.getProperty("baseURL");

        Response response = RestAssured.given()
                .queryParam("email", validUserCredential.getEmail())
                .queryParam("password", validUserCredential.getPassword())
                .queryParam("token", validUserCredential.getToken())
                .post(endPointproperties.getProperty("loginEndpoint"));

        accessToken = response.jsonPath().getString("token");
        assertNotNull(accessToken);
        validUserCredential.setAccessToken(accessToken);
    }

    @Test(priority = 2)
    public void validateHTTPStatusCodeWhenPOSTUsedInsteadOfGETWithWhoamiAPIIsHTTP_MethodNotAllowed() {
        //This test is failing as there is no validation on the Method for the login API
        RestAssured.baseURI =  endPointproperties.getProperty("baseURL");

        Response response = RestAssured.given()
                .header("Authorization",
                        "Bearer " + validUserCredential.getAccessToken())
                .post(endPointproperties.getProperty("whoamiEndpoint"));

        response.then().assertThat().statusCode(405);
    }

    @Test(priority = 2)
    public void validateUserDataHasReceivedSuccessfullyFromWhoamiAPI(){
        RestAssured.baseURI =  endPointproperties.getProperty("baseURL");

        Response response = RestAssured.given()
                .header("Authorization",
                        "Bearer " + validUserCredential.getAccessToken())
                .get(endPointproperties.getProperty("whoamiEndpoint"));

        response.then().assertThat().statusCode(200);

        String ResponseBody = response.getBody().asString();
        JSONObject jsonResponse = new JSONObject(ResponseBody);
        JSONObject jsonUser = jsonResponse.getJSONObject("user");
        String email = jsonUser.getString("email");

        assertEquals(validUserCredential.getEmail(),email);
    }

    @Test
    public void validateUserDataNotReceivedFromWhoamiAPIWhileUsingInvalidToken(){
        JSONObject jsonUser = null;
        RestAssured.baseURI =  endPointproperties.getProperty("baseURL");

        Response response = RestAssured.given()
                .header("Authorization",
                        "Bearer " + "Foodics123456789Amira")
                .get(endPointproperties.getProperty("whoamiEndpoint"));

        try
        {
            String ResponseBody = response.getBody().asString();
            JSONObject jsonResponse = new JSONObject(ResponseBody);
            jsonUser = jsonResponse.getJSONObject("user");
        }
        catch (Exception JSONException)
        {
            System.out.println("validateUserDataNotReceivedFromWhoamiAPIWhileUsingInvalidToken(): Exception While Trying To Parse Response To JSON");
        }

        assertNull(jsonUser);
    }
}



