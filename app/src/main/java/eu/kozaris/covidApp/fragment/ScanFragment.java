package eu.kozaris.covidApp.fragment;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import eu.kozaris.covidApp.R;

import eu.kozaris.covidApp.activity.InternetActivity;
import eu.kozaris.covidApp.utils.Utils;

import java.util.HashMap;
import java.util.Map;



public class ScanFragment extends Fragment {

    private static final String TAG= "ScanFragment";


    public ScanFragment() {
        // Required empty public constructor
    }

    public static ScanFragment scanFragment;

    public static ScanFragment getInstance() {
        return scanFragment;
    }

    public static ScanFragment newInstance() {
        ScanFragment fragment = new ScanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    NfcAdapter mAdapter;
    PendingIntent mPendingIntent;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = NfcAdapter.getDefaultAdapter(getActivity());
        if (mAdapter == null) {
            //nfc not support your device.
            return;
        }
        mPendingIntent = PendingIntent.getActivity(getActivity(), 0, new Intent(getActivity(),
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter!=null) {
            mAdapter.enableForegroundDispatch(getActivity(), mPendingIntent, null, null);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(getActivity());
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        scanFragment = this;
        if (Utils.internetCheck(requireActivity())) {
            startActivity(new Intent(getActivity(), InternetActivity.class));
            requireActivity().finish();
        }


        return view;
    }

    void doNFCScan(){

    }
    void saveContactPoint(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create a new user with a first and last name
        Map<String, Object> contactPoint = new HashMap<>();
        contactPoint.put("tagID", 123);
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



}
