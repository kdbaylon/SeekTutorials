package com.example.seektutorials.ui.chat;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.seektutorials.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Tutor messages fragment
 */
public class MessagesFragment extends Fragment {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uid;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        uid=mAuth.getCurrentUser().getUid();
        View view = inflater.inflate(R.layout.messages, null);
        RecyclerView messages = (RecyclerView) view.findViewById(R.id.messages);
        messages.setLayoutManager(new LinearLayoutManager(getActivity()));
        messages.setItemAnimator(new DefaultItemAnimator());
        Query query = db.collection("users").document(uid).collection("chats");
        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>().setQuery(query, Message.class).build();
        final FirestoreRecyclerAdapter<Message, MessagesFragment.MessageViewHolder> adapter = new FirestoreRecyclerAdapter<Message, MessagesFragment.MessageViewHolder>(options) {
            @Override
            public void onBindViewHolder(MessagesFragment.MessageViewHolder holder, int position, final Message model) {
                holder.setFname(model.getUID());
                holder.setLname(model.getUID());
                holder.setProfilepic(model.getUID());
                holder.setMessage(model.getUID());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment nextFrag= ChatRoomFragment.newInstance(model.getUID());
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(((ViewGroup)getView().getParent()).getId(), nextFrag)
                                .addToBackStack(null)
                                .commit();
                    }
                });

            }

            @Override
            public MessagesFragment.MessageViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Using a custom layout for each item, we create a new instance of the viewholder
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.chat_card, group, false);
                return new MessagesFragment.MessageViewHolder(view);
            }
            @Override
            public void onError(FirebaseFirestoreException e) {
                Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
            }

        };
        adapter.startListening();
        messages.setAdapter(adapter);
        return view;
    }
    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView firstname, lastname, message;
        public CircleImageView profilepic;
        public MessageViewHolder(View itemView) {
            super(itemView);
            firstname = itemView.findViewById(R.id.fname);
            lastname = itemView.findViewById(R.id.lname);
            profilepic = itemView.findViewById(R.id.profilepic);
            message = itemView.findViewById(R.id.message);

        }
        public void setMessage(String string) {
            Query messageQuery = db.collection("users").document(uid).collection("chats").document(string).collection("chatroom").orderBy("timestamp",Query.Direction.DESCENDING).limit(1);
            messageQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            message.setText(document.getString("message"));
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        public void setFname(String string) {
            DocumentReference reference = db.collection("users").document(string);
            reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if(document != null && document.exists()){
                            //get text
                            String fname = document.getString("fname");
                            firstname.setText(fname);
                        } else {
                            Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error getting document", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        public void setLname(String string) { DocumentReference reference = db.collection("users").document(string);
            reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if(document != null && document.exists()){
                            //get text
                            String lname = document.getString("lname");
                            lastname.setText(lname);
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

}