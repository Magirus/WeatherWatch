package com.magirus.weatherwatch.bean;

public interface Weather {

    public CommonWeather convertToCommonWeather();

    public int convertSkyState(String skyStateStr);
}
