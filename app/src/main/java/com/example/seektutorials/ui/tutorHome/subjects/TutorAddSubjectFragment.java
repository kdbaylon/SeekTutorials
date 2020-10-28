package com.example.seektutorials.ui.tutorHome.subjects;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.seektutorials.R;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class TutorAddSubjectFragment extends DialogFragment {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        return inflater.inflate(R.layout.tutor_add_subject, container, false);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

}
