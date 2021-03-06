package com.proggroup.areasquarecalculator.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.proggroup.areasquarecalculator.BaseLoadTask;
import com.proggroup.areasquarecalculator.data.Constants;
import com.proggroup.areasquarecalculator.utils.AutoCalculations;
import com.proggroup.areasquarecalculator.utils.CurveHelper;
import com.proggroup.squarecalculations.CalculateUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateCalibrationCurveForAutoTask extends AsyncTask<File, Integer, File> {

    private SparseArray<List<File>> mCurveFiles;
    private final BaseLoadTask task;
    private final Context context;
    private ProgressBar progressBar;
    private final boolean is0Connect;
    private CurveHelper curveHelper;

    public CreateCalibrationCurveForAutoTask(BaseLoadTask task, Context context, boolean
            is0Connect) {
        this.task = task;
        this.context = context;
        this.is0Connect = is0Connect;
        this.curveHelper = new CurveHelper();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (task != null && task instanceof AutoCalculations.LoadPpmAvgValuesTask) {
            AutoCalculations.LoadPpmAvgValuesTask autoTask =
                    ((AutoCalculations.LoadPpmAvgValuesTask) task);
            FrameLayout frameLayout = autoTask.getFrameLayout();
            progressBar = new ProgressBar(frameLayout.getContext(), null,
                    android.R.attr.progressBarStyleHorizontal);
            progressBar.setIndeterminate(false);
            progressBar.setMax(100);
            progressBar.setProgress(0);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(300, ViewGroup
                    .LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            frameLayout.addView(progressBar, params);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (progressBar != null) {
            progressBar.setProgress(values[0]);
        }
    }

    @Override
    protected File doInBackground(File... params) {
        if (params.length != 1) {
            return null;
        }

        mCurveFiles = new SparseArray<>();

        File folderWithCalibrationFiles = params[0];
        File filesInside[] = folderWithCalibrationFiles.listFiles();
        File folderWithCurve = null;
        List<File> ppmFiles = new ArrayList<>(filesInside.length);
        for (File file : filesInside) {
            if (file.isDirectory()) {
                if (file.getName().contains(Constants.CALIBRATION_CURVE_NAME)) {
                    folderWithCurve = file;
                }
            } else {
                int index = detectPpmFromName(file);
                if(index != -1) {
                    if(mCurveFiles.get(index) == null) {
                        mCurveFiles.put(index, new ArrayList<File>());
                    }
                    mCurveFiles.get(index).add(file);
                }
                ppmFiles.add(file);
            }
        }

        if (folderWithCurve != null) {
            filesInside = folderWithCurve.listFiles();
            if (filesInside != null && filesInside.length != 0) {
                File newestFile = null;
                for (File f : filesInside) {
                    if (!f.isDirectory()) {
                        if (newestFile == null) {
                            newestFile = f;
                        } else if (f.lastModified() > newestFile.lastModified()) {
                            newestFile = f;
                        }
                    }
                }

                if(newestFile != null) {
                    publishProgress(100);
                    return newestFile;
                }
            } else {
                deleteAllInside(folderWithCurve);
            }
        } else {
            folderWithCurve = new File(folderWithCalibrationFiles, Constants
                    .CALIBRATION_CURVE_NAME);
        }

        folderWithCurve.mkdir();

        int countFilesForProcess = 0;

        for (int i = 0; i < mCurveFiles.size(); i++) {
            countFilesForProcess += mCurveFiles.valueAt(i).size();
        }

        List<Float> ppmValues = new ArrayList<>();
        List<List<Float>> averageValues = new ArrayList<>();

        int countOperationsProcessed = 0;

        for (int i = 0; i < mCurveFiles.size(); i++) {
            int ppmValue = mCurveFiles.keyAt(i);
            ppmValues.add((float)ppmValue);

            List<Float> curveSquares = new ArrayList<>();
            averageValues.add(curveSquares);

            List<File> curveFiles = mCurveFiles.valueAt(i);
            for (File file : curveFiles) {
                float square = CalculateUtils.calculateSquare(file);
                if(square < 0f) {
                    publishProgress(100);
                    return null;
                }
                curveSquares.add(square);

                countOperationsProcessed++;

                if (progressBar != null) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                publishProgress((int) ((countOperationsProcessed / (float) countFilesForProcess) *
                        100));
            }
        }

        File tableFile = new File(folderWithCurve, Constants.CALIBRATION_CURVE_NAME + "_" +
                generateDate() + ".csv");
        try {
            tableFile.createNewFile();
            curveHelper.saveCurve(ppmValues, averageValues, tableFile
                    .getAbsolutePath(), is0Connect);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return tableFile;
    }

    private String generateDate() {
        Calendar calendar = Calendar.getInstance();
        StringBuilder builder = new StringBuilder();
        builder.append(calendar.get(Calendar.YEAR));
        builder.append(normalizeValue(calendar.get(Calendar.MONTH) + 1));
        builder.append(normalizeValue(calendar.get(Calendar.DAY_OF_MONTH)));
        builder.append("_");
        builder.append(normalizeValue(calendar.get(Calendar.HOUR_OF_DAY)));
        builder.append(normalizeValue(calendar.get(Calendar.MINUTE)));
        builder.append(normalizeValue(calendar.get(Calendar.SECOND)));
        return builder.toString();
    }

    private String normalizeValue(int value) {
        if(value > 99 || value < 0) {
            throw new IllegalArgumentException();
        }
        StringBuilder builder = new StringBuilder();
        if(value < 10) {
            builder.append(0);
        }
        builder.append(value);
        return builder.toString();
    }

    private int detectPpmFromName(File file) {
        String fileName = file.getName();
        String revisionPrefix = "_R";
        int index = fileName.lastIndexOf(revisionPrefix);
        if (index == -1) {
            return -1;
        }

        String endString = fileName.substring(index + revisionPrefix.length());
        String csvPostfix = ".csv";
        if (endString.indexOf(csvPostfix) == -1) {
            return -1;
        }

        try {
            Integer.parseInt(endString.substring(0, endString.indexOf(csvPostfix)));
        } catch (NumberFormatException e) {
            return -1;
        }
        fileName = fileName.substring(0, index);
        index = fileName.lastIndexOf('_');
        if(index == -1) {
            return -1;
        }

        try {
            return Integer.parseInt(fileName.substring(index + 1));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void deleteAllInside(File root) {
        if (!root.isDirectory()) {
            root.delete();
        } else for (File file : root.listFiles()) {
            deleteAllInside(file);
        }
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        if(file != null) {
            if (progressBar != null) {
                ((AutoCalculations.LoadPpmAvgValuesTask) task).setProgressBar(progressBar);
            }

            task.setUrl(file.getAbsolutePath());
            task.execute();
        } else {
            Toast.makeText(context, "There is no curve values", Toast.LENGTH_LONG).show();
        }
    }
}
