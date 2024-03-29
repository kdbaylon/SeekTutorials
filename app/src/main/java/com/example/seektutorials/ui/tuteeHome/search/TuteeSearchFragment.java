package com.example.seektutorials.ui.tuteeHome.search;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.seektutorials.R;
import com.example.seektutorials.ui.chat.ChatRoomFragment;
import com.example.seektutorials.ui.tuteeHome.TuteeHome;
import com.example.seektutorials.ui.tuteeHome.bookings.TuteeBookSessionFragment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Tutee search fragment
 */
public class TuteeSearchFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirestoreRecyclerOptions<Subjectt> options;
    private RecyclerView mRecyclerView;
    private Query query;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uid;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) view.findViewById(R.id.topAppBar);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        ((TuteeHome)getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tutee_search, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        uid=mAuth.getCurrentUser().getUid();
        final Button sortButton = view.findViewById(R.id.sort_button);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        query = db.collection("subjects");
        options = new FirestoreRecyclerOptions.Builder<Subjectt>().setQuery(query, Subjectt.class).build();
        //show subjects
        FirestoreRecyclerAdapter<Subjectt, TuteeSearchFragment.TuteeSubjectCardViewHolder> adapter = new FirestoreRecyclerAdapter<Subjectt, TuteeSearchFragment.TuteeSubjectCardViewHolder>(options) {
            @Override
            public void onBindViewHolder(TuteeSearchFragment.TuteeSubjectCardViewHolder holder, int position, Subjectt model) {
                holder.setName(model.getName());
                holder.setDescription(model.getDescription());
                holder.setWeekly_sched(model.getWeekly_sched());
                holder.setTime(model.getTime());
                holder.setFee(model.getFee());
                holder.setTutorFname(model.getTutorUID());
                holder.setTutorLname(model.getTutorUID());
                holder.setProfilepic(model.getTutorUID());
                holder.setDesc(model.getTutorUID());
                final String tutorUID=model.getTutorUID();
                final String subjUUID=model.getSubjUUID();
                holder.book.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogFragment nextFrag= TuteeBookSessionFragment.newInstance(subjUUID,tutorUID);
                        nextFrag.show(getActivity().getSupportFragmentManager(),"Book");
                    }
                });
                holder.view_tutor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment nextFrag= ViewTutorProfileFragment.newInstance(tutorUID);
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(((ViewGroup)getView().getParent()).getId(), nextFrag)
                                .addToBackStack(null)
                                .commit();
                    }
                });
                holder.message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment nextFrag= ChatRoomFragment.newInstance(tutorUID);
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(((ViewGroup)getView().getParent()).getId(), nextFrag)
                                .addToBackStack(null)
                                .commit();
                    }
                });
            }
            @Override
            public TuteeSearchFragment.TuteeSubjectCardViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Using a custom layout for each item, we create a new instance of the viewholder
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.tutee_subject_card, group, false);
                return new TuteeSearchFragment.TuteeSubjectCardViewHolder(view);
            }


        };
        //make adapter listen so it updates
        adapter.startListening();
        mRecyclerView.setAdapter(adapter);

        //sort button
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu p = new PopupMenu(getActivity(), sortButton);
                p.getMenuInflater().inflate(R.menu.sort_menu, p .getMenu());
                p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().equals("Subject A-Z")){
                            query= query.orderBy("name",Query.Direction.ASCENDING);
                            options = new FirestoreRecyclerOptions.Builder<Subjectt>().setQuery(query, Subjectt.class).build();
                            FirestoreRecyclerAdapter<Subjectt, TuteeSearchFragment.TuteeSubjectCardViewHolder> adapter = new FirestoreRecyclerAdapter<Subjectt, TuteeSearchFragment.TuteeSubjectCardViewHolder>(options) {
                                @Override
                                public void onBindViewHolder(TuteeSearchFragment.TuteeSubjectCardViewHolder holder, int position, Subjectt model) {
                                    holder.setName(model.getName());
                                    holder.setDescription(model.getDescription());
                                    holder.setWeekly_sched(model.getWeekly_sched());
                                    holder.setTime(model.getTime());
                                    holder.setFee(model.getFee());
                                    holder.setTutorFname(model.getTutorUID());
                                    holder.setTutorLname(model.getTutorUID());
                                    holder.setProfilepic(model.getTutorUID());
                                    holder.setDesc(model.getTutorUID());
                                    final String tutorUID=model.getTutorUID();
                                    final String subjUUID=model.getSubjUUID();
                                    holder.book.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            DialogFragment nextFrag= TuteeBookSessionFragment.newInstance(subjUUID,tutorUID);
                                            nextFrag.show(getActivity().getSupportFragmentManager(),"Book");
                                        }
                                    });
                                    holder.view_tutor.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Fragment nextFrag= ViewTutorProfileFragment.newInstance(tutorUID);
                                            getActivity().getSupportFragmentManager()
                                                    .beginTransaction()
                                                    .replace(((ViewGroup)getView().getParent()).getId(), nextFrag)
                                                    .addToBackStack(null)
                                                    .commit();
                                        }
                                    });
                                    holder.message.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Fragment nextFrag= ChatRoomFragment.newInstance(tutorUID);
                                            getActivity().getSupportFragmentManager()
                                                    .beginTransaction()
                                                    .replace(((ViewGroup)getView().getParent()).getId(), nextFrag)
                                                    .addToBackStack(null)
                                                    .commit();
                                        }
                                    });
                                }
                                @Override
                                public TuteeSearchFragment.TuteeSubjectCardViewHolder onCreateViewHolder(ViewGroup group, int i) {
                                    // Using a custom layout for each item, we create a new instance of the viewholder
                                    View view = LayoutInflater.from(group.getContext()).inflate(R.layout.tutee_subject_card, group, false);
                                    return new TuteeSearchFragment.TuteeSubjectCardViewHolder(view);
                                }
                            };
                            adapter.startListening();
                            mRecyclerView.setAdapter(adapter);
                        }else if(item.getTitle().equals("Subject Z-A")){
                            query = db.collection("subjects").orderBy("name",Query.Direction.DESCENDING);
                            options = new FirestoreRecyclerOptions.Builder<Subjectt>().setQuery(query, Subjectt.class).build();
                            FirestoreRecyclerAdapter<Subjectt, TuteeSearchFragment.TuteeSubjectCardViewHolder> adapter = new FirestoreRecyclerAdapter<Subjectt, TuteeSearchFragment.TuteeSubjectCardViewHolder>(options) {
                                @Override
                                public void onBindViewHolder(TuteeSearchFragment.TuteeSubjectCardViewHolder holder, int position, Subjectt model) {
                                    holder.setName(model.getName());
                                    holder.setDescription(model.getDescription());
                                    holder.setWeekly_sched(model.getWeekly_sched());
                                    holder.setTime(model.getTime());
                                    holder.setFee(model.getFee());
                                    holder.setTutorFname(model.getTutorUID());
                                    holder.setTutorLname(model.getTutorUID());
                                    holder.setProfilepic(model.getTutorUID());
                                    holder.setDesc(model.getTutorUID());
                                    final String tutorUID=model.getTutorUID();
                                    final String subjUUID=model.getSubjUUID();
                                    holder.book.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            DialogFragment nextFrag= TuteeBookSessionFragment.newInstance(subjUUID,tutorUID);
                                            nextFrag.show(getActivity().getSupportFragmentManager(),"Book");
                                        }
                                    });
                                    holder.view_tutor.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Fragment nextFrag= ViewTutorProfileFragment.newInstance(tutorUID);
                                            getActivity().getSupportFragmentManager()
                                                    .beginTransaction()
                                                    .replace(((ViewGroup)getView().getParent()).getId(), nextFrag)
                                                    .addToBackStack(null)
                                                    .commit();
                                        }
                                    });
                                    holder.message.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Fragment nextFrag= ChatRoomFragment.newInstance(tutorUID);
                                            getActivity().getSupportFragmentManager()
                                                    .beginTransaction()
                                                    .replace(((ViewGroup)getView().getParent()).getId(), nextFrag)
                                                    .addToBackStack(null)
                                                    .commit();
                                        }
                                    });
                                }
                                @Override
                                public TuteeSearchFragment.TuteeSubjectCardViewHolder onCreateViewHolder(ViewGroup group, int i) {
                                    // Using a custom layout for each item, we create a new instance of the viewholder
                                    View view = LayoutInflater.from(group.getContext()).inflate(R.layout.tutee_subject_card, group, false);
                                    return new TuteeSearchFragment.TuteeSubjectCardViewHolder(view);
                                }
                            };
                            adapter.startListening();
                            mRecyclerView.setAdapter(adapter);
                        }else if(item.getTitle().equals("Fee low to high")){
                            query = db.collection("subjects").orderBy("fee",Query.Direction.ASCENDING);
                            options = new FirestoreRecyclerOptions.Builder<Subjectt>().setQuery(query, Subjectt.class).build();
                            FirestoreRecyclerAdapter<Subjectt, TuteeSearchFragment.TuteeSubjectCardViewHolder> adapter = new FirestoreRecyclerAdapter<Subjectt, TuteeSearchFragment.TuteeSubjectCardViewHolder>(options) {
                                @Override
                                public void onBindViewHolder(TuteeSearchFragment.TuteeSubjectCardViewHolder holder, int position, Subjectt model) {
                                    holder.setName(model.getName());
                                    holder.setDescription(model.getDescription());
                                    holder.setWeekly_sched(model.getWeekly_sched());
                                    holder.setTime(model.getTime());
                                    holder.setFee(model.getFee());
                                    holder.setTutorFname(model.getTutorUID());
                                    holder.setTutorLname(model.getTutorUID());
                                    holder.setProfilepic(model.getTutorUID());
                                    holder.setDesc(model.getTutorUID());
                                    final String tutorUID=model.getTutorUID();
                                    final String subjUUID=model.getSubjUUID();
                                    holder.book.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            DialogFragment nextFrag= TuteeBookSessionFragment.newInstance(subjUUID,tutorUID);
                                            nextFrag.show(getActivity().getSupportFragmentManager(),"Book");
                                        }
                                    });
                                    holder.view_tutor.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Fragment nextFrag= ViewTutorProfileFragment.newInstance(tutorUID);
                                            getActivity().getSupportFragmentManager()
                                                    .beginTransaction()
                                                    .replace(((ViewGroup)getView().getParent()).getId(), nextFrag)
                                                    .addToBackStack(null)
                                                    .commit();
                                        }
                                    });
                                    holder.message.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Fragment nextFrag= ChatRoomFragment.newInstance(tutorUID);
                                            getActivity().getSupportFragmentManager()
                                                    .beginTransaction()
                                                    .replace(((ViewGroup)getView().getParent()).getId(), nextFrag)
                                                    .addToBackStack(null)
                                                    .commit();
                                        }
                                    });
                                }
                                @Override
                                public TuteeSearchFragment.TuteeSubjectCardViewHolder onCreateViewHolder(ViewGroup group, int i) {
                                    // Using a custom layout for each item, we create a new instance of the viewholder
                                    View view = LayoutInflater.from(group.getContext()).inflate(R.layout.tutee_subject_card, group, false);
                                    return new TuteeSearchFragment.TuteeSubjectCardViewHolder(view);
                                }
                            };
                            adapter.startListening();
                            mRecyclerView.setAdapter(adapter);
                        }else if(item.getTitle().equals("Fee high to low")){
                            query = db.collection("subjects").orderBy("fee",Query.Direction.DESCENDING);
                            options = new FirestoreRecyclerOptions.Builder<Subjectt>().setQuery(query, Subjectt.class).build();
                            FirestoreRecyclerAdapter<Subjectt, TuteeSearchFragment.TuteeSubjectCardViewHolder> adapter = new FirestoreRecyclerAdapter<Subjectt, TuteeSearchFragment.TuteeSubjectCardViewHolder>(options) {
                                @Override
                                public void onBindViewHolder(TuteeSearchFragment.TuteeSubjectCardViewHolder holder, int position, Subjectt model) {
                                    holder.setName(model.getName());
                                    holder.setDescription(model.getDescription());
                                    holder.setWeekly_sched(model.getWeekly_sched());
                                    holder.setTime(model.getTime());
                                    holder.setFee(model.getFee());
                                    holder.setTutorFname(model.getTutorUID());
                                    holder.setTutorLname(model.getTutorUID());
                                    holder.setProfilepic(model.getTutorUID());
                                    holder.setDesc(model.getTutorUID());
                                    final String tutorUID=model.getTutorUID();
                                    final String subjUUID=model.getSubjUUID();
                                    holder.book.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            DialogFragment nextFrag= TuteeBookSessionFragment.newInstance(subjUUID,tutorUID);
                                            nextFrag.show(getActivity().getSupportFragmentManager(),"Book");
                                        }
                                    });
                                    holder.view_tutor.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Fragment nextFrag= ViewTutorProfileFragment.newInstance(tutorUID);
                                            getActivity().getSupportFragmentManager()
                                                    .beginTransaction()
                                                    .replace(((ViewGroup)getView().getParent()).getId(), nextFrag)
                                                    .addToBackStack(null)
                                                    .commit();
                                        }
                                    });
                                    holder.message.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Fragment nextFrag= ChatRoomFragment.newInstance(tutorUID);
                                            getActivity().getSupportFragmentManager()
                                                    .beginTransaction()
                                                    .replace(((ViewGroup)getView().getParent()).getId(), nextFrag)
                                                    .addToBackStack(null)
                                                    .commit();
                                        }
                                    });
                                }
                                @Override
                                public TuteeSearchFragment.TuteeSubjectCardViewHolder onCreateViewHolder(ViewGroup group, int i) {
                                    // Using a custom layout for each item, we create a new instance of the viewholder
                                    View view = LayoutInflater.from(group.getContext()).inflate(R.layout.tutee_subject_card, group, false);
                                    return new TuteeSearchFragment.TuteeSubjectCardViewHolder(view);
                                }
                            };
                            adapter.startListening();
                            mRecyclerView.setAdapter(adapter);
                        }

                        return true;
                    }
                });
                p.show();
            }
        });
        return view;
    }

    public class TuteeSubjectCardViewHolder extends RecyclerView.ViewHolder{
        public TextView name, description, weekly_sched, time, fee, tutorFname, tutorLname, desc;
        public Button book, view_tutor, message;
        public CircleImageView profilepic;
        public TuteeSubjectCardViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            weekly_sched = itemView.findViewById(R.id.weekly_sched);
            time = itemView.findViewById(R.id.time);
            fee = itemView.findViewById(R.id.fee);
            tutorFname = itemView.findViewById(R.id.fname);
            tutorLname = itemView.findViewById(R.id.lname);
            profilepic = itemView.findViewById(R.id.profilepic);
            book = itemView.findViewById(R.id.request);
            view_tutor = itemView.findViewById(R.id.view_tutor);
            message = itemView.findViewById(R.id.message);
            desc = itemView.findViewById(R.id.desc);
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
        public void setFee(String string) { fee.setText(string); }
        public void setDesc(String string) {
            DocumentReference reference = db.collection("users").document(string);
            reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if(document != null && document.exists()){
                            //get text
                            String description = document.getString("desc");
                            desc.setText(description);
                        } else {
                            Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        public void setTutorFname(String string) {
            DocumentReference reference = db.collection("users").document(string);
            reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if(document != null && document.exists()){
                            //get text
                            String fname = document.getString("fname");
                            tutorFname.setText(fname);
                        } else {
                            Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        public void setTutorLname(String string) { DocumentReference reference = db.collection("users").document(string);
            reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if(document != null && document.exists()){
                            //get text
                            String lname = document.getString("lname");
                            tutorLname.setText(lname);
                        }else {
                            Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                    }
                }
            });}
        public void setProfilepic(String string) {
            // get the Firebase  storage reference
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/"+string);
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri){
                    //load img using glide
                    Glide.with(getActivity()).load(uri.toString()).placeholder(R.drawable.round_account_circle_24).dontAnimate().into(profilepic);
                }
            });

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
                Query query = db.collection("subjects").whereEqualTo("name",s);
                //show subjects
                options = new FirestoreRecyclerOptions.Builder<Subjectt>().setQuery(query, Subjectt.class).build();
                FirestoreRecyclerAdapter<Subjectt, TuteeSearchFragment.TuteeSubjectCardViewHolder> adapter = new FirestoreRecyclerAdapter<Subjectt, TuteeSearchFragment.TuteeSubjectCardViewHolder>(options) {
                    @Override
                    public void onBindViewHolder(TuteeSearchFragment.TuteeSubjectCardViewHolder holder, int position, Subjectt model) {
                        holder.setName(model.getName());
                        holder.setDescription(model.getDescription());
                        holder.setWeekly_sched(model.getWeekly_sched());
                        holder.setTime(model.getTime());
                        holder.setFee(model.getFee());
                        holder.setTutorFname(model.getTutorUID());
                        holder.setTutorLname(model.getTutorUID());
                        holder.setProfilepic(model.getTutorUID());
                        holder.setDesc(model.getTutorUID());
                        final String tutorUID=model.getTutorUID();
                        final String subjUUID=model.getSubjUUID();
                        holder.book.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DialogFragment nextFrag= TuteeBookSessionFragment.newInstance(subjUUID,tutorUID);
                                nextFrag.show(getActivity().getSupportFragmentManager(),"Book");
                            }
                        });
                        holder.view_tutor.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Fragment nextFrag= ViewTutorProfileFragment.newInstance(tutorUID);
                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(((ViewGroup)getView().getParent()).getId(), nextFrag)
                                        .addToBackStack(null)
                                        .commit();
                            }
                        });
                        holder.message.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Fragment nextFrag= ChatRoomFragment.newInstance(tutorUID);
                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(((ViewGroup)getView().getParent()).getId(), nextFrag)
                                        .addToBackStack(null)
                                        .commit();
                            }
                        });
                    }
                    @Override
                    public TuteeSearchFragment.TuteeSubjectCardViewHolder onCreateViewHolder(ViewGroup group, int i) {
                        // Using a custom layout for each item, we create a new instance of the viewholder
                        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.tutee_subject_card, group, false);
                        return new TuteeSearchFragment.TuteeSubjectCardViewHolder(view);
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