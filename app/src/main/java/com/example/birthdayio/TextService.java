package com.example.birthdayio;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ContentResolver;

public class TextService extends JobService {
    private boolean jobCancelled = false;
    @Override
    public boolean onStartJob(JobParameters params) {
        runBackgroundWork(params);

        return false;
    }

    private void runBackgroundWork(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!jobCancelled) {
                    ContentResolver cr = getContentResolver();
                    new TextBusiness(cr).run();
                }
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        jobCancelled = true;
        return true;
    }

}