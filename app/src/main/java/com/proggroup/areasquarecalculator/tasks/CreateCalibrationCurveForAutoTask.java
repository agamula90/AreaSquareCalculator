package com.proggroup.areasquarecalculator.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.widget.Toast;

import com.proggroup.areasquarecalculator.data.Constants;
import com.proggroup.areasquarecalculator.fragments.CalculatePpmSimpleFragment;
import com.proggroup.areasquarecalculator.utils.CalculatePpmUtils;
import com.proggroup.squarecalculations.CalculateUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateCalibrationCurveForAutoTask extends AsyncTask<File, Integer, File> {

    private SparseArray<List<File>> mCurveFiles;
    private final CalculatePpmSimpleFragment.LoadPpmAvgValuesTask task;
    private final Context context;

    public CreateCalibrationCurveForAutoTask(CalculatePpmSimpleFragment.LoadPpmAvgValuesTask
                                                     task, Context context) {
        this.task = task;
        this.context = context;
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
                if (file.getName().equals(Constants.CALIBRATION_CURVE_NAME)) {
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

                publishProgress((int) ((countOperationsProcessed / (float) countFilesForProcess) *
                        100));
            }
        }

        File tableFile = new File(folderWithCurve, "calibration_curve.csv");
        try {
            tableFile.createNewFile();
            CalculatePpmUtils.saveAvgValuesToFile(ppmValues, averageValues, tableFile.getAbsolutePath(), false);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return tableFile;
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
            task.setUrl(file.getAbsolutePath());
            task.execute();
        } else {
            Toast.makeText(context, "There is no curve values", Toast.LENGTH_LONG).show();
        }
    }
}