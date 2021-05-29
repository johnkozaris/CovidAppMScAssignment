package eu.kozaris.covidApp.fragment;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import eu.kozaris.covidApp.R;

import eu.kozaris.covidApp.activity.InternetActivity;
import eu.kozaris.covidApp.utils.Utils;

import java.util.HashMap;
import java.util.Map;



public class ScanFragment extends Fragment {

    private static final String TAG= "ScanFragment";
    OnScanPass scanPasser;

    public ScanFragment() {
        // Required empty public constructor
    }

    public  ScanFragment scanFragment;

    public  ScanFragment getInstance() {
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

    ImageView scanButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        scanFragment = this;
        scanButton = view.findViewById(R.id.imageViewScan);
        scanButton.setOnClickListener(v -> scanPasser.OnScanPass("test"));
        return view;
    }


    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        scanPasser = (OnScanPass)context;
    }





    public interface OnScanPass {
        public void OnScanPass(String data);
    }
}
