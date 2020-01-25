package com.example.simplecopy.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.simplecopy.ui.fragment.MainNumbers.Numbers.NumberListFragment;
import com.example.simplecopy.ui.fragment.MainNumbers.Daily.DailyWalletListFragment;

import java.util.ArrayList;
import java.util.List;

public class viewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> fragmentList = new ArrayList<> ();
    private final List<String> FragmentListTitles = new ArrayList<> ();

    public viewPagerAdapter(@NonNull FragmentManager fm) {
        super (fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (fragmentList.get (position).equals (0) )return new NumberListFragment ();
        if (fragmentList.get (position).equals (1) )return new DailyWalletListFragment ();
        return fragmentList.get (position);
    }

    @Override
    public int getCount() {
        return fragmentList.size ();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return FragmentListTitles.get (position);
    }

    public void AddFragment (Fragment fragment, String Title){
        fragmentList.add (fragment);
        FragmentListTitles.add (Title);
    }
}
