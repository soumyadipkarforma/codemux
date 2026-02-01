package com.termux.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
        if (mCurrentFile == null) return;
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(mCurrentFile));
            bw.write(mEditor.getText().toString());
            bw.close();
            Toast.makeText(getContext(), "Saved " + mCurrentFile.getName(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getContext(), "Save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void openFile(File file) {
        mCurrentFile = file;
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mEditor != null) {
            mEditor.setText(text.toString());
        }
    }
}