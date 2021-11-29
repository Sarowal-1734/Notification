package com.sarowal.notification.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sarowal.notification.adapter.ChatAdapter;
import com.sarowal.notification.databinding.FragmentChatBinding;
import com.sarowal.notification.model.ChatModel;
import com.sarowal.notification.notification.FcmNotificationsSender;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DatabaseReference databaseReference;
    private ArrayList<ChatModel> chatModelList = new ArrayList<>();

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Get and set data to the view
        Bundle bundle = this.getArguments();
        String ReceiverUserId = bundle.getString("ReceiverUserId");

        // Show Receiver Name as AppBar Title
        if (currentUser != null) {
            db.collection("users").document(ReceiverUserId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String ReceiverName = documentSnapshot.get("userName").toString();
                            // Change activity title
                            getActivity().setTitle(ReceiverName);
                        }
                    });
        }

        // Show current userName
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            binding.textViewUsername.setText("Current User: " + documentSnapshot.get("userName"));
                        }
                    });
        }

        // Setting Up Recycler view for showing message
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerViewChatDetails.setLayoutManager(layoutManager);
        layoutManager.setStackFromEnd(true);
        binding.recyclerViewChatDetails.setHasFixedSize(true);
        ChatAdapter chatAdapter = new ChatAdapter(getActivity(), chatModelList);
        binding.recyclerViewChatDetails.setAdapter(chatAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatModelList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                    if (chatModel.getSenderId().equals(currentUser.getUid()) || chatModel.getReceiverId().equals(currentUser.getUid())) {
                        if (chatModel.getSenderId().equals(ReceiverUserId) || chatModel.getReceiverId().equals(ReceiverUserId)) {
                            chatModelList.add(chatModel);
                        }
                    }
                    if (chatAdapter.getItemCount() > 0) {
                        binding.recyclerViewChatDetails.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                    }
                    chatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // On Send Message Button Clicked
        binding.buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Write a message to the database
                databaseReference = FirebaseDatabase.getInstance().getReference();
                // Store user info in Database
                ChatModel chatModel = new ChatModel();
                chatModel.setSenderId(currentUser.getUid());
                chatModel.setReceiverId(ReceiverUserId);
                chatModel.setMessage(binding.editTextSendText.getText().toString());
                chatModel.setStatus("Sent");
                databaseReference.child("Chats").push().setValue(chatModel);
                sendNotification(ReceiverUserId);
            }
        });


        return binding.getRoot();
    }

    // Send notification
    private void sendNotification(String receiverUserId) {
        db.collection("users").document(receiverUserId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String title = "You have a new text message";
                        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
                                documentSnapshot.get("userToken").toString(), receiverUserId,
                                title, binding.editTextSendText.getText().toString(), getActivity(), getActivity());

                        notificationsSender.SendNotifications();
                    }
                });
    }
}