package com.magirus.weatherwatch.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommonWeather implements Serializable {
    public static final int CLEAR_SKY = 0;
    public static final int CLOUDY_SKY = 1;
    public static final int RAINY_SKY = 2;

    public String location;
    public List<Forecast> forecasts = new ArrayList<Forecast>();

    public class Forecast implements Serializable {
        public Date date;
        public int tempMin;
        public int tempMax;
        public int pressure;
        public int humidity;
        public int wind;
        public int sky;
    }

    public Forecast getForecastInstance (){
        return new Forecast();
    }
}
