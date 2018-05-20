package graph.approximation;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.proggroup.areasquarecalculator.R;
import com.proggroup.areasquarecalculator.databinding.FileDialogMainNewBinding;

import java.io.File;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FileDialogMainNewBinding binding = FileDialogMainNewBinding.inflate(getLayoutInflater());
        FileViewModel model = new FileViewModel(Environment.getExternalStorageDirectory(), FileViewModel.SelectionMode.OPEN);
        model.setCanSelectDir(true);
        model.setChooseFileOrFolder(true);
        binding.setFile(model);

        final AlertDialog dialog = new AlertDialog.Builder(this, R.style.DialogTheme).setView(binding.getRoot())
                .setCancelable(false)
                .show();

        binding.setCallback(new SelectFileCallback() {
            @Override
            public void onFileSelected(File file) {
                if (file != null) {
                    Log.e(TAG, "File selected: " + file.getName());
                }
                dialog.dismiss();
            }
        });
    }
}
