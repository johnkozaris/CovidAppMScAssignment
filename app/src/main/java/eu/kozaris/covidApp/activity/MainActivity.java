package eu.kozaris.covidApp.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import eu.kozaris.covidApp.R;

public class MainActivity extends AppCompatActivity implements ActivityResultCallback<ActivityResult> {

private static final int RC_SIGN_IN = 1;
public static final String EXTRA_NAME = "profile_extra_name";
public static final String EXTRA_PHONE = "profile_extra_phone";
public static final String EXTRA_MAIL = "profile_extra_mail";

    ActivityResultLauncher<Intent> someActivityResultLauncher;
    List<AuthUI.IdpConfig> providers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button signInButton= findViewById(R.id.buttonSignIn);
        providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build());

        someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this);

        signInButton.setOnClickListener(v -> someActivityResultLauncher.launch(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers)
                .build()));
        // Choose authentication providers

    }

    @Override
    public void onActivityResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Intent intent = new Intent(this.getApplicationContext(), HomeActivity.class);
//                    assert user != null;
//                    intent.putExtra(EXTRA_NAME, user.getDisplayName());
//                    intent.putExtra(EXTRA_MAIL, user.getEmail());
//                    intent.putExtra(EXTRA_PHONE, user.getPhoneNumber());

            startActivity(intent);
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = new Intent(this.getApplicationContext(), HomeActivity.class);
//                    assert user != null;
//                    intent.putExtra(EXTRA_NAME, user.getDisplayName());
//                    intent.putExtra(EXTRA_MAIL, user.getEmail());
//                    intent.putExtra(EXTRA_PHONE, user.getPhoneNumber());

        startActivity(intent);
    }
}
