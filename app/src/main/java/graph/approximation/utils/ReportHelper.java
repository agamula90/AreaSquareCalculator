package graph.approximation.utils;

import android.graphics.Color;
import android.os.Environment;
import android.text.Layout;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;

import com.proggroup.areasquarecalculator.utils.CurveHelper;
import com.proggroup.areasquarecalculator.utils.FloatFormatter;
import com.proggroup.squarecalculations.CalculateUtils;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ReportHelper {
    private static class TextSizes {
        static final int HEADER = 35;
        static final int DEFAULT = 14;
        static final int MEDIUM = 18;
        static final int CURVE_TYPE = 28;
        static final int SMALL = 18;
    }

    private static class Colors {
        static final int GREEN = Color.rgb(38, 166, 154);
    }

    private static final String TAG = ReportHelper.class.getSimpleName();

    private static final String REPORT_FOLDER_NAME = "AEToC_Report_Files";
    private static final File REPORTS_DIRECTORY = new File(Environment.getExternalStorageDirectory(), REPORT_FOLDER_NAME);

    private static final String REPORT_START_NAME = "RPT_CAL_";

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
    private static final SimpleDateFormat HTML_FILE_NAME_FORMATTER = new SimpleDateFormat
            ("yyyyMMdd_HHmmss");
    private static final String UNKNOWN = "Unknown";

    private CurveHelper curveHelper;

    public ReportHelper() {
        curveHelper = new CurveHelper();
    }

    public File getPdfReportFile(Date reportDate) {
        return new File(REPORTS_DIRECTORY, createReportName(reportDate) + ".pdf");
    }

    public String getPrintManagerJobName(Date reportDate) {
        return createReportName(reportDate) + " Report";
    }

    private String createReportName(Date date) {
        int reportNumber = getCountReports();
        return REPORT_START_NAME + HTML_FILE_NAME_FORMATTER.format(date) + "_" + reportNumber;
    }

    private int getCountReports() {
        File files[] = REPORTS_DIRECTORY.listFiles();
        if (files == null) {
            return 0;
        }

        List<File> reportFiles = new ArrayList<>();
        for (File htmlFile : files) {
            String htmlName = htmlFile.getName();
            if (!htmlFile.isDirectory() && (htmlName.endsWith(".html") || htmlName
                    .endsWith(".xhtml")) && htmlName.startsWith(REPORT_START_NAME)) {
                reportFiles.add(htmlFile);
            }
        }

        int maxCount = 0;

        for (File htmlFile : reportFiles) {
            String name = htmlFile.getName();
            try {
                String str = name.substring(name.lastIndexOf('_') + 1, name
                        .lastIndexOf("."));
                int count = Integer.parseInt(str);
                if (count > maxCount) {
                    maxCount = count;
                }
            } catch (NumberFormatException e) {
            }
        }

        return maxCount + 1;
    }

    /**
     * @param text the text to write
     */
    public void write(String text, Date reportDate) {
        File reportFile = new File(REPORTS_DIRECTORY, createReportName(reportDate) + ".html");
        reportFile.getParentFile().mkdirs();
        OutputStreamWriter writer;
        BufferedWriter out;
        try {
            reportFile.createNewFile();
            writer = new OutputStreamWriter(new FileOutputStream(reportFile),
                    Charset.defaultCharset());
            out = new BufferedWriter(writer);
            out.write(text);
            out.close();
        } catch (IOException e) {
            Log.w(TAG, "Can't write to file " + reportFile.getPath(), e);
        }
    }

    public List<Report.ReportItem> generateItems(ReportInput reportInput, Date currentDate) {
        List<Report.ReportItem> reportDataItemList = new ArrayList<>();

        reportDataItemList.add(createTitle());
        reportDataItemList.addAll(createFirstSection(currentDate));
        reportDataItemList.addAll(createAutoSection());

        ReportInput.CurveData curveData = reportInput.curveData;
        if (curveData != null) {
            reportDataItemList.addAll(createCurveSection(curveData));
        }

        reportDataItemList.addAll(createMeasurementSection(reportInput));

        /*
        reportDataItemList.add(new Report.ReportItem.Builder().build(""));
        reportDataItemList.add(new Report.ReportItem.Builder().build(""));

        String mesData = "Measurements data:";

        reportDataItemList.add(new Report.ReportItem.Builder().build(mesData));

        String auto = "Auto: ";
        String duration = "Duration: ";
        String volume = "Volume: ";

        destLength = maxLength(auto, duration, volume);

        reportDataItemList.add(new Report.ReportItem.Builder()
                .build(fill(' ', mesData.length())
                        + auto + fill(' ', destLength - auto.length()) +
                        reportData.countMeasurements + " measurements"));*/

        return reportDataItemList;
    }

    private Report.ReportItem createTitle() {
        return new Report.ReportItem.Builder()
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setFontSize(TextSizes.HEADER)
                .setForegroundColor(Colors.GREEN)
                .build("Calibration Curve Report");
    }

    private List<Report.ReportItem> createFirstSection(Date reportDate) {
        String aligningTexts[] = new String[] {"Date: "/*, "SampleId: ", "Location: "*/};
        String resolved[] = new String[] {FORMATTER.format(reportDate)/*, "", ""*/};

        List<Report.ReportItem> res = new ArrayList<>(createKeyValueList(aligningTexts, resolved));

        res.add(new Report.ReportItem.Builder()
                .setFontSize(TextSizes.MEDIUM)
                .build(""));

        res.addAll(createKeyValueList(new String[] {"Operator: "}, new String[] {""}));

        res.add(new Report.ReportItem.Builder()
                .setFontSize(TextSizes.MEDIUM)
                .build(""));

        return res;
    }

    private List<Report.ReportItem> createKeyValueList(String boldTexts[], String plainTexts[]) {
        List<Report.ReportItem> res = new ArrayList<>();

        int destLength = maxLength(boldTexts);
        int destLengthResolved = maxLength(plainTexts);

        for (int i = 0; i < boldTexts.length; i++) {
            if (i == plainTexts.length) {
                break;
            }
            res.add(new Report.ReportItem.Builder()
                    .setFontSize(TextSizes.MEDIUM)
                    .setAutoAddBreak(false)
                    .build(boldTexts[i]));

            res.add(new Report.ReportItem.Builder()
                    .setFontSize(TextSizes.MEDIUM)
                    .setBold(false)
                    .build(fill(' ', destLength - boldTexts[i].length()) +
                            plainTexts[i] + fill(' ', destLengthResolved - plainTexts[i].length())));
        }

        return res;
    }

    private List<Report.ReportItem> createAutoSection() {
        String aligningTexts[] = new String[] {
                "Carrier Gas Flow: ",
                "Duration: ",
                "Volume: ", "Calibration Folder: "};

        String resolved[] = new String[] {"05L/min", "3 minutes", "20 uL"};
        List<Report.ReportItem> res = new ArrayList<>(createKeyValueList(aligningTexts, resolved));

        return res;
    }

    private List<Report.ReportItem> createCurveSection(ReportInput.CurveData curveData) {
        List<Report.ReportItem> res = new ArrayList<>();

        Pair<List<Float>, List<Float>> curveValues = curveHelper.parseCurveValuesFromFile(curveData.curveFile.getPath());
        ReportInput.CurveData.CurveType curveType = curveData.viewInfo.curveType;

        float regression = 0f;
        if (curveType == ReportInput.CurveData.CurveType.BFit) {
            SimpleRegression simpleRegression = new SimpleRegression();
            if (curveData.viewInfo.connectTo0) {
                simpleRegression.addData(0, 0);
            }

            for (int i = 0; i < curveValues.first.size(); i++) {
                float ppm = curveValues.first.get(i);
                float square = curveValues.second.get(i);

                if (ppm != 0 || square != 0 || !curveData.viewInfo.connectTo0) {
                    simpleRegression.addData(ppm, square);
                }
            }

            regression = (float) simpleRegression.getR();
        }

        float min = 0;
        float max = 0;
        for (int i = 0; i < curveValues.first.size(); i++) {
            float ppm = curveValues.first.get(i);

            if (i == 0) {
                min = ppm;
                max = ppm;
                continue;
            }

            if (min > ppm) {
                min = ppm;
            }
            if (max < ppm) {
                max = ppm;
            }
        }

        res.addAll(createKeyValueList(new String[] {"Calibration Folder: "}, new String[] {curveData.curveFile.getParentFile().getAbsolutePath()}));

        res.add(new Report.ReportItem.Builder()
                .setFontSize(TextSizes.CURVE_TYPE)
                .build(""));
        res.add(new Report.ReportItem.Builder()
                .setFontSize(TextSizes.CURVE_TYPE)
                .build(""));

        res.add(new Report.ReportItem.Builder()
                .setFontSize(TextSizes.CURVE_TYPE)
                .setForegroundColor(Colors.GREEN)
                .build(composeCurveType(curveData, min, max, regression)));

        res.add(new Report.ReportItem.Builder().build(""));

        res.addAll(createKeyValueList(new String[] {"Calibration Curve Name: "}, new String[] {curveData.curveFile.getName()}));

        res.add(new Report.ReportItem.Builder()
                .setBold(false)
                .build(composePpmCurveText(curveValues.first, curveValues.second)));

        res.add(new Report.ReportItem.Builder().build(""));
        return res;
    }

    private List<Report.ReportItem> createMeasurementSection(ReportInput reportInput) {
        List<Report.ReportItem> res = new ArrayList<>();
        String measurementText = "Calibration Measurement Files:";
        res.add(new Report.ReportItem.Builder()
                .setFontSize(TextSizes.MEDIUM)
                .build(measurementText));
        res.add(new Report.ReportItem.Builder().build(""));

        int destLength = 0;

        for (int i = 0; i < reportInput.measurementFiles.length; i++) {
            List<File> measurementFiles = new ArrayList<>();

            for (int j = 0; j < reportInput.measurementFiles[i].length; j++) {
                File file = reportInput.measurementFiles[i][j];
                if (file != null) {
                    measurementFiles.add(file);
                }
            }

            int newDestLength = maxLength(measurementFiles);
            if (destLength < newDestLength) {
                destLength = newDestLength;
            }
        }

        for (int i = 0; i < reportInput.measurementFiles.length; i++) {
            List<File> measurementFiles = new ArrayList<>();

            for (int j = 0; j < reportInput.measurementFiles[i].length; j++) {
                File file = reportInput.measurementFiles[i][j];
                if (file != null) {
                    measurementFiles.add(file);
                }
            }

            String asvText = fill(' ', 4) + "ASV" + fill(' ', 2);

            float average = 0;
            int countDigits = 0;
            for (File measurementFile : measurementFiles) {
                String measurementFileName = measurementFile.getName();
                float avg = CalculateUtils.calculateSquare(measurementFile);
                average += avg;
                String avgString = FloatFormatter.format(avg);

                if (countDigits < avgString.length()) {
                    countDigits = avgString.length();
                }

                res.add(new Report.ReportItem.Builder()
                        .setBold(false)
                        .build(fill(' ', measurementText.length()) +
                                measurementFileName + fill(' ', destLength - measurementFileName.length()) +
                                asvText +
                                avgString + fill(' ', countDigits - avgString.length())));
            }

            average /= measurementFiles.size();

            res.add(new Report.ReportItem.Builder()
                    .build(fill(' ', measurementText.length() + destLength + asvText.length()) + fill('-', countDigits)));

            String avgAsString = FloatFormatter.format(average);

            res.add(new Report.ReportItem.Builder()
                    .build(fill(' ', measurementText.length() + destLength + asvText.length()) +
                            fill(' ', countDigits - avgAsString.length()) + avgAsString));

            res.add(new Report.ReportItem.Builder().build(""));
            res.add(new Report.ReportItem.Builder().build(""));
        }

        return res;
    }

    private String fill(char symbol, int count) {
        if (count == 0) {
            return "";
        }
        char symbols[] = new char[count];
        Arrays.fill(symbols, symbol);
        return new String(symbols);
    }

    private int maxLength(String... values) {
        int max = 0;
        for (String value : values) {
            if (value != null) {
                max = Math.max(max, value.length());
            }
        }
        return max;
    }

    private int maxLength(List<File> values) {
        int max = 0;
        for (int i = 0; i < values.size(); i++) {
            max = Math.max(max, values.get(i).getName().length());
        }
        return max;
    }

    private String composePpmCurveText(List<Float> ppmPoints, List<Float>
            avgSquarePoints) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < ppmPoints.size(); i++) {
            builder.append(ppmPoints.get(i).intValue())
                    .append(fill(' ', 1))
                    .append(FloatFormatter.format(avgSquarePoints.get(i)))
                    .append(fill(' ', 4));
        }
        return builder.toString();
    }

    private String composeCurveType(ReportInput.CurveData curveData, float min, float max, float regression) {
        ReportInput.CurveData.CurveType curveType = curveData.viewInfo.curveType;
        boolean connectTo0 = curveData.viewInfo.connectTo0;

        StringBuilder builder = new StringBuilder("Calibration Curve ");

        builder.append((int) min).append("-").append((int) max).append("ppm   ");

        if (connectTo0) {
            builder.append("(0,0), ");
        }
        builder.append(curveType.toString());
        if (curveType == ReportInput.CurveData.CurveType.BFit) {
            builder.append(", r").append(FloatFormatter.format(regression));
        }
        return builder.toString();
    }
}
