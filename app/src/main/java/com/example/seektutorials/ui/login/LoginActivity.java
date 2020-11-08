package com.example.seektutorials.ui.login;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.example.seektutorials.ui.googleRegister.SignUpGoogle;
import com.example.seektutorials.ui.register.SignUp;
import com.example.seektutorials.ui.tuteeHome.TuteeHome;
import com.example.seektutorials.ui.tutorHome.TutorHome;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.seektutorials.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1001;

    GoogleSignInClient googleSignInClient;
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
        GoogleSignInOptions gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("61224848583-3iakjsm1lefj8eckfevc85m0gj2f7j1r.apps.googleusercontent.com")
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso);

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
                signInToGoogle();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(this, "Google Sign in Succeeded", Toast.LENGTH_SHORT).show();
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("Google Sign In", "signInResult:failed code=", e);
                Toast.makeText(this, "Google Sign in Failed"+e, Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d("GoogleSignIn", "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d("Google Sign In", "signInWithCredential:success: currentUser: " + user.getEmail());
                            Toast.makeText(LoginActivity.this, "Google Auth Success ", Toast.LENGTH_SHORT).show();
                            String uid=user.getUid();
                            final String name=user.getDisplayName();
                            final String emailadd=user.getEmail();
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
                                            Intent mIntent = new Intent(LoginActivity.this, SignUpGoogle.class);
                                            mIntent.putExtra("emailAdd", emailadd);
                                            mIntent.putExtra("name", name);
                                            startActivity(mIntent);
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Error getting document", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.

                            Log.w("Google Sign In", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Firebase Authentication failed:" + task.getException(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    public void signInToGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
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
                            String uid=user.getUid();
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