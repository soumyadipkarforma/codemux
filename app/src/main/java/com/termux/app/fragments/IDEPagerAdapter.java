package com.termux.app.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class IDEPagerAdapter extends FragmentStateAdapter {
    public FileManagerFragment fileManagerFragment;
    public FileEditorFragment fileEditorFragment;
    public ShellFragment shellFragment;

    public IDEPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        fileManagerFragment = new FileManagerFragment();
        fileEditorFragment = new FileEditorFragment();
        shellFragment = new ShellFragment();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return fileManagerFragment;
            case 1: return fileEditorFragment;
            case 2: return shellFragment;
            default: return shellFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
