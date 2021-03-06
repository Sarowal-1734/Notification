package com.sarowal.notification.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.sarowal.notification.R;
import com.sarowal.notification.model.ChatModel;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<ChatModel> chatList;

    public ChatAdapter(Context context, ArrayList<ChatModel> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (viewType == 1) {
            View view = layoutInflater.inflate(R.layout.sample_sender_chat, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        } else {
            View view = layoutInflater.inflate(R.layout.sample_receiver_chat, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ChatModel chatModel = chatList.get(position);
        holder.textViewMessage.setText(chatModel.getMessage());
        holder.textViewStatus.setText(chatModel.getStatus());
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage, textViewStatus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.showMessage);
            textViewStatus = itemView.findViewById(R.id.status);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChatModel chatModel = chatList.get(position);
        String currenUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (currenUserId.equals(chatModel.getSenderId())) {
            return 1;
        }
        return 0;
    }
}
