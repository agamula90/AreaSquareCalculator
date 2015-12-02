package com.proggroup.areasquarecalculator;

import android.os.AsyncTask;

public abstract class BaseLoadTask extends AsyncTask<Void, Void, Boolean> {
    private String mUrl;

    public BaseLoadTask(String mUrl) {
        this.mUrl = mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }
}
