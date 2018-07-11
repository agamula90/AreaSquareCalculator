package graph.approximation.utils;

import android.util.SparseArray;

import java.util.Collections;
import java.util.List;

public class ReportInput {
    public final float ppm;
    public final String measurementFolder;
    public final SparseArray<List<String>> measurementFiles;
    public final List<List<Float>> squares;
    public final String calibrationCurveFolder;
    public final List<Float> ppmData;
    public final List<Float> avgData;
    public final int countMeasurements;

    private ReportInput(float ppm, String measurementFolder, SparseArray<List<String>> measurementFiles, List<List<Float>> squares,
                        String calibrationCurveFolder, List<Float> ppmData, List<Float> avgData, int countMeasurements) {
        this.ppm = ppm;
        this.measurementFolder = measurementFolder;
        this.measurementFiles = measurementFiles;
        this.squares = squares;
        this.calibrationCurveFolder = calibrationCurveFolder;
        this.ppmData = ppmData;
        this.avgData = avgData;
        this.countMeasurements = countMeasurements;
    }

    public static class Builder {
        private float ppm;
        private String measurementFolder;
        private SparseArray<List<String>> measurementFiles;
        private List<List<Float>> squares;
        private String calibrationCurveFolder;
        private List<Float> ppmData;
        private List<Float> avgData;
        private int countMeasurements;

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

        public Builder setCalibrationCurveFolder(String calibrationCurveFolder) {
            this.calibrationCurveFolder = calibrationCurveFolder;
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
            return new ReportInput(ppm, measurementFolder, measurementFiles, squares, calibrationCurveFolder,
                    ppmData, avgData, countMeasurements);
        }
    }
}
