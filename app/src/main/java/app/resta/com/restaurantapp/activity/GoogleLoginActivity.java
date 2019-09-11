package app.resta.com.restaurantapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.controller.AuthenticationController;
import app.resta.com.restaurantapp.controller.LoginController;
import app.resta.com.restaurantapp.db.dao.user.device.DeviceUserDaoI;
import app.resta.com.restaurantapp.db.dao.user.device.DeviceUserFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.util.MyApplication;

public class GoogleLoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    SignInButton signInButton;
    Button signOutButton;
    TextView statusView;
    GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth firebaseAuth;
    DeviceUserDaoI deviceUserDaoI;
    AuthenticationController authenticationController;
    //    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        statusView = (TextView) findViewById(R.id.login_status_textView);
        signInButton = (SignInButton) findViewById(R.id.signinbutton);
        signInButton.setOnClickListener(this);

        signOutButton = (Button) findViewById(R.id.signoutbutton);
        signOutButton.setOnClickListener(this);
        firebaseAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        deviceUserDaoI = new DeviceUserFireStoreDao();
        authenticationController = new AuthenticationController(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signinbutton:
                signIn();
                break;
            case R.id.signoutbutton:
                signOut();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private static String TAG = "GoogleLogin";

    private void firebaseAuthWithGoogle(final String email, AuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            Toast.makeText(MyApplication.getAppContext(), "Login successful", Toast.LENGTH_SHORT).show();
                            gotoApp(email);
                        } else {
                            Log.w(TAG, "signInWithCredential" + task.getException().getMessage());
                            task.getException().printStackTrace();
                            Toast.makeText(MyApplication.getAppContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            statusView.setText("Hello " + account.getDisplayName() + ". Please wait while we validate your account.");


            final String idToken = account.getIdToken();
            String name = account.getDisplayName();
            final String email = account.getEmail();
            LoginController.getInstance().setLoggedInEmail(email);
//                        LoginController.markAsInvalidLicence();
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            firebaseAuthWithGoogle(email, credential);

        } else {
            String text = "Problem when signing in :" + result.getStatus();
            statusView.setText(text);
            Toast.makeText(MyApplication.getAppContext(), text, Toast.LENGTH_LONG).show();
        }
    }

    private void gotoApp(final String email) {
        deviceUserDaoI.isValidEmail_u(email, new OnResultListener<String>() {
            @Override
            public void onCallback(String result) {
                if (result != null && result.equalsIgnoreCase("success")) {
                    authenticationController.goToMainActivity(null);
                } else {
                    String text = "The email " + email + " is not registered. Please sign out and sign in using a different email id or talk to support.";
                    statusView.setText(text);
                    Toast.makeText(MyApplication.getAppContext(), text, Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                statusView.setText("Signed out");
            }
        });
    }
}
