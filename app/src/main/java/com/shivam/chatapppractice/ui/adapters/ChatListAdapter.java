package com.shivam.chatapppractice.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.shivam.chatapppractice.R;
import com.shivam.chatapppractice.model.Chat;
import com.shivam.chatapppractice.model.RxFireBaseChildEvent;
import com.shivam.chatapppractice.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Shivam on 28-01-2017.
 */

public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Chat> mList;
    private List<String> mKeys;
    private String senderId;
    private Context context;

    public ChatListAdapter(Context context) {
        this.context = context;
        mKeys = new ArrayList<>();
        mList = new ArrayList<>();
        senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public int getItemViewType(int position) {
        if (isFooter(position)) return AppUtils.TYPE_FOOTER;
        Chat chat = mList.get(position);
        if (senderId.trim().equals(chat.getUserId().trim())) {
            return AppUtils.TYPE_SENDER;
        } else return AppUtils.TYPE_RECEIVER;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case AppUtils.TYPE_RECEIVER:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receive_chat_list_item, parent, false);
                return new ReceiveChatListViewHolder(view);
            case AppUtils.TYPE_SENDER:
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_chat_list_item, parent, false);
                return new SendChatListViewHolder(view1);
            case AppUtils.TYPE_FOOTER:
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_chat_list_item, parent, false);
                return new FooterViewHolder(view2);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!isFooter(position)) {
            Chat chat = mList.get(position);
            if (holder instanceof SendChatListViewHolder) {
                ((SendChatListViewHolder) holder).mSendMessage.setVisibility(View.GONE);
                ((SendChatListViewHolder) holder).mImgMessage.setVisibility(View.GONE);
                switch (chat.getMsgType()) {
                    case AppUtils.MSG_TYPE_TXT:
                        ((SendChatListViewHolder) holder).mSendMessage.setVisibility(View.VISIBLE);
                        ((SendChatListViewHolder) holder).mSendMessage.setText(chat.getMessage());
                        break;
                    case AppUtils.MSG_TYPE_IMG:
                        ((SendChatListViewHolder) holder).mImgMessage.setVisibility(View.VISIBLE);
                        Glide.with(context).load(chat.getMessage()).centerCrop().crossFade().into(((SendChatListViewHolder) holder).mImgMessage);
                        break;
                    case AppUtils.MSG_TYPE_VID:
                        break;
                }

                ((SendChatListViewHolder) holder).mSendMessageTime.setText(AppUtils.getlocalTime(chat.getTime()));
                ((SendChatListViewHolder) holder).mSendUserName.setText(chat.getUserName());
                switch (chat.getSendStatus()) {
                    case AppUtils.SEND_STATUS_SENT:
                        ((SendChatListViewHolder) holder).mSendStatus.setImageResource(R.drawable.ic_sent);
                        break;
                    case AppUtils.SEND_STATUS_RECEIVE:
                        ((SendChatListViewHolder) holder).mSendStatus.setImageResource(R.drawable.ic_received);
                        break;
                    case AppUtils.SEND_STATUS_READ:
                        ((SendChatListViewHolder) holder).mSendStatus.setImageResource(R.drawable.ic_read);
                        break;
                }
            } else if (holder instanceof ReceiveChatListViewHolder) {
                ((ReceiveChatListViewHolder) holder).mReceiveMessage.setText(chat.getMessage());
                ((ReceiveChatListViewHolder) holder).mReceiveMessageTime.setText(AppUtils.getlocalTime(chat.getTime()));
                ((ReceiveChatListViewHolder) holder).mReceiveUserName.setText(chat.getUserName());
            }
        }
    }

    private boolean isFooter(int position) {
        if (mList.size() == 0 && position == 0) return true;
        else if (position == mList.size()) return true;
        return false;
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    public void addItem(RxFireBaseChildEvent<Chat> item) {
        if (!mKeys.contains(item.getKey())) {
            int insertedPosition;
            if (item.getPreviousChildName() == null) {
                mList.add(0, item.getValue());
                mKeys.add(0, item.getKey());
                insertedPosition = 0;
            } else {
                int previousIndex = mKeys.indexOf(item.getPreviousChildName());
                int nextIndex = previousIndex + 1;
                if (nextIndex == mList.size()) {
                    mList.add(item.getValue());
                    mKeys.add(item.getKey());
                } else {
                    mList.add(nextIndex, item.getValue());
                    mKeys.add(nextIndex, item.getKey());
                }
                insertedPosition = nextIndex;
            }
            notifyItemInserted(insertedPosition);
        }
    }

    public void changeItem(RxFireBaseChildEvent<Chat> item) {
        if (mKeys.contains(item.getKey())) {
            int index = mKeys.indexOf(item.getKey());
            mList.set(index, item.getValue());
            notifyItemChanged(index);
        }
    }

    public void removeItem(RxFireBaseChildEvent<Chat> item) {
        if (mKeys.contains(item.getKey())) {
            int index = mKeys.indexOf(item.getKey());
            mKeys.remove(index);
            mList.remove(index);
            notifyItemRemoved(index);
        }
    }

    class SendChatListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_send_user_name)
        TextView mSendUserName;
        @BindView(R.id.txt_send_msg)
        TextView mSendMessage;
        @BindView(R.id.txt_send_time)
        TextView mSendMessageTime;
        @BindView(R.id.img_send_status)
        ImageView mSendStatus;
        @BindView(R.id.img_msg)
        ImageView mImgMessage;

        public SendChatListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class ReceiveChatListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_receive_user_name)
        TextView mReceiveUserName;
        @BindView(R.id.txt_receive_msg)
        TextView mReceiveMessage;
        @BindView(R.id.txt_receive_time)
        TextView mReceiveMessageTime;

        public ReceiveChatListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View view2) {
            super(view2);
        }
    }
}
