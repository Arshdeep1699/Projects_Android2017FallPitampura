package com.example.arshdeepsingh.mymemegenerator.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.arshdeepsingh.mymemegenerator.R;

public class AboutActivity extends AppCompatActivity {

    Toolbar aboutToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        aboutToolbar = (Toolbar) findViewById(R.id.about_toolbar);
        setSupportActionBar(aboutToolbar);
        getSupportActionBar().setTitle("About");
    }
}
