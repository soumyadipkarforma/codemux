package com.termux.app.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class IDEPagerAdapter extends FragmentStateAdapter {

    public IDEPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new FileManagerFragment();
            case 1: return new FileEditorFragment();
            case 2: return new ShellFragment();
            default: return new ShellFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
