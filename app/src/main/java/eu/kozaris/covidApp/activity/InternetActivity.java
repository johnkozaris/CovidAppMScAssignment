package eu.kozaris.covidApp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import eu.kozaris.covidApp.R;

public class InternetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
