package eu.kozaris.covidApp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import eu.kozaris.covidApp.R;
import eu.kozaris.covidApp.activity.BeamActivity;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final String TAG="ProfileFragment";
    Button buttonAllowScan;
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
        buttonAllowScan=view.findViewById(R.id.buttonAllowScan);
        buttonAllowScan.setOnClickListener(this);
        readContactPoints();
        return view;
    }
    @Override
    public void onClick(View v) {
        Intent beamIntent = new Intent(requireActivity(),BeamActivity.class);
        startActivity(beamIntent);
    }

    //TODO this needs to save all the contact points
    //TODO then we need to querry all found contact points for covid status
    void readContactPoints(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<String> contactPoints = new ArrayList<>();
        db.collection("contactPoints")
                .whereEqualTo("myID", "1500")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            contactPoints.add(document.getData().get("tagID").toString());
                        }
                        searchInContactPoints(contactPoints);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

    }
    //TODO if this finds something show it to the user
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
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        }

    }

    //TODO give the user the option to setStatus to Infected
    void sendInfectedStatus(int myNFCTagId,String myLocation){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create a new user with a first and last name
        Map<String, Object> contactPoint = new HashMap<>();
        contactPoint.put("tagID", myNFCTagId);
        contactPoint.put("location", myLocation);
        contactPoint.put("covidStatus", true);
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
                        //TODO Alexandre we probably need to add some kind of local save method to fire when we can connect to DB. I thinks its out of scope for the assignment
                    }
                });
    }


}
