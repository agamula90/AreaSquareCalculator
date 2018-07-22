package graph.approximation.utils;

import java.io.File;

public class ReportInput {
    public final File[][] measurementFiles;
    public final CurveData curveData;

    private ReportInput(File[][] measurementFiles, CurveData curveData) {
        this.measurementFiles = measurementFiles;
        this.curveData = curveData;
    }

    public static class Builder {
        private File[][] measurementFiles;
        private CurveData curveData;

        //model data, retrieved from table
        public Builder setMeasurementFiles(File[][] measurementFiles) {
            this.measurementFiles = measurementFiles;
            return this;
        }

        public Builder setCurveData(CurveData curveData) {
            this.curveData = curveData;
            return this;
        }

        public ReportInput build() {
            return new ReportInput(measurementFiles, curveData);
        }
    }

    public static class CurveData {
        public final File curveFile;
        public final ViewInfo viewInfo;

        public CurveData(File curveFile, ViewInfo viewInfo) {
            this.curveFile = curveFile;
            this.viewInfo = viewInfo;
        }

        public enum CurveType {
            PointToPoint, BFit
        }

        public static class ViewInfo {
            public final CurveType curveType;
            public final boolean connectTo0;

            public ViewInfo(CurveType curveType, boolean connectTo0) {
                this.curveType = curveType;
                this.connectTo0 = connectTo0;
            }
        }
    }
}
