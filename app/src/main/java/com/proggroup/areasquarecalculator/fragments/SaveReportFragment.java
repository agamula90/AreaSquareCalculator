package com.proggroup.areasquarecalculator.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;

import com.itextpdf.text.DocumentException;
import com.proggroup.areasquarecalculator.R;
import com.proggroup.areasquarecalculator.activities.BaseAttachableActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;

import graph.approximation.utils.Report;
import graph.approximation.utils.ReportHelper;

public class SaveReportFragment extends Fragment {

    private static final String KEY_HTML_TEXT = "html_text";
    private static final String KEY_TIME_MILLIS = "time_millis";

    public static SaveReportFragment newInstance(String htmlText, long reportTimeMillis) {
        Bundle args = new Bundle();
        args.putString(KEY_HTML_TEXT, htmlText);
        args.putLong(KEY_TIME_MILLIS, reportTimeMillis);
        SaveReportFragment res = new SaveReportFragment();
        res.setArguments(args);
        return res;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        String htmlText = getArguments().getString(KEY_HTML_TEXT);
        final Date reportDate = new Date(getArguments().getLong(KEY_TIME_MILLIS));
        final ReportHelper reportHelper = new ReportHelper();

        Context context = container.getContext();
        FrameLayout frameLayout = new FrameLayout(context);

        final WebView webView = new WebView(context);
        frameLayout.addView(webView, new FrameLayout.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        webView.loadDataWithBaseURL(null, htmlText, null, "UTF-8", null);

        Button button = new Button(context);
        if (Build.VERSION.SDK_INT >= 21) {
            button.setBackground(getResources().getDrawable(R.drawable
                    .button_drawable, null));
        } else if (Build.VERSION.SDK_INT >= 16) {
            button.setBackground(getResources().getDrawable(R.drawable
                    .button_drawable));
        } else {
            button.setBackgroundResource(R.drawable.button_drawable);
        }
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R
                .dimen.edit_text_size_default));
        button.setText("SAVE PDF");
        int padding = (int)getResources().getDimension(R.dimen.text_margin_default);
        button.setPadding(padding, padding, padding, padding);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup
                .LayoutParams.WRAP_CONTENT, (int) getResources().getDimension(R.dimen
                .button_height_default));
        params.gravity = GravityCompat.END | Gravity.RIGHT;
        params.rightMargin = 10;
        params.topMargin = 10;
        frameLayout.addView(button, params);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PrintDocumentAdapter printAdapter;
                String jobName = reportHelper.getPrintManagerJobName(reportDate);
                if (Build.VERSION.SDK_INT >= 21) {
                    printAdapter = webView
                            .createPrintDocumentAdapter(jobName);
                } else if (Build.VERSION.SDK_INT >= 19) {
                    printAdapter = webView
                            .createPrintDocumentAdapter();
                } else {
                    printAdapter = null;
                }

                if (printAdapter != null && Build.VERSION.SDK_INT >= 19) {
                    // Create a print job with name and adapter instance
                    PrintManager printManager = (PrintManager) getActivity()
                            .getSystemService(Context.PRINT_SERVICE);
                    printManager.print(jobName, printAdapter,
                            new PrintAttributes.Builder().build());
                } else {
                                /*
                                Toast.makeText(getActivity(), "Impossible to print because of " +
                                        "old api! Current api: " + Build.VERSION.SDK_INT + ". " +
                                        "Required api: 19", Toast.LENGTH_LONG).show();*/
                }

                File newPdf = reportHelper.getPdfReportFile(reportDate);
                Report report = ((BaseAttachableActivity)container.getContext()).getReport();
                try {
                    report.save(newPdf.getAbsolutePath());
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        return frameLayout;
    }
}
