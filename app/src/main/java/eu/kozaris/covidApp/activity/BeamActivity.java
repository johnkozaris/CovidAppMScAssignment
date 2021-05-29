package eu.kozaris.covidApp.activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.PendingIntent;
import android.content.Intent;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;

import android.os.Parcelable;

import android.util.Log;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import eu.kozaris.covidApp.R;

import static android.nfc.NdefRecord.createMime;


public class BeamActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback,
        NfcAdapter.OnNdefPushCompleteCallback {

    public static final String IS_RECEIVER= "bool_extra_is_receiver";
    public static final String TAG= "BeamActivity";
    TextView nfcInfoText;
    TextView nfcInfoTitle;
    TextView nfcInfoTitle2;
    NfcAdapter mAdapter;
    PendingIntent mPendingIntent;
    public static final String MIME_TEXT_PLAIN = "text/plain";
    private BeamActivity activity;
    boolean isReceiver;
    public BeamActivity(BeamActivity activity) {
        this.activity = activity;
    }
    public BeamActivity( ) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beam);
        Intent intent = getIntent();
         isReceiver = intent.getBooleanExtra(BeamActivity.IS_RECEIVER,false);
        nfcInfoText = findViewById(R.id.textViewNfcInfo);
        nfcInfoTitle = findViewById(R.id.textViewNfcTitle);
        nfcInfoTitle2 = findViewById(R.id.textViewActivatedTitle);
        activity=this;
        if (isReceiver){
            nfcInfoTitle.setText("Scan");
            nfcInfoTitle2.setText("A Device");
            nfcInfoText.setText("Place your phone over another \n device to scan its pass");
        }


        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            //nfc not support your device.
            return;
        }
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

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
            if (ndef!=null) {
                ndef.connect();
            }
            Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (messages != null) {
                NdefMessage[] ndefMessages = new NdefMessage[messages.length];
                for (int i = 0; i < messages.length; i++) {
                    ndefMessages[i] = (NdefMessage) messages[i];
                }
                NdefRecord record = ndefMessages[0].getRecords()[0];

                byte[] payload = record.getPayload();
                String text = new String(payload);
                saveContactPoint(text);
                signalResult();
                if (ndef!=null) {
                    ndef.close();
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Cannot Read From Tag.", Toast.LENGTH_LONG).show();
        }
    }




    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        // creating outcoming NFC message with a helper method
        // you could as well create it manually and will surely need, if Android version is too low
        String outString = "";
        byte[] outBytes = outString.getBytes();
        NdefRecord outRecord = createMime(MIME_TEXT_PLAIN, outBytes);
//
        return new NdefMessage(outRecord);
    }
    private void signalResult(){
        if (isReceiver){
            nfcInfoText.setText("A device was scanned. This user is covid free");
        }else {
            nfcInfoText.setText("Someone Scanned this device");
        }
    }

    void saveContactPoint(String text){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create a new user with a first and last name
        Map<String, Object> contactPoint = new HashMap<>();
        Random random = new Random();
        int uid =random.nextInt(1000);
        contactPoint.put("tagID", uid);
        contactPoint.put("myID", "1500");
        contactPoint.put("location", "Thessaloniki");
        contactPoint.put("covidStatus", false);
        db.collection("contactPoints")
                .add(contactPoint)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        //TODO Alexandre we probably need to add some kind of local save method to fire when we can connect to DB. I thinks its out of scope for te assignment
                    }
                });
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