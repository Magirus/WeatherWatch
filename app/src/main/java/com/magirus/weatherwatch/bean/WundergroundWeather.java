package com.magirus.weatherwatch.bean;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class WundergroundWeather implements Weather {

    public static final String NAME = "wunderground";

    public static final String CLEAR_SKY_TEMPLATE = "clear";
    public static final String CLOUDY_SKY_TEMPLATE = "cloud";
    public static final String RAINY_SKY_TEMPLATE = "rain";

    @SerializedName("forecast")
    public Forecast weather;
    @SerializedName("current_observation")
    public Pressure pressure;

    public class Forecast {
        public SimpleForecast simpleforecast;
    }

    public class Pressure {
        @SerializedName("pressure_mb")
        public int pressure;
    }

    public class SimpleForecast {
        @SerializedName("forecastday")
        public List<ForecastDay> forecasts;
    }

    public class ForecastDay {
        @SerializedName("date")
        public DateLocation date;
        @SerializedName("high")
        public Temperature tempMax;
        @SerializedName("low")
        public Temperature tempMin;
        @SerializedName("conditions")
        public String sky;
        @SerializedName("maxwind")
        public Wind wind;
        @SerializedName("avehumidity")
        public int humidity;

    }

    public class DateLocation {
        @SerializedName("epoch")
        public long date;
        @SerializedName("tz_long")
        public String location;
    }

    public class Temperature {
        @SerializedName("celsius")
        public int temp;
    }

    public class Wind {
        @SerializedName("kph")
        public int speed;
    }

    @Override
    public CommonWeather convertToCommonWeather() {
        CommonWeather commonWeather = new CommonWeather();
        commonWeather.location = weather.simpleforecast.forecasts.get(0).date.location;
        for (ForecastDay dayForecast : weather.simpleforecast.forecasts) {
            CommonWeather.Forecast forecast = commonWeather.getForecastInstance();
            forecast.date = new Date(dayForecast.date.date * 1000);
            forecast.tempMin = dayForecast.tempMin.temp;
            forecast.tempMax = dayForecast.tempMax.temp;
            forecast.humidity = dayForecast.humidity;
            forecast.pressure = pressure.pressure;
            forecast.wind = dayForecast.wind.speed;
            forecast.sky = convertSkyState(dayForecast.sky);
            commonWeather.forecasts.add(forecast);
        }
        return commonWeather;
    }

    @Override
    public int convertSkyState(String strSkyState) {
        if (strSkyState.toLowerCase().contains(CLEAR_SKY_TEMPLATE))
            return CommonWeather.CLEAR_SKY;
        else if (strSkyState.toLowerCase().contains(RAINY_SKY_TEMPLATE))
            return CommonWeather.RAINY_SKY;
        else return CommonWeather.CLOUDY_SKY;
    }
}
