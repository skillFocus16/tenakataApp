package com.naamini.tenakataapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.naamini.tenakataapp.R;
/**
 * Created by Naamini Yonazi on 19/07/20
 */
public class SplashScreenActivity extends Activity {

    private final int SPLASH_TIME_OUT = 2000;
    Thread splashTread;
    ImageView splash;
    LinearLayout splashLayout;

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash_screen);
            initComponents();
            startAnimations();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initComponents() {
        splash = findViewById(R.id.splash);
        splashLayout = findViewById(R.id.splashLayout);

        Glide.with(SplashScreenActivity.this)
                .load(R.drawable.tenakata_logo)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .apply(RequestOptions.overrideOf(500, 500))
                .apply(RequestOptions.placeholderOf(R.color.colorPrimary))
                .apply(RequestOptions.errorOf(R.color.colorPrimary))
                .into(splash);
    }

    private void startAnimations() {
        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(SPLASH_TIME_OUT);
                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    finish();
                }
            }
        };
        splashTread.start();
    }
}