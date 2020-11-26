package com.example.seektutorials.ui.tutorHome.subjects;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.seektutorials.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class TutorAddSubjectFragment extends DialogFragment {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private TextInputEditText subjNoEditText, subjDescEditText, daysEditText, feeEditText;
    private EditText timePickerStart, timePickerEnd;
    private Calendar c;
    private AutoCompleteTextView selectSubject;
    //for calendar date and time picker
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uid;
    String[]
            subjects ={"AAE","ABE","ABM","ABME","ABSE","ABT","ACHM","AEC","AECO","AENG","AERS","AF","AFBE","AGME",
            "AGR","AGRI","AGRS","AMAT","AMPE","ANP","ANSC","ANTH","APHY","ARDS","ARTS","ASYS","BIO","BM","BOT","CE",
            "CED","CEN","CERP","CHE","CHEM","CMSC","COMA","COMM","COST","CRPT","CRSC","DEVC","DM","DMG","DSC","DVST",
            "ECON","EDFD","EDUC","EE","EM","ENG","ENRE","ENS","ENSC","ENT","ENTR","ETHICS","FBS","FEX","FIL","FOR","FPPS",
            "FR","FRCH","FRM","FST","GEO","GER","HFDS","HIST","HK","HNF","HORT","HUM","HUME","IE","IT","JAP","KAS","KOM",
            "LAF","LGD","LWRE","MAED","MATH","MBB","MCB","MGT","MST","NRC","PA","PAF","PGR","PHI","PHILARTS","PHLO","PHYS",
            "PI","POSC","PPT","PPTH","PSY","SAS","SCIENCE","SDS","SFFG","SFI","SOC","SOIL","SOSC","SPAN","SPCM","SPEC","SPPS",
            "STAT","SUTC","THEA","TM","TMEM","VEPI","VETA","VMCB","VMED","VPAR","VPH","VPHM","VPHY","VPTH","VSUR","VTHE","WIKA",
            "WLDL","WD","WSTH","ZOO","ZOTC"};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.tutor_add_subject, container, false);
        c= Calendar.getInstance();
        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        uid=mAuth.getUid();
        //getting input
        Button addButton = view.findViewById(R.id.addButton);
        selectSubject=view.findViewById(R.id.select_subj);
        subjNoEditText=view.findViewById(R.id.subj_no);
        subjDescEditText=view.findViewById(R.id.subj_desc);
        daysEditText=view.findViewById(R.id.days);
        timePickerStart = view.findViewById(R.id.timePickerStart);
        timePickerEnd = view.findViewById(R.id.timePickerEnd);
        feeEditText=view.findViewById(R.id.fee);

        //timepickers
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

        // Create the instance of ArrayAdapter with the subjects
        ArrayAdapter adapter=new ArrayAdapter<>(this.getActivity(),android.R.layout.simple_spinner_item, subjects);
        //adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        selectSubject.setAdapter(adapter);
        selectSubject.setInputType(0);
        selectSubject.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    selectSubject.showDropDown();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSubject();

            }
        });

        return view;
    }
    public void addSubject(){
        //Take the values
        final String name, description, fee, timeStart, timeEnd, weekly_sched, tutorUID;
        name = selectSubject.getText().toString()+subjNoEditText.getText().toString();
        description = subjDescEditText.getText().toString();
        fee = feeEditText.getText().toString();
        timeStart = timePickerStart.getText().toString();
        timeEnd = timePickerEnd.getText().toString();
        weekly_sched = daysEditText.getText().toString();
        tutorUID= uid;
        //errors
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getActivity(), "Enter subject name!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(description)) {
            Toast.makeText(getActivity(), "Enter subject description!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(fee)) {
            Toast.makeText(getActivity(), "Enter fee per hour!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (timeStart.equals("Enter time")) {
            Toast.makeText(getActivity(), "Enter time start!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (timeEnd.equals("Enter time")) {
            Toast.makeText(getActivity(), "Enter time end!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(weekly_sched)) {
            Toast.makeText(getActivity(), "Enter weekly schedule!", Toast.LENGTH_SHORT).show();
            return;
        }
        String subjUUID=UUID.randomUUID().toString();
        //map to a hashmap
        final Map<String, Object> subject =new HashMap<>();
        subject.put("name",name);
        subject.put("description",description);
        subject.put("fee",fee +" php per hour");
        subject.put("time",timeStart+"-"+timeEnd);
        subject.put("weekly_sched",weekly_sched);
        subject.put("tutorUID",tutorUID);
        subject.put("subjUUID",subjUUID);
        //add to subject collection

        db.collection("subjects").document(subjUUID)
                .set(subject)
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
        //add to user-subjects
        //map to a hashmap
        final Map<String, Object> subject0 =new HashMap<>();
        subject0.put("name",name);
        subject0.put("description",description);
        subject0.put("fee",fee +" php per hour");
        subject0.put("time",timeStart+"-"+timeEnd);
        subject0.put("weekly_sched",weekly_sched);
        subject0.put("subjUUID",subjUUID);
        db.collection("users").document(uid).collection("subjects").document(subjUUID)
                .set(subject0)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Document written.", Toast.LENGTH_SHORT).show();
                        Activity act=getActivity();
                        act.onBackPressed();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Document written failed.", Toast.LENGTH_SHORT).show();
                    }
                });

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

}
