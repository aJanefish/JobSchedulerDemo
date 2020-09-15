package com.example.jobschedulerdemo;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.jobschedulerdemo.service.MyJobService;
import com.example.jobschedulerdemo.utils.Constant;

import java.lang.ref.WeakReference;

import static com.example.jobschedulerdemo.utils.Constant.MESSENGER_INTENT_KEY;
import static com.example.jobschedulerdemo.utils.Constant.MSG_COLOR_START;
import static com.example.jobschedulerdemo.utils.Constant.MSG_COLOR_STOP;
import static com.example.jobschedulerdemo.utils.Constant.MSG_UNCOLOR_START;
import static com.example.jobschedulerdemo.utils.Constant.MSG_UNCOLOR_STOP;
import static com.example.jobschedulerdemo.utils.Constant.WORK_DURATION_KEY;

public class MainActivity extends AppCompatActivity {

    private IncomingMessageHandler mHandler;
    private int mJobId = 0;
    private ComponentName mServiceComponent;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new IncomingMessageHandler(this);
        mServiceComponent = new ComponentName(this, MyJobService.class);


    }

    @Override
    protected void onStop() {
        // A service can be "started" and/or "bound". In this case, it's "started" by this Activity
        // and "bound" to the JobScheduler (also called "Scheduled" by the JobScheduler). This call
        // to stopService() won't prevent scheduled jobs to be processed. However, failing
        // to call stopService() would keep it alive indefinitely.
        stopService(new Intent(this, MyJobService.class));
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start service and provide it a way to communicate with this class.
        Intent startServiceIntent = new Intent(this, MyJobService.class);
        Messenger messengerIncoming = new Messenger(mHandler);
        startServiceIntent.putExtra(MESSENGER_INTENT_KEY, messengerIncoming);
        startService(startServiceIntent);
    }

    public void scheduleJob(View view) {
        JobInfo.Builder builder = new JobInfo.Builder(mJobId++, new ComponentName(this, MyJobService.class));

        //String delay = mDelayEditText.getText().toString();
        String delay = "1";
        //设置延迟时间
        if (!TextUtils.isEmpty(delay)) {
            builder.setMinimumLatency(Long.parseLong(delay) * 1000);
        }
        //String deadline = mDeadlineEditText.getText().toString();
        String deadline = "15";
        //设置最后期限
        if (!TextUtils.isEmpty(deadline)) {
            builder.setOverrideDeadline(Long.parseLong(deadline) * 1000);
        }
        //设置网络类型 - 设定工作需要的基本网络描述
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        //如果你需要对网络能力进行更精确的控制
        //builder.setRequiredNetwork()

        //是否需要Idle - 默认false
        builder.setRequiresDeviceIdle(false);
        //是否需要充电 - 默认false
        builder.setRequiresCharging(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //指定要运行此作业，设备的电池电量不得过低。
            builder.setRequiresBatteryNotLow(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //指定要运行此作业，设备的可用存储空间不得过低
            builder.setRequiresStorageNotLow(false);
        }

        //设置额外参数
        PersistableBundle extras = new PersistableBundle();
        //String workDuration = mDurationTimeEditText.getText().toString();
        String workDuration = "";
        if (TextUtils.isEmpty(workDuration)) {
            workDuration = "1";
        }
        extras.putLong(WORK_DURATION_KEY, Long.parseLong(workDuration) * 1000);

        builder.setExtras(extras);


        JobInfo jobInfo = builder.build();
        Constant.tag(TAG, "Scheduling job:" + builder);
        Constant.tag(TAG, "Scheduling job:" + jobInfo);
        Constant.tag(TAG, "Scheduling job:" + jobInfo.getId());
        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.schedule(jobInfo);
    }

    public void cancelAllJobs(View view) {
    }

    /**
     * A {@link Handler} allows you to send messages associated with a thread. A {@link Messenger}
     * uses this handler to communicate from {@link MyJobService}. It's also used to make
     * the start and stop views blink for a short period of time.
     */
    private static class IncomingMessageHandler extends Handler {

        // Prevent possible leaks with a weak reference.
        private WeakReference<MainActivity> mActivity;

        IncomingMessageHandler(MainActivity activity) {
            super(/* default looper */);
            this.mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity mainActivity = mActivity.get();
            if (mainActivity == null) {
                // Activity is no longer available, exit.
                return;
            }

            Message m;
            switch (msg.what) {
                case MSG_COLOR_START:
                    Constant.tag(TAG, "MSG_COLOR_START:" + msg.obj);
                    break;
                case MSG_COLOR_STOP:
                    Constant.tag(TAG, "MSG_COLOR_STOP:" + msg.obj);
                    break;
                case MSG_UNCOLOR_START:
                    Constant.tag(TAG, "MSG_UNCOLOR_START:" + msg.obj);
                    break;
                case MSG_UNCOLOR_STOP:
                    Constant.tag(TAG, "MSG_UNCOLOR_STOP:" + msg.obj);
                    break;
            }
        }

    }


}