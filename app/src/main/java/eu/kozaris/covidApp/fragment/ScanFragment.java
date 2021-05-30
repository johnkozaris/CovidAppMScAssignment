package eu.kozaris.covidApp.fragment;

import android.content.Context;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import org.jetbrains.annotations.NotNull;

import eu.kozaris.covidApp.R;



public class ScanFragment extends Fragment {

//    private static final String TAG = "ScanFragment";
    OnScanPass scanPasser;

    //Boilerplate code
    public ScanFragment() {
        // Required empty public constructor
    }

    //Boilerplate code
    public ScanFragment scanFragment;

    //Boilerplate code
    public ScanFragment getInstance() {
        return scanFragment;
    }

    //Boilerplate code
    public static ScanFragment newInstance() {
        ScanFragment fragment = new ScanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    ImageView scanButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        //Inflate Views
        scanFragment = this;
        scanButton = view.findViewById(R.id.imageViewScan);
        //If the scan button is clicked notify the parent activity,the passed string is not used
        scanButton.setOnClickListener(v -> scanPasser.OnScanPass("test"));
        return view;
    }


    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        scanPasser = (OnScanPass) context;
    }

    //Interface tha the parent activity implements to get notified that the imageview has been clicked
    public interface OnScanPass {
        public void OnScanPass(String data);
    }
}
