package com.magirus.weatherwatch.service;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

import com.magirus.weatherwatch.bean.ForecastIOWeather;
import com.magirus.weatherwatch.bean.OpenWeather;
import com.magirus.weatherwatch.bean.WundergroundWeather;

public interface WeatherService
{
    public static int DAYS_FORECAST = 5;

    @GET("/data/2.5/forecast")
    Call<OpenWeather> getOpenWeatherForecast(@Query("lat") double lat, @Query("lon") double lon, @Query("appId") String appId);

    @GET("/forecast/{appId}/{lat},{lon}")
    Call<ForecastIOWeather> getForecastIOForecast(@Path("appId") String appId, @Path("lat") double lat, @Path("lon") double lon);

    @GET("/api/{appId}/conditions/forecast/q/{lat},{lon}.json")
    Call<WundergroundWeather> getWundergroundForecast(@Path("appId") String appId, @Path("lat") double lat, @Path("lon") double lon);
}
