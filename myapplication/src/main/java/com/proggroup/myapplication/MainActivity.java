package com.proggroup.myapplication;

import android.support.v4.widget.DrawerLayout;

import com.proggroup.areasquarecalculator.activities.BaseAttachableActivity;

public class MainActivity extends BaseAttachableActivity {

    @Override
    public int getFragmentContainerId() {
        return R.id.fragment_container;
    }

    @Override
    public DrawerLayout getDrawerLayout() {
        return (DrawerLayout) findViewById(R.id.drawer_layout);
    }

    @Override
    public int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    public int getLeftDrawerFragmentId() {
        return R.id.fragment_select_category;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }
}
