package com.wwj.custom.scan.view;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private ScanView mScanView;
    private Handler mHandler;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mScanView = (ScanView) findViewById(R.id.scanView);
        mScanView.start();

        final TextView textView = (TextView) findViewById(R.id.speedText);

        mHandler = new Handler();

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                count++;
                textView.setText(String.format("%s%s", count, "%"));
                if (count == 100) {
                    mScanView.stop();
                    textView.setText("OK");
                } else {
                    mHandler.postDelayed(this, 1000);
                }
            }
        });
    }
}
