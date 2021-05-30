package eu.kozaris.covidApp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import eu.kozaris.covidApp.R;

/**
 * The only use of this is to notify that there is no internet and not let the user continue
 */
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
