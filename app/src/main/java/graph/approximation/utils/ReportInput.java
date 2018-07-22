package graph.approximation.utils;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class ReportInput {
    public final float ppm;
    public final String measurementFolder;
    public final File[][] measurementFiles;
    public final List<List<Float>> squares;
    public final File curveFile;
    public final CurveData curveData;

    private ReportInput(float ppm, String measurementFolder, File[][] measurementFiles, List<List<Float>> squares,
                        File curveFile, CurveData curveData) {
        this.ppm = ppm;
        this.measurementFolder = measurementFolder;
        this.measurementFiles = measurementFiles;
        this.squares = squares;
        this.curveFile = curveFile;
        this.curveData = curveData;
    }

    public static class Builder {
        private float ppm;
        private String measurementFolder;
        private File[][] measurementFiles;
        private List<List<Float>> squares;
        private File curveFile;
        private CurveData curveData;

        //view info, from "avg square value" TextView, after "Mes av. Cal"
        public Builder setPpm(float ppm) {
            this.ppm = ppm;
            return this;
        }

        //model data, name of parent File of first measurement file.
        public Builder setMeasurementFolder(String measurementFolder) {
            this.measurementFolder = measurementFolder;
            return this;
        }

        //model data, retrieved from table
        public Builder setMeasurementFiles(File[][] measurementFiles) {
            this.measurementFiles = measurementFiles;
            return this;
        }

        //model data, retrieved from table. (Squares, calculated based on Files from table)
        public Builder setMeasurementAverages(List<List<Float>> measurementAverages) {
            this.squares = Collections.unmodifiableList(measurementAverages);
            return this;
        }

        //model data, get from "load ppm curve" File
        public Builder setCurveFile(File curveFile) {
            this.curveFile = curveFile;
            return this;
        }

        public Builder setCurveData(CurveData curveData) {
            this.curveData = curveData;
            return this;
        }

        public ReportInput build() {
            return new ReportInput(ppm, measurementFolder, measurementFiles, squares, curveFile, curveData);
        }
    }

    public static class CurveData {
        private boolean connectTo0;
        private CurveType curveType;
        private double regressionR;
        private List<Float> ppmData, avgData;

        public enum CurveType {
            PointToPoint, BFit
        }

        public CurveData(List<Float> ppmData, List<Float> avgData) {
            this.ppmData = ppmData;
            this.avgData = avgData;
        }

        // view info
        public void setConnectTo0(boolean connectTo0) {
            this.connectTo0 = connectTo0;
        }

        //view info
        public void setCurveType(CurveType curveType) {
            this.curveType = curveType;
        }

        //based on ppm / avgsquare values, retrieved from "load curve" or "mes av. cal" buttons
        public void setRegressionR(double regressionR) {
            this.regressionR = regressionR;
        }

        public CurveType getCurveType() {
            return curveType;
        }

        public double getRegressionR() {
            return regressionR;
        }

        public boolean isConnectTo0() {
            return connectTo0;
        }

        public List<Float> getPpmData() {
            return ppmData;
        }

        public List<Float> getAvgData() {
            return avgData;
        }
    }
}
