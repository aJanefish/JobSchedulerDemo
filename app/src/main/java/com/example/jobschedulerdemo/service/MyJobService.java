package com.example.jobschedulerdemo.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.example.jobschedulerdemo.utils.Constant;

import static com.example.jobschedulerdemo.utils.Constant.MESSENGER_INTENT_KEY;
import static com.example.jobschedulerdemo.utils.Constant.MSG_COLOR_START;
import static com.example.jobschedulerdemo.utils.Constant.MSG_COLOR_STOP;
import static com.example.jobschedulerdemo.utils.Constant.WORK_DURATION_KEY;

public class MyJobService extends JobService {

    private static final String TAG = MyJobService.class.getSimpleName();
    private Messenger mActivityMessenger;

    @Override
    public void onCreate() {
        super.onCreate();
        Constant.tag(TAG, "Service created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Constant.tag(TAG, "Service destroyed");
    }

    /**
     * When the app's MainActivity is created, it starts this service. This is so that the
     * activity and this service can communicate back and forth. See "setUiCallback()"
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mActivityMessenger = intent.getParcelableExtra(MESSENGER_INTENT_KEY);
        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(final JobParameters params) {
        Constant.tag(TAG, "on start job: " + params.getJobId());
        sendMessage(MSG_COLOR_START, params.getJobId());

        long duration = params.getExtras().getLong(WORK_DURATION_KEY);
        Constant.tag(TAG, "onStartJob duration:" + duration);
        // Uses a handler to delay the execution of jobFinished().
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendMessage(MSG_COLOR_STOP, params.getJobId());
                jobFinished(params, false);
            }
        }, duration);

        // Return true as there's more work to be done with this job.
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Constant.tag(TAG, "on stop job: " + params.getJobId());
        sendMessage(MSG_COLOR_STOP, params.getJobId());
        return false;
    }


    //服务端通过mActivityMessenger给客户端发送信息
    private void sendMessage(int messageID, @Nullable Object params) {
        // If this service is launched by the JobScheduler, there's no callback Messenger. It
        // only exists when the MainActivity calls startService() with the callback in the Intent.
        if (mActivityMessenger == null) {
            Constant.tag(TAG, "Service is bound, not started. There's no callback to send a message to.");
            return;
        }
        Message m = Message.obtain();
        m.what = messageID;
        m.obj = params;
        try {
            Constant.tag(TAG, "sendMessage m.what:" + m.what + " m.ob:" + m.obj);
            mActivityMessenger.send(m);
        } catch (RemoteException e) {
            Constant.tag(TAG, "Error passing service object back to activity.");
        }
    }
}
