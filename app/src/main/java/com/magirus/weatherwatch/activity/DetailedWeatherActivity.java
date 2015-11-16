package com.magirus.weatherwatch.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.magirus.weatherwatch.R;
import com.magirus.weatherwatch.bean.CommonWeather;
import com.magirus.weatherwatch.util.GpsUtils;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;

public class DetailedWeatherActivity extends AppCompatActivity {

    public static final String TAG = "DetailedWeatherActivity";

    public static Activity instance;
    String location;
    CommonWeather.Forecast forecast;

    ImageView skyImage;
    TextView locationTxt, dateTxt, tempMinTxt, tempMaxTxt, pressureTxt, humidityTxt, windTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.details_property_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        location = getIntent().getStringExtra("location");
        forecast = (CommonWeather.Forecast) getIntent().getSerializableExtra("forecast");

        locationTxt = (TextView) findViewById(R.id.location);
        dateTxt = (TextView) findViewById(R.id.date);
        tempMinTxt = (TextView) findViewById(R.id.tempMin);
        tempMaxTxt = (TextView) findViewById(R.id.tempMax);
        pressureTxt = (TextView) findViewById(R.id.pressure);
        humidityTxt = (TextView) findViewById(R.id.humidity);
        windTxt = (TextView) findViewById(R.id.wind);
        skyImage = (ImageView) findViewById(R.id.sky_image);

        locationTxt.setText(location);
        dateTxt.setText(new SimpleDateFormat("dd-MM-yyyy").format(forecast.date).toString());
        tempMinTxt.setText(String.valueOf(forecast.tempMin));
        tempMaxTxt.setText(String.valueOf(forecast.tempMax));
        pressureTxt.setText(String.valueOf(forecast.pressure));
        humidityTxt.setText(String.valueOf(forecast.humidity));
        windTxt.setText(String.valueOf(forecast.wind));

        if (forecast.sky == CommonWeather.CLEAR_SKY) {
            skyImage.setImageDrawable(getResources().getDrawable(R.drawable.sky_clear));
        } else if (forecast.sky == CommonWeather.CLOUDY_SKY) {
            skyImage.setImageDrawable(getResources().getDrawable(R.drawable.clouds));
        } else if (forecast.sky == CommonWeather.RAINY_SKY) {
            skyImage.setImageDrawable(getResources().getDrawable(R.drawable.rain));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.main_activity_in, R.anim.details_activity_off);
    }

}
