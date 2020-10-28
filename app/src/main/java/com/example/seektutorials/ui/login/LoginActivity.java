package com.example.seektutorials.ui.login;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;

import com.example.seektutorials.SignUp;
import com.example.seektutorials.TuteeHome;
import com.example.seektutorials.TutorHome;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.seektutorials.R;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private GoogleApiClient googleApiClient;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressBar loadingProgressBar;
    private TextInputEditText emailEditText, passwordEditText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //initializing firebase auth object
        mAuth = FirebaseAuth.getInstance();
        //if getCurrentUser does not returns null
        if(mAuth.getCurrentUser() != null){
            //that means user is already logged in
            //so close this activity
            finish();
            //and open profile activity
            String uid=mAuth.getCurrentUser().getUid();
            DocumentReference docRef = db.collection("users").document(uid);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            //get text
                            String userType = document.getString("userType");
                            if(userType.equals("tutor")){
                                Intent intent = new Intent(LoginActivity.this, TutorHome.class);
                                startActivity(intent);
                            }else{
                                Intent intent = new Intent(LoginActivity.this, TuteeHome.class);
                                startActivity(intent);
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "Error getting document", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Error getting document", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        //connect variables to layout
        MaterialButton loginButton = findViewById(R.id.login);
        MaterialButton signupButton = findViewById(R.id.signup);
        MaterialButton googleButton = findViewById(R.id.sign_in_google);
        loadingProgressBar = findViewById(R.id.loading);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        //google login
//        GoogleSignInOptions gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//        googleApiClient=new GoogleApiClient.Builder(this)
//                .enableAutoManage(this,this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
//                .build();


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginUserAccount();
            }
        });
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(LoginActivity.this, SignUp.class);
                startActivity(intent);

            }
        });
        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingProgressBar.setVisibility(View.VISIBLE);
            }
        });

    }
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode==RC_SIGN_IN){
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            handleSignInResult(result);
//        }
//    }
//
//    private void handleSignInResult(GoogleSignInResult result){
//        if(result.isSuccess()){
//            GoogleSignInAccount account = result.getSignInAccount();
//            idToken = account.getIdToken();
//            name = account.getDisplayName();
//            email = account.getEmail();
//            // you can store user data to SharedPreference
//            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
//            firebaseAuthWithGoogle(credential);
//        }else{
//            // Google Sign In failed, update UI appropriately
//            Log.e(TAG, "Login Unsuccessful. "+result);
//            Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
//        }
//    }
//    private void firebaseAuthWithGoogle(AuthCredential credential){
//
//        firebaseAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
//                        if(task.isSuccessful()){
//                            Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
//                            gotoProfile();
//                        }else{
//                            Log.w(TAG, "signInWithCredential" + task.getException().getMessage());
//                            task.getException().printStackTrace();
//                            Toast.makeText(MainActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                });
//    }
    private void loginUserAccount() {
        // show the visibility of progress bar to show loading
        loadingProgressBar.setVisibility(View.VISIBLE);

        // Take the value of two edit texts in Strings
        String email, password;
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();

        // validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(),
                    "Please enter email!!",
                    Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),
                    "Please enter password!!",
                    Toast.LENGTH_LONG)
                    .show();
            return;
        }
        // signin existing user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // hide the progress bar
                            loadingProgressBar.setVisibility(View.GONE);
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid=mAuth.getCurrentUser().getUid();
                            // if sign-in is successful
                            //get info from firestore document
                            DocumentReference docRef = db.collection("users").document(uid);
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document != null && document.exists()) {
                                            //get text
                                            String userType = document.getString("userType");
                                            if(userType.equals("tutor")){
                                                Intent intent = new Intent(LoginActivity.this, TutorHome.class);
                                                startActivity(intent);
                                            }else{
                                                Intent intent = new Intent(LoginActivity.this, TuteeHome.class);
                                                startActivity(intent);
                                            }

                                        } else {
                                            Toast.makeText(getApplicationContext(), "Error getting document", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Error getting document", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                        else {
                            // sign-in failed
                            Toast.makeText(getApplicationContext(), "Login failed!!", Toast.LENGTH_LONG).show();
                            // hide the progress bar
                            loadingProgressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }


}