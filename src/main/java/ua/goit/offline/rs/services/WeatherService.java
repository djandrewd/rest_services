package ua.goit.offline.rs.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

public class WeatherService {

  @GET
  @Path("/weather")
  public String getWeather(@QueryParam("city")
                                 String city) {
    return "sunny";
  }
}
