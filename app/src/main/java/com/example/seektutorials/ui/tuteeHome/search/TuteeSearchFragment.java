package com.example.seektutorials.ui.tuteeHome.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seektutorials.R;
import com.example.seektutorials.ui.tutorHome.subjects.Subject;
import com.example.seektutorials.ui.tutorHome.subjects.TutorSubjectsFragment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

/**
 * Tutee search fragment
 */
public class TuteeSearchFragment extends Fragment {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uid;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tutee_search, null);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        uid=mAuth.getCurrentUser().getUid();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        Query query = db.collection("subjects").limit(10);
        FirestoreRecyclerOptions<Subjectt> options = new FirestoreRecyclerOptions.Builder<Subjectt>().setQuery(query, Subjectt.class).build();
        final FirestoreRecyclerAdapter<Subjectt, TuteeSearchFragment.TuteeSubjectCardViewHolder> adapter = new FirestoreRecyclerAdapter<Subjectt, TuteeSearchFragment.TuteeSubjectCardViewHolder>(options) {
            @Override
            public void onBindViewHolder(TuteeSearchFragment.TuteeSubjectCardViewHolder holder, int position, Subjectt model) {
                holder.setName(model.getName());
                holder.setDescription(model.getDescription());
                holder.setWeekly_sched(model.getWeekly_sched());
                holder.setTime(model.getTime());
                holder.setFee(model.getFee());
            }

            @Override
            public TuteeSearchFragment.TuteeSubjectCardViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Using a custom layout for each item, we create a new instance of the viewholder
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.tutee_subject_card, group, false);
                return new TuteeSearchFragment.TuteeSubjectCardViewHolder(view);
            }
            @Override
            public void onError(FirebaseFirestoreException e) {
                Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
            }

        };
        //make adapter listen so it updates
        adapter.startListening();
        //Final step, where "mRecyclerView" is defined in your xml layout as
        //the recyclerview
        mRecyclerView.setAdapter(adapter);
        return view;
    }
    public class TuteeSubjectCardViewHolder extends RecyclerView.ViewHolder{
        public TextView name, description, weekly_sched, time, fee;
        public TuteeSubjectCardViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            weekly_sched = itemView.findViewById(R.id.weekly_sched);
            time = itemView.findViewById(R.id.time);
            fee = itemView.findViewById(R.id.fee);
        }
        public void setName(String string) {
            name.setText(string);
        }
        public void setDescription(String string) {
            description.setText(string);
        }
        public void setWeekly_sched(String string) {
            weekly_sched.setText(string);
        }
        public void setTime(String string) {
            time.setText(string);
        }
        public void setFee(String string) {
            fee.setText(string);
        }
    }

}