package com.magirus.weatherwatch.bean;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class OpenWeather implements Weather {

    public static final String NAME = "openweather";

    public static final String CLEAR_SKY_TEMPLATE = "clear";
    public static final String CLOUDY_SKY_TEMPLATE = "cloud";
    public static final String RAINY_SKY_TEMPLATE = "rain";

    public static final int KELVIN_IN_0_CELCII = 273;

    @SerializedName("city")
    public City location;
    @SerializedName("list")
    public List<Weather> weatherForecasts;

    public class City {
        public String name;
    }

    public class Weather {
        @SerializedName("dt")
        public long dateMillis;

        @SerializedName("main")
        public MainWeather main;

        @SerializedName("weather")
        public List<Cloud> sky;

        @SerializedName("wind")
        public Wind wind;
    }

    public class MainWeather {
        @SerializedName("temp_min")
        public double tempMmin;
        @SerializedName("temp_max")
        public double tempMax;
        public double pressure;
        public int humidity;
    }

    public class Cloud {
        public String main;
        public String description;
    }

    public class Wind {
        public double speed;
        public double deg;
    }

    @Override
    public CommonWeather convertToCommonWeather() {
        CommonWeather commonWeather = new CommonWeather();
        commonWeather.location = location.name;
        for (Weather openWeather : weatherForecasts) {
            CommonWeather.Forecast forecast = commonWeather.getForecastInstance();
            forecast.date = new Date(openWeather.dateMillis * 1000);
            forecast.tempMin = (int) (openWeather.main.tempMmin - KELVIN_IN_0_CELCII);
            forecast.tempMax = (int) (openWeather.main.tempMax - KELVIN_IN_0_CELCII);
            forecast.pressure = (int) openWeather.main.pressure;
            forecast.humidity = openWeather.main.humidity;
            forecast.wind = (int) openWeather.wind.speed;
            forecast.sky = convertSkyState(openWeather.sky.get(0).main);
            commonWeather.forecasts.add(forecast);
        }
        return commonWeather;
    }

    @Override
    public int convertSkyState(final String skyStateStr) {
        if (skyStateStr.toLowerCase().contains(CLEAR_SKY_TEMPLATE))
            return CommonWeather.CLEAR_SKY;
        else if (skyStateStr.toLowerCase().contains(RAINY_SKY_TEMPLATE))
            return CommonWeather.RAINY_SKY;
        else return CommonWeather.CLOUDY_SKY;
    }


}
