package com.example.seektutorials.ui.tutorHome.subjects;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.seektutorials.R;
import com.example.seektutorials.TutorHome;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class TutorAddSubjectFragment extends DialogFragment {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private TextInputEditText subjNoEditText, subjDescEditText, daysEditText, timeEditText, feeEditText;
    private AutoCompleteTextView selectSubject;
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
        timeEditText=view.findViewById(R.id.time);
        feeEditText=view.findViewById(R.id.fee);

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
        final String name, description, fee, time, weekly_sched, tutorUID;
        name = selectSubject.getText().toString()+subjNoEditText.getText().toString();
        description = subjDescEditText.getText().toString();
        fee = feeEditText.getText().toString();
        time = timeEditText.getText().toString();
        weekly_sched = daysEditText.getText().toString();
        tutorUID=uid.toString();
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
        if (TextUtils.isEmpty(time)) {
            Toast.makeText(getActivity(), "Enter time!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(weekly_sched)) {
            Toast.makeText(getActivity(), "Enter weekly schedule!", Toast.LENGTH_SHORT).show();
            return;
        }
        //map to a hashmap
        final Map<String, Object> subject =new HashMap<>();
        subject.put("name",name);
        subject.put("description",description);
        subject.put("fee",fee +" per hour");
        subject.put("time",time);
        subject.put("weekly_sched",weekly_sched);
        subject.put("tutorUID",tutorUID);
        //add to subject collection
        String subjUUID=UUID.randomUUID().toString();
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
        subject0.put("fee",fee +" per hour");
        subject0.put("time",time);
        subject0.put("weekly_sched",weekly_sched);
        subject0.put("subjUUID",subjUUID);
        db.collection("users").document(uid).collection("subjects").document(subjUUID)
                .set(subject0)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Document written.", Toast.LENGTH_SHORT).show();
                        Activity act=getActivity();
                        ((TutorHome)act).onBackPressed();

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
