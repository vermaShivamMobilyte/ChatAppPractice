package com.shivam.chatapppractice.utils.base;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

/**
 * Created by ripan on 17/01/17.
 */

public class BaseFragment extends Fragment {

    protected final String TAG = "Chat App";

    protected void hideKeyboard() {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).hideKeyboard();
        }
    }

    protected void showToast(String message) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).showToast(message);
        }
    }

    protected void showSnack(String message) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).showSnack(message);
        }
    }

    protected void showSnackWithButton(String message, String button, View.OnClickListener clickListener) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).showSnackWithButton(message, button, clickListener);
        }
    }

    protected void printLog(String msg) {
        Log.e("app", msg);
    }

    protected void replaceFragment(Fragment fragment, boolean addToBackStack) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).replaceFragment(fragment, addToBackStack);
        }
    }

    protected void showProgressDialog() {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).showProgressDialog();
        }
    }

    protected void hideProgressDialog() {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).hideProgressDialog();
        }
    }

}

