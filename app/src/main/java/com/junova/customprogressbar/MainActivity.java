package com.junova.customprogressbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    NumberProgressBar progressBar;
    RendarView rendarView;
    private String[] titles;
    private Double[] values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (NumberProgressBar) this.findViewById(R.id.progress);
        progressBar.setProgress(20);
        rendarView = (RendarView) findViewById(R.id.rendarView);
        Map<String, Double> params = new HashMap<>();
        titles = new String[]{"A", "B", "C", "D", "E", "F"};
        values = new Double[]{100.0, 50.0, 50.0, 0.0, 50.0, 50.0};

        rendarView.setTilte(titles);
        rendarView.setValues(values);

    }
}
