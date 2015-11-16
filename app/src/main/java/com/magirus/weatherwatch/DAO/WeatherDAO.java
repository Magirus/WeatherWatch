package com.magirus.weatherwatch.DAO;

import android.location.Location;

import java.util.List;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

import com.magirus.weatherwatch.bean.ForecastIOWeather;
import com.magirus.weatherwatch.bean.OpenWeather;
import com.magirus.weatherwatch.bean.WundergroundWeather;
import com.magirus.weatherwatch.service.WeatherService;

public class WeatherDAO {

    public Call<OpenWeather> getOpenWeatherForecast(final Location location, final String openWeatherId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org").addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherService service = retrofit.create(WeatherService.class);
        return service.getOpenWeatherForecast(location.getLatitude(), location.getLongitude(), openWeatherId);
    }

    public Call<ForecastIOWeather> getForecastIOForecast(final Location location, final String forecastIOId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.forecast.io").addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherService service = retrofit.create(WeatherService.class);
        return service.getForecastIOForecast(forecastIOId, location.getLatitude(), location.getLongitude());
    }

    public Call<WundergroundWeather> getWundergroundForecast(final Location location, final String wundergroundId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.wunderground.com").addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherService service = retrofit.create(WeatherService.class);
        return service.getWundergroundForecast(wundergroundId, location.getLatitude(), location.getLongitude());
    }
}
