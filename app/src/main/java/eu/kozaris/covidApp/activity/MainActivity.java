package eu.kozaris.covidApp.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import eu.kozaris.covidApp.R;

/**
 * This activity is the Launcher it logs the user in using Firebase Auth
 * If you want to bypass the login proccess click on the app name textview
 */
public class MainActivity extends AppCompatActivity implements ActivityResultCallback<ActivityResult> {
    //Permission IDs
    private static final int RC_PERM_NFC = 2;
    private static final int RC_PERM_INTERNET = 3;
    private static final int RC_PERM_NETWORK_CH = 4;
    private static final int RC_PERM_NETWORK_AC = 5;

    ActivityResultLauncher<Intent> someActivityResultLauncher;
    List<AuthUI.IdpConfig> providers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Inflate Views
        Button signInButton = findViewById(R.id.buttonSignIn);
        TextView title = findViewById(R.id.textViewLoginTitle);

        //Bypass the login process by clicking the Textview Title. This is for testing purposes
        title.setOnClickListener(v -> {
            Intent intent = new Intent(this.getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        });

        // Choose authentication providers
        providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build());

        //Launch the firebase Auth UI activity and set this activity as a callback to get the result of the login proccess this will happen in the OnActivityResult
        someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this);
        signInButton.setOnClickListener(v -> someActivityResultLauncher.launch(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers)
                .build()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermission(Manifest.permission.NFC, RC_PERM_NFC);
        checkPermission(Manifest.permission.INTERNET, RC_PERM_INTERNET);
        checkPermission(Manifest.permission.ACCESS_NETWORK_STATE, RC_PERM_NETWORK_CH);
        checkPermission(Manifest.permission.CHANGE_NETWORK_STATE, RC_PERM_NETWORK_AC);
    }

    /**
     * Check if we have permmisiion and if we dont ask for permissions
     *
     * @param permission  The permission to ask
     * @param requestCode Permission id
     */
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        }
    }

    /**
     * The Firebase Auth UI finished
     *
     * @param result the result code of the Auth UI activity
     */
    @Override
    public void onActivityResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            //Firebase instance is a singleton and can be called from anywhere , here we will only check if the login was a success
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Intent intent = new Intent(this.getApplicationContext(), HomeActivity.class);
            if (user != null) {
                //The login is a success go to the HomeActivity
                startActivity(intent);
            } else {
                //The user probably canceled
                Toast.makeText(this, "Login Failed try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            //The login proccess resulted in an error
            Toast.makeText(this, "Login Failed try again", Toast.LENGTH_SHORT).show();
        }
    }
}
