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
  private WeatherService mockWeatherService; // acts as a mock weatherService so it is not actually making real API calls

  @InjectMocks
  private WeatherController mockWeatherController; // acts as a mock weatherController

  @BeforeEach
  void setUp(){
    MockitoAnnotations.openMocks(this); // before each tests it initialises the mocks
  }

  // creating new instances of CityInfo and populating them with test data using createCityInfo method
  CityInfo London = createCityInfo("London", "07:20:48", "17:32:05");
  CityInfo Chicago = createCityInfo("Chicago", "06:49:50", "17:20:20");
  CityInfo Birmingham = createCityInfo("Birmingham", "07:20:48", "17:32:05");

  @Test
  void testLongestDayCity1(){ 
    // Checking the case where the city with the logest day is returned

    // Instead of making real API calls, it returns the test data for each city name
    when(mockWeatherService.forecastByCity("London")).thenReturn(London);
    when(mockWeatherService.forecastByCity("Chicago")).thenReturn(Chicago);

    // Calling the longestDay method in the controller to get the city with the longest dat
    ResponseEntity<String> response = mockWeatherController.longestDay("Chicago", "London");
    assertEquals("Chicago", response.getBody()); // Chicago has the longer day so it should return Chicago

  }
  @Test
  void testLongestDayCity2(){
    // Checking for the same case of cities but are swapped

    when(mockWeatherService.forecastByCity("London")).thenReturn(London);
    when(mockWeatherService.forecastByCity("Chicago")).thenReturn(Chicago);

    ResponseEntity<String> response = mockWeatherController.longestDay("London", "Chicago");
    assertEquals("Chicago", response.getBody());

  }
  @Test
  void testLongestDayCityNeither(){

    // Checking the case where both cities have the same amount of daylight hours

    when(mockWeatherService.forecastByCity("London")).thenReturn(London);
    when(mockWeatherService.forecastByCity("Birmingham")).thenReturn(Birmingham);

    ResponseEntity<String> response = mockWeatherController.longestDay("Birmingham", "London");
    assertEquals("Both cities have the same amount of daylight hours.", response.getBody());

  }






  private CityInfo createCityInfo(String cityName, String sunrise, String sunset){
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
      currentConditionsField.set(cityInfo, currentConditions);
        
    } catch (Exception e) {
      e.printStackTrace();
    }
    return cityInfo;
  }
  



}