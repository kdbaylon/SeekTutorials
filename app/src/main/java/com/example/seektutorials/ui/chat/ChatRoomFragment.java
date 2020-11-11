package com.example.seektutorials.ui.chat;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatRoomFragment extends Fragment {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private EditText messageEditText;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private FirebaseStorage storage;
    String uid;
    String otherUID;

    public static ChatRoomFragment newInstance(String string) {
        Bundle bundle = new Bundle();
        bundle.putString("otherUID", string);

        ChatRoomFragment fragment = new ChatRoomFragment();
        fragment.setArguments(bundle);

        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.chatroom, null);
        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        uid=mAuth.getCurrentUser().getUid();
        //getlayouts
        final CircleImageView other_profilepic = view.findViewById(R.id.other_profilepic);
        final TextView fnameTextView = view.findViewById(R.id.fname);
        final TextView lnameTextView = view.findViewById(R.id.lname);
        RecyclerView chatlogRecyclerView = view.findViewById(R.id.chatLog);
        messageEditText = view.findViewById(R.id.chat);
        MaterialButton sendButton = view.findViewById(R.id.sendButton);
        //fill basic info
        Bundle bundle = getArguments();
        if(bundle!=null){
            otherUID = bundle.getString("otherUID");
        }
        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("images/"+otherUID);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri){
                //load img using glide
                Glide.with(getActivity()).load(uri.toString()).placeholder(R.drawable.round_account_circle_24).dontAnimate().into(other_profilepic);
            }
        });
        DocumentReference docref = db.collection("users").document(otherUID);
        docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
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
        //messageButton
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
                messageEditText.setText("");
            }
        });
        //chatlog
        chatlogRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        chatlogRecyclerView.setItemAnimator(new DefaultItemAnimator());
        Query query = FirebaseFirestore.getInstance().collection("users").document(uid)
                .collection("chats").document(otherUID).collection("chatroom").orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Chat> options = new FirestoreRecyclerOptions.Builder<Chat>().setQuery(query, Chat.class).build();
        final FirestoreRecyclerAdapter<Chat, ChatRoomFragment.MessageHolder> chat_adapter = new FirestoreRecyclerAdapter<Chat, ChatRoomFragment.MessageHolder>(options) {
            @Override
            public void onBindViewHolder(ChatRoomFragment.MessageHolder holder, int position, Chat model) {
                holder.setMessage(model.getMessage());
                holder.setProfilepic(model.getUID());
            }

            @Override
            public ChatRoomFragment.MessageHolder onCreateViewHolder(ViewGroup group, int viewType) {
                // Using a custom layout for each item, we create a new instance of the viewholder
                if(viewType == MSG_TYPE_RIGHT){
                    View msg = LayoutInflater.from(group.getContext()).inflate(R.layout.chat_user, group, false);
                    return new ChatRoomFragment.MessageHolder(msg);
                }else{
                    View msg = LayoutInflater.from(group.getContext()).inflate(R.layout.chat_other, group, false);
                    return new ChatRoomFragment.MessageHolder(msg);
                }


            }

            @Override
            public int getItemViewType(int position) {
                if(getItem(position).getUID().equals(uid))
                    return MSG_TYPE_RIGHT;
                else
                    return MSG_TYPE_LEFT;
            }


        };
        chat_adapter.startListening();
        chatlogRecyclerView.setAdapter(chat_adapter);

        return view;
    }
    public void sendMessage(){
        //take values
        final String message, UID;
        final Timestamp timestamp;
        message = messageEditText.getText().toString();
        UID =uid;
        Date date= new Date();
        timestamp = new Timestamp(date);
        //errors
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(getActivity(), "Enter message!", Toast.LENGTH_SHORT).show();
            return;
        }

        //put in a hashmap
        final Map<String, Object> newchat =new HashMap<>();
        newchat.put("UID",UID);
        newchat.put("message",message);
        newchat.put("timestamp",timestamp);

        //add to chatroom of both users
        //generate rand uuid for each document of chat
        String chatUUID = UUID.randomUUID().toString();
        db.collection("users").document(uid).collection("chats").document(otherUID).collection("chatroom").document(chatUUID)
                .set(newchat)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Message sent.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Message sent failed.", Toast.LENGTH_SHORT).show();
                    }
                });
        db.collection("users").document(otherUID).collection("chats").document(uid).collection("chatroom").document(chatUUID)
                .set(newchat)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Message sent.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Message sent failed.", Toast.LENGTH_SHORT).show();
                    }
                });
        //add document ID
        final Map<String, Object> docuID1 =new HashMap<>();
        docuID1.put("UID",uid);
        db.collection("users").document(otherUID).collection("chats").document(uid)
                .set(docuID1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Message sent.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Message sent failed.", Toast.LENGTH_SHORT).show();
                    }
                });
        final Map<String, Object> docuID2 =new HashMap<>();
        docuID2.put("UID",otherUID);
        db.collection("users").document(uid).collection("chats").document(otherUID)
                .set(docuID2)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Message sent.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Message sent failed.", Toast.LENGTH_SHORT).show();
                    }
                });





    }
    public class MessageHolder extends RecyclerView.ViewHolder {
        TextView message;
        CircleImageView profilepic;
        public MessageHolder(View itemView) {
            super(itemView);
            message= itemView.findViewById(R.id.message);
            profilepic = itemView.findViewById(R.id.profilepic);
        }
        public void setMessage(String string) { message.setText(string); }
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
