package com.magirus.weatherwatch.activity;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.magirus.weatherwatch.R;
import com.magirus.weatherwatch.bean.CommonWeather;
import com.magirus.weatherwatch.util.GpsUtils;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Random;

public class DetailedWeatherActivity extends AppCompatActivity {

    public static final String TAG = "DetailedWeatherActivity";

    public static Activity instance;
    String location;
    CommonWeather.Forecast forecast;

    ViewGroup rootView;
    ImageView skyImage;
    TextView locationTxt, dateTxt, tempMinTxt, tempMaxTxt, pressureTxt, humidityTxt, windTxt;

    private boolean sizeChanged;
    private int savedWidth;
    private int elementNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setupWindowAnimations();
        setContentView(R.layout.details_property_layout);
        rootView = (ViewGroup) findViewById(R.id.rootView);
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

        skyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLayout();
            }
        });
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random rnd = new Random();
                int color = Color.argb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                animateRevealColorFromCoordinates(rootView, color);
            }
        });
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

    private void changeLayout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Transition slideTransition = TransitionInflater.from(this).inflateTransition(R.transition.image_bounce);
            TransitionManager.beginDelayedTransition(rootView, slideTransition);

            ViewGroup.LayoutParams params = skyImage.getLayoutParams();
            if (sizeChanged) {
                params.width = savedWidth;
            } else {
                savedWidth = params.width;
                params.width = 200;
            }
            sizeChanged = !sizeChanged;
            skyImage.setLayoutParams(params);
        }
    }

    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition changeBounds = TransitionInflater.from(this).inflateTransition(R.transition.changebounds_with_arcmotion);
            Transition fade = TransitionInflater.from(this).inflateTransition(R.transition.fade);
            Transition slide = TransitionInflater.from(this).inflateTransition(R.transition.slide_from_bottom);
            getWindow().setSharedElementEnterTransition(changeBounds);
            getWindow().setReturnTransition(fade);
            getWindow().setEnterTransition(slide);
            slide.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {

                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    addViewAnimations();
                }

                @Override
                public void onTransitionCancel(Transition transition) {

                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            });
        }
    }

    private void animateRevealColorFromCoordinates(ViewGroup viewRoot, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int x = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
            int y = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
            float finalRadius = (float) Math.hypot(viewRoot.getWidth(), viewRoot.getHeight());
            Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, x, y, 0, finalRadius);
            viewRoot.setBackgroundColor(color);
            anim.setDuration(getResources().getInteger(R.integer.anim_duration_long));
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.start();
        }
    }

    private void addViewAnimations() {
        elementNumber = 0;
        animateViews(rootView);
    }

    private void animateViews(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                animateViews(child);
            }
        } else if (view instanceof TextView) {
            view.animate()
                    .setStartDelay(100 + elementNumber * 100)
                    .setInterpolator(AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in))
                    .alpha(1)
                    .scaleX(1)
                    .scaleY(1);
            elementNumber++;
        }
    }
}
