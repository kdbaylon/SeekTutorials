package com.example.seektutorials.ui.tuteeHome.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.seektutorials.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


/**
 * Tutee profile fragment
 */
public class EditTuteeProfileFragment extends Fragment {
    //a constant to track the file chooser intent
    private static final int PICK_IMAGE_REQUEST = 234;
    //where to store img
    private Uri filePath;
    //Firebase
    //storage
    private StorageReference storageReference;
    private FirebaseStorage storage;
    //auth
    private FirebaseAuth mAuth;
    //firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uid;
    //layout
    CircleImageView imageView;
    EditText fnameEditText;
    EditText lnameEditText;
    EditText locationEditText;
    EditText courseEditText;
    EditText emailEditText;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tutee_edit_profile, null);
        //get layout addresses
        final ImageButton backButton = view.findViewById(R.id.back);
        final Button register = view.findViewById(R.id.registerButton);
        imageView = view.findViewById(R.id.profilepic);
        final ImageButton imageButton = view.findViewById(R.id.imageButton);
        fnameEditText = view.findViewById(R.id.fname);
        lnameEditText = view.findViewById(R.id.lname);
        locationEditText = view.findViewById(R.id.loc);
        courseEditText = view.findViewById(R.id.course);
        emailEditText = view.findViewById(R.id.email);
        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        uid=mAuth.getCurrentUser().getUid();
        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("images/"+uid);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri){
                //load img using glide
                Glide.with(getActivity()).load(uri.toString()).placeholder(R.drawable.round_account_circle_24).dontAnimate().into(imageView);
            }
        });
        DocumentReference docRef = db.collection("users").document(uid);
        //get info from firestore document
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        //get text
                        String fname =document.getString("fname");
                        String lname =document.getString("lname");
                        String email =document.getString("email");
                        String course =document.getString("course");
                        String location =document.getString("location");
                        //set text
                        fnameEditText.setText(fname);
                        lnameEditText.setText(lname);
                        emailEditText.setText(email);
                        courseEditText.setText(course);
                        locationEditText.setText(location);
                    } else {
                        Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage(view);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile(v);
                v.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                v.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
            }
        });

        return view;
    }

    public void changeImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }
    //handling the image chooser activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void uploadImage(){
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            StorageReference ref = storageReference.child( "images/" + uid);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successful
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying a success toast
                            Toast.makeText(getActivity(), "File Uploaded ", Toast.LENGTH_LONG).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            Toast.makeText(getActivity(),"Error uploading picture", Toast.LENGTH_LONG).show();
        }

    }
    public void editProfile(View view){
        //upload image to storage
        uploadImage();
        //get profile picture uid
        StorageReference ref = storageReference.child( "images/" + uid);
        String profilePicUri =ref.getDownloadUrl().toString();
        // Take the values
        final String fname, lname, email, school, course, location, desc;
        fname = fnameEditText.getText().toString();
        lname = lnameEditText.getText().toString();
        email = emailEditText.getText().toString();
        course = courseEditText.getText().toString();
        location = locationEditText.getText().toString();
        //errors
        if (TextUtils.isEmpty(fname)) {
            Toast.makeText(getActivity(), "Enter first name!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(lname)) {
            Toast.makeText(getActivity(), "Enter last name!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getActivity(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(course)) {
            Toast.makeText(getActivity(), "Enter course!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(location)) {
            Toast.makeText(getActivity(), "Enter city!", Toast.LENGTH_SHORT).show();
            return;
        }

        final Map<String, Object> user = new HashMap<>();
        user.put("userType","tutee");
        user.put("fname", fname);
        user.put("lname", lname);
        user.put("email", email);
        user.put("course", course);
        user.put("location", location);
        user.put("profilePictureUrl",profilePicUri);
        //Update
        uid=mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid)
           .set(user)
           .addOnSuccessListener(new OnSuccessListener<Void>() {
             @Override
             public void onSuccess(Void aVoid) {
               Toast.makeText(getActivity(), "Document written.", Toast.LENGTH_SHORT).show();
             }
           })
           .addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
             Toast.makeText(getActivity(), "Document written failed.", Toast.LENGTH_SHORT).show();
             }
           });


    }

}