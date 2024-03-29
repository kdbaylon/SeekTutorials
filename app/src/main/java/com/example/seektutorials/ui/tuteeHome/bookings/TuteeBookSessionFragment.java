package com.example.seektutorials.ui.tuteeHome.bookings;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.seektutorials.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;


public class TuteeBookSessionFragment extends DialogFragment {
    Boolean isSchedFree;
    private String schedule;
    String subjUUID,tutorUID;
    String fee;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uid;
    private TextView subjTextView, descTextView, fnameTextView, lnameTextView, feeTextView, weeklySchedTextView,timeTextView,hourlyFeeTextView;
    private EditText datePicker, timePickerStart, timePickerEnd;
    private Button generateFeeButton;
    private Calendar c;
    public static TuteeBookSessionFragment newInstance(String string, String string2) {
        Bundle bundle = new Bundle();
        bundle.putString("subjUUID", string);
        bundle.putString("tutorUID", string2);
        TuteeBookSessionFragment fragment = new TuteeBookSessionFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater i = getActivity().getLayoutInflater();
        View view = i.inflate(R.layout.tutee_book_session,null);
        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        uid=mAuth.getCurrentUser().getUid();
        //for calendar date and time picker
        c= Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, monthOfYear);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel();
            }
        };
        final TimePickerDialog.OnTimeSetListener timeStart = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                c.set(Calendar.HOUR_OF_DAY, hour);
                c.set(Calendar.MINUTE, minute);
                updateTimeStartLabel();
            }
        };
        final TimePickerDialog.OnTimeSetListener timeEnd = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                c.set(Calendar.HOUR_OF_DAY, hour);
                c.set(Calendar.MINUTE, minute);
                updateTimeEndLabel();
            }
        };
        //get layout
        subjTextView = view.findViewById(R.id.subject);
        descTextView = view.findViewById(R.id.desc);
        fnameTextView = view.findViewById(R.id.fname);
        lnameTextView = view.findViewById(R.id.lname);
        weeklySchedTextView = view.findViewById(R.id.weekly_sched);
        timeTextView = view.findViewById(R.id.time);
        hourlyFeeTextView =view.findViewById(R.id.hourly_fee);
        feeTextView = view.findViewById(R.id.fee);
        datePicker = view.findViewById(R.id.datePicker);
        timePickerStart = view.findViewById(R.id.timePickerStart);
        timePickerEnd = view.findViewById(R.id.timePickerEnd);
        generateFeeButton = view.findViewById(R.id.generateFee);
        //get bundle
        Bundle bundle = getArguments();
        if(bundle!=null){
            subjUUID = bundle.getString("subjUUID");
            tutorUID = bundle.getString("tutorUID");
        }
        //populate textviews
        DocumentReference ref =db.collection("subjects").document(subjUUID);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        //get text
                        String subject = document.getString("name");
                        String desc = document.getString("description");
                        String fee1 = document.getString("fee");
                        String weekly_sched = document.getString("weekly_sched");
                        String time = document.getString("time");


                        fee =fee1.replaceAll(" php per hour","");
                        //set text
                        subjTextView.setText(subject);
                        descTextView.setText(desc);
                        weeklySchedTextView.setText(weekly_sched);
                        timeTextView.setText(time);
                        hourlyFeeTextView.setText(fee1);

                    } else {
                        Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                }
            }
        });
        DocumentReference ref2 =db.collection("users").document(tutorUID);
        ref2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        //get text
                        String fname =document.getString("fname");
                        String lname =document.getString("lname");

                        //set text
                        fnameTextView.setText(fname);
                        lnameTextView.setText(lname);

                    } else {
                        Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                }
            }
        });



        //date and time picker buttons
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(),AlertDialog.THEME_DEVICE_DEFAULT_DARK, date, c
                        .get(Calendar.YEAR), c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        timePickerStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK, timeStart, c
                        .get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),false).show();
            }
        });
        timePickerEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK, timeEnd, c
                        .get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE), false).show();
            }
        });
        generateFeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timePickerStart.getText().toString().equals("Enter time")){
                    Toast.makeText(getActivity(), "Enter time start!", Toast.LENGTH_SHORT).show();
                }else if(timePickerEnd.getText().toString().equals("Enter time")){
                    Toast.makeText(getActivity(), "Enter time end!", Toast.LENGTH_SHORT).show();
                }else {
                    getTimeDifference();
                }

            }
        });

        AlertDialog.Builder b=  new  AlertDialog.Builder(getActivity())
                .setPositiveButton("Book",
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int whichButton) {
                                //error
                                if (TextUtils.isEmpty(feeTextView.getText())) {
                                    Toast.makeText(getActivity(), "Click generate total fee!", Toast.LENGTH_SHORT).show();
                                }
                                if (feeTextView.getText().toString().equals("0.0 php")) {
                                    Toast.makeText(getActivity(), "Click generate total fee!", Toast.LENGTH_SHORT).show();
                                }
                                if (datePicker.getText().toString().equals("Enter date")) {
                                    Toast.makeText(getActivity(), "Enter date!", Toast.LENGTH_SHORT).show();
                                }
                                //check if schedule is already booked
                                if(isSchedFree){
                                    //generate rand uuid for each document of booking session
                                    final String bookingUUID = UUID.randomUUID().toString();
                                    //put in a hashmap
                                    final Map<String, Object> book_tutee =new HashMap<>();
                                    book_tutee.put("bookingUUID",bookingUUID);
                                    book_tutee.put("uid",tutorUID);
                                    book_tutee.put("subject",subjUUID);
                                    book_tutee.put("sched",schedule);
                                    book_tutee.put("fee",feeTextView.getText().toString());
                                    book_tutee.put("status","pending");

                                    final Map<String, Object> book_tutor =new HashMap<>();
                                    book_tutor.put("bookingUUID",bookingUUID);
                                    book_tutor.put("uid",uid);
                                    book_tutor.put("subject",subjUUID);
                                    book_tutor.put("sched",schedule);
                                    book_tutor.put("fee",feeTextView.getText().toString());
                                    book_tutor.put("status","pending");

                                    //put in booking collection of both users

                                    db.collection("users").document(uid).collection("bookings").document(bookingUUID)
                                            .set(book_tutee)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    db.collection("users").document(tutorUID).collection("bookings").document(bookingUUID)
                                                            .set(book_tutor)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    dialog.dismiss();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(getActivity(), "Tutoring session booking failed.", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getActivity(), "Tutoring session booking failed.", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                }else{
                                    Toast.makeText(getActivity(), "This schedule is already booked", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                );
        b.setView(view);
        return b.create();
    }
    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#03989E"));
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#03989E"));
    }
    private void updateDateLabel() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        datePicker.setText(sdf.format(c.getTime()));
    }
    private void updateTimeStartLabel() {
        String myFormat = "hh:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        timePickerStart.setText(sdf.format(c.getTime()));
    }
    private void updateTimeEndLabel() {
        String myFormat = "hh:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        timePickerEnd.setText(sdf.format(c.getTime()));
    }
    private void getTimeDifference(){
        schedule = datePicker.getText().toString()+ " " +timePickerStart.getText().toString()+"-"+timePickerEnd.getText().toString();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

        LocalTime start = LocalTime.parse(timePickerStart.getText().toString(), timeFormatter);
        LocalTime end = LocalTime.parse(timePickerEnd.getText().toString(), timeFormatter);

        Duration diff = Duration.between(start, end);

        long hours = diff.toHours();
        long minutes = diff.minusHours(hours).toMinutes();
        String totalTimeString = String.format("%02d.%02d", hours, minutes);
        Float total = Float.valueOf(fee) * Float.valueOf(totalTimeString);
        String finalTotal = total + " php";
        feeTextView.setText(finalTotal);
        schedChecker(schedule);
    }
    private void schedChecker(final String s){
        final Query acceptedTutorSched = db.collection("users").document(tutorUID).collection("bookings").whereEqualTo("status","accepted").whereEqualTo("sched", s);
        final Query acceptedTuteeSched = db.collection("users").document(uid).collection("bookings").whereEqualTo("status","accepted").whereEqualTo("sched", s);
        acceptedTutorSched.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    isSchedFree = task.getResult().isEmpty();
                    if (isSchedFree){
                        acceptedTuteeSched.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    isSchedFree = task.getResult().isEmpty();
                                    if (isSchedFree){

                                    }else{
                                        Toast.makeText(getActivity(), "This schedule is booked", Toast.LENGTH_SHORT).show();
                                    }


                                } else {
                                    Toast.makeText(getActivity(), "Schedule is available", Toast.LENGTH_SHORT).show();
                                    Log.d("TAG", "Error getting documents: ", task.getException());
                                }
                            }
                        });
                    }else{
                        Toast.makeText(getActivity(), "This schedule is booked", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(getActivity(), "Schedule is available", Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
    }


}
