package com.termux.app.fragments;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import java.util.Stack;

public class UndoRedoHelper {
    private static final int MAX_STACK_SIZE = 50;
    private Stack<String> undoStack = new Stack<>();
    private Stack<String> redoStack = new Stack<>();
    private EditText editText;
    private boolean isIterating = false;

    public UndoRedoHelper(EditText editText) {
        this.editText = editText;
        this.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!isIterating) {
                    String newText = s.toString();
                    if (undoStack.isEmpty() || !newText.equals(undoStack.peek())) {
                        undoStack.push(newText);
                        if (undoStack.size() > MAX_STACK_SIZE) {
                            undoStack.remove(0);
                        }
                        redoStack.clear();
                    }
                }
            }
        });
        undoStack.push("");
    }

    public void undo() {
        if (undoStack.size() > 1) {
            isIterating = true;
            redoStack.push(undoStack.pop());
            editText.setText(undoStack.peek());
            editText.setSelection(editText.getText().length());
            isIterating = false;
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            isIterating = true;
            String text = redoStack.pop();
            editText.setText(text);
            editText.setSelection(editText.getText().length());
            undoStack.push(text);
            isIterating = false;
        }
    }
}
