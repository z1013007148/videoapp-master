package com.example.px.videoapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.LogPrinter;
import android.util.Range;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Policy;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.netty.channel.MessageSizeEstimator;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int NOT_NOTICE = 2;//如果勾选了不再询问
    private boolean isPreview = false, isRecording = false;
    private Camera mCamera;
    private CameraPreview mPreview;
    private MediaRecorder mMediaRecorder;
    private RelativeLayout preview;
    private Button btn_record;           //录像摄像头
    private Button myMessage;


    private float forTimeDelay_time = 1, backTimeDelay_time = 1, betweenTimeDelay_time = 0;
    private int loopTimes_num = 1;

    private ArrayList<String> musics;
    private File[] files;
    private int musicNow;

    private MediaPlayer player;

    private String musicType, timeNow;
    private int bitRate = 5;
    private int mWidth,mHeight;

    private String[] seq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initdata();
        setContentView(R.layout.activity_main);
        bindViews();
        myMessage = findViewById(R.id.myMessage);

        requestPower();

        getData();
        init();



    }
    public void requestPower() {
//判断是否已经赋予权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {//这里可以写个对话框之类的项向用户解释为什么要申请权限，并在对话框的确认键后续再次申请权限.它在用户选择"不再询问"的情况下返回false
            } else {
                //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 1);
            }
        }
        //判断是否已经赋予权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {//这里可以写个对话框之类的项向用户解释为什么要申请权限，并在对话框的确认键后续再次申请权限.它在用户选择"不再询问"的情况下返回false
            } else {
                //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,}, 1);
            }
        }
        //判断是否已经赋予权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {//这里可以写个对话框之类的项向用户解释为什么要申请权限，并在对话框的确认键后续再次申请权限.它在用户选择"不再询问"的情况下返回false
            } else {
                //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO,}, 1);
            }
        }
        //判断是否已经赋予权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {//这里可以写个对话框之类的项向用户解释为什么要申请权限，并在对话框的确认键后续再次申请权限.它在用户选择"不再询问"的情况下返回false
            } else {
                //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA,}, 1);
            }
        }
    }

    public void getData(){
        Intent intent=getIntent();

        forTimeDelay_time=intent.getFloatExtra("forTimeDelay",forTimeDelay_time);
        backTimeDelay_time=intent.getFloatExtra("backTimeDelay",backTimeDelay_time);
        betweenTimeDelay_time=intent.getFloatExtra("betweenTimeDelay",betweenTimeDelay_time);
        loopTimes_num=intent.getIntExtra("loopTime",loopTimes_num);
        bitRate=intent.getIntExtra("bieRate",bitRate);

        File filePar = new File(Environment.getExternalStorageDirectory()+"/videoappVideo/music/");
        //如果不存在这个文件夹就去创建
        if (!filePar.exists()){

            filePar.mkdirs();
        }
        File file = new File(Environment.getExternalStorageDirectory()+"/videoappVideo/music");
        files=file.listFiles();


        int start,end;
        String name[]=new String[files.length];
        seq=new String[files.length];
        String str;
        for(int i = 0;i<files.length;i++){
            name[i]=files[i].getAbsolutePath();
            start=name[i].lastIndexOf("/")+1;
            end=name[i].lastIndexOf("_");
            str=name[i].substring(start,end);
            seq[Integer.parseInt(str)]=name[i];
        }
        Toast.makeText(MainActivity.this,"发现"+files.length+"段音频",Toast.LENGTH_SHORT).show();
    }


    /**
     * 初始化摄像头参数
     */
    private void initCamera() {
        //设备支持摄像头才创建实例
        if (checkCameraHardware(MainActivity.this)){
            mCamera = getCameraInstance();//打开硬件摄像头，这里导包得时候一定要注意是android.hardware.Camera
        }else{
            Toast.makeText(MainActivity.this,"当前设备不支持摄像头",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 判断摄像头是否存在
     * @param context
     * @return
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // 摄像头存在
            return true;
        } else {
            // 摄像头不存在
            return false;
        }

    }

    /**
     * 获取Camera实例
     * @return Camera实例
     */
    public  Camera getCameraInstance(){

        Camera c = null;
        //android 6.0以后必须动态调用权限
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) MainActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    3);
        }else {
            try {
                c = Camera.open(0); // 试图获取Camera实例
            } catch (Exception e){
                Log.e("sda",e.toString());
                // 摄像头不可用（正被占用或不存在）
            }
        }
        return c; // 不可用则返回null
    }

    public void loadMusic()
    {
        String addr;

        musics = new ArrayList<String>();
        for(int i = 0;i<files.length;i++){
            for(int j = 0;j<loopTimes_num;j++){
                addr=seq[i];
                musics.add(addr);
            }
        }


    }
    /**
     * 初始化数据
     */
    private void init() {
        //初始化音频
        loadMusic();

        // 创建Camera实例
        initCamera();
        if(mCamera != null){
            // 创建Preview view并将其设为activity中的内容
            mPreview = new CameraPreview(this, mCamera);
            preview.addView(mPreview,0);

            try {
                Toast.makeText(MainActivity.this,preview.getChildCount(),Toast.LENGTH_SHORT).show();
            }catch (Exception e){

            }
        }else{
            Toast.makeText(MainActivity.this,"打开摄像头失败",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {//切换到后台
        closeCamera();
        super.onPause();
    }

    private void closeCamera() {
        if (mCamera != null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            if (mPreview != null){
                mPreview.getHolder().removeCallback(mPreview.getmCallback());
                preview.removeView(mPreview);
            }
        }
    }

    @Override
    protected void onResume() {//前台
        if (mCamera == null){
            init();
        }

        super.onResume();
    }

    private void initCameraData() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);//得到窗口管理器
        Display display  = wm.getDefaultDisplay();//得到当前屏幕
        Camera.Parameters parameters = mCamera.getParameters();//得到摄像头的参数
        parameters.setPreviewSize(display.getWidth(), display.getHeight());//设置预览照片的大小
        parameters.setPreviewFrameRate(3);//设置每秒3帧
        parameters.setPictureFormat(PixelFormat.JPEG);//设置照片的格式
        parameters.setJpegQuality(85);//设置照片的质量
        parameters.setPictureSize(display.getHeight(), display.getWidth());//设置照片的大小，默认是和     屏幕一样大
        mCamera.setParameters(parameters);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 3){
            init();                 //获取权限后在去验证一次
        }else if (requestCode == 4){
            startRecord();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    /**
     * 未绑定页面时的数据初始化操作
     */
    private void initdata() {
        Window window = getWindow();                    //得到窗口
        requestWindowFeature(Window.FEATURE_NO_TITLE);              //请求没有标题
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, //设置全屏
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);    //设置高亮
    }
    /**
     * 绑定视图
     */
    private void bindViews() {
        preview = (RelativeLayout) findViewById(R.id.camera_preview);
        btn_record = (Button)findViewById(R.id.btn_record);
    }

    public void MyThread(){
        //prepareRecord();
        isRecording=true;//开始摄影
        new Thread(){
            public boolean musicDone=false;
            @Override
            public void run() {
                super.run();
                while (isRecording) {
                    if(musicNow<musics.size()) {
                        musicDone=false;
                        prepare();
                        mMediaRecorder.start();
                        myMessage.setText("无声");
                        mCamera.autoFocus(null);//对焦

                        try {
                            Thread.sleep((int) (forTimeDelay_time * 1000));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }//延时
                        if(!isRecording)break;
                        try {//放声音
                            player = new MediaPlayer();
                            player.setDataSource(musics.get(musicNow));
                            player.prepare();
                            player.start();
                            myMessage.setText(musicType);
                            musicNow++;
                            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    musicDone=true;
                                    myMessage.setText("无声");
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        while(true){
                            if(!isRecording)break;
                            if(musicDone){
                                musicDone = false;
                                try {
                                    Thread.sleep((int) ((backTimeDelay_time + betweenTimeDelay_time) * 1000));
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }//延时
                                myMessage.setText("停止");
                                player.release();
                                player = null;
                                mMediaRecorder.stop(); // 停止录像
                                releaseMediaRecorder(); // 释放MediaRecorder对象
                                mCamera.lock();         // 将控制权从MediaRecorder 交回camera
                                break;
                            }
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    else{//播放完毕
                        isRecording = false;
                        musicNow=0;
                        myMessage.setText("未拍摄");
                        btn_record.setText("开始录像");
                        break;//播放结束,跳出线程
                    }

                }
            }
        }.start();
    }
    public void DoRecord(View view){
        try{
            if(isRecording){
                if(player!=null){
                    player.stop();
                    player.release();
                    player=null;
                }
                if(musicNow>0)musicNow--;
                //stopRecord();
                isRecording = false;
                mMediaRecorder.stop(); // 停止录像
                releaseMediaRecorder(); // 释放MediaRecorder对象
                mCamera.lock();         // 将控制权从MediaRecorder 交回camera
                // 通知用户录像已停止
                myMessage.setText("停止");
                showToast("录像已暂停");
                myMessage.setText("暂停");
                btn_record.setText("继续录像");
            }
            else{
                MyThread();
                showToast("录像已开始");
                btn_record.setText("停止录像");
            }

        }catch (Exception e){
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
        }

    }
//    public void DoFocus(View view){
//        mCamera.autoFocus(null);//自动对焦
//    }

    public void TurnSet(View view){
        Intent intent=new Intent(this,MenuActivity.class);
        intent.putExtra("forTimeDelay",forTimeDelay_time);
        intent.putExtra("backTimeDelay",forTimeDelay_time);
        intent.putExtra("betweenTimeDelay",forTimeDelay_time);
        intent.putExtra("loopTimes",loopTimes_num);
        startActivity(intent);

    }
    public static int chooseFixedPreviewFps(Camera.Parameters parameters, int expectedThoudandFps) {
        List<int[]> supportedFps = parameters.getSupportedPreviewFpsRange();
        for (int[] entry : supportedFps) {
            if (entry[0] == entry[1] && entry[0] == expectedThoudandFps) {
                parameters.setPreviewFpsRange(entry[0], entry[1]);
                return entry[0];
            }
        }
        int[] temp = new int[2];
        int guess;
        parameters.getPreviewFpsRange(temp);
        if (temp[0] == temp[1]) {
            guess = temp[0];
        } else {
            guess = temp[1] / 2;
        }
        return guess;
    }
    public void prepare(){
        mMediaRecorder = new MediaRecorder();
        // 第1步：解锁并将摄像头指向MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        // 第2步：指定源 录音权限6.0以上需要动态获取
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);//指定视频文件格式
        // 第3步：指定CamcorderProfile（需要API Level 8以上版本）

        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        // 第4步：指定输出文件
        mMediaRecorder.setOutputFile(getVideoFile());
        // 第5步：指定预览输出
        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
        mWidth=1920;//1280 1920
        mHeight=1080;//960 1080
        mMediaRecorder.setVideoSize(mWidth, mHeight); //设置录制视频尺寸     mWidth   mHeight
        mMediaRecorder.setVideoEncodingBitRate(bitRate * mWidth * mHeight );


        // 第6步：根据以上配置准备MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            //释放资源
            releaseMediaRecorder();
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            //释放资源
            releaseMediaRecorder();
        }
    }
    //准备录制视屏，设置相关参数
    private boolean prepareRecord(){
        if (mCamera == null){
            //showToast("未打开摄像头");
            return  false;
        }
        //如果没有权限
        if (!checkRecordPermiss()){
            //showToast("未获得录音权限");
            return  false;
        }
        btn_record.setText("停止录像");
        try{
            prepare();
        }catch (Exception e){
            return false;
        }
        return true;
    }
    private String getVideoFile(){

        Date date=new Date();
        int start,end;

        timeNow=date.getHours()+"_"+date.getMinutes()+"_" +date.getSeconds();
        musicType=musics.get(musicNow);
        start=musicType.lastIndexOf("/")+1;
        end=musicType.lastIndexOf(".");
        musicType=musicType.substring(start,end);
        File filePar = new File(Environment.getExternalStorageDirectory()+"/videoappVideo/movie/"+musicType);
        //如果不存在这个文件夹就去创建
        if (!filePar.exists()){
            filePar.mkdirs();
        }
        return  Environment.getExternalStorageDirectory() + "/videoappVideo/movie/" + musicType+"/"+ timeNow +".mp4";
    }
    private void releaseMediaRecorder() {
        if (mMediaRecorder != null){
            mMediaRecorder.release();
        }
    }
    //检测录音权限
    private boolean checkRecordPermiss(){
        //如果没有拿到权限，申请权限
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) MainActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    4);
            return  false;
        }
        return  true;
    }




    private void startRecord() {
        // 初始化视频camera
        if (prepareRecord()) {
            // Camera已可用并解锁，MediaRecorder已就绪,
            // 现在可以开始录像

            isRecording = true;
            // 通知用户录像已开始
            btn_record.setText("停止录像");
            showToast("开始录像");

        } else {
            // 准备未能完成，释放camera
            releaseMediaRecorder();
            // 通知用户
            showToast("录制失败");
        }
    }
    //停止录制
    private void stopRecord(){
        // 停止录像并释放camera
        isRecording = false;
        mMediaRecorder.stop(); // 停止录像
        releaseMediaRecorder(); // 释放MediaRecorder对象
        mCamera.lock();         // 将控制权从MediaRecorder 交回camera
        // 通知用户录像已停止
        btn_record.setText("开始录像");

    }
    private void showToast(String msg){
        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
    }
    class CameraPreview extends SurfaceView implements  SurfaceHolder.Callback{

        private SurfaceHolder mHolder;
        private Camera mCamera;
        private final String TAG = "CameraPreview";
        private SurfaceHolder.Callback mCallback;

        public CameraPreview(Context context,Camera camera) {
            super(context);
            mCamera = camera;
            mHolder = getHolder();
            mCallback = this;
            mHolder.addCallback(mCallback);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            mHolder.setFormat(PixelFormat.TRANSPARENT);
            setZOrderOnTop(true);
            setZOrderMediaOverlay(true);
        }
        public SurfaceHolder.Callback getmCallback(){
            return  this.mCallback;
        }

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
                isPreview = true;       //开始预览
            } catch (IOException e) {
                Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            // 如果预览无法更改或旋转，注意此处的事件
            // 确保在缩放或重排时停止预览
            if (mHolder.getSurface() == null){
                // 预览surface不存在
                return;
            }
            // 更改时停止预览
            try {
                mCamera.stopPreview();
            } catch (Exception e){
                // 忽略：试图停止不存在的预览
            }
            // 在此进行缩放、旋转和重新组织格式
            // 以新的设置启动预览
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch (Exception e){

                Log.d(TAG, "Error starting camera preview: " + e.getMessage());
            }
        }
        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            if(mCamera != null) {
                if (isPreview) {            //如果正在预览
                    mCamera.stopPreview();   //停止预览
                    mCamera.release();       //释放资源
                    isPreview = false;
                }
            }
        }
    }
}
