package com.termux.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.termux.R;
import com.termux.view.TerminalView;

public class ShellFragment extends Fragment {
    private TerminalView mTerminalView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shell, container, false);
        mTerminalView = view.findViewById(R.id.terminal_view);
        if (getActivity() instanceof com.termux.app.TermuxActivity) {
            ((com.termux.app.TermuxActivity) getActivity()).onTerminalViewCreated(mTerminalView);
        }
        return view;
    }

    public TerminalView getTerminalView() {
        return mTerminalView;
    }
}
