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

public class HomeActivity extends AppCompatActivity implements  ScanFragment.OnScanPass{

    public static final String MIME_TEXT_PLAIN = "text/plain";

    BottomNavigationView bottomNavigation;
    TextView txtActionTitle;
    EditText edSearch;
    LinearLayout lvlSearch;
    AppBarLayout appBarLayout;
    NfcAdapter mAdapter;
    PendingIntent mPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavigation=findViewById(R.id.bottom_navigation);
        txtActionTitle =findViewById(R.id.txt_actiontitle);
        edSearch=findViewById(R.id.ed_search);
        lvlSearch=findViewById(R.id.lvl_search);
        appBarLayout=findViewById(R.id.appBarLayout);
        lvlSearch.setVisibility(View.GONE);
        callFragment(new HomeFragment());
        addTextWatcher();

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

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            //nfc not support your device.
            return;
        }
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    public void callFragment(Fragment fragmentClass) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_fragment, fragmentClass).commit();

    }

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
//                if (edSearch.getText().toString().trim().length() != 0) {
                Bundle args = new Bundle();
                args.putInt("id", 0);
                args.putString("search", edSearch.getText().toString().trim());
                if (SearchFragment.getInstance() != null) {
                    SearchFragment.getInstance().getSearch(s.toString());
                }
//                } else {

//                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_fragment);
        if (fragment instanceof HomeFragment && fragment.isVisible()) {
            finish();
        } else {
            bottomNavigation.setSelectedItemId(R.id.menu_home);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            enableForegroundDispatch(this,mAdapter);
        }

    }

    @Override
    public void OnScanPass(String data) {
        Intent intent = new Intent(this, BeamActivity.class);
        intent.putExtra(BeamActivity.IS_RECEIVER, true);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIntent(intent);
    }



    void processIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            NdefMessage inNdefMessage = (NdefMessage) parcelables[0];
            NdefRecord[] inNdefRecords = inNdefMessage.getRecords();
            NdefRecord ndefRecord_0 = inNdefRecords[0];

            String inMessage = new String(ndefRecord_0.getPayload());
            this.txtActionTitle.setText(inMessage);
        }
    }
    public void enableForegroundDispatch(AppCompatActivity activity, NfcAdapter adapter) {

        // here we are setting up receiving activity for a foreground dispatch
        // thus if activity is already started it will take precedence over any other activity or app
        // with the same intent filters


        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        //
        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException ex) {
            throw new RuntimeException("Check your MIME type");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }
}
