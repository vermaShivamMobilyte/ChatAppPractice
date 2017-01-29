package com.shivam.chatapppractice.ui.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;
import com.miguelbcr.ui.rx_paparazzo2.entities.Response;
import com.miguelbcr.ui.rx_paparazzo2.entities.size.CustomMaxSize;
import com.miguelbcr.ui.rx_paparazzo2.entities.size.Size;
import com.miguelbcr.ui.rx_paparazzo2.entities.size.SmallSize;
import com.shivam.chatapppractice.R;
import com.shivam.chatapppractice.model.Chat;
import com.shivam.chatapppractice.model.RxFireBaseChildEvent;
import com.shivam.chatapppractice.ui.adapters.ChatListAdapter;
import com.shivam.chatapppractice.utils.AppUtils;
import com.shivam.chatapppractice.utils.base.BaseActivity;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

public class ChatActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rcyle_chat_list)
    RecyclerView mChatList;
    @BindView(R.id.ed_chat_msg)
    AppCompatEditText mChatMsg;
    private DatabaseReference reference;
    private ChatListAdapter mChatListAdapter;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        String conversationId = getIntent().getStringExtra(AppUtils.CONVERSATION_ID);
        reference = FirebaseDatabase.getInstance().getReference().child(AppUtils.APP_NAME)
                .child(AppUtils.TABLE_CHATS).child(conversationId);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mChatList.setLayoutManager(new LinearLayoutManager(this));
        mChatListAdapter = new ChatListAdapter(this);
        mChatList.setAdapter(mChatListAdapter);
        mChatList.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                if (i3 < i7) {
                    mChatList.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mChatListAdapter.getItemCount() > 0)
                                mChatList.smoothScrollToPosition(mChatListAdapter.getItemCount() - 1);
                        }
                    }, 100);
                }
            }
        });

        setUpFireBase();
    }

    private void setUpFireBase() {
        showProgressDialog();
        DisposableSubscriber disposableSubscriber = new DisposableSubscriber<RxFireBaseChildEvent<Chat>>() {

            @Override
            public void onNext(RxFireBaseChildEvent<Chat> chatRxFireBaseChildEvent) {
                hideProgressDialog();
                switch (chatRxFireBaseChildEvent.getEventType()) {
                    case ADDED:
                        mChatListAdapter.addItem(chatRxFireBaseChildEvent);
                        mChatList.smoothScrollToPosition(mChatListAdapter.getItemCount());
                        break;
                    case CHANGED:
                        mChatListAdapter.changeItem(chatRxFireBaseChildEvent);
                        break;
                    case REMOVED:
                        mChatListAdapter.removeItem(chatRxFireBaseChildEvent);
                        break;
                }
            }

            @Override
            public void onError(Throwable t) {
                hideProgressDialog();
                t.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        };
        Flowable.create(new FlowableOnSubscribe<RxFireBaseChildEvent<Chat>>() {
            @Override
            public void subscribe(final FlowableEmitter<RxFireBaseChildEvent<Chat>> emitter) throws Exception {
                final ChildEventListener childEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        emitter.onNext(new RxFireBaseChildEvent<Chat>(dataSnapshot.getKey(), dataSnapshot.getValue(Chat.class), s, RxFireBaseChildEvent.EventType.ADDED));
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        emitter.onNext(new RxFireBaseChildEvent<Chat>(dataSnapshot.getKey(), dataSnapshot.getValue(Chat.class), s, RxFireBaseChildEvent.EventType.CHANGED));
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        emitter.onNext(new RxFireBaseChildEvent<Chat>(dataSnapshot.getKey(), dataSnapshot.getValue(Chat.class), RxFireBaseChildEvent.EventType.REMOVED));
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        emitter.onNext(new RxFireBaseChildEvent<Chat>(dataSnapshot.getKey(), dataSnapshot.getValue(Chat.class), s, RxFireBaseChildEvent.EventType.MOVED));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        emitter.onError(databaseError.toException());
                    }
                };
                reference.addChildEventListener(childEventListener);
                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        reference.removeEventListener(childEventListener);
                    }
                });
            }
        }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposableSubscriber);
    }

    @OnClick(R.id.flt_send)
    void sendMessage() {
        String msg = mChatMsg.getText().toString().trim();
        if (msg != null && msg.length() != 0) {
            final Chat chat = new Chat();
            chat.setMessage(msg);
            chat.setTime(AppUtils.getCurrentTime());
            chat.setMsgType(AppUtils.MSG_TYPE_TXT);
            chat.setSendStatus(AppUtils.SEND_STATUS_SENT);
            chat.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
            chat.setUserName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            chat.setUserPic(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
            chat.setChatId(reference.push().getKey());
            reference.child(chat.getChatId()).setValue(chat);
            mChatMsg.setText("");
        }
    }

    @OnClick(R.id.img_attachment)
    void attachment() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(
                R.layout.attachment_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.BOTTOM | Gravity.CENTER;
        wmlp.x = 0;   //x position
        wmlp.y = 150;   //y position
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setView(view);
        view.findViewById(R.id.dialog_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Size size = new CustomMaxSize(512);
                RxPaparazzo.takeImage(ChatActivity.this)
                        .size(size)
                        .usingCamera()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Response<ChatActivity, String>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Response<ChatActivity, String> response) {
                                if (checkResultCode(response.resultCode())) {
                                    response.targetUI().sendImage(response.data());
                                }
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                throwable.printStackTrace();
                                showToast("ERROR " + throwable.getMessage());
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        });
        view.findViewById(R.id.dialog_gallery).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Size size = new SmallSize();
                RxPaparazzo.takeImages(ChatActivity.this)
                        .useInternalStorage()
                        .crop()
                        .size(size)
                        .usingGallery()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Response<ChatActivity, List<String>>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Response<ChatActivity, List<String>> response) {
                                if (checkResultCode(response.resultCode())) {
                                    if (response.data().size() == 1)
                                        response.targetUI().sendImage(response.data().get(0));
                                    else response.targetUI().sendImages(response.data());
                                }
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                throwable.printStackTrace();
                                showToast("ERROR " + throwable.getMessage());
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        });
        /*deleteDialogView.findViewById(R.id.dialog_vid_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
                takeVideo();
            }
        });
        deleteDialogView.findViewById(R.id.dialog_vid_gallery).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
                pickVideoSingle();
            }
        });*/
        hideKeyboard();
        alertDialog.show();
    }

    private void sendImages(List<String> filePaths) {
        for (String filePath : filePaths) {
            sendImage(filePath);
        }
    }

    private void sendImage(final String filePath) {
        DisposableSubscriber disposableSubscriber = new DisposableSubscriber<String>() {

            @Override
            public void onNext(String s) {
                System.out.println("URLL>>>" + s);
                Chat chat = new Chat();
                chat.setMessage(s);
                chat.setTime(AppUtils.getCurrentTime());
                chat.setMsgType(AppUtils.MSG_TYPE_IMG);
                chat.setSendStatus(AppUtils.SEND_STATUS_SENT);
                chat.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                chat.setUserName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                chat.setUserPic(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
                chat.setChatId(reference.push().getKey());
                reference.child(chat.getChatId()).setValue(chat);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        };
        Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(final FlowableEmitter<String> e) throws Exception {
                Uri file = Uri.fromFile(new File(filePath));
                final StorageReference riversRef = FirebaseStorage.getInstance().getReference().child("images/" + file.getLastPathSegment());
                final UploadTask uploadTask = riversRef.putFile(file);
                final OnFailureListener failureListener = new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        e.onError(exception);
                        e.onComplete();
                    }
                };
                final OnSuccessListener successListener = new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        e.onNext(downloadUrl.toString());
                        e.onComplete();
                    }
                };
                uploadTask.addOnFailureListener(failureListener)
                        .addOnSuccessListener(successListener);
                e.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        uploadTask.removeOnFailureListener(failureListener);
                        uploadTask.removeOnSuccessListener(successListener);
                    }
                });
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposableSubscriber);
    }

    private boolean checkResultCode(int code) {
        if (code == RxPaparazzo.RESULT_DENIED_PERMISSION) {
            showToast("User Denied Permission");
        } else if (code == RxPaparazzo.RESULT_DENIED_PERMISSION_NEVER_ASK) {
            showToast("User Denied With Never Ask Permission");
        } else if (code != RESULT_OK) {
            showToast("User Cancelled");
        }
        return code == RESULT_OK;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}