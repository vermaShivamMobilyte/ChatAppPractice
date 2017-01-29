package com.shivam.chatapppractice.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shivam.chatapppractice.R;
import com.shivam.chatapppractice.model.RxFireBaseChildEvent;
import com.shivam.chatapppractice.model.User;
import com.shivam.chatapppractice.ui.adapters.UsersListAdapter;
import com.shivam.chatapppractice.utils.AppUtils;
import com.shivam.chatapppractice.utils.base.BaseActivity;
import com.shivam.chatapppractice.utils.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Cancellable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UsersListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersListFragment extends BaseFragment {

    @BindView(R.id.rcyle_users_list)
    RecyclerView mUsersList;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private UsersListAdapter mUsersListAdapter;

    public UsersListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UsersListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UsersListFragment newInstance() {
        UsersListFragment fragment = new UsersListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mUsersList.setLayoutManager(new LinearLayoutManager(getContext()));
        mUsersListAdapter = new UsersListAdapter(getContext());
        mUsersList.setAdapter(mUsersListAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((BaseActivity) getActivity()).setSupportActionBar(mToolbar);
        ((BaseActivity) getActivity()).getSupportActionBar().setTitle("Users List");
        ((BaseActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((BaseActivity) getActivity()).getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        final DatabaseReference userReference = FirebaseDatabase.getInstance().getReference()
                .child(AppUtils.APP_NAME)
                .child(AppUtils.TABLE_USERS);
        DisposableSubscriber disposableSubscriber = new DisposableSubscriber<RxFireBaseChildEvent<User>>() {
            @Override
            public void onNext(RxFireBaseChildEvent<User> userRxFireBaseChildEvent) {
                switch (userRxFireBaseChildEvent.getEventType()) {
                    case ADDED:
                        mUsersListAdapter.addItem(userRxFireBaseChildEvent.getValue());
                        break;
                }
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        };
        Flowable.create(new FlowableOnSubscribe<RxFireBaseChildEvent<User>>() {
            @Override
            public void subscribe(final FlowableEmitter<RxFireBaseChildEvent<User>> emitter) throws Exception {
                final ChildEventListener childEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (!dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            emitter.onNext(new RxFireBaseChildEvent<User>(dataSnapshot.getKey(), dataSnapshot.getValue(User.class), s, RxFireBaseChildEvent.EventType.ADDED));
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        if (!dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            emitter.onNext(new RxFireBaseChildEvent<User>(dataSnapshot.getKey(), dataSnapshot.getValue(User.class), s, RxFireBaseChildEvent.EventType.CHANGED));
                        }
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                            emitter.onNext(new RxFireBaseChildEvent<User>(dataSnapshot.getKey(), dataSnapshot.getValue(User.class), RxFireBaseChildEvent.EventType.REMOVED));
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        if (!dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                            emitter.onNext(new RxFireBaseChildEvent<User>(dataSnapshot.getKey(), dataSnapshot.getValue(User.class), s, RxFireBaseChildEvent.EventType.MOVED));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        emitter.onError(databaseError.toException());
                    }
                };
                userReference.addChildEventListener(childEventListener);
                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        userReference.removeEventListener(childEventListener);
                    }
                });
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposableSubscriber);
    }
}
