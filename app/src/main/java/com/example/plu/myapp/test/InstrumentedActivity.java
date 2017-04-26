package com.example.plu.myapp.test;

import com.example.plu.myapp.main.MainActivity;

public class InstrumentedActivity extends MainActivity {
    public static String TAG = "InstrumentedActivity";

    private FinishListener mListener;

    public void setFinishListener(FinishListener listener) {
        mListener = listener;
    }


    @Override
    public void onDestroy() {
        super.finish();
        if (mListener != null) {
            mListener.onActivityFinished();
        }
        super.onDestroy();
    }

}