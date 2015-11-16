package com.magirus.weatherwatch.bean;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class ForecastIOWeather implements Weather{

    public static final String NAME = "forecast IO";

    public static final String CLEAR_SKY_TEMPLATE = "clear";
    public static final String CLOUDY_SKY_TEMPLATE = "cloud";
    public static final String RAINY_SKY_TEMPLATE = "rain";

    public static final int FAHRENHEIT_IN_0_CELCII = 32;

    @SerializedName("timezone")
    public String location;
    public Daily daily;


    public class Daily {
        @SerializedName("data")
        public List<Weather> weatherForecasts;
    }

    public class Weather {
        @SerializedName("time")
        public long date;
        @SerializedName("summary")
        public String description;
        public double temperatureMin;
        public double temperatureMax;
        public double humidity;
        public double windSpeed;
        public double pressure;
    }

    @Override
    public CommonWeather convertToCommonWeather() {
        CommonWeather commonWeather = new CommonWeather();
        commonWeather.location = location;
        for (Weather fioWeather : daily.weatherForecasts) {
            CommonWeather.Forecast forecast = commonWeather.getForecastInstance();
            forecast.date = new Date(fioWeather.date * 1000);
            forecast.tempMin = (int) (fioWeather.temperatureMin - FAHRENHEIT_IN_0_CELCII);
            forecast.tempMax = (int) (fioWeather.temperatureMax - FAHRENHEIT_IN_0_CELCII);
            forecast.pressure = (int) fioWeather.pressure;
            forecast.humidity = (int)(fioWeather.humidity * 100);
            forecast.wind = (int) fioWeather.windSpeed;
            forecast.sky = convertSkyState(fioWeather.description);
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
