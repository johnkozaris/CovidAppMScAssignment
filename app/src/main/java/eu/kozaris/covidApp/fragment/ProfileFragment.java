package eu.kozaris.covidApp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import eu.kozaris.covidApp.R;
import eu.kozaris.covidApp.activity.BeamActivity;
import eu.kozaris.covidApp.activity.MainActivity;
import eu.kozaris.covidApp.adepter.ContactsAdapter;


public class ProfileFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG="ProfileFragment";
    Button buttonAllowScan;
    RecyclerView recyclerViewContacts;
    ContactsAdapter adapter;
    Switch isUserInfected;
    TextView textViewLogOut;
    TextView textViewName;
    TextView textViewLocation;
    TextView textViewPhone;
    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        recyclerViewContacts = view.findViewById(R.id.recyclerViewContacts);
        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(requireActivity()));
        isUserInfected = view.findViewById(R.id.switchInfected);
        isUserInfected.setOnCheckedChangeListener(this);
        textViewLogOut = view.findViewById(R.id.textViewLogOut);
        textViewLogOut.setOnClickListener(this);
        buttonAllowScan=view.findViewById(R.id.buttonAllowScan);
        buttonAllowScan.setOnClickListener(this);

        textViewName = view.findViewById(R.id.textViewName);
        textViewLocation = view.findViewById(R.id.textView8);
        textViewPhone = view.findViewById(R.id.textViewPhone);
        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();

        if (user!=null){
            textViewName.setText(user.getDisplayName());
            textViewLocation.setText("Thessaloniki");
            textViewPhone.setText(user.getPhoneNumber());
        }

        readContactPoints();
        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
       sendChangedStatus(1500,"Thessaloniki",isChecked);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewLogOut:
                try {
                    FirebaseAuth.getInstance().signOut();
                }catch (Exception e){
                    Log.e(TAG, "Sign out Error ");
                }

                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.buttonAllowScan:
                Intent beamIntent = new Intent(requireActivity(),BeamActivity.class);
                startActivity(beamIntent);
                break;
            default:
                break;
        }

    }

    //TODO this needs to save all the contact points
    //TODO then we need to querry all found contact points for covid status
    void readContactPoints(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<String> contactPointsIDs = new ArrayList<>();
        ArrayList<String> contactPointsLocations = new ArrayList<>();
        ArrayList<String> contactPointsStatuses = new ArrayList<>();
        db.collection("contactPoints")
                .whereEqualTo("myID", "1500")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            contactPointsIDs.add(document.getData().get("tagID").toString());
                            contactPointsLocations.add(document.getData().get("location").toString());
                            contactPointsStatuses.add(document.getData().get("covidStatus").toString());
                        }
                        adapter = new ContactsAdapter(requireActivity(), contactPointsIDs,contactPointsLocations,contactPointsStatuses);
                        recyclerViewContacts.setAdapter(adapter);
                        searchInContactPoints(contactPointsIDs);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

    }
    void searchInContactPoints(ArrayList<String> contactPoints){
        ArrayList<String> infectedContactPoints = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        for (String contactPoint : contactPoints) {
            db.collection("contactPoints")
                    .whereEqualTo("myID", contactPoint)
                    .whereEqualTo("covidStatus", true)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                infectedContactPoints.add(document.get("tagID").toString());
                            }
                            if (!task.getResult().isEmpty()){
                                Toast.makeText(getActivity(), "YOU CAME IN CONTACT WITH AN INFECTED PERSON", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        }

    }

    void sendChangedStatus(int myNFCTagId,String myLocation,boolean status){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create a new user with a first and last name
        Map<String, Object> contactPoint = new HashMap<>();
        contactPoint.put("myID", myNFCTagId);
        contactPoint.put("tagID", myNFCTagId);
        contactPoint.put("location", myLocation);
        contactPoint.put("covidStatus", status);
        db.collection("contactPoints")
                .add(contactPoint)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                    //TODO Alexandre we probably need to add some kind of method to change this entry to not infected
                });
    }



}
