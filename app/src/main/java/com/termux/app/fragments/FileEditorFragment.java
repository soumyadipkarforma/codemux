package com.termux.app.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.termux.R;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileEditorFragment extends Fragment {
    private EditText mEditor;
    private File mCurrentFile;
    private UndoRedoHelper mUndoRedoHelper;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_editor, container, false);
        mEditor = view.findViewById(R.id.editor);
        mUndoRedoHelper = new UndoRedoHelper(mEditor);

        view.findViewById(R.id.btn_undo).setOnClickListener(v -> mUndoRedoHelper.undo());
        view.findViewById(R.id.btn_redo).setOnClickListener(v -> mUndoRedoHelper.redo());
        view.findViewById(R.id.btn_save).setOnClickListener(v -> saveFile());

        return view;
    }

    private void saveFile() {
        if (mCurrentFile == null || mEditor == null) return;
        final String content = mEditor.getText().toString();
        new Thread(() -> {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(mCurrentFile))) {
                bw.write(content);
                mHandler.post(() -> {
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Saved " + mCurrentFile.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                mHandler.post(() -> {
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    public void openFile(File file) {
        mCurrentFile = file;
        new Thread(() -> {
            StringBuilder text = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    text.append(line).append('\n');
                }
                mHandler.post(() -> {
                    if (mEditor != null) {
                        mEditor.setText(text.toString());
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}