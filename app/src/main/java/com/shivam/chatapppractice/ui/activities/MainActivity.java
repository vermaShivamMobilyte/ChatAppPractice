package com.shivam.chatapppractice.ui.activities;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.shivam.chatapppractice.R;
import com.shivam.chatapppractice.ui.fragments.LoginFragment;
import com.shivam.chatapppractice.ui.fragments.UsersListFragment;
import com.shivam.chatapppractice.utils.base.BaseActivity;

import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            replaceFragment(UsersListFragment.newInstance(), false);
        } else {
            replaceFragment(LoginFragment.newInstance(), false);
        }
    }
}
