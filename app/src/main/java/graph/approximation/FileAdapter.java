package graph.approximation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.proggroup.areasquarecalculator.databinding.FileDialogRowNewBinding;

import java.io.File;
import java.io.FilenameFilter;

public class FileAdapter extends RecyclerView.Adapter<FileViewHolder> {
    private static final String TAG = FileAdapter.class.getSimpleName();

    private File rootDirectory;
    private File files[];
    private OnClickListener onClickListener;
    private boolean addRootItem;
    private File selectedFile;
    private File parent;

    FileAdapter(File rootDirectory, File parent, final String formatFilters[], OnClickListener onClickListener) {
        this.rootDirectory = rootDirectory;
        this.parent = parent;
        this.files = parent.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (formatFilters.length == 0) {
                    return true;
                }

                boolean res = false;
                for (String formatFilter : formatFilters) {
                    if (filename.endsWith(formatFilter)) {
                        res = true;
                        break;
                    }
                }

                return res;
            }
        });
        this.onClickListener = onClickListener;

        addRootItem = !rootDirectory.equals(parent);
    }

    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        final FileViewHolder holder = new FileViewHolder(FileDialogRowNewBinding.inflate(inflater, parent, false));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();

                if (onClickListener != null) {
                    onClickListener.onClick(context, getFile(pos), addRootItem && pos < 2);
                } else {
                    Log.e(TAG, "Click listener null");
                }
            }
        });

        return holder;
    }

    @Override
    public void onViewDetachedFromWindow(FileViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.unbind();
    }

    @Override
    public void onBindViewHolder(FileViewHolder holder, int position) {
        if (!addRootItem) {
            File file = files[position];
            holder.setFile(file.getName(), file.isDirectory(), file.equals(selectedFile));
            return;
        }

        if (position == 0) {
            holder.setFile(rootDirectory.getName(), true, rootDirectory.equals(selectedFile));
        } else if (position == 1) {
            holder.setFile("../", true, parent.equals(selectedFile));
        } else {
            File file = files[position - 2];
            holder.setFile(file.getName(), file.isDirectory(), file.equals(selectedFile));
        }
    }

    public void setSelectedFile(File selectedFile) {
        this.selectedFile = selectedFile;

        int index = -1;

        for (int i = 0; i < files.length; i++) {
            if (files[i].equals(selectedFile)) {
                index = i + (addRootItem ? 2 : 0);
                break;
            }
        }

        if (index != -1) {
            notifyItemChanged(index);
            return;
        }

        if (addRootItem) {
            if (selectedFile.equals(parent)) {
                notifyItemChanged(1);
            } else if (selectedFile.equals(rootDirectory)) {
                notifyItemChanged(0);
            } else {
                Log.e(TAG, "File not found");
            }
        } else {
            Log.e(TAG, "File not found");
        }
    }

    File getSelectedFile() {
        return selectedFile;
    }

    @Override
    public int getItemCount() {
        return (addRootItem ? 2 : 0) + files.length;
    }

    private File getFile(int position) {
        if (!addRootItem) {
            return files[position];
        }

        if (position == 0) {
            return rootDirectory;
        }

        if (position == 1) {
            return parent.getParentFile();
        }

        return files[position - 2];
    }

    interface OnClickListener {
        void onClick(Context context, File file, boolean isRootItem);
    }
}
