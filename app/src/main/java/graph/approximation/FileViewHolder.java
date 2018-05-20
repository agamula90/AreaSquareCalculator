package graph.approximation;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v7.widget.RecyclerView;

import com.proggroup.areasquarecalculator.R;
import com.proggroup.areasquarecalculator.databinding.FileDialogRowNewBinding;

public class FileViewHolder extends RecyclerView.ViewHolder {

    private FileDialogRowNewBinding binding;

    FileViewHolder(FileDialogRowNewBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    void setFile(String fileName, boolean isDirectory, boolean isSelected) {
        final int imageResource;

        if (isDirectory) {
            imageResource = R.drawable.folder;
        } else {
            imageResource = R.drawable.file;
        }
        binding.setFileItem(new FileItemViewModel(fileName, imageResource, isSelected));
    }

    void unbind() {
        binding.unbind();
    }

    public static class FileItemViewModel extends BaseObservable{
        private String filePath;
        private int imageResource;
        private boolean selected;

        FileItemViewModel(String filePath, int imageResource, boolean isSelected) {
            this.filePath = filePath;
            this.imageResource = imageResource;
            this.selected = isSelected;
        }

        @Bindable
        public String getFilePath() {
            return filePath;
        }

        @Bindable
        public int getImageResource() {
            return imageResource;
        }

        @Bindable
        public boolean isSelected() {
            return selected;
        }
    }
}
