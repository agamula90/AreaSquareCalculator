package graph.approximation.utils;

import android.graphics.Color;
import android.os.Environment;
import android.text.Layout;
import android.util.Log;

import com.proggroup.areasquarecalculator.utils.FloatFormatter;

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
    private static final String TAG = ReportHelper.class.getSimpleName();

    private static final String REPORT_FOLDER_NAME = "AEToC_Report_Files";
    private static final File REPORTS_DIRECTORY = new File(Environment.getExternalStorageDirectory(), REPORT_FOLDER_NAME);

    private static final String REPORT_START_NAME = "RPT_CAL_";

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
    private static final SimpleDateFormat HTML_FILE_NAME_FORMATTER = new SimpleDateFormat
            ("yyyyMMdd_HHmmss");
    private static final String UNKNOWN = "Unknown";

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

    public List<Report.ReportItem> generateItems(ReportInput reportData, Date currentDate) {
        List<Report.ReportItem> reportDataItemList = new ArrayList<>();

        int backgroundColor = Color.rgb(38, 166, 154);

        reportDataItemList.add(new Report.ReportItem.Builder()
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setFontSize(FontTextSize.HEADER_TITLE_SIZE)
                .setForegroundColor(backgroundColor)
                .build("Calibration Curve Report"));

        String reportDate = FORMATTER.format(currentDate);

        String leftSide[] = new String[] {"Date ", "SampleId ", "Location ", "Operator "};

        String leftSideResolved[] = new String[] {reportDate, UNKNOWN, UNKNOWN, UNKNOWN};
        int destLengthLeftResolved = maxLength(leftSideResolved);

        int destLength = maxLength(leftSide);

        String rightSide[] = new String[] {"Measurements data: ", "Auto: ",
                "Duration:" + fill(' ', 4) + "minutes",
                "Volume:" + fill(' ', 4) + "20 uL"};

        int rightSideDestLength = maxLength(rightSide);

        for (int i = 0; i < leftSide.length; i++) {
            reportDataItemList.add(new Report.ReportItem.Builder()
                    .setFontSize(FontTextSize.MEDIUM_TEXT_SIZE)
                    .build(leftSide[i] + fill(' ', destLength - leftSide[i].length()) +
                            leftSideResolved[i] + fill(' ', destLengthLeftResolved - leftSideResolved[i].length())));

            /*reportDataItemList.add(new Report.ReportItem.Builder()
                    .setFontSize(FontTextSize.MEDIUM_TEXT_SIZE)
                    .setAlignment(Layout.Alignment.ALIGN_OPPOSITE)
                    .build(rightSide[i] + fill(' ', rightSideDestLength - rightSide[i].length())));*/
        }

        reportDataItemList.add(new Report.ReportItem.Builder()
                .setFontSize(FontTextSize.MEDIUM_TEXT_SIZE)
                .build(""));


        String curveName = reportData.curveName;

        if (curveName != null) {
            reportDataItemList.add(new Report.ReportItem.Builder()
                    .build("Calibration Curve Name: " + curveName));

            reportDataItemList.add(new Report.ReportItem.Builder()
                    .build("Calibration Curve Type: " + composeCurveType(reportData.curveData)));

            reportDataItemList.add(new Report.ReportItem.Builder()
                    .build("Calibration Curve Data: from CSV file"));

            reportDataItemList.add(new Report.ReportItem.Builder()
                    .build("Curve points: " + composePpmCurveText(reportData.ppmData, reportData.avgData)));

            reportDataItemList.add(new Report.ReportItem.Builder().build(""));
            reportDataItemList.add(new Report.ReportItem.Builder().build(""));
        }

        String measurementFolder = reportData.measurementFolder;
        reportDataItemList.add(new Report.ReportItem.Builder()
                .build("Measurement Folder: " + measurementFolder));

        String measurementFiles = "Measurement Files:";
        reportDataItemList.add(new Report.ReportItem.Builder()
                .build(measurementFiles));

        for (int i = 0; i < reportData.squares.size(); i++) {
            float average = 0;
            String squares[] = new String[reportData.squares.get(i).size()];

            for (int j = 0; j < squares.length; j++) {
                float avg = reportData.squares.get(i).get(j);

                average += avg;
                squares[j] = FloatFormatter.format(avg);
            }

            float countInitializedSquares = 0;

            int countDigits = maxLength(squares);

            List<String> measurementPaths = reportData.measurementFiles.get(i);
            destLength = maxLength(measurementPaths.toArray(new String[0]));

            String asvText = fill(' ', 4) + "ASV" + fill(' ', 2);

            for (int j = 0; j < measurementPaths.size(); j++) {
                if (measurementPaths.get(j) != null) {
                    countInitializedSquares++;
                    reportDataItemList.add(new Report.ReportItem.Builder()
                            .build(fill(' ', measurementFiles.length()) +
                                    measurementPaths.get(j) + fill(' ', destLength - measurementPaths.get(j).length()) +
                                    asvText +
                                    squares[j] + fill(' ', countDigits - squares[j].length())));
                }
            }

            average /= countInitializedSquares;

            reportDataItemList.add(new Report.ReportItem.Builder()
                    .build(fill(' ', measurementFiles.length() + destLength + asvText.length()) + fill('-', countDigits)));

            String avgAsString = FloatFormatter.format(average);

            reportDataItemList.add(new Report.ReportItem.Builder()
                    .build(fill(' ', measurementFiles.length() + destLength + asvText.length()) +
                            fill(' ', countDigits - avgAsString.length()) + avgAsString));

            reportDataItemList.add(new Report.ReportItem.Builder().build(""));
            reportDataItemList.add(new Report.ReportItem.Builder().build(""));
        }

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

    private String composeCurveType(ReportInput.CurveData curveData) {
        StringBuilder builder = new StringBuilder();
        if (curveData.isConnectTo0()) {
            builder.append("(0,0), ");
        }
        builder.append(curveData.getCurveType().toString());
        if (curveData.getCurveType() == ReportInput.CurveData.CurveType.BFit) {
            builder.append(", r #=").append(FloatFormatter.formatRegressionR(curveData.getRegressionR()));
        }
        return builder.toString();
    }
}
