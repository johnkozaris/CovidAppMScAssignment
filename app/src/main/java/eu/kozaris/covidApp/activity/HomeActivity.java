package eu.kozaris.covidApp.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import eu.kozaris.covidApp.R;
import eu.kozaris.covidApp.fragment.ProfileFragment;
import eu.kozaris.covidApp.fragment.HomeFragment;
import eu.kozaris.covidApp.fragment.ScanFragment;
import eu.kozaris.covidApp.fragment.SearchFragment;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static android.nfc.NdefRecord.createMime;

/**
 * This is the Central activity it handles a lot, it handles API calls as well as all the fragments of the
 * different Views for the bottom navigation
 */
public class HomeActivity extends AppCompatActivity implements ScanFragment.OnScanPass {


    BottomNavigationView bottomNavigation;
    TextView txtActionTitle;
    EditText edSearch;
    LinearLayout lvlSearch;
    AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Inflate views
        bottomNavigation = findViewById(R.id.bottom_navigation);
        txtActionTitle = findViewById(R.id.txt_actiontitle);
        edSearch = findViewById(R.id.ed_search);
        lvlSearch = findViewById(R.id.lvl_search);
        appBarLayout = findViewById(R.id.appBarLayout);
        lvlSearch.setVisibility(View.GONE);

        //Starting for the 1st time so transact to the home fragment
        callFragment(new HomeFragment());
        addTextWatcher();

        //Inflate the Navigation bar and show the edit text for search only in the search fragment.
        // Initially we had the option to search for the country of the home fragment but this was difficult to implement
        // that is why  the editext is not part ONLY of the Search fragment
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                lvlSearch.setVisibility(View.GONE);
                Drawable img = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_home, null);
                assert img != null;
                img.setBounds(0, 0, 60, 60);
                txtActionTitle.setCompoundDrawables(img, null, null, null);
                txtActionTitle.setText("Home");
                callFragment(new HomeFragment());
            } else if (itemId == R.id.menu_profile) {
                Drawable img;
                lvlSearch.setVisibility(View.GONE);
                txtActionTitle.setText("My Profile");
                img = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_account_circle_24, null);
                assert img != null;
                img.setBounds(0, 0, 60, 60);
                txtActionTitle.setCompoundDrawables(img, null, null, null);
                callFragment(new ProfileFragment());
            } else if (itemId == R.id.menu_search) {
                Drawable img;
                callFragment(new SearchFragment());
                lvlSearch.setVisibility(View.VISIBLE);
                txtActionTitle.setText("Search");
                img = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_search_earth_white, null);
                assert img != null;
                img.setBounds(0, 0, 60, 60);
                txtActionTitle.setCompoundDrawables(img, null, null, null);
            } else if (itemId == R.id.menu_scan) {
                Drawable img;
                lvlSearch.setVisibility(View.GONE);
                txtActionTitle.setText("Scan");
                img = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_nfc_24, null);
                img.setBounds(0, 0, 60, 60);
                txtActionTitle.setCompoundDrawables(img, null, null, null);
                callFragment(new ScanFragment());
            }
            return true;
        });
    }

    /**
     * make the Fragment manager to change to a new fragment
     *
     * @param fragmentClass The fragment to change to
     */
    public void callFragment(Fragment fragmentClass) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_fragment, fragmentClass).commit();

    }

    /**
     * The text in the edit text has changed, the user is searching for a country in the API
     * notify the search fragment.
     * (The edit text lives inside the activity not in the fragment)
     */
    private void addTextWatcher() {
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Bundle args = new Bundle();
                args.putInt("id", 0);
                args.putString("search", edSearch.getText().toString().trim());
                if (SearchFragment.getInstance() != null) {
                    SearchFragment.getInstance().getSearch(s.toString());
                }
            }
        });
    }

    /**
     * If the user pressed the back button always move to the HOME Fragment
     */
    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_fragment);
        if (fragment instanceof HomeFragment && fragment.isVisible()) {
            finish();
        } else {
            bottomNavigation.setSelectedItemId(R.id.menu_home);
        }
    }


    /**
     * The Scan fragment called for a scan so start the Beam Activity as a Receiver
     *
     * @param data NOT implemented but can hold this devices id
     */
    @Override
    public void OnScanPass(String data) {
        Intent intent = new Intent(this, BeamActivity.class);
        intent.putExtra(BeamActivity.IS_RECEIVER, true);
        startActivity(intent);
    }

}
