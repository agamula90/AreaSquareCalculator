package graph.approximation.utils;

import android.util.SparseArray;

import java.util.Collections;
import java.util.List;

public class ReportInput {
    public final float ppm;
    public final String measurementFolder;
    public final SparseArray<List<String>> measurementFiles;
    public final List<List<Float>> squares;
    public final List<Float> ppmData;
    public final List<Float> avgData;
    public final int countMeasurements;
    public final String curveName;
    public final CurveData curveData;

    private ReportInput(float ppm, String measurementFolder, SparseArray<List<String>> measurementFiles, List<List<Float>> squares,
                        List<Float> ppmData, List<Float> avgData, int countMeasurements,
                        String curveName, CurveData curveData) {
        this.ppm = ppm;
        this.measurementFolder = measurementFolder;
        this.measurementFiles = measurementFiles;
        this.squares = squares;
        this.ppmData = ppmData;
        this.avgData = avgData;
        this.countMeasurements = countMeasurements;
        this.curveName = curveName;
        this.curveData = curveData;
    }

    public static class Builder {
        private float ppm;
        private String measurementFolder;
        private SparseArray<List<String>> measurementFiles;
        private List<List<Float>> squares;
        private List<Float> ppmData;
        private List<Float> avgData;
        private int countMeasurements;
        private String curveName;
        private CurveData curveData;

        public Builder setPpm(float ppm) {
            this.ppm = ppm;
            return this;
        }

        public Builder setMeasurementFolder(String measurementFolder) {
            this.measurementFolder = measurementFolder;
            return this;
        }

        public Builder setMeasurementFiles(SparseArray<List<String>> measurementFiles) {
            this.measurementFiles = measurementFiles;
            return this;
        }

        public Builder setMeasurementAverages(List<List<Float>> measurementAverages) {
            this.squares = Collections.unmodifiableList(measurementAverages);
            return this;
        }

        public Builder setCurveName(String curveName) {
            this.curveName = curveName;
            return this;
        }

        public Builder setCurveData(CurveData curveData) {
            this.curveData = curveData;
            return this;
        }

        public Builder setPpmData(List<Float> ppmData) {
            this.ppmData = Collections.unmodifiableList(ppmData);
            return this;
        }

        public Builder setAvgData(List<Float> avgData) {
            this.avgData = Collections.unmodifiableList(avgData);
            return this;
        }

        public Builder setCountMeasurements(int countMeasurements) {
            this.countMeasurements = countMeasurements;
            return this;
        }

        public ReportInput build() {
            return new ReportInput(ppm, measurementFolder, measurementFiles, squares,
                    ppmData, avgData, countMeasurements, curveName, curveData);
        }
    }

    public static class CurveData {
        private boolean connectTo0;
        private CurveType curveType;
        private double regressionR;

        public enum CurveType {
            PointToPoint, BFit
        }

        public CurveData() {

        }

        public void setConnectTo0(boolean connectTo0) {
            this.connectTo0 = connectTo0;
        }

        public void setCurveType(CurveType curveType) {
            this.curveType = curveType;
        }

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
    }
}
