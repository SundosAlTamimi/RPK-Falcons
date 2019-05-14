package com.example.restposkitchen.fortesting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.Toast;
import com.example.restposkitchen.R;

public class ShowingButtonActivity extends AppCompatActivity {

    Button showHideButton;
    Button getShowHideButton;
    int state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showing_button);

        showHideButton = findViewById(R.id.button);
        getShowHideButton = findViewById(R.id.button2);
        getShowHideButton.setVisibility(View.INVISIBLE);

        showHideButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.e("show", "true");
                    state = 1;
                    getShowHideButton.setVisibility(View.VISIBLE);
                    TranslateAnimation animate = new TranslateAnimation(
                            0,                 // fromXDelta
                            0,                 // toXDelta
                            0,  // fromYDelta
                            getShowHideButton.getHeight());                // toYDelta
                    animate.setDuration(100);
                    animate.setFillAfter(true);
                    getShowHideButton.startAnimation(animate);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.e("show", "false");
                    TranslateAnimation animate = new TranslateAnimation(
                            0,                 // fromXDelta
                            0,                 // toXDelta
                            getShowHideButton.getHeight(),                 // fromYDelta
                            0); // toYDelta
                    animate.setDuration(100);
//                    animate.setFillAfter(true);
                    getShowHideButton.startAnimation(animate);
                    getShowHideButton.setVisibility(View.INVISIBLE);
                    state = 0;
                }
                return false;
            }
        });

    }


}
