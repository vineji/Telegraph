package com.weatherapp.myweatherapp.controller;
import java.time.Duration;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.service.WeatherService;

@Controller
public class WeatherController {

  @Autowired
  WeatherService weatherService;

  @GetMapping("/forecast/{city}")
  public ResponseEntity<CityInfo> forecastByCity(@PathVariable("city") String city) {

    CityInfo ci = weatherService.forecastByCity(city);
    return ResponseEntity.ok(ci);
  }


  // Endpoint to compare two cities daylight hours and return the city with the longest day
  @GetMapping("/longestDay/{city1}/{city2}")
  public ResponseEntity<String> longestDay(@PathVariable("city1") String city1, @PathVariable("city2") String city2) {


    CityInfo city1Info;

    // Fetches weather data for each city and handle exceptions (e.g., city does not exist)
    try {
      city1Info = weatherService.forecastByCity(city1);

    } catch (Exception e) {

      return ResponseEntity.status(400).body("Error fetching weather data for city with name " + city1 + ", Error Status Code " + e.getMessage());
    }

    CityInfo city2Info;
    try {
      city2Info = weatherService.forecastByCity(city2);

    } catch (Exception e) {

      return ResponseEntity.status(400).body("Error fetching weather data for city with name " + city2 + ", Error Status Code " + e.getMessage());
    }

    
    // Created getter methods for currentConditions, sunrise and sunset as they are private variables
    String sunriseCity1 = city1Info.getCurrentConditions().getSunrise();
    String sunsetCity1 = city1Info.getCurrentConditions().getSunset();

    String sunriseCity2 = city2Info.getCurrentConditions().getSunrise();
    String sunsetCity2 = city2Info.getCurrentConditions().getSunset();

    double daylightHrs1 = getDaylightHours(sunriseCity1, sunsetCity1); // Using the helper method to calculate daylight hours for each city
    double daylightHrs2 = getDaylightHours(sunriseCity2, sunsetCity2);

    String res = "Both " + city1Info.getAddress() + " and " + city2Info.getAddress() + " have the same amount of daylight hours.";
    
    // Compares daylight hours of both cities
    if (daylightHrs1 > daylightHrs2){
      res = city1Info.getAddress();
    }
    else if (daylightHrs2 > daylightHrs1){
      res = city2Info.getAddress();
    }

    return ResponseEntity.ok(res);

  }

  private double getDaylightHours(String sunrise, String sunset) {    // Helper method to calculate daylight hours using LocalTime

    LocalTime localSunrise = LocalTime.parse(sunrise);
    LocalTime localSunset = LocalTime.parse(sunset);

    double daylightHrs = Duration.between(localSunrise, localSunset).toMinutes() / 60.0;

    return daylightHrs;
  }

  @GetMapping("/rainCheck/{city1}/{city2}")
  public ResponseEntity<String> rainCheck(@PathVariable("city1") String city1, @PathVariable("city2") String city2) {

  // TODO: given two city names, check which city its currently raining in

    CityInfo city1Info;

    // Fetches weather data for each city and handle exceptions (e.g., city does not exist)
    try {
      city1Info = weatherService.forecastByCity(city1);

    } catch (Exception e) {
      return ResponseEntity.status(400).body("Error fetching weather data for city with name " + city1 + ", Error Status Code " + e.getMessage());
    }

    CityInfo city2Info;
    try {
      city2Info = weatherService.forecastByCity(city2);

    } catch (Exception e) {
      return ResponseEntity.status(400).body("Error fetching weather data for city with name " + city2 + ", Error Status Code " + e.getMessage());
    }

    String conditionsCity1 = city1Info.getCurrentConditions().getConditions(); // Gets the conditions from currentConditions
    String conditionsCity2 = city2Info.getCurrentConditions().getConditions();

    // Converts string to lowercase to ensure case-insensitive comparison then checks it it contains the string "rain" which indicates it is raining
    Boolean isCity1Raining = conditionsCity1.toLowerCase().contains("rain");
    Boolean isCity2Raining = conditionsCity2.toLowerCase().contains("rain");

    String res = "It is not raining in neither city."; // For the case where neither cities are raining

    if (isCity1Raining && isCity2Raining){
      res = city1Info.getAddress() + ", " + city2Info.getAddress();
    }
    else if (isCity1Raining){
      res = city1Info.getAddress();
    }
    else if (isCity2Raining){
      res = city2Info.getAddress();
    }

    return ResponseEntity.ok(res);
  }
}

