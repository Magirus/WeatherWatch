package com.magirus.weatherwatch.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.magirus.weatherwatch.R;
import com.magirus.weatherwatch.activity.DetailedWeatherActivity;
import com.magirus.weatherwatch.bean.CommonWeather;

import java.text.SimpleDateFormat;

public class ForecastListAdapter extends RecyclerView.Adapter<ForecastListAdapter.ViewHolder> implements View.OnClickListener {

    private Activity context;
    private RecyclerView list;
    private CommonWeather weatherForecasts;

    public ForecastListAdapter(final Activity context, final RecyclerView list, final CommonWeather weatherForecasts) {
        this.context = context;
        this.list = list;
        this.weatherForecasts = weatherForecasts;
    }

    @Override
    public ForecastListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listItem = inflater.inflate(R.layout.day_forecst, parent, false);
        listItem.setOnClickListener(this);
        ViewHolder vh = new ViewHolder(listItem);
        return vh;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView skyImage;
        public TextView date, skyText, temperature;
        public Button detailBtn;

        public ViewHolder(View v) {
            super(v);
            skyImage = (ImageView) v.findViewById(R.id.sky_picture);
            date = (TextView) v.findViewById(R.id.date);
            skyText = (TextView) v.findViewById(R.id.sky_text);
            temperature = (TextView) v.findViewById(R.id.temperature);
            detailBtn = (Button) v.findViewById(R.id.detail_btn);
            v.setTag(this);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        CommonWeather.Forecast forecast = (CommonWeather.Forecast) getItem(position);
        if (forecast.sky == CommonWeather.CLEAR_SKY) {
            holder.skyImage.setImageDrawable(context.getResources().getDrawable(R.drawable.sky_clear));
            holder.skyText.setText(context.getString(R.string.weather_sky_clear));
        } else if (forecast.sky == CommonWeather.CLOUDY_SKY) {
            holder.skyImage.setImageDrawable(context.getResources().getDrawable(R.drawable.clouds));
            holder.skyText.setText(context.getString(R.string.weather_sky_clouds));
        } else if (forecast.sky == CommonWeather.RAINY_SKY) {
            holder.skyImage.setImageDrawable(context.getResources().getDrawable(R.drawable.rain));
            holder.skyText.setText(context.getString(R.string.weather_sky_rainy));
        }

        holder.date.setText(new SimpleDateFormat("dd-MM-yyyy").format(forecast.date));
        if (forecast.tempMin != forecast.tempMax)
            holder.temperature.setText(forecast.tempMin + context.getString(R.string.temperature_celcium) + " - " + forecast.tempMax + context.getString(R.string.temperature_celcium));
        else
            holder.temperature.setText(forecast.tempMin + context.getString(R.string.temperature_celcium));

        Animation fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.weather_image);
        holder.skyImage.setAnimation(fadeInAnimation);
        holder.detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailedWeatherActivity.class);
                intent.putExtra("location", weatherForecasts.location);
                intent.putExtra("forecast", (CommonWeather.Forecast) getItem(position));
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(context, holder.skyImage, "sky_image");
                ActivityCompat.startActivity(context, intent, options.toBundle());
            }
        });
    }

    @Override
    public void onClick(View v) {
        int position = list.getChildPosition(v);
        Intent intent = new Intent(context, DetailedWeatherActivity.class);
        intent.putExtra("location", weatherForecasts.location);
        intent.putExtra("forecast", (CommonWeather.Forecast) getItem(position));
        //context.startActivity(intent);
        //context.overridePendingTransition(R.anim.details_activity_entire, R.anim.main_activity_out);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(context, ((ViewHolder) v.getTag()).skyImage, "sky_image");
        ActivityCompat.startActivity(context, intent, options.toBundle());
    }

    @Override
    public int getItemCount() {
        return weatherForecasts.forecasts.size();
    }

    public Object getItem(int position) {
        return weatherForecasts.forecasts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return weatherForecasts.forecasts.get(position).date.getTime();
    }
}