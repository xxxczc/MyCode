package com.czc.myrongdemo.controller;

/*
 * 录音按钮，之所以放到Controller包下，是因为涉及到文件读写、消息发送等事件
 */

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.czc.myrongdemo.Api;
import com.czc.myrongdemo.R;
import com.czc.myrongdemo.adapter.MessageListAdapter;
import com.czc.myrongdemo.utils.MLog;
import com.czc.myrongdemo.utils.uiutils.MUIToast;


public class RecordVoiceBtn extends Button {

    private File myRecAudioFile;

    private Handler mHandler;
    private static final int MIN_INTERVAL_TIME = 1000;// 1s
    private final static int CANCEL_RECORD = 5;
    private final static int SEND_CALLBACK = 6;
    private final static int START_RECORD = 7;
    private final static int RECORD_DENIED_STATUS = 1000;
    //依次为按下录音键坐标、手指离开屏幕坐标、手指移动坐标
    float mTouchY1, mTouchY2, mTouchY;
    private final float MIN_CANCEL_DISTANCE = 300f;
    //依次为开始录音时刻，按下录音时刻，松开录音按钮时刻
    private long startTime, time1, time2;

    private Dialog recordIndicator;

    private static int[] res = { R.drawable.rc_ic_volume_1,
        R.drawable.rc_ic_volume_2, R.drawable.rc_ic_volume_3,
        R.drawable.rc_ic_volume_4, R.drawable.rc_ic_volume_5,
        R.drawable.rc_ic_volume_cancel };
    
    private ImageView mVolumeIv;
    private TextView mRecordHintTv;
    private int mWidth;//屏幕像素宽度
    private double mDensity;

    private MediaRecorder recorder;

    private ObtainDecibelThread mThread;

    private Handler mVolumeHandler;
    private boolean sdCardExist;
    public static boolean mIsPressed = false;
    private Context mContext;
    private Timer timer = new Timer();
    private Timer mCountTimer;
    private boolean isTimerCanceled = false;
    private boolean mTimeUp = false;
    private final MyHandler myHandler = new MyHandler(this);
    private Vibrator mVibrator;

	public MessageListAdapter mMsgListAdapter;

    public RecordVoiceBtn(Context context) {
        super(context);
        init();
    }

    public RecordVoiceBtn(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

    public RecordVoiceBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {
        mVolumeHandler = new ShowVolumeHandler(this);
        Activity activity = (Activity) mContext;
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        mWidth = dm.widthPixels;
        mDensity = dm.density;
        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
    }
    
    public void initConv(Handler handler) {
        this.mHandler = handler;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.setPressed(true);
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
//            	mChatView.startRecordAnim();
                this.setText(mContext.getString(R.string.send_voice_hint));
                mIsPressed = true;
                time1 = System.currentTimeMillis();
                mTouchY1 = event.getY();
                //检查sd卡是否存在
                sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
                if (sdCardExist) {
                    if (isTimerCanceled) {
                        timer = createTimer();
                    }
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            android.os.Message msg = myHandler.obtainMessage();
                            msg.what = START_RECORD;
                            msg.sendToTarget();
                        }
                    }, 500);
                } else {
                    MUIToast.toast(this.getContext(), R.string.sdcard_not_exist_toast);
                    this.setPressed(false);
                    this.setText(mContext.getString(R.string.record_voice_hint));
                    mIsPressed = false;
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
//            	mChatView.stopRecordAnim();
                this.setText(mContext.getString(R.string.record_voice_hint));
                mIsPressed = false;
                this.setPressed(false);
                mTouchY2 = event.getY();
                time2 = System.currentTimeMillis();
                if (time2 - time1 < 500) {
                    cancelTimer();
                    return true;
                } else if (time2 - time1 < 1000) {
                    cancelRecord();
                } else if (mTouchY1 - mTouchY2 > MIN_CANCEL_DISTANCE) {
                    cancelRecord();
                } else if (time2 - time1 < 60000)
                    finishRecord();
                break;
            case MotionEvent.ACTION_MOVE:
                mTouchY = event.getY();
                //手指上滑到超出限定后，显示松开取消发送提示
                if (mTouchY1 - mTouchY > MIN_CANCEL_DISTANCE) {
                    this.setText(mContext.getString(R.string.cancel_record_voice_hint));
                    mVolumeHandler.sendEmptyMessage(CANCEL_RECORD);
//                    mChatView.stopRecordAnim();
//                    mChatView.setWaveHeight(5);
                    if (mThread != null)
                        mThread.exit();
                    mThread = null;
                } else {
                    this.setText(mContext.getString(R.string.send_voice_hint));
                    if (mThread == null) {
                        mThread = new ObtainDecibelThread();
                        mThread.start();
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:// 当手指移动到view外面，会cancel
                this.setText(mContext.getString(R.string.record_voice_hint));
//            	mChatView.stopRecordAnim();
                cancelRecord();
                break;
        }

        return true;
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            isTimerCanceled = true;
        }
        if (mCountTimer != null) {
            mCountTimer.cancel();
            mCountTimer.purge();
        }
    }

    private Timer createTimer() {
        timer = new Timer();
        isTimerCanceled = false;
        return timer;
    }
    
    private void vibrate(){
        mVibrator.vibrate(new long[]{10,50},-1);
    }

    private void initDialogAndStartRecord() {
        //存放录音文件目录
        File destDir = mContext.getFilesDir();
        //录音文件的命名格式
        myRecAudioFile = new File(destDir, new DateFormat().format("yyyyMMdd_hhmmss",
                Calendar.getInstance(Locale.CHINA)) + ".amr");
        if (myRecAudioFile == null) {
            cancelTimer();
            stopRecording();
            MUIToast.toast(mContext, R.string.create_file_failed);
        }
        Log.i("FileCreate", "Create file success file path: " + myRecAudioFile.getAbsolutePath());
        vibrate();
        
        recordIndicator = new Dialog(getContext(), R.style.record_voice_dialog);
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.dialog_record_voice, null);
        
//        Window dialogWindow = recordIndicator.getWindow();
//        dialogWindow.setLayout((int) (0.8 * mWidth),
//				WindowManager.LayoutParams.WRAP_CONTENT);
        
        mVolumeIv = (ImageView) view.findViewById(R.id.dialog_voice_icon);
        mRecordHintTv = (TextView) view.findViewById(R.id.dialog_voice_message);
        mRecordHintTv.setText(mContext.getString(R.string.move_to_cancel_hint));
        startRecording();
        recordIndicator.show();
        recordIndicator.setContentView(view);
    }

    //录音完毕加载 ListView item
    private void finishRecord() {
        cancelTimer();
        stopRecording();
        if (recordIndicator != null)
            recordIndicator.dismiss();

        long intervalTime = System.currentTimeMillis() - startTime;
        if (intervalTime < MIN_INTERVAL_TIME) {
            MUIToast.toast(getContext(), R.string.time_too_short_toast);
            myRecAudioFile.delete();
            return;
        } else {
            if (myRecAudioFile != null && myRecAudioFile.exists()) {
                MediaPlayer mp = MediaPlayer.create(mContext, Uri.parse(myRecAudioFile.getAbsolutePath()));
                //某些手机会限制录音，如果用户拒接使用录音，则需判断mp是否存在
                if (mp != null) {
                    int duration = mp.getDuration() / 1000;//即为时长 是s
                    if (duration < 1)
                        duration = 1;
                    else if (duration > 60)
                        duration = 60;
                    
                    mHandler.obtainMessage(Api.UPLOAD_VOICE_CALLBACK,duration,0,myRecAudioFile.getAbsolutePath()).sendToTarget();
                } else {
                    MUIToast.toast(mContext, R.string.record_voice_permission_request);
                }
            }
        }
    }

    //取消录音，清除计时
    private void cancelRecord() {
        //可能在消息队列中还存在HandlerMessage，移除剩余消息
        mVolumeHandler.removeMessages(56, null);
        mVolumeHandler.removeMessages(57, null);
        mVolumeHandler.removeMessages(58, null);
        mVolumeHandler.removeMessages(59, null);
        mTimeUp = false;
        cancelTimer();
        stopRecording();
        if (recordIndicator != null)
            recordIndicator.dismiss();
        if (myRecAudioFile != null)
            myRecAudioFile.delete();
    }

    private void startRecording() {
    	Log.i("RecordVoiceController", "startRecording");
        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            recorder.setOutputFile(myRecAudioFile.getAbsolutePath());
            myRecAudioFile.createNewFile();
            recorder.prepare();
            recorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mediaRecorder, int i, int i2) {
                    Log.i("RecordVoiceController", "recorder prepare failed!");
                }
            });
            recorder.start();
            startTime = System.currentTimeMillis();
            mCountTimer = new Timer();
            mCountTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mTimeUp = true;
                    android.os.Message msg = mVolumeHandler.obtainMessage();
                    msg.what = 55;
                    Bundle bundle = new Bundle();
                    bundle.putInt("restTime", 5);
                    msg.setData(bundle);
                    msg.sendToTarget();
                    mCountTimer.cancel();
                }
            }, 56000);

        } catch (IOException e) {
        	Log.e("RecordVoiceController", "e:",e);
            e.printStackTrace();
        } catch (RuntimeException e) {
        	Log.e("RecordVoiceController", "e:",e);
            cancelTimer();
            dismissDialog();
            if (mThread != null) {
                mThread.exit();
                mThread = null;
            }
            if (myRecAudioFile != null)
                myRecAudioFile.delete();
            recorder.release();
            recorder = null;
        }

        mThread = new ObtainDecibelThread();
        mThread.start();

    }

    //停止录音，隐藏录音动画
    private void stopRecording() {
        if (mThread != null) {
            mThread.exit();
            mThread = null;
        }
        releaseRecorder();
    }

    public void releaseRecorder() {
        if (recorder != null) {
            try {
                recorder.stop();
            }catch (Exception e){
                MLog.d("RecordVoice", "Catch exception: stop recorder failed!");
            }finally {
                recorder.release();
                recorder = null;
            }
        }
    }

    private class ObtainDecibelThread extends Thread {

        private volatile boolean running = true;

        public void exit() {
            running = false;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (recorder == null || !running) {
                    break;
                }
                try {
                    int x = recorder.getMaxAmplitude();
                    if (x != 0) {
                        int f = (int) (10 * Math.log(x) / Math.log(10));
                        if (f < 20) {
                            mVolumeHandler.sendEmptyMessage(0);
                        } else if (f < 26) {
                            mVolumeHandler.sendEmptyMessage(1);
                        } else if (f < 32) {
                            mVolumeHandler.sendEmptyMessage(2);
                        } else if (f < 38) {
                            mVolumeHandler.sendEmptyMessage(3);
                        } else {
                            mVolumeHandler.sendEmptyMessage(4);
                        }
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    public void dismissDialog() {
        if (recordIndicator != null)
            recordIndicator.dismiss();
        this.setText(mContext.getString(R.string.record_voice_hint));
    }

    /**
     * 录音动画控制
     */
    private static class ShowVolumeHandler extends Handler {

        private final WeakReference<RecordVoiceBtn> mController;

        public ShowVolumeHandler(RecordVoiceBtn controller){
            mController = new WeakReference<RecordVoiceBtn>(controller);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            RecordVoiceBtn controller = mController.get();
            if (controller != null){
                int restTime = msg.getData().getInt("restTime", -1);
                // 若restTime>0, 进入倒计时
                if (restTime > 0) {
                    controller.mTimeUp = true;
                    android.os.Message msg1 = controller.mVolumeHandler.obtainMessage();
                    msg1.what = 60 - restTime + 1;
                    Bundle bundle = new Bundle();
                    bundle.putInt("restTime", restTime - 1);
                    msg1.setData(bundle);
                    //创建一个延迟一秒执行的HandlerMessage，用于倒计时
                    controller.mVolumeHandler.sendMessageDelayed(msg1, 1000);
                    controller.mRecordHintTv.setText(String.format(controller.mContext.getString(R.string.rest_record_time_hint), restTime));
                    // 倒计时结束，发送语音, 重置状态
                } else if (restTime == 0) {
                    controller.finishRecord();
                    controller.setPressed(false);
                    controller.mTimeUp = false;
                    // restTime = -1, 一般情况
                } else {
                    // 没有进入倒计时状态
                    if (!controller.mTimeUp) {
                    	if (msg.what < CANCEL_RECORD) {
                            controller.mRecordHintTv
                                    .setBackgroundColor(Color.TRANSPARENT);
                            controller.mRecordHintTv
                                    .setText(controller.mContext
                                            .getString(R.string.move_to_cancel_hint));
                        } else {
                            controller.mRecordHintTv
                                    .setBackgroundColor(Color.RED);
                            controller.mRecordHintTv
                                    .setText(controller.mContext
                                            .getString(R.string.cancel_record_voice_hint));
                        }
                        // 进入倒计时
                    } else {
                        if (msg.what == CANCEL_RECORD) {
                            controller.mRecordHintTv.setText(controller.mContext
                                    .getString(R.string.cancel_record_voice_hint));
                            if (!mIsPressed)
                                controller.cancelRecord();
                        }
                    }
//                    if (msg.what >=0 && msg.what < 5){
//                    	controller.mChatView.setWaveHeight(msg.what);
//                    }
                    if (controller.mVolumeIv!= null) {
                        controller.mVolumeIv.setImageResource(res[msg.what]);
                    }
//                    if (msg.what <=3) {
//                    	controller.mChatView.getRecordAnimImage().setImageResource(recordRes[msg.what]);
//                    } else {
//                    	controller.mChatView.getRecordAnimImage().setImageResource(recordRes[3]);
//                    }
                }
            }
        }
    }

    private static class MyHandler extends Handler{
        private final WeakReference<RecordVoiceBtn> mController;

        public MyHandler(RecordVoiceBtn controller){
            mController = new WeakReference<RecordVoiceBtn>(controller);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            RecordVoiceBtn controller = mController.get();
            if (controller != null){
                switch (msg.what) {
                    case SEND_CALLBACK:
                        int status = msg.getData().getInt("status", -1);
                        if(status == 803008){
                            return;
                        }else if (status != 0){
                        }
                        controller.mMsgListAdapter.notifyDataSetChanged();
                        break;
                    case START_RECORD:
                        if (mIsPressed)
                            controller.initDialogAndStartRecord();
                        break;
                }
            }
        }
    }
}
