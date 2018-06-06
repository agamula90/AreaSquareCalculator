package graph.approximation.utils;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

public class Report {

    private final List<ReportItem> items;
    public Report(List<ReportItem> items) {
        this.items = items;
    }

    public Spannable toSpannable() {
        StringBuilder builder = new StringBuilder();

        String startMargin = "  ";

        builder.append(startMargin);

        for (ReportItem reportDataItem : items) {
            builder.append(reportDataItem.getText());
            if (reportDataItem.isAutoAddBreak()) {
                builder.append("\n");
                builder.append(startMargin);
            }
        }

        Spannable spannable = Spannable.Factory.getInstance().newSpannable(builder);
        int currentLineStartPosition = 0;
        for (ReportItem reportDataItem : items) {
            String text = reportDataItem.getText();
            int length = text.length() + (reportDataItem.isAutoAddBreak() ? startMargin.length()
                    : 0);

            spannable.setSpan(new TypefaceSpan("monospace"), currentLineStartPosition,
                    currentLineStartPosition + length, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            if (currentLineStartPosition == 0) {
                spannable.setSpan(new AlignmentSpan() {
                                      @Override
                                      public Layout.Alignment getAlignment() {
                                          return Layout.Alignment.ALIGN_CENTER;
                                      }
                                  }, currentLineStartPosition, currentLineStartPosition + length,
                        Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }

            if (reportDataItem.isBold()) {
                spannable.setSpan(new StyleSpan(Typeface.BOLD), currentLineStartPosition,
                        currentLineStartPosition + length, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
            if (reportDataItem.getForegroundColor() != Color.TRANSPARENT) {
                spannable.setSpan(new ForegroundColorSpan(reportDataItem.getForegroundColor()),
                        currentLineStartPosition, currentLineStartPosition + length, Spanned
                                .SPAN_INCLUSIVE_INCLUSIVE);
            }

            spannable.setSpan(new AbsoluteSizeSpan(reportDataItem.getFontSize()),
                    currentLineStartPosition, currentLineStartPosition + length, Spanned
                            .SPAN_INCLUSIVE_INCLUSIVE);
            currentLineStartPosition += length + (reportDataItem.isAutoAddBreak() ? 1 : 0);
        }
        return spannable;
    }

    public void save(String folderForWrite) throws DocumentException, FileNotFoundException {
        String startMargin = "  ";

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(folderForWrite));
        document.open();

        boolean isNextLineNew = true;
        boolean isFirstItem = true;

        Paragraph newParagraph = null;

        for (ReportItem reportDataItem : items) {
            if (isNextLineNew) {
                reportDataItem.applyLeftPadding(startMargin);
            }

            Font font = new Font(Font.FontFamily.COURIER, reportDataItem.getFontSize() / 1.8f);

            if (reportDataItem.isBold()) {
                font.setStyle(Font.BOLD);
            }

            if (reportDataItem.getForegroundColor() != Color.TRANSPARENT) {
                int color = reportDataItem.getForegroundColor();
                font.setColor(Color.red(color), Color.green(color), Color.blue(color));
            }

            if (isNextLineNew) {
                if (newParagraph != null) {
                    document.add(newParagraph);
                }
                newParagraph = new Paragraph(reportDataItem.getText(), font);
            } else {
                if (newParagraph != null) {
                    newParagraph.add(new Phrase(reportDataItem.getText(), font));
                }
            }

            if (isFirstItem) {
                newParagraph.setAlignment(Element.ALIGN_CENTER);
            }

            isNextLineNew = reportDataItem.isAutoAddBreak();
            isFirstItem = false;
        }

        if (newParagraph != null) {
            document.add(newParagraph);
        }

        document.newPage();
        document.close();
    }

    public static class ReportItem {
        private int mForegroundColor;
        private boolean mIsBold;
        private @FontTextSize int mFontSize;
        private String mText;
        private boolean mAutoAddBreak = true;

        ReportItem(@FontTextSize int fontSize, String text) {
            this(fontSize, text, false);
        }

        ReportItem(@FontTextSize int fontSize, String text, boolean isBold) {
            this(fontSize, text, Color.TRANSPARENT, isBold);
        }

        ReportItem(@FontTextSize int fontSize, String text, int foregroundColor, boolean
                isBold) {
            this.mFontSize = fontSize;
            this.mText = text;
            this.mIsBold = isBold;
            this.mForegroundColor = foregroundColor;
        }

        void setAutoAddBreak(boolean autoAddBreak) {
            this.mAutoAddBreak = autoAddBreak;
        }

        @FontTextSize int getFontSize() {
            return mFontSize;
        }

        int getForegroundColor() {
            return mForegroundColor;
        }

        String getText() {
            return mText;
        }

        void applyLeftPadding(String paddingString) {
            mText = paddingString + mText;
        }

        boolean isBold() {
            return mIsBold;
        }

        boolean isAutoAddBreak() {
            return mAutoAddBreak;
        }
    }
}
