package com.termux.app.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.termux.R;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileManagerFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private FileAdapter mAdapter;
    private File mCurrentDir;
    private File mWorkspaceRoot;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_manager, container, false);
        mRecyclerView = view.findViewById(R.id.file_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        mWorkspaceRoot = new File(Environment.getExternalStorageDirectory(), "sworkspace");
        if (!mWorkspaceRoot.exists()) {
            mWorkspaceRoot.mkdirs();
        }
        mCurrentDir = mWorkspaceRoot;

        mAdapter = new FileAdapter(new ArrayList<>());
        mRecyclerView.setAdapter(mAdapter);

        loadFiles();

        view.findViewById(R.id.fab_plus).setOnClickListener(v -> showPlusMenu());

        return view;
    }

    private void loadFiles() {
        File[] files = mCurrentDir.listFiles();
        List<File> fileList = new ArrayList<>();
        if (files != null) {
            fileList.addAll(Arrays.asList(files));
        }
        // Sort files: directories first, then alphabetical
        fileList.sort((f1, f2) -> {
            if (f1.isDirectory() && !f2.isDirectory()) return -1;
            if (!f1.isDirectory() && f2.isDirectory()) return 1;
            return f1.getName().compareToIgnoreCase(f2.getName());
        });
        mAdapter.setFiles(fileList);

        if (getActivity() instanceof com.termux.app.TermuxActivity) {
            ((com.termux.app.TermuxActivity) getActivity()).onDirectoryChanged(mCurrentDir);
        }
    }

    private void showPlusMenu() {
        PopupMenu popup = new PopupMenu(getContext(), getView().findViewById(R.id.fab_plus));
        boolean isRoot = mCurrentDir.equals(mWorkspaceRoot);
        
        popup.getMenu().add(isRoot ? "Create Project" : "Create Folder");
        popup.getMenu().add("Create File");
        popup.getMenu().add("Import File");
        popup.getMenu().add("Import Folder");

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getTitle().toString()) {
                case "Create Project":
                case "Create Folder":
                    createFolder();
                    return true;
                case "Create File":
                    createFile();
                    return true;
                case "Import File":
                    // TODO: Implement import
                    return true;
                case "Import Folder":
                    // TODO: Implement import
                    return true;
            }
            return false;
        });
        popup.show();
    }

    private void createFolder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("New Folder");
        final EditText input = new EditText(getContext());
        builder.setView(input);
        builder.setPositiveButton("OK", (dialog, which) -> {
            File newDir = new File(mCurrentDir, input.getText().toString());
            if (newDir.mkdirs()) {
                loadFiles();
            } else {
                Toast.makeText(getContext(), "Failed to create folder", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void createFile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("New File");
        final EditText input = new EditText(getContext());
        builder.setView(input);
        builder.setPositiveButton("OK", (dialog, which) -> {
            File newFile = new File(mCurrentDir, input.getText().toString());
            try {
                if (newFile.createNewFile()) {
                    loadFiles();
                } else {
                    Toast.makeText(getContext(), "Failed to create file", Toast.LENGTH_SHORT).show();
                }
            } catch (java.io.IOException e) {
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private class FileAdapter extends RecyclerView.Adapter<FileViewHolder> {
        private List<File> mFiles;

        public FileAdapter(List<File> files) {
            mFiles = files;
        }

        public void setFiles(List<File> files) {
            mFiles = files;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new FileViewHolder(view);
        }

import android.widget.PopupMenu;
import android.app.AlertDialog;
import android.widget.EditText;

...

        @Override
        public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
            File file = mFiles.get(position);
            holder.mTextView.setText(file.isDirectory() ? "[DIR] " + file.getName() : file.getName());
            holder.itemView.setOnClickListener(v -> {
                if (file.isDirectory()) {
                    mCurrentDir = file;
                    loadFiles();
                } else {
                    if (getActivity() instanceof com.termux.app.TermuxActivity) {
                        ((com.termux.app.TermuxActivity) getActivity()).openFileInEditor(file);
                    }
                }
            });
            holder.itemView.setOnLongClickListener(v -> {
                showContextMenu(v, file);
                return true;
            });
        }

    private void showContextMenu(View v, File file) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        popup.getMenu().add("Rename");
        popup.getMenu().add("Delete");
        popup.getMenu().add("Compress to ZIP");
        popup.getMenu().add("Compress to GZIP");
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getTitle().toString()) {
                case "Rename": renameFile(file); return true;
                case "Delete": deleteFile(file); return true;
                case "Compress to ZIP": compressToZip(file); return true;
                case "Compress to GZIP": compressToGzip(file); return true;
            }
            return false;
        });
        popup.show();
    }

    private void renameFile(File file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Rename");
        final EditText input = new EditText(getContext());
        input.setText(file.getName());
        builder.setView(input);
        builder.setPositiveButton("OK", (dialog, which) -> {
            File newFile = new File(file.getParent(), input.getText().toString());
            if (file.renameTo(newFile)) {
                loadFiles();
            } else {
                Toast.makeText(getContext(), "Rename failed", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void deleteFile(File file) {
        new AlertDialog.Builder(getContext())
            .setTitle("Delete")
            .setMessage("Are you sure you want to delete " + file.getName() + "?")
            .setPositiveButton("Yes", (dialog, which) -> {
                if (deleteRecursive(file)) {
                    loadFiles();
                } else {
                    Toast.makeText(getContext(), "Delete failed", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("No", null)
            .show();
    }

    private boolean deleteRecursive(File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteRecursive(child);
            }
        }
        return file.delete();
    }

    private void compressToZip(File file) {
        File zipFile = new File(file.getParent(), file.getName() + ".zip");
        try (java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(new java.io.FileOutputStream(zipFile))) {
            zipRecursive(file, file.getName(), zos);
            loadFiles();
            Toast.makeText(getContext(), "Compressed to " + zipFile.getName(), Toast.LENGTH_SHORT).show();
        } catch (java.io.IOException e) {
            Toast.makeText(getContext(), "Compression failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void zipRecursive(File file, String path, java.util.zip.ZipOutputStream zos) throws java.io.IOException {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                zipRecursive(child, path + "/" + child.getName(), zos);
            }
        } else {
            zos.putNextEntry(new java.util.zip.ZipEntry(path));
            try (java.io.FileInputStream fis = new java.io.FileInputStream(file)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
            }
            zos.closeEntry();
        }
    }

    private void compressToGzip(File file) {
        if (file.isDirectory()) {
            Toast.makeText(getContext(), "GZIP only supports single files", Toast.LENGTH_SHORT).show();
            return;
        }
        File gzipFile = new File(file.getParent(), file.getName() + ".gz");
        try (java.util.zip.GZIPOutputStream gzos = new java.util.zip.GZIPOutputStream(new java.io.FileOutputStream(gzipFile));
             java.io.FileInputStream fis = new java.io.FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                gzos.write(buffer, 0, len);
            }
            loadFiles();
            Toast.makeText(getContext(), "Compressed to " + gzipFile.getName(), Toast.LENGTH_SHORT).show();
        } catch (java.io.IOException e) {
            Toast.makeText(getContext(), "Compression failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

        @Override
        public int getItemCount() {
            return mFiles.size();
        }
    }

    private class FileViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(android.R.id.text1);
        }
    }
}
