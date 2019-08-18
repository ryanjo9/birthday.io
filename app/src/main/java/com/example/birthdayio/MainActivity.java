package com.example.birthdayio;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private boolean jobRunning;
    private static final int MS_IN_SECONDS = 1000;
    private static final int SECONDS_IN_MINUTES = 60;
    private static final int MINUTES_IN_HOUR = 60;
    private static final int HOURS_IN_DAY= 24;
    private static final int JOB_ID = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobRunning = scheduler.getPendingJob(JOB_ID) != null;
        updateStatus();
    }

    public void scheduleJob(View v) {
        ComponentName componentName = new ComponentName(this, TextService.class);
        JobInfo info = new JobInfo.Builder(JOB_ID, componentName)
                .setPeriodic(MS_IN_SECONDS * SECONDS_IN_MINUTES * MINUTES_IN_HOUR * HOURS_IN_DAY)
                .setPersisted(true)
                .build();
        try {
            JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            scheduler.schedule(info);
            jobRunning = true;
            updateStatus();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void cancelJob(View v) {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
        jobRunning = false;
        updateStatus();
    }

    public void updateStatus() {
        TextView status = findViewById(R.id.statusText);
        String message = jobRunning ? "Text service is running" : "Text service is not running";
        status.setText(message);
    }
}
