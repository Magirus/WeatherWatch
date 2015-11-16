package com.magirus.weatherwatch.util;

import com.magirus.weatherwatch.bean.CommonWeather;
import com.magirus.weatherwatch.service.WeatherService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class Agregator {

    private List<CommonWeather> forecastsSources = new ArrayList<>();

    public CommonWeather agregate() {
        if (forecastsSources.size() > 0) {
            CommonWeather weather = new CommonWeather();
            Date currentDay = getMinDate();
            Date nextDay = getNextDate(currentDay);
            Date maxDay = getMaxDate();
            weather.location = forecastsSources.get(0).location;
            while (nextDay.compareTo(maxDay) <= 0) {
                CommonWeather.Forecast dailyforecast = null;
                int clearWeaather = 0;
                int cloudyWeather = 0;
                int rainyWeather = 0;
                for (CommonWeather forecastSource : forecastsSources) {
                    for (CommonWeather.Forecast forecast : forecastSource.forecasts) {
                        if (currentDay.compareTo(forecast.date) <= 0 && nextDay.compareTo(forecast.date) > 0) {
                            if (dailyforecast == null) {
                                dailyforecast = forecast;
                                dailyforecast.date = currentDay;
                            } else {
                                dailyforecast.wind = (dailyforecast.wind + forecast.wind) / 2;
                                dailyforecast.humidity = (dailyforecast.humidity + forecast.humidity) / 2;
                                dailyforecast.pressure = (dailyforecast.pressure + forecast.pressure) / 2;
                                dailyforecast.tempMin = (dailyforecast.tempMin + forecast.tempMin) / 2;
                                dailyforecast.tempMax = (dailyforecast.tempMax + forecast.tempMax) / 2;
                            }
                        }
                        if (forecast.sky == CommonWeather.CLEAR_SKY)
                            clearWeaather++;
                        else if (forecast.sky == CommonWeather.CLOUDY_SKY)
                            cloudyWeather++;
                        else if (forecast.sky == CommonWeather.RAINY_SKY)
                            rainyWeather++;
                    }
                }
                if (dailyforecast != null) {
                    if (clearWeaather > cloudyWeather && clearWeaather > rainyWeather)
                        dailyforecast.sky = CommonWeather.CLEAR_SKY;
                    else if (clearWeaather < cloudyWeather && cloudyWeather > rainyWeather)
                        dailyforecast.sky = CommonWeather.CLOUDY_SKY;
                    else if (rainyWeather > cloudyWeather && clearWeaather < rainyWeather)
                        dailyforecast.sky = CommonWeather.RAINY_SKY;
                    weather.forecasts.add(dailyforecast);
                }
                currentDay = nextDay;
                nextDay = getNextDate(nextDay);
            }

            return weather;
        } else
            return null;
    }

    public void addNewForecastsSource(CommonWeather forecasts) {
        if (forecasts != null)
            forecastsSources.add(forecasts);
    }

    private Date getMinDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        return calendar.getTime();
    }

    private Date getNextDate(final Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }

    private Date getMaxDate() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(getMinDate());
        calendar.add(Calendar.DAY_OF_YEAR, WeatherService.DAYS_FORECAST);
        return calendar.getTime();
    }

}
