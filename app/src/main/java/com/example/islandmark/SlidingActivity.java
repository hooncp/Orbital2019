package com.example.islandmark;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SlidingActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout layoutDot;
    private TextView[]dotstv;
    private int[]layouts;
    private Button btnSkip;
    private Button btnNext;
    private CustomPageAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!isFirstTimeStartApp()) {
            startMainActivity();
            finish();
        }

        setStatusBarTransparent();

        setContentView(R.layout.activity_sliding);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        layoutDot = findViewById(R.id.dotLayout);
        btnNext = findViewById(R.id.btn_next);
        btnSkip = findViewById(R.id.btn_skip);

        //When user press skip, start Main Activity
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMainActivity();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPage = viewPager.getCurrentItem()+1;
                if(currentPage < layouts.length) {
                    //move to next page
                    viewPager.setCurrentItem(currentPage);
                } else {
                    startMainActivity();
                }
            }
        });
        layouts = new int[]{R.layout.slider_1,R.layout.slider_2, R.layout.slider_3};
        pagerAdapter = new CustomPageAdapter(layouts,getApplicationContext());
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == layouts.length-1){
                    //LAST PAGE
                    btnNext.setText("START");
                    btnSkip.setVisibility(View.GONE);
                }else {
                    btnNext.setText("NEXT");
                    btnSkip.setVisibility(View.VISIBLE);
                }
                setDotStatus(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setDotStatus(0);
    }

    private boolean isFirstTimeStartApp() {
        SharedPreferences ref = getApplicationContext().getSharedPreferences("IntroSliderApp", Context.MODE_PRIVATE);
        return ref.getBoolean("FirstTimeStartFlag", true);
    }

    private void setFirstTimeStartStatus(boolean stt) {
        SharedPreferences ref = getApplicationContext().getSharedPreferences("IntroSliderApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = ref.edit();
        editor.putBoolean("FirstTimeStartFlag", stt);
        editor.apply();
    }

    private void setDotStatus(int page){
        layoutDot.removeAllViews();
        dotstv =new TextView[layouts.length];
        for (int i = 0; i < dotstv.length; i++) {
            dotstv[i] = new TextView(this);
            dotstv[i].setText("•");
            dotstv[i].setTextSize(30);
            dotstv[i].setTextColor(Color.parseColor("#a9b4bb"));
            layoutDot.addView(dotstv[i]);
        }
        //Set current dot active
        if(dotstv.length>0){
            dotstv[page].setTextColor(Color.parseColor("#ffffff"));
        }
    }
    private void startMainActivity(){
        setFirstTimeStartStatus(false);
        startActivity(new Intent(SlidingActivity.this, MainActivity.class));
        finish();
    }
    private void setStatusBarTransparent(){
        if (Build.VERSION.SDK_INT >= 21){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

    }
}
