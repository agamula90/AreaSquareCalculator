package graph.approximation.utils;

import android.graphics.Color;
import android.os.Environment;
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
import java.util.Date;
import java.util.List;

public class ReportUtils {
    private static final String TAG = ReportUtils.class.getSimpleName();

    private static final String REPORT_FOLDER_NAME = "AEToC_Report_Files";
    private static final File REPORTS_DIRECTORY = new File(Environment.getExternalStorageDirectory(), REPORT_FOLDER_NAME);

    private static final String REPORT_START_NAME = "RPT_MES_";

    private static int getCountReports() {
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

    public static File getHtmlReportFile(Date currentDate) {
        int reportNumber = getCountReports();
        String reportFileName = REPORT_START_NAME + FORMATTER.format(currentDate) + "_" + reportNumber;

        return new File(REPORTS_DIRECTORY, reportFileName + ".html");
    }

    /**
     * @param text the text to write
     */
    public static void write(String text, File file) {
        file.getParentFile().mkdirs();
        OutputStreamWriter writer;
        BufferedWriter out;
        try {
            file.createNewFile();
            writer = new OutputStreamWriter(new FileOutputStream(file),
                    Charset.defaultCharset());
            out = new BufferedWriter(writer);
            out.write(text);
            out.close();
        } catch (IOException e) {
            Log.w(TAG, "Can't write to file " + file.getPath(), e);
        }
    }

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
    private static final String UNKNOWN = "Unknown";

    public static List<Report.ReportItem> generateItems(ReportInput reportData, Date currentDate) {
        List<Report.ReportItem> reportDataItemList = new ArrayList<>();

        int backgroundColor = Color.rgb(38, 166, 154);

        reportDataItemList.add(new Report.ReportItem(FontTextSize.HEADER_TITLE_SIZE, "EToC Report",
                backgroundColor, false));

        String reportDate = FORMATTER.format(currentDate);

        String dateString = "Date ";
        String sampleIdString = "SampleId ";
        String locationString = "Location ";
        String ppmString = "PPM ";

        int maxCount = maxCount(dateString, sampleIdString, locationString, ppmString);

        dateString = changedToMax(dateString, maxCount);

        reportDataItemList.add(new Report.ReportItem(FontTextSize.MEDIUM_TEXT_SIZE, dateString +
                reportDate));

        String sampleId = UNKNOWN;

        sampleIdString = changedToMax(sampleIdString, maxCount);

        reportDataItemList.add(new Report.ReportItem(FontTextSize.MEDIUM_TEXT_SIZE, sampleIdString +
                sampleId));

        String location = UNKNOWN;

        locationString = changedToMax(locationString, maxCount);

        reportDataItemList.add(new Report.ReportItem(FontTextSize.MEDIUM_TEXT_SIZE, locationString +
                location));

        reportDataItemList.add(new Report.ReportItem(FontTextSize.MEDIUM_TEXT_SIZE, ""));

        ppmString = changedToMax(ppmString, maxCount);

        Report.ReportItem data = new Report.ReportItem(FontTextSize.MEDIUM_TEXT_SIZE, ppmString,
                backgroundColor, false);
        data.setAutoAddBreak(false);
        reportDataItemList.add(data);

        reportDataItemList.add(new Report.ReportItem(FontTextSize.BIG_TEXT_SIZE, "" + reportData
                .ppm, backgroundColor, false));
        reportDataItemList.add(new Report.ReportItem(FontTextSize.NORMAL_TEXT_SIZE, ""));
        reportDataItemList.add(new Report.ReportItem(FontTextSize.NORMAL_TEXT_SIZE, ""));

        String measurementFolder = reportData.measurementFolder;

        reportDataItemList.add(new Report.ReportItem(FontTextSize.NORMAL_TEXT_SIZE, "Measurement " +
                "Folder: " +
                measurementFolder));

        reportDataItemList.add(new Report.ReportItem(FontTextSize.NORMAL_TEXT_SIZE, ""));
        reportDataItemList.add(new Report.ReportItem(FontTextSize.NORMAL_TEXT_SIZE, ""));

        String measurementFilesText = "Measurement Files:";

        reportDataItemList.add(new Report.ReportItem(FontTextSize.NORMAL_TEXT_SIZE,
                measurementFilesText));

        List<String> measurementFiles = reportData.measurementFiles;
        List<Float> measurementAverages = reportData.measurementAverages;
        float average = 0;

        int countMeasurements = measurementFiles.size();

        String beforeAsvString = "    ";
        String asvString = beforeAsvString + "ASV  ";

        String measurementFilesTextEmptyString = changedToMax("", measurementFilesText.length());

        int maxCountSymbolsInFileName = 0;
        int maxPowerOfSquare = 0;

        for (int i = 0; i < countMeasurements; i++) {
            if (maxCountSymbolsInFileName < measurementFiles.get(i).length()) {
                maxCountSymbolsInFileName = measurementFiles.get(i).length();
            }
            if (maxPowerOfSquare < FloatFormatter.format(measurementAverages.get(i)).length()) {
                maxPowerOfSquare = FloatFormatter.format(measurementAverages.get(i)).length();
            }
        }

        List<String> measurementAverageStrings = new ArrayList<>(measurementAverages.size());

        for (int i = 0; i < countMeasurements; i++) {
            measurementFiles.set(i, changedToMax(measurementFiles.get(i),
                    maxCountSymbolsInFileName));

            measurementAverageStrings.add(changedToMaxFromLeft(FloatFormatter.format
                    (measurementAverages.get(i)), maxPowerOfSquare));
        }

        StringBuilder lineBuilder = new StringBuilder();
        StringBuilder measureAverageBuilder = new StringBuilder();

        for (int i = 0; i < countMeasurements; i++) {
            reportDataItemList.add(new Report.ReportItem(FontTextSize.NORMAL_TEXT_SIZE,
                    measurementFilesTextEmptyString + measurementFiles.get(i) + asvString +
                            measurementAverageStrings.get(i)));

            if (i == 0) {
                measureAverageBuilder.append(measurementFilesTextEmptyString);
                lineBuilder.append(measurementFilesTextEmptyString);
                measureAverageBuilder.append(changedToMax("", measurementFiles.get(i).length
                        ()));
                lineBuilder.append(changedToMax("", measurementFiles.get(i).length()));
                measureAverageBuilder.append(changedToMax("", beforeAsvString.length()));
                lineBuilder.append(changedToMax("", beforeAsvString.length()));
                measureAverageBuilder.append(changedToMax("", asvString.length() - beforeAsvString
                        .length()));
                lineBuilder.append(changedToMax("", '-', asvString.length() - beforeAsvString
                        .length()));
            }
            average += measurementAverages.get(i);
        }

        average /= countMeasurements;

        measureAverageBuilder.append(changedToMaxFromLeft(FloatFormatter.format
                (average), maxPowerOfSquare));

        lineBuilder.append(changedToMax("", '-', maxPowerOfSquare));

        reportDataItemList.add(new Report.ReportItem(FontTextSize.NORMAL_TEXT_SIZE,
                lineBuilder.toString()));

        reportDataItemList.add(new Report.ReportItem(FontTextSize.NORMAL_TEXT_SIZE,
                measureAverageBuilder.toString()));
        reportDataItemList.add(new Report.ReportItem(FontTextSize.NORMAL_TEXT_SIZE, ""));
        reportDataItemList.add(new Report.ReportItem(FontTextSize.NORMAL_TEXT_SIZE, ""));

        String calibrationFolder = reportData.calibrationCurveFolder;

        reportDataItemList.add(new Report.ReportItem(FontTextSize.NORMAL_TEXT_SIZE, "Calibration" +
                " " +
                "Curve: " + calibrationFolder));
        reportDataItemList.add(new Report.ReportItem(FontTextSize.NORMAL_TEXT_SIZE,
                composePpmCurveText(reportData.ppmData, reportData.avgData)));

        reportDataItemList.add(new Report.ReportItem(FontTextSize.NORMAL_TEXT_SIZE, ""));
        reportDataItemList.add(new Report.ReportItem(FontTextSize.NORMAL_TEXT_SIZE, ""));

        measurementFilesText = "Measurements data:";

        reportDataItemList.add(new Report.ReportItem(FontTextSize.NORMAL_TEXT_SIZE,
                measurementFilesText));

        measurementFilesTextEmptyString = changedToMax("", measurementFilesText.length());

        String auto = "Auto: ";
        String duration = "Duration: ";
        String volume = "Volume: ";

        maxCount = maxCount(auto, duration, volume);

        auto = changedToMax(auto, maxCount);

        reportDataItemList.add(new Report.ReportItem(FontTextSize.NORMAL_TEXT_SIZE,
                measurementFilesTextEmptyString + auto +
                        reportData.countMeasurements + " measurements"));

        reportDataItemList.add(new Report.ReportItem(FontTextSize.NORMAL_TEXT_SIZE, ""));
        reportDataItemList.add(new Report.ReportItem(FontTextSize.NORMAL_TEXT_SIZE, ""));

        String operator = UNKNOWN;
        String date = FORMATTER.format(currentDate);

        reportDataItemList.add(new Report.ReportItem(FontTextSize.NORMAL_TEXT_SIZE,
                "Operator: " + operator + "                "
                        + "Date: " + date));
        return reportDataItemList;
    }

    private static int maxCount(String... values) {
        int max = 0;
        for (String value : values) {
            max = Math.max(max, value.length());
        }
        return max;
    }

    private static String changedToMax(String value, int maxCount) {
        StringBuilder builder = new StringBuilder(value);
        for (int i = 0; i < maxCount - value.length(); i++) {
            builder.append(" ");
        }
        return builder.toString();
    }

    private static String changedToMax(String value, char addSymbol, int maxCount) {
        StringBuilder builder = new StringBuilder(value);
        for (int i = 0; i < maxCount - value.length(); i++) {
            builder.append(addSymbol);
        }
        return builder.toString();
    }

    private static String changedToMaxFromLeft(String value, int maxCount) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < maxCount - value.length(); i++) {
            builder.append(" ");
        }
        builder.append(value);
        return builder.toString();
    }

    private static String composePpmCurveText(List<Float> ppmPoints, List<Float>
            avgSquarePoints) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < ppmPoints.size(); i++) {
            builder.append(ppmPoints.get(i).intValue() + " " + FloatFormatter.format
                    (avgSquarePoints.get(i)) + "    ");
        }
        return builder.toString();
    }
}
