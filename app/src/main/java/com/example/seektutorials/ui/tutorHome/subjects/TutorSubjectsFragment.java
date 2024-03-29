package com.example.seektutorials.ui.tutorHome.subjects;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seektutorials.R;
import com.example.seektutorials.ui.tutorHome.TutorHome;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


/**
 * Tutor subjects fragment
 */
public class TutorSubjectsFragment extends Fragment {
    private FirebaseAuth mAuth;
    private RecyclerView mRecyclerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uid;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.topAppBar);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        ((TutorHome)getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tutor_subjects, null);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        uid=mAuth.getCurrentUser().getUid();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        Query query = db.collection("users").document(uid).collection("subjects");
        FirestoreRecyclerOptions<Subject> options = new FirestoreRecyclerOptions.Builder<Subject>().setQuery(query, Subject.class).build();
        final FirestoreRecyclerAdapter<Subject, TutorSubjectCardViewHolder> adapter = new FirestoreRecyclerAdapter<Subject, TutorSubjectCardViewHolder>(options) {
            @Override
            public void onBindViewHolder(TutorSubjectCardViewHolder holder, int position, final Subject model) {
                holder.setName(model.getName());
                holder.setDescription(model.getDescription());
                holder.setWeekly_sched(model.getWeekly_sched());
                holder.setTime(model.getTime());
                holder.setFee(model.getFee());
                final String subjUUID = model.getSubjUUID();

                //add the data to a bundle to be accessed by other fragments
                holder.editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment nextFrag= TutorEditSubjectFragment.newInstance(subjUUID);
                        getActivity().getSupportFragmentManager()
                                 .beginTransaction()
                                .replace(((ViewGroup)getView().getParent()).getId(), nextFrag)
                                .addToBackStack(null)
                                .commit();
                    }
                });
                holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MaterialAlertDialogBuilder dialog =new MaterialAlertDialogBuilder(getActivity(),R.style.AlertDialogTheme);
                        dialog.setMessage(R.string.delete_subject)
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        uid=mAuth.getCurrentUser().getUid();
                                        db.collection("users").document(uid).collection("subjects").document(subjUUID)
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        db.collection("subjects").document(subjUUID)
                                                                .delete()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Toast.makeText(getActivity(),"Subject deleted",
                                                                                Toast.LENGTH_SHORT).show();
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(getActivity(),"Failure on deleting subject",
                                                                                Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getActivity(),"Failure on deleting subject",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //  Action for 'NO' Button
                                        dialog.cancel();
                                    }
                                });
                        dialog.show();
                    }
                });
            }

            @Override
            public TutorSubjectCardViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Using a custom layout for each item, we create a new instance of the viewholder
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.tutor_subject_card, group, false);
                return new TutorSubjectCardViewHolder(view);
            }


        };
        //make adapter listen so it updates
        adapter.startListening();
        mRecyclerView.setAdapter(adapter);

        return view;


    }

    public class TutorSubjectCardViewHolder extends RecyclerView.ViewHolder{
        public TextView name, description, weekly_sched, time, fee;
        public Button editButton, deleteButton;
        public TutorSubjectCardViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            weekly_sched = itemView.findViewById(R.id.wk_sched);
            time = itemView.findViewById(R.id.time);
            fee = itemView.findViewById(R.id.fee);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
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
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.top_app_bar, menu);
        super.onCreateOptionsMenu(menu,inflater);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setQueryHint("SUBJECT101");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //called when search button is pressed
                Query query = db.collection("users").document(uid).collection("subjects").whereEqualTo("name",s);
                //show subjects
                FirestoreRecyclerOptions<Subject> options = new FirestoreRecyclerOptions.Builder<Subject>().setQuery(query, Subject.class).build();
                final FirestoreRecyclerAdapter<Subject, TutorSubjectCardViewHolder> adapter = new FirestoreRecyclerAdapter<Subject, TutorSubjectCardViewHolder>(options) {
                    @Override
                    public void onBindViewHolder(TutorSubjectCardViewHolder holder, int position, final Subject model) {
                        holder.setName(model.getName());
                        holder.setDescription(model.getDescription());
                        holder.setWeekly_sched(model.getWeekly_sched());
                        holder.setTime(model.getTime());
                        holder.setFee(model.getFee());
                        final String subjUUID = model.getSubjUUID();

                        //add the data to a bundle to be accessed by other fragments
                        holder.editButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Fragment nextFrag= TutorEditSubjectFragment.newInstance(subjUUID);
                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(((ViewGroup)getView().getParent()).getId(), nextFrag)
                                        .addToBackStack(null)
                                        .commit();
                            }
                        });
                        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                MaterialAlertDialogBuilder dialog =new MaterialAlertDialogBuilder(getActivity(),R.style.AlertDialogTheme);
                                dialog.setMessage(R.string.delete_subject)
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                uid=mAuth.getCurrentUser().getUid();
                                                db.collection("users").document(uid).collection("subjects").document(subjUUID)
                                                        .delete()
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                db.collection("subjects").document(subjUUID)
                                                                        .delete()
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Toast.makeText(getActivity(),"Subject deleted",
                                                                                        Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Toast.makeText(getActivity(),"Failure on deleting subject",
                                                                                        Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(getActivity(),"Failure on deleting subject",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //  Action for 'NO' Button
                                                dialog.cancel();
                                            }
                                        });
                                dialog.show();
                            }
                        });
                    }

                    @Override
                    public TutorSubjectCardViewHolder onCreateViewHolder(ViewGroup group, int i) {
                        // Using a custom layout for each item, we create a new instance of the viewholder
                        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.tutor_subject_card, group, false);
                        return new TutorSubjectCardViewHolder(view);
                    }


                };
                adapter.startListening();
                mRecyclerView.setAdapter(adapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //called when typing
                return false;
            }

        });
    }
}