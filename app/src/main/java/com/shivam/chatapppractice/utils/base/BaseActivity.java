package com.shivam.chatapppractice.utils.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.shivam.chatapppractice.R;

/**
 * Created by Shivam on 28-01-2017.
 */

public class BaseActivity extends AppCompatActivity {

    protected final String TAG = "Chat App";
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    protected void hideKeyboard() {
        try {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void replaceFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (addToBackStack) {
            transaction.replace(R.id.activity_container, fragment).addToBackStack(fragment.getClass().getName());
        } else {
            transaction.replace(R.id.activity_container, fragment);
        }
        transaction.commit();
    }

    protected void showSnack(String message) {
        try {
            hideKeyboard();
            Snackbar.make(getCurrentFocus(), message, Snackbar.LENGTH_LONG).show();
        } catch (NullPointerException e) {
            e.printStackTrace();
            showToast(message);
        }
    }

    protected void showSnackWithButton(String message, String button, View.OnClickListener clickListener) {
        try {
            hideKeyboard();
            Snackbar.make(getCurrentFocus(), message, Snackbar.LENGTH_INDEFINITE).setAction(button, clickListener).show();
        } catch (NullPointerException e) {
            e.printStackTrace();
            showToast(message);
        }
    }


}
