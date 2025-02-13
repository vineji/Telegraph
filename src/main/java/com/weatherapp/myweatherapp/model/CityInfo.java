package com.weatherapp.myweatherapp.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CityInfo {

  @JsonProperty("address")
  String address;

  public String getAddress() {
      return address;
  }

  @JsonProperty("description")
  String description;

  @JsonProperty("currentConditions")
  CurrentConditions currentConditions;

  public CurrentConditions getCurrentConditions() {
      return currentConditions;
  }

  @JsonProperty("days")
  List<Days> days;

  public static class CurrentConditions {
    @JsonProperty("temp")
    String currentTemperature;

    @JsonProperty("sunrise")
    String sunrise;

    public String getSunrise() {
        return sunrise;
    }

    @JsonProperty("sunset")
    String sunset;

    public String getSunset() {
        return sunset;
    }
    @JsonProperty("feelslike")
    String feelslike;

    @JsonProperty("humidity")
    String humidity;

    @JsonProperty("conditions")
    String conditions;

    public String getConditions() {
        return conditions;
    }
  }

  static class Days {

    @JsonProperty("datetime")
    String date;

    @JsonProperty("temp")
    String currentTemperature;

    @JsonProperty("tempmax")
    String maxTemperature;

    @JsonProperty("tempmin")
    String minTemperature;

    @JsonProperty("conditions")
    String conditions;

    @JsonProperty("description")
    String description;

  }

}
