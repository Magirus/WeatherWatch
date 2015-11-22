package com.magirus.weatherwatch.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.magirus.weatherwatch.BuildConfig;
import com.magirus.weatherwatch.DAO.WeatherDAO;
import com.magirus.weatherwatch.R;
import com.magirus.weatherwatch.bean.CommonWeather;
import com.magirus.weatherwatch.bean.ForecastIOWeather;
import com.magirus.weatherwatch.bean.OpenWeather;
import com.magirus.weatherwatch.bean.WundergroundWeather;
import com.magirus.weatherwatch.util.Agregator;
import com.magirus.weatherwatch.util.GpsUtils;
import com.magirus.weatherwatch.adapter.ForecastListAdapter;

import java.util.concurrent.TransferQueue;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainWeatherActivity extends AppCompatActivity {

    public static final String TAG = "MainWeatherActivity";

    public static Activity instance;
    LinearLayoutManager layoutManager;

    WeatherDAO weatherDAO = new WeatherDAO();
    CommonWeather weatherForecasts;
    LocationListener locationListener;
    Location userLocation;
    GpsUtils utils;

    LinearLayout gpsStatusProgressLayout;
    RecyclerView forecastsList;
    TextView locationTxt;

    boolean locationListenerEnabled = true;
    boolean openWeatherRequested = false;
    boolean forecastIORequested = false;
    boolean wundergroundRequested = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setupWindowAnimations();
        layoutManager = new LinearLayoutManager(instance);
        setContentView(R.layout.activity_main_weather);
        gpsStatusProgressLayout = (LinearLayout) findViewById(R.id.gps_status_progress_container);
        forecastsList = (RecyclerView) findViewById(R.id.list_forecasts);
        forecastsList.setHasFixedSize(true);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        forecastsList.setItemAnimator(itemAnimator);
        locationTxt = (TextView) findViewById(R.id.location);
        utils = new GpsUtils(this);
        locationListener = new LocationListenerCallback();
    }

    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition slideTransition = TransitionInflater.from(this).inflateTransition(R.transition.slide);
            Transition slideTransitionReenter = TransitionInflater.from(this).inflateTransition(R.transition.slide);
            slideTransitionReenter.setInterpolator(new FastOutSlowInInterpolator());
            getWindow().setReenterTransition(slideTransitionReenter);
            getWindow().setExitTransition(slideTransition);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!utils.isGpsEnabled())
            makeGpsDialog().show();
        else {
            if (userLocation == null) {
                Location location = utils.getUserLocation();
                locationListener.onLocationChanged(location);
            }
        }

        if (locationListenerEnabled && !utils.isLocationListenerExists())
            utils.setUserLocationRequestInterval(locationListener, GpsUtils.GPS_INTERVAL_TIME_INTENSE, GpsUtils.GPS_INTERVAL_TIME_INTENSE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            utils.removeUserLocationRequestInterval();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_gps) {
            if (!utils.isGpsEnabled())
                makeGpsDialog().show();
            else {
                Location location = utils.getUserLocation();
                locationListener.onLocationChanged(location);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public AlertDialog makeGpsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.gps_message_title));
        builder.setMessage(getString(R.string.gps_message));
        builder.setPositiveButton(getString(R.string.gps_message_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
                utils.setUserLocationRequestInterval(locationListener, GpsUtils.GPS_INTERVAL_TIME_INTENSE, GpsUtils.GPS_INTERVAL_TIME_INTENSE);
                locationListenerEnabled = true;
            }
        });
        builder.setNegativeButton(getString(R.string.gps_message_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    utils.removeUserLocationRequestInterval();
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
                locationListenerEnabled = false;
                dialog.dismiss();
            }
        });
        return builder.create();
    }

    public class GetWeathersTask extends AsyncTask<Void, Void, CommonWeather> {
        Agregator agregator = new Agregator();
        Call<OpenWeather> weathersCall;
        Call<ForecastIOWeather> forecastIOCall;
        Call<WundergroundWeather> wundergroundCall;
        OpenWeather openWeatherForecasts = null;
        ForecastIOWeather forecastIOForecasts = null;
        WundergroundWeather wundergroundForecasts = null;

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "Requesting for weather forecasts");
        }

        @Override
        protected CommonWeather doInBackground(Void... params) {
            try {
                weathersCall = weatherDAO.getOpenWeatherForecast(userLocation, getString(R.string.openweather_key));
                forecastIOCall = weatherDAO.getForecastIOForecast(userLocation, getString(R.string.forecastio_key));
                wundergroundCall = weatherDAO.getWundergroundForecast(userLocation, getString(R.string.wunderground_key));
                weathersCall.enqueue(openWeatherCallback);
                forecastIOCall.enqueue(forecastIOCallback);
                wundergroundCall.enqueue(wundergroundCallback);
                while (!openWeatherRequested || !forecastIORequested || !wundergroundRequested) {
                    Thread.sleep(10);
                }
                if (openWeatherForecasts != null)
                    agregator.addNewForecastsSource(openWeatherForecasts.convertToCommonWeather());
                if (forecastIOForecasts != null)
                    agregator.addNewForecastsSource(forecastIOForecasts.convertToCommonWeather());
                if (wundergroundForecasts != null)
                    agregator.addNewForecastsSource(wundergroundForecasts.convertToCommonWeather());
                return agregator.agregate();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected final void onPostExecute(CommonWeather resultForecast) {
            weatherForecasts = resultForecast;
            openWeatherRequested = false;
            forecastIORequested = false;
            if (resultForecast != null) {
                locationTxt.setText(resultForecast.location);
                forecastsList.setLayoutManager(layoutManager);
                forecastsList.setAdapter(new ForecastListAdapter(instance, forecastsList, resultForecast));
                forecastsList.invalidate();
            }
        }

        Callback<OpenWeather> openWeatherCallback = new Callback<OpenWeather>() {
            @Override
            public void onResponse(Response<OpenWeather> response, Retrofit retrofit) {
                openWeatherForecasts = response.body();
                openWeatherRequested = true;
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(instance, String.format(getString(R.string.network_error), OpenWeather.NAME), Toast.LENGTH_SHORT).show();
                openWeatherRequested = true;
            }
        };

        Callback<ForecastIOWeather> forecastIOCallback = new Callback<ForecastIOWeather>() {
            @Override
            public void onResponse(Response<ForecastIOWeather> response, Retrofit retrofit) {
                forecastIOForecasts = response.body();
                forecastIORequested = true;
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(instance, String.format(getString(R.string.network_error), ForecastIOWeather.NAME), Toast.LENGTH_SHORT).show();
                forecastIORequested = true;
            }
        };

        Callback<WundergroundWeather> wundergroundCallback = new Callback<WundergroundWeather>() {
            @Override
            public void onResponse(Response<WundergroundWeather> response, Retrofit retrofit) {
                wundergroundForecasts = response.body();
                wundergroundRequested = true;
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(instance, String.format(getString(R.string.network_error), WundergroundWeather.NAME), Toast.LENGTH_SHORT).show();
                wundergroundRequested = true;
            }
        };
    }

    class LocationListenerCallback implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            userLocation = location;
            if (userLocation != null) {
                gpsStatusProgressLayout.setVisibility(View.GONE);
                utils.setUserLocationRequestInterval(locationListener, GpsUtils.GPS_INTERVAL_TIME_NORMAL, GpsUtils.GPS_DISTANCE_NORMAL);
                new GetWeathersTask().execute();
            } else
                utils.setUserLocationRequestInterval(locationListener, GpsUtils.GPS_INTERVAL_TIME_INTENSE, GpsUtils.GPS_DISTANCE_INTENSE);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

}
