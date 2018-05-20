package graph.approximation;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.proggroup.areasquarecalculator.BR;
import com.proggroup.areasquarecalculator.R;

import java.io.File;
import java.io.IOException;

public class FileViewModel extends BaseObservable implements FileAdapter.OnClickListener{
    private static final String TAG = FileViewModel.class.getSimpleName();

    /**
     * Root directory to open file view from.
     */
    private final File root;

    /**
     * Name for new file to being created.
     */
    private String newFileName;

    /**
     * Can be CREATE or OPEN
     */
    private SelectionMode selectionMode;

    /**
     * Can select dir or only file. File will be highlighted by adapter in list.
     */
    private boolean canSelectDir;

    /**
     * Directory, which subdirectories are showing to user.
     */
    private File currentDirectory;

    /**
     * Show creating screen or open screen.
     */
    private boolean isCreating;

    /**
     * Format filters. Support filtering by extensions.
     * E.g. {"csv", "txt"} will filter all csv and txt files only.
     */
    private String formatFilters[];

    /**
     * Adapter to display list of files.
     */
    private FileAdapter adapter;

    /**
     * True, if folder can be chosen to be returned from getSelectedFile().
     * Otherwise false.
     */
    private boolean chooseFileOrFolder;

    public FileViewModel(SelectionMode selectionMode) {
        this(new File("/"), selectionMode);
    }

    public FileViewModel(File root, SelectionMode selectionMode) {
        this(root, selectionMode, new String[0]);
    }

    public FileViewModel(File root, SelectionMode selectionMode, String formatFilters[]) {
        this.root = root;
        this.canSelectDir = false;
        this.currentDirectory = root;
        this.selectionMode = selectionMode;
        this.formatFilters = formatFilters;
        changeAdapter(currentDirectory);
    }

    public void setChooseFileOrFolder(boolean chooseFileOrFolder) {
        this.chooseFileOrFolder = chooseFileOrFolder;
    }

    public void setCanSelectDir(boolean canSelectDir) {
        this.canSelectDir = canSelectDir;
    }

    public void createFile() {
        if (!newFileName.isEmpty()) {
            try {
                new File(currentDirectory, newFileName).createNewFile();
            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
            }
        }
    }

    public void showCreateScreen() {
        isCreating = true;
        notifyPropertyChanged(BR.creating);
        newFileName = "";
        notifyPropertyChanged(BR.newFileName);
    }

    public void hideCreateScreen() {
        isCreating = false;
        notifyPropertyChanged(BR.creating);
    }

    @Bindable
    public boolean isCreating() {
        return isCreating;
    }

    @Bindable
    public boolean isCanCreateFile() {
        return selectionMode == SelectionMode.CREATE;
    }

    @Bindable
    public boolean isCanSelectDir() {
        return canSelectDir;
    }

    @Bindable
    public String getNewFileName() {
        return newFileName;
    }

    public void setNewFileName(String newFileName) {
        this.newFileName = newFileName;
    }

    @Bindable
    public <T extends RecyclerView.Adapter<? extends RecyclerView.ViewHolder>> T getAdapter() {
        return (T) adapter;
    }

    @Bindable
    public File getSelectedFile() {
        return adapter.getSelectedFile();
    }

    @Bindable
    public File getCurrentDirectory() {
        return currentDirectory;
    }

    @Override
    public void onClick(Context context, File file, boolean isRootItem) {
        if (!file.isDirectory()) {
            adapter.setSelectedFile(file);
            notifyPropertyChanged(BR.selectedFile);
            return;
        }

        currentDirectory = file;
        notifyPropertyChanged(BR.currentDirectory);

        if (!isRootItem) {
            if (chooseFileOrFolder) {
                String[] chooseFileNames = new String[]{"CAL_FILES",
                        "MES_Files"};
                if (file.getParentFile() != null) {
                    for (String fileName : chooseFileNames) {
                        if (file.getParentFile().getName().endsWith(fileName)) {
                            showSelectOption(context, file);
                            return;
                        }
                    }
                }
            }
        }

        if (canSelectDir) {
            adapter.setSelectedFile(file);
            notifyPropertyChanged(BR.selectedFile);
        }

        changeAdapter(file);
    }

    private void changeAdapter(File parent) {
        adapter = new FileAdapter(root, parent, formatFilters, this);
        notifyPropertyChanged(BR.adapter);
    }

    private void showSelectOption(Context context, final File file) {
        android.support.v7.app.AlertDialog dialog = new android.support.v7.app
                .AlertDialog.Builder(context).setNeutralButton(R.string.select_folder_for_auto, new DialogInterface
                .OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapter.setSelectedFile(file);
                notifyPropertyChanged(BR.selectedFile);
                dialog.dismiss();
            }
        }).setPositiveButton(R.string.select_file_for_calculations, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeAdapter(file);
                dialog.dismiss();
            }
        }).setMessage(R.string.select_option_to_continue)
                .create();
        dialog.show();
        View decorView = dialog.getWindow().getDecorView();
        ((TextView)decorView.findViewById(android.R.id.message)).setGravity(Gravity
                .CENTER);

        Button button3 = (Button) decorView.findViewById(android.R.id.button3);
        button3.setTextColor(Color.BLACK);
        button3.setBackgroundResource(R.drawable.button_drawable);

        Button button1 = ((Button) decorView.findViewById(android.R.id.button1));
        button1.setTextColor(Color.BLACK);
        button1.setBackgroundResource(R.drawable.button_drawable);
    }

    public enum SelectionMode {
        CREATE, OPEN
    }
}
