package com.example.simplecopy.ui.fragment;

import androidx.fragment.app.Fragment;

import com.example.simplecopy.ui.activity.BaseActivity;


public class BaseFragment extends Fragment {

    private BaseActivity baseActivity;

    public void initFragment(){
        baseActivity = (BaseActivity) getActivity();
        assert baseActivity != null;
        baseActivity.baseFragment = this;
    }

    public void onBack(){
        baseActivity.superBackPressed();
    }


}
