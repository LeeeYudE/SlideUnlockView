package com.demo.charcolee.slideunlockview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.demo.charcolee.slideunlockview.view.SlideUnlockView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        SlideUnlockView slideUnlockView = findViewById(R.id.slideView);
        slideUnlockView.setUnlockListener(new SlideUnlockView.OnUnlockListener() {
            @Override
            public void onUnlock() {
                Toast.makeText(getApplicationContext(),"解锁成功",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
