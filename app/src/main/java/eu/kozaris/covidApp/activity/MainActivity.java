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

public class MainActivity extends AppCompatActivity implements ActivityResultCallback<ActivityResult> {

    private static final int RC_PERM_NFC = 2;
    private static final int RC_PERM_INTERNET = 3;
    private static final int RC_PERM_NETWORK_CH = 4;
    private static final int RC_PERM_NETWORK_AC = 5;

    public static final String EXTRA_NAME = "profile_extra_name";
    public static final String EXTRA_PHONE = "profile_extra_phone";
    public static final String EXTRA_MAIL = "profile_extra_mail";

    ActivityResultLauncher<Intent> someActivityResultLauncher;
    List<AuthUI.IdpConfig> providers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button signInButton = findViewById(R.id.buttonSignIn);
        TextView title = findViewById(R.id.textViewLoginTitle);
        title.setOnClickListener(v -> {
            Intent intent = new Intent(this.getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        });
        // Choose authentication providers
        providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build());

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

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        }
    }

    @Override
    public void onActivityResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Intent intent = new Intent(this.getApplicationContext(), HomeActivity.class);
            if (user != null) {
                intent.putExtra(EXTRA_NAME, user.getDisplayName());
                intent.putExtra(EXTRA_MAIL, user.getEmail());
                intent.putExtra(EXTRA_PHONE, user.getPhoneNumber());

                startActivity(intent);
            } else {
                Toast.makeText(this, "Login Failed try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Login Failed try again", Toast.LENGTH_SHORT).show();
        }
    }
}
