package com.example.victor_friends.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.victor_friends.OnFriendItemClickListener;
import com.example.victor_friends.databinding.RvItemFriendBinding;
import com.example.victor_friends.models.Friend;

import java.util.ArrayList;

/**
 * Making_Friends created by vitto
 */
public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {
    private final String TAG = this.getClass().getCanonicalName();
    private final Context context;
    private final ArrayList<Friend> friendArrayList;
    private final OnFriendItemClickListener clickListener;

    public FriendAdapter(Context context, ArrayList<Friend> friendArrayList, OnFriendItemClickListener clickListener) {
        this.context = context;
        this.friendArrayList = friendArrayList;
        this.clickListener = clickListener;
    }


    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return null;
        return new FriendViewHolder(RvItemFriendBinding.inflate(LayoutInflater.from(context)));
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        final Friend currentFriend = this.friendArrayList.get(position);

        holder.bind(context, currentFriend, clickListener);
    }

    @Override
    public int getItemCount() {
//        return 0;
        return this.friendArrayList.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder{
        RvItemFriendBinding binding;

        public FriendViewHolder(RvItemFriendBinding friendBinding) {
            super(friendBinding.getRoot());
            this.binding = friendBinding;
        }

        //add binding method
        public void bind(Context context, final Friend currentFriend, OnFriendItemClickListener clickListener){
            binding.tvListName.setText(currentFriend.getFriendName());
            binding.tvListPhone.setText("Phone: " + currentFriend.getFriendNumber());
            binding.tvListEmail.setText("Email: " + currentFriend.getFriendEmail());
            binding.tvListAddress.setText("Address: " + currentFriend.getFriendAddress());

            itemView.setOnClickListener( new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Log.d("FriendViewHolder - bind()-", "onClick: " + currentFriend.toString());
                    clickListener.onFriendItemClicked(currentFriend);
                }
            });
        }
    }
}
