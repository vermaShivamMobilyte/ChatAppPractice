package com.shivam.chatapppractice.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shivam.chatapppractice.R;
import com.shivam.chatapppractice.model.User;
import com.shivam.chatapppractice.ui.activities.ChatActivity;
import com.shivam.chatapppractice.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Shivam on 29-01-2017.
 */

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.UserListViewHolder> {

    private Context context;
    private List<User> mList;

    public UsersListAdapter(Context context) {
        this.context = context;
        mList = new ArrayList<>();
    }

    @Override
    public UserListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_list_item, parent, false);
        return new UserListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserListViewHolder holder, int position) {
        User user = mList.get(position);
        holder.mUserName.setText(user.getName());
        Glide.with(context).load(user.getProfilePic()).crossFade().into(holder.mUserPic);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addItem(User user) {
        mList.add(user);
        notifyItemInserted(mList.size() - 1);
    }

    class UserListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_user_pic)
        ImageView mUserPic;
        @BindView(R.id.txt_name)
        TextView mUserName;

        public UserListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    User user = mList.get(getAdapterPosition());
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra(AppUtils.USER_ID, user.getUserId());
                    context.startActivity(intent);
                }
            });
        }
    }
}
