package ua.goit.offline.rs.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

public class WeatherService {

  @GET
  @Path("/weather")
  @Produces(MediaType.APPLICATION_JSON)
  public WeatherResult getWeather(@QueryParam("city") String city) {
    return new WeatherResult(city, "Sunny", 30);
  }

  public static class WeatherResult {
    private String city;
    private String value;
    private int temperature;

    WeatherResult(String city, String value, int temperature) {
      this.city = city;
      this.value = value;
      this.temperature = temperature;
    }

    public String getCity() {
      return city;
    }

    public String getValue() {
      return value;
    }

    public int getTemperature() {
      return temperature;
    }
  }
}
