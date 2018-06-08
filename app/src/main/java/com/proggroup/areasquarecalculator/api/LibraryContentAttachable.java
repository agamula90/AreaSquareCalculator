package com.proggroup.areasquarecalculator.api;

import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;

import graph.approximation.utils.Report;

public interface LibraryContentAttachable {
    FragmentManager getSupportFragmentManager();
    int getFragmentContainerId();
    DrawerLayout getDrawerLayout();
    int getToolbarId();
    int getLeftDrawerFragmentId();
    void setReport(Report report);
    Report getReport();
}
