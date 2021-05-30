package eu.kozaris.covidApp.activity;


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

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import eu.kozaris.covidApp.R;

import static android.nfc.NdefRecord.createMime;

/**
 * This class start an Android BEAM activity, during this, 2 android phone that support BEAM will exchange a single packet
 * The phone MUST support Android BEAM AND NFC - NDEF
 *
 * ATTENTION BEAM or NDEF may not be available in your phone, please use an nfc tag for testing
 */
public class BeamActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback,
        NfcAdapter.OnNdefPushCompleteCallback {

    public static final String IS_RECEIVER = "bool_extra_is_receiver";
    public static final String TAG = "BeamActivity";

    TextView nfcInfoText;
    TextView nfcInfoTitle;
    TextView nfcInfoTitle2;
    NfcAdapter mAdapter;
    PendingIntent mPendingIntent;
    public static final String MIME_TEXT_PLAIN = "text/plain";
    private BeamActivity activity;
    boolean isReceiver;
    String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beam);
        Intent intent = getIntent();
        //Check from the intent if this activity was called to be a message sender or a Receiver
        isReceiver = intent.getBooleanExtra(BeamActivity.IS_RECEIVER, false);

        //Inflate TextViews
        nfcInfoText = findViewById(R.id.textViewNfcInfo);
        nfcInfoTitle = findViewById(R.id.textViewNfcTitle);
        nfcInfoTitle2 = findViewById(R.id.textViewActivatedTitle);
        activity = this;

        //Set alterative textviews if the activity is a message Receiver
        if (isReceiver) {
            nfcInfoTitle.setText("Scan");
            nfcInfoTitle2.setText("A Device");
            nfcInfoText.setText("Place your phone over another \n device to scan its pass");
        }

        //Start an NFC adapted and set this activity as callback listener
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            //nfc not supported in this device.
            return;
        }
        //Set the activity that the intent will point to when an NFC intent is generated
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        //TODO Try to get the device id. if no permission was given then deviceID will default to 1500
        //TODO We had a proccess that got the IMEI but this permission is only granted to systems apps so it was left out
//        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.
//                TELEPHONY_SERVICE);
//        deviceId = "";
//        try {
//            deviceId = telephonyManager.getImei();
//        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), "No permission to read Device ID", Toast.LENGTH_LONG).show();
//        }
//        if (deviceId.isEmpty()) {
            deviceId = "1500";
//        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            //Disable NFC  receiving/sending , app is not on the foreground
            mAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            //Resume NFC  receiving/sending , app is  on the foreground
            mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // A new NFC intent arrived, handle it
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        GetDataFromTag(tag, intent);

    }

    /**
     * Parses the usefull data from an NFC Tag
     *
     * @param tag    the NFC tag that the intent gave us
     * @param intent the calling intent
     */
    private void GetDataFromTag(Tag tag, Intent intent) {
        Ndef ndef = Ndef.get(tag);
        try {
            if (ndef != null) {
                ndef.connect();
            }
            //Get the message data from NFC intent
            Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (messages != null) {
                NdefMessage[] ndefMessages = new NdefMessage[messages.length];
                for (int i = 0; i < messages.length; i++) {
                    ndefMessages[i] = (NdefMessage) messages[i];
                }
                NdefRecord record = ndefMessages[0].getRecords()[0];
                byte[] payload = record.getPayload();
                String text = new String(payload);
                //This Tag is a new contact point send it to Firestore
                saveContactPoint(text);
                //Signal that we completed the NFC read
                signalResult();
                if (ndef != null) {
                    ndef.close();
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Cannot Read From Tag.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * System Callback that Creates an NFC NDEF message WHEN the phone is near an NFC tag
     *
     * @param event The phone is near an NFC tag
     * @return Message formated as NDEF Message
     */
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {


        byte[] outBytes = deviceId.getBytes();
        //Convert string to bytes and then to NDEF message , (requires MIME Type)
        NdefRecord outRecord = createMime(MIME_TEXT_PLAIN, outBytes);
        return new NdefMessage(outRecord);
    }

    private void signalResult() {
        //Handle textviews appropriately if this a reveicer or a sender
        if (isReceiver) {
            nfcInfoText.setText("A device was scanned. This user is covid free");
        } else {
            nfcInfoText.setText("Someone Scanned this device");
        }
    }

    /**
     * Saves the new Tag to the Firestore Database as a new Contact point
     *
     * @param text not implemented, but can carry the NFC tag ID
     */
    void saveContactPoint(String text) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create a new Contact point with the contact point id, my id, location, and covid status
        Map<String, Object> contactPoint = new HashMap<>();
        Random random = new Random();
        int uid = random.nextInt(1000);

        contactPoint.put("tagID", uid);
        contactPoint.put("myID", deviceId);
        //We could use the GPS to get the location but its out of scope for the assginment
        contactPoint.put("location", "Thessaloniki");
        //We dont actually get the covid status for testing purposes,this can be fixed
        contactPoint.put("covidStatus", false);

        //Save to the collection named "contact points"
        db.collection("contactPoints")
                .add(contactPoint)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                    //TODO Alexandre we probably need to add some kind of local save method to fire when we can connect to DB. I thinks its out of scope for te assignment
                });
    }

    /**
     * Called when a send action has been completed
     * onNdefPushComplete() is called on the Binder thread, so remember to explicitly notify
     *
     * @param event NFC push Action completed
     */
    @Override
    public void onNdefPushComplete(NfcEvent event) {
        // notify the rest of the app
        activity.signalResult();
    }


}