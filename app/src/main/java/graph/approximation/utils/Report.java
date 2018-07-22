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

        for (ReportItem reportDataItem : items) {
            builder.append(reportDataItem.text);
            if (reportDataItem.autoAddBreak) {
                builder.append("\n");
            }
        }

        Spannable spannable = Spannable.Factory.getInstance().newSpannable(builder);
        int currentLineStartPosition = 0;
        for (final ReportItem reportDataItem : items) {
            String text = reportDataItem.text;
            int length = text.length();

            spannable.setSpan(new TypefaceSpan("monospace"), currentLineStartPosition,
                    currentLineStartPosition + length, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            if (currentLineStartPosition == 0) {
                spannable.setSpan(new AlignmentSpan.Standard(reportDataItem.alignment), currentLineStartPosition, currentLineStartPosition + length,
                        Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }

            if (reportDataItem.isBold) {
                spannable.setSpan(new StyleSpan(Typeface.BOLD), currentLineStartPosition,
                        currentLineStartPosition + length, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
            if (reportDataItem.foregroundColor != Color.TRANSPARENT) {
                spannable.setSpan(new ForegroundColorSpan(reportDataItem.foregroundColor),
                        currentLineStartPosition, currentLineStartPosition + length, Spanned
                                .SPAN_INCLUSIVE_INCLUSIVE);
            }

            spannable.setSpan(new AbsoluteSizeSpan(reportDataItem.fontSize),
                    currentLineStartPosition, currentLineStartPosition + length, Spanned
                            .SPAN_INCLUSIVE_INCLUSIVE);
            currentLineStartPosition += length + (reportDataItem.autoAddBreak ? 1  : 0);
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
            String text = reportDataItem.text;
            if (isNextLineNew) {
                text = startMargin + text;
            }

            Font font = new Font(Font.FontFamily.COURIER, reportDataItem.fontSize / 1.8f);

            if (reportDataItem.isBold) {
                font.setStyle(Font.BOLD);
            }

            if (reportDataItem.foregroundColor != Color.TRANSPARENT) {
                int color = reportDataItem.foregroundColor;
                font.setColor(Color.red(color), Color.green(color), Color.blue(color));
            }

            if (isNextLineNew) {
                if (newParagraph != null) {
                    document.add(newParagraph);
                }
                newParagraph = new Paragraph(text, font);
            } else {
                newParagraph.add(new Phrase(text, font));
            }

            if (isFirstItem) {
                newParagraph.setAlignment(Element.ALIGN_CENTER);
            }

            isNextLineNew = reportDataItem.autoAddBreak;
            isFirstItem = false;
        }

        if (newParagraph != null) {
            document.add(newParagraph);
        }

        document.newPage();
        document.close();
    }

    public static class ReportItem {
        public final String text;
        public final int foregroundColor;
        public final boolean isBold;
        public final int fontSize;
        public final boolean autoAddBreak;
        public final Layout.Alignment alignment;

        private ReportItem(String mText, int mForegroundColor, boolean mIsBold, int fontSize, boolean mAutoAddBreak, Layout.Alignment alignment) {
            this.text = mText;
            this.foregroundColor = mForegroundColor;
            this.isBold = mIsBold;
            this.fontSize = fontSize;
            this.autoAddBreak = mAutoAddBreak;
            this.alignment = alignment;
        }

        public static class Builder {
            private int mForegroundColor = Color.TRANSPARENT;
            private boolean mIsBold = true;
            private int mFontSize = 18;
            private boolean mAutoAddBreak = true;
            private Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;

            public Builder setForegroundColor(int mForegroundColor) {
                this.mForegroundColor = mForegroundColor;
                return this;
            }

            public Builder setBold(boolean mIsBold) {
                this.mIsBold = mIsBold;
                return this;
            }

            public Builder setFontSize(int mFontSize) {
                this.mFontSize = mFontSize;
                return this;
            }

            public Builder setAutoAddBreak(boolean mAutoAddBreak) {
                this.mAutoAddBreak = mAutoAddBreak;
                return this;
            }

            public Builder setAlignment(Layout.Alignment alignment) {
                this.alignment = alignment;
                return this;
            }

            public ReportItem build(String text) {
                return new ReportItem(text, mForegroundColor, mIsBold, mFontSize, mAutoAddBreak, alignment);
            }
        }
    }
}
