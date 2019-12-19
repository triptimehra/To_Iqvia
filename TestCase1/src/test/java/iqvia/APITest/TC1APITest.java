package iqvia.APITest;

import static io.restassured.RestAssured.given;

import java.util.List;

import static io.restassured.RestAssured.*;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
//import static org.hamcrest.Matchers.*;

import org.hamcrest.Matchers;

public class TC1APITest {
	List<String> capital;
	String currency ;
	String WrongCapital = "delii";
	
	@BeforeTest
	public void getCapitalList()
	{  
		//Get request to get capital name and associated currency
		RestAssured.baseURI = "https://restcountries.eu";	
		
		Response res = given()
				.param("fields", "capital;currencies;latlng")
		.when()
		.get("/rest/v2/all" )
		.then()
		.statusCode(200)
		.extract().response();
		//extracting all capital in a list
			capital = res.jsonPath().getList("capital");
		//Find currency code for first capital
			currency = res.jsonPath().param("cap", capital.get(0)).getString("find{it.capital== cap}.currencies[0].code");
		System.out.println(currency);
		
	}
	//positive tc
	@Test
	public void checkCapital()
	{   
		RestAssured.baseURI = "https://restcountries.eu";	
		given()
		.param("fields", "name;capital;currencies;latlng;regionalBlocs")
		   .when().get("/rest/v2/capital/"+ capital.get(0))
		   .then()
		   .statusCode(200)
		   .assertThat().body("currencies[0].code", Matchers.hasItem(currency))
		   .log().all();
	}
	//negative tc
	@Test
	public void checkCapitalFalse()
	{  //case where capital is not in list
		RestAssured.baseURI = "https://restcountries.eu";	
		if(!capital.contains(WrongCapital))
		{
	   given()
	   .param("fields", "name;capital;currencies;latlng;regionalBlocs")
	   .when().get("/rest/v2/capital/"+WrongCapital)
	   .then()
	   .statusCode(404)
	   .statusLine("HTTP/1.1 404 ")
	.log().all();
		}
		else
		{
			System.out.println("choose another value for capital");
		}
	}

}
