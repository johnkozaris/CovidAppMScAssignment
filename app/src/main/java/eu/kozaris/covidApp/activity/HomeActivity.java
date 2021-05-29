package eu.kozaris.covidApp.activity;

import android.app.PendingIntent;
import android.content.Intent;
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

public class HomeActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback,
        NfcAdapter.OnNdefPushCompleteCallback {

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

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
            }
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
            mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        }

    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        GetDataFromTag(tag, intent);

    }

    private void GetDataFromTag(Tag tag, Intent intent) {
        Ndef ndef = Ndef.get(tag);
        try {
            ndef.connect();
//            txtType.setText(ndef.getType().toString());
//            txtSize.setText(String.valueOf(ndef.getMaxSize()));
//            txtWrite.setText(ndef.isWritable() ? "True" : "False");
            Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (messages != null) {
                NdefMessage[] ndefMessages = new NdefMessage[messages.length];
                for (int i = 0; i < messages.length; i++) {
                    ndefMessages[i] = (NdefMessage) messages[i];
                }
                NdefRecord record = ndefMessages[0].getRecords()[0];

                byte[] payload = record.getPayload();
                String text = new String(payload);
                Log.e("tag", "vahid" + text);
                ndef.close();

            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Cannot Read From Tag.", Toast.LENGTH_LONG).show();
        }
    }


    public static final String MIME_TEXT_PLAIN = "text/plain";
    private NfcActivity activity;

    public HomeActivity(NfcActivity activity) {
        this.activity = activity;
    }
    public HomeActivity( ) {

    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        // creating outcoming NFC message with a helper method
        // you could as well create it manually and will surely need, if Android version is too low
        String outString = "";
            byte[] outBytes = outString.getBytes();
          NdefRecord outRecord = NdefRecord.createMime(MIME_TEXT_PLAIN, outBytes);
//
        return new NdefMessage(outRecord);
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        // onNdefPushComplete() is called on the Binder thread, so remember to explicitly notify
        // your view on the UI thread
        activity.signalResult();
    }

    public interface NfcActivity {
        String getOutgoingMessage();

        void signalResult();
    }
}
