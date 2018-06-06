package graph.approximation.utils;

import java.util.Collections;
import java.util.List;

public class ReportInput {
    public final float ppm;
    public final String measurementFolder;
    public final List<String> measurementFiles;
    public final List<Float> measurementAverages;
    public final String calibrationCurveFolder;
    public final List<Float> ppmData;
    public final List<Float> avgData;
    public final int countMeasurements;

    private ReportInput(float ppm, String measurementFolder, List<String> measurementFiles, List<Float> measurementAverages,
                        String calibrationCurveFolder, List<Float> ppmData, List<Float> avgData, int countMeasurements) {
        this.ppm = ppm;
        this.measurementFolder = measurementFolder;
        this.measurementFiles = measurementFiles;
        this.measurementAverages = measurementAverages;
        this.calibrationCurveFolder = calibrationCurveFolder;
        this.ppmData = ppmData;
        this.avgData = avgData;
        this.countMeasurements = countMeasurements;
    }

    public static class Builder {
        private float ppm;
        private String measurementFolder;
        private List<String> measurementFiles;
        private List<Float> measurementAverages;
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

        public Builder setMeasurementFiles(List<String> measurementFiles) {
            this.measurementFiles = Collections.unmodifiableList(measurementFiles);
            return this;
        }

        public Builder setMeasurementAverages(List<Float> measurementAverages) {
            this.measurementAverages = Collections.unmodifiableList(measurementAverages);
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
            return new ReportInput(ppm, measurementFolder, measurementFiles, measurementAverages, calibrationCurveFolder,
                    ppmData, avgData, countMeasurements);
        }
    }
}
