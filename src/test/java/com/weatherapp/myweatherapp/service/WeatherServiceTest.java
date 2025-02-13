package com.weatherapp.myweatherapp.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.model.CityInfo.CurrentConditions;
import com.weatherapp.myweatherapp.controller.WeatherController;

import java.lang.reflect.Field;

import org.springframework.http.ResponseEntity;

class WeatherServiceTest {

  // TODO: 12/05/2023 write unit tests

  @Mock
  private WeatherService mockWeatherService; // Acts as a mock weatherService so it is not actually making real API calls

  @InjectMocks
  private WeatherController mockWeatherController; // Acts as a mock weatherController

  @BeforeEach
  void setUp(){
    MockitoAnnotations.openMocks(this); // Before each tests it initialises the mocks
  }
  private CityInfo createCityInfo(String cityName, String sunrise, String sunset, String conditions){
    //This helper method uses reflection to make the variable accessible and set a value to them

    CityInfo cityInfo = new CityInfo();

    try {
      Field addressField = CityInfo.class.getDeclaredField("address");
      addressField.setAccessible(true);
      addressField.set(cityInfo, cityName);

      Field currentConditionsField = CityInfo.class.getDeclaredField("currentConditions");
      currentConditionsField.setAccessible(true);
      CurrentConditions currentConditions = new CurrentConditions();

      Field sunriseField = CurrentConditions.class.getDeclaredField("sunrise");
      sunriseField.setAccessible(true);
      sunriseField.set(currentConditions, sunrise);

      Field sunsetField = CurrentConditions.class.getDeclaredField("sunset");
      sunsetField.setAccessible(true);
      sunsetField.set(currentConditions, sunset);


      Field conditionsField = CurrentConditions.class.getDeclaredField("conditions");
      conditionsField.setAccessible(true);
      conditionsField.set(currentConditions, conditions);

      currentConditionsField.set(cityInfo, currentConditions);


        
    } catch (Exception e) {
      e.printStackTrace();
    }
    return cityInfo;
  }

  // Creating new instances of CityInfo and populating them with test data using createCityInfo method
  CityInfo London = createCityInfo("London", "07:20:48", "17:32:05", "Rain, Overcast");
  CityInfo Chicago = createCityInfo("Chicago", "06:49:50", "17:20:20","Partially cloudy");
  CityInfo Birmingham = createCityInfo("Birmingham", "07:20:48", "17:32:05","Partially cloudy, Rain");
  CityInfo Montreal = createCityInfo("Montreal", "06:58:36", "17:18:58","Snow, Overcast");

  @Test
  void testLongestDayCity1(){ 
    // Checking the case where the city with the logest day is returned

    String city1 = "Chicago";
    String city2 = "London";

    // Instead of making real API calls, it returns the test data for each city name
    when(mockWeatherService.forecastByCity("London")).thenReturn(London);
    when(mockWeatherService.forecastByCity("Chicago")).thenReturn(Chicago);

    // Calling the longestDay method in the controller to get the city with the longest dat
    ResponseEntity<String> response = mockWeatherController.longestDay(city1, city2);
    assertEquals(Chicago.getAddress(), response.getBody()); // Chicago has the longer day so it should return Chicago

  }
  @Test
  void testLongestDayCity2(){
    // Checking for the same case of cities but are swapped

    String city1 = "London";
    String city2 = "Chicago";

    when(mockWeatherService.forecastByCity("London")).thenReturn(London);
    when(mockWeatherService.forecastByCity("Chicago")).thenReturn(Chicago);

    ResponseEntity<String> response = mockWeatherController.longestDay(city1, city2);
    assertEquals(Chicago.getAddress(), response.getBody());

  }
  @Test
  void testLongestDay_Neither(){

    String city1 = "Birmingham";
    String city2 = "London";

    // Checking the case where both cities have the same amount of daylight hours

    when(mockWeatherService.forecastByCity("London")).thenReturn(London);
    when(mockWeatherService.forecastByCity("Birmingham")).thenReturn(Birmingham);

    ResponseEntity<String> response = mockWeatherController.longestDay(city1, city2);
    assertEquals("Both " + Birmingham.getAddress() + " and " + London.getAddress() + " have the same amount of daylight hours.", response.getBody());

  }

  @Test 
  void testLongestDay_ExceptionHandling_city1(){
    // Case for when city1 does not exist
    String city1 = "Lond1";
    String city2 = "Chicago";
    
    when(mockWeatherService.forecastByCity("Chicago")).thenReturn(Chicago);
    when(mockWeatherService.forecastByCity("Lond1")).thenThrow(new RuntimeException("400 : \"Bad API Request:Invalid location parameter value.\""));
    // Mocks the API so it returns an Exception when the city does not exist

    ResponseEntity<String> response = mockWeatherController.longestDay(city1, city2);

    assertEquals(400, response.getStatusCode().value());
    assertEquals("Error fetching weather data for city with name " + city1 + ", Error Status Code 400 : \"Bad API Request:Invalid location parameter value.\"", response.getBody());
    
  }

  @Test 
  void testLongestDay_ExceptionHandling_city2(){

    String city1 = "Chicago";
    String city2 = "Lond1";
    
    when(mockWeatherService.forecastByCity("Chicago")).thenReturn(Chicago);
    when(mockWeatherService.forecastByCity("Lond1")).thenThrow(new RuntimeException("400 : \"Bad API Request:Invalid location parameter value.\""));

    ResponseEntity<String> response = mockWeatherController.longestDay(city1, city2);

    assertEquals(400, response.getStatusCode().value());
    assertEquals("Error fetching weather data for city with name " + city2 + ", Error Status Code 400 : \"Bad API Request:Invalid location parameter value.\"", response.getBody());
    
  }

  @Test 
  void testLongestDay_ExceptionHandling_BothCities(){

    String city1 = "Chicixp";
    String city2 = "Lond1";
    
    when(mockWeatherService.forecastByCity("Chicixp")).thenThrow(new RuntimeException("400 : \"Bad API Request:Invalid location parameter value.\""));
    when(mockWeatherService.forecastByCity("Lond1")).thenThrow(new RuntimeException("400 : \"Bad API Request:Invalid location parameter value.\""));

    ResponseEntity<String> response = mockWeatherController.longestDay(city1, city2);

    assertEquals(400, response.getStatusCode().value()); 
    assertEquals("Error fetching weather data for city with name " + city1 + ", Error Status Code 400 : \"Bad API Request:Invalid location parameter value.\"", response.getBody());
    // It always checks if city1 exists first so if it does not exist the method stops there and returns an exception
  }


  @Test
  void rainCheckCity1(){
    // Checks the case where city1 is raining

    String city1 = "London";
    String city2 = "Chicago";

    when(mockWeatherService.forecastByCity("London")).thenReturn(London);
    when(mockWeatherService.forecastByCity("Chicago")).thenReturn(Chicago);

    ResponseEntity<String> response = mockWeatherController.rainCheck(city1, city2);

    assertEquals(London.getAddress(), response.getBody());

  }

  @Test
  void rainCheckCity2(){
    // Checks the case where city2 is raining

    String city1 = "Montreal";
    String city2 = "London";

    when(mockWeatherService.forecastByCity("London")).thenReturn(London);
    when(mockWeatherService.forecastByCity("Montreal")).thenReturn(Montreal);

    ResponseEntity<String> response = mockWeatherController.rainCheck(city1, city2);

    assertEquals(London.getAddress(), response.getBody());

  }

  @Test
  void rainCheckCity_BothCities(){
    // Checks the case where both cities are raining

    String city1 = "Birmingham";
    String city2 = "London";

    when(mockWeatherService.forecastByCity("London")).thenReturn(London);
    when(mockWeatherService.forecastByCity("Birmingham")).thenReturn(Birmingham);

    ResponseEntity<String> response = mockWeatherController.rainCheck(city1, city2);

    // Returns both cities in order city1, city2
    assertEquals(Birmingham.getAddress() + ", " + London.getAddress(), response.getBody());

  }

  @Test
  void rainCheckCity_NeitherCities(){
    // Checks the case where it is not raining in both cities

    String city1 = "Montreal";
    String city2 = "Chicago";

    when(mockWeatherService.forecastByCity("Montreal")).thenReturn(Montreal);
    when(mockWeatherService.forecastByCity("Chicago")).thenReturn(Chicago);

    ResponseEntity<String> response = mockWeatherController.rainCheck(city1, city2);

    // Returns both cities in order city1, city2
    assertEquals("It is not raining in neither city.", response.getBody());

  }

}