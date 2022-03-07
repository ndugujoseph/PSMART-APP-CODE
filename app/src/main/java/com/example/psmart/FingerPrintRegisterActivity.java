package com.example.psmart;


import java.io.DataOutputStream;
import java.io.File;
import java.util.HashMap;

import com.za.finger.ZA_finger;
import com.za.finger.ZAandroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FingerPrintRegisterActivity extends Activity {


    private Button btnopen ;
    private Button btneroll;
    private Button btnsearch;
    private Button btnupchar;
    private ImageView mFingerprintIv ;
    Bitmap bmpDefaultPic;

    private boolean fpflag=false;
    private boolean fpcharflag = false;
    private boolean fperoll = false;
    private boolean fpsearch = false;
    private boolean isfpon  = false;




    private TextView mtvMessage;
    long ssart = System.currentTimeMillis();
    long ssend = System.currentTimeMillis();
    private Handler objHandler_fp;
    //private HandlerThread thread;



    private int testcount = 0;
    private final ZAandroid a6 = new ZAandroid();
    private int fpcharbuf = 1;
    private final byte[] pTempletbase = new byte[2304];

    private final String TAG = "zazdemo";
    private final int DEV_ADDR = 0xffffffff;
    private final byte[] pPassword = new byte[4];
    private Handler objHandler_3 ;
    private int rootqx = 1;///0 noroot  1root
    //private int defDeviceType =  2;//zaz060
    private int defDeviceType =  12;//zaz050
    private int defiCom = 4;//;
    private final boolean isshowbmp = false;

    private int iPageID = 0;
    Context ahandle;
    //////////////////


    public static final int opensuccess = 101;
    public static final int openfail = 102;
    public static final int usbfail = 103;

    @SuppressLint("HandlerLeak")
    private final Handler m_fEvent = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            String temp  = null;
            switch (msg.what) {
                case opensuccess:
                    temp =getResources().getString(R.string.opensuccess_str);
                    mtvMessage.setText(temp);
                    //btnopen.setText(getResources().getString(R.string.close_str));
                    break;
                case openfail:
                    temp =getResources().getString(R.string.openfail_str);
                    mtvMessage.setText(temp);
                    //btnopen.setText(getResources().getString(R.string.open_str));
                    break;
                case usbfail:
                    temp =getResources().getString(R.string.usbfail_str);
                    mtvMessage.setText(temp);
                    //btnopen.setText(getResources().getString(R.string.open_str));
                    break;
            }
        }
    };

    private void Sleep(int times)
    {
        try {
            Thread.sleep(times);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private void skipshow(String Str)
    {
        Toast.makeText(ahandle,Str,Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_print_register);

        mtvMessage =  findViewById(R.id.textView2);
        mFingerprintIv = findViewById(R.id.imageView1);
        btnopen =  findViewById(R.id.btnopen);
        btneroll = findViewById(R.id.btneroll);
        btnsearch = findViewById(R.id.btnsearch);
        btnOnClick();


        objHandler_fp = new Handler();

        ahandle = this;		//页面句柄
        rootqx = 1;			//系统权限(0:not root  1:root)
        defDeviceType=12;	//设备通讯类型(2:usb  1:串口)
        defiCom= 6;			//设备波特率(1:9600 2:19200 3:38400 4:57600 6:115200  usb无效)

    }



    private void btnOnClick()
    {
        //打开
        btnopen.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            byte[] pPassword = new byte[4];
            skipshow("open");
            Runnable r = () -> {
                isusbfinshed = 3;
                ZA_finger fppower = new ZA_finger();
                //fppower.finger_power_on();
                Sleep(1000);
                OpenDev();
            };
            Thread s = new Thread(r);
            s.start();
        });

        btneroll.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            setflag(true);
            Sleep(500);
            fperoll = false;
            objHandler_fp.removeCallbacks(fpcharTasks);
            objHandler_fp.removeCallbacks(fperollTasks);
            objHandler_fp.removeCallbacks(fpsearchTasks);
            objHandler_fp.removeCallbacks(fpTasks);

            erollfp();
        });
        //搜索指纹
        btnsearch.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            setflag(true);
            Sleep(500);
            fpsearch = false;
            objHandler_fp.removeCallbacks(fpcharTasks);
            objHandler_fp.removeCallbacks(fperollTasks);
            objHandler_fp.removeCallbacks(fpsearchTasks);
            objHandler_fp.removeCallbacks(fpTasks);

            searchfp();
        });


    }


    //打开设备
    private void OpenDev() {
        // TODO Auto-generated method stub
        Log.i(TAG,"start Opendev");
        int status = -1;
        rootqx = 1;
        //同参数：（0:256x288 1:256x360）
        int IMG_SIZE = 0;
        int defiBaud = 6;
        //	skipshow("tryusbroot");
        Log.i(TAG,"use by root ");
        LongDunD8800_CheckEuq();
        status = a6.ZAZOpenDevice(-1, 12, defiCom, defiBaud, 0, 0);
        Log.i(TAG,"status =  "+status + "  (1:success other：error)");
        if(status == 0 ){
            status = a6.ZAZVfyPwd(DEV_ADDR, pPassword) ;
            a6.ZAZSetImageSize(IMG_SIZE);
        }
        else{
            rootqx = 0;
        }

        //if(false)
        if( 0 == rootqx)
        {
            Log.i(TAG,"use by not root ");
            device = null;
            isusbfinshed  = 0;
            int fd = 0;
            defDeviceType = 12;
            isusbfinshed = getrwusbdevices();
            //skipshow("watting a time");
            Log.i(TAG,"waiting user put root ");
            if(!WaitForInterfaces())  {
                m_fEvent.sendMessage(m_fEvent.obtainMessage(usbfail, R.id.btnopen, 0));
                return;
            }
            fd = OpenDeviceInterfaces();
            if(fd == -1)
            {
                m_fEvent.sendMessage(m_fEvent.obtainMessage(usbfail, R.id.btnopen, 0));
                return;
            }
            Log.e(TAG, "open fd: " + fd);
            status = a6.ZAZOpenDevice(fd, defDeviceType, defiCom, defiBaud, 0, 0);
            Log.e("ZAZOpenDeviceEx",""+defDeviceType +"  "+defiCom+"   "+ defiBaud +"  status "+status);
            if(status == 0 ){
                status = a6.ZAZVfyPwd(DEV_ADDR, pPassword) ;
                a6.ZAZSetImageSize(IMG_SIZE);
            }
        }
        Log.e(TAG, " open status: " + status);
        if(status == 0){
            m_fEvent.sendMessage(m_fEvent.obtainMessage(opensuccess, R.id.btnopen, 0)  );
        }
        else{
            m_fEvent.sendMessage(m_fEvent.obtainMessage(openfail, R.id.btnopen, 0));
            if(defDeviceType == 2)
                defDeviceType =5;
            else if(defDeviceType ==5)
                defDeviceType =12;
            else if(defDeviceType ==12)
                defDeviceType =15;
            else
                defDeviceType =2;
        }
    }





    private final Runnable fpTasks = new Runnable() {
        public void run()// 运行该服务执行此函数
        {
            String temp="";
            long st = System.currentTimeMillis();
            long sd = System.currentTimeMillis();
            long timecount=0;
            ssend = System.currentTimeMillis();
            timecount = (ssend - ssart);
            if (timecount >10000)
            {
                temp =getResources().getString(R.string.readfptimeout_str)+"\r\n";
                mtvMessage.setText(temp);
                return;
            }
            if(fpflag){
                temp =getResources().getString(R.string.stopgetimage_str)+"\r\n";
                mtvMessage.setText(temp);
                return;
            }
            int nRet = 0;
            st = System.currentTimeMillis();
            nRet = a6.ZAZGetImage(DEV_ADDR);
            sd = System.currentTimeMillis();
            timecount = (sd - st);
            temp = getResources().getString(R.string.getimagesuccess_str);
            st = System.currentTimeMillis();
            if(nRet  == 0)
            {
                testcount = 0;
                int[] len = { 0, 0 };
                byte[] Image = new byte[256 * 360];
                a6.ZAZUpImage(DEV_ADDR, Image, len);
                sd = System.currentTimeMillis();
                timecount = (sd - st);
                temp += getResources().getString(R.string.upimagesuccess_str) + "耗时:"+timecount+"ms\r\n";
                mtvMessage.setText(temp);

                @SuppressLint("SdCardPath") String str = "/mnt/sdcard/test.bmp";
                a6.ZAZImgData2BMP(Image, str);
                bmpDefaultPic = BitmapFactory.decodeFile(str,null);
                mFingerprintIv.setImageBitmap(bmpDefaultPic);
            }
            else if(nRet==a6.PS_NO_FINGER){
                temp = getResources().getString(R.string.readingfp_str)+((10000-(ssend - ssart)))/1000 +"."+(1000-(ssend - ssart)%1000) +"s";
                mtvMessage.setText(temp);
                objHandler_fp.postDelayed(fpTasks, 100);
            }
            else if(nRet==a6.PS_GET_IMG_ERR){
                temp =getResources().getString(R.string.getimageing_str);
                Log.d(TAG, temp+"2: "+nRet);
                objHandler_fp.postDelayed(fpTasks, 100);
                mtvMessage.setText(temp);
            }else if(nRet == -2)
            {
                testcount ++;
                if(testcount <3){
                    temp = getResources().getString(R.string.readingfp_str)+((10000-(ssend - ssart)))/1000 +"s";
                    isfpon = false;
                    mtvMessage.setText(temp);
                    objHandler_fp.postDelayed(fpTasks, 10);
                }
                else{
                    temp =getResources().getString(R.string.Communicationerr_str);
                    Log.d(TAG, temp+": "+nRet);
                    mtvMessage.setText(temp);
                }
            }
            else
            {
                temp =getResources().getString(R.string.Communicationerr_str);
                Log.d(TAG, temp+"2: "+nRet);
                mtvMessage.setText(temp);
            }

        }
    };


    private final Runnable fpcharTasks = new Runnable() {
        public void run()// 运行该服务执行此函数
        {
            String temp="";
            long st = System.currentTimeMillis();
            long sd = System.currentTimeMillis();
            long timecount=0;
            ssend = System.currentTimeMillis();
            timecount = (ssend - ssart);
            if (timecount >10000)
            {
                temp =getResources().getString(R.string.readfptimeout_str)+"\r\n";
                mtvMessage.setText(temp);
                return;
            }
            if(fpcharflag){
                temp =getResources().getString(R.string.stopgetchar_str)+"\r\n";
                mtvMessage.setText(temp);
                return;
            }
            int nRet = 0;
            st = System.currentTimeMillis();
            nRet = a6.ZAZGetImage(DEV_ADDR);
            sd = System.currentTimeMillis();
            timecount = (sd - st);
            temp = getResources().getString(R.string.getimagesuccess_str);
            st = System.currentTimeMillis();

            if(nRet  == 0)
            {
                if(isshowbmp)
                {
                    int[] len = { 0, 0 };
                    byte[] Image = new byte[256 * 360];
                    a6.ZAZUpImage(DEV_ADDR, Image, len);
                    sd = System.currentTimeMillis();
                    timecount = (sd - st);
                    temp += getResources().getString(R.string.upimagesuccess_str) + "耗时:"+timecount+"ms\r\n";
                    st = System.currentTimeMillis();
                    mtvMessage.setText(temp);

                    @SuppressLint("SdCardPath") String str = "/mnt/sdcard/test.bmp";
                    a6.ZAZImgData2BMP(Image, str);
                    bmpDefaultPic = BitmapFactory.decodeFile(str,null);
                    mFingerprintIv.setImageBitmap(bmpDefaultPic);
                }
                nRet= a6.ZAZGenChar(DEV_ADDR, a6.CHAR_BUFFER_A);// != PS_OK) {
                if(nRet ==a6.PS_OK)
                {
                    sd = System.currentTimeMillis();
                    timecount = (sd - st);
                    temp = getResources().getString(R.string.getcharsuccess_str) + "耗时:"+timecount+"ms\r\n";
                    st = System.currentTimeMillis();
                    int[] iTempletLength = { 0, 0 };
                    byte[] pTemplet = new byte[512];
                    a6.ZAZSetCharLen(512);
                    nRet=a6.ZAZUpChar(DEV_ADDR,a6.CHAR_BUFFER_A, pTemplet, iTempletLength);
                    if(nRet ==a6.PS_OK)
                    {
                        sd = System.currentTimeMillis();
                        timecount = (sd - st);
                        temp += getResources().getString(R.string.upcharsuccess_str) + "耗时:"+timecount+"ms\r\n";
                        st = System.currentTimeMillis();
                        temp +=charToHexString(pTemplet,20);
                        temp += ".....\r\n";
                        st = System.currentTimeMillis();
                        mtvMessage.setText(temp);
                        Log.e("ssss ","特征: "+charToHexString(pTemplet,512));
                    }
                    nRet = a6.ZAZDownChar(DEV_ADDR, a6.CHAR_BUFFER_A, pTemplet, iTempletLength[0]);
                    if(nRet ==a6.PS_OK)
                    {
                        sd = System.currentTimeMillis();
                        timecount = (sd - st);
                        temp += getResources().getString(R.string.downsuccess_str) + "耗时:"+timecount+"ms\r\n";
                        st = System.currentTimeMillis();
                        mtvMessage.setText(temp);
                    }
                }
                else
                {	temp =getResources().getString(R.string.getfailchar_str);
                    mtvMessage.setText(temp);
                    ssart = System.currentTimeMillis();
                    objHandler_fp.postDelayed(fpcharTasks, 1000);

                }
            }
            else if(nRet==a6.PS_NO_FINGER){
                temp = getResources().getString(R.string.readingfp_str)+((10000-(ssend - ssart)))/1000 +"."+(1000-(ssend - ssart)%1000) +"s";
                mtvMessage.setText(temp);
                objHandler_fp.postDelayed(fpcharTasks, 10);
            }else if(nRet==a6.PS_GET_IMG_ERR){
                temp =getResources().getString(R.string.getimageing_str);
                Log.d(TAG, temp+"1: "+nRet);
                objHandler_fp.postDelayed(fpcharTasks, 10);
                mtvMessage.setText(temp);
            }else if(nRet == -2)
            {
                testcount ++;
                if(testcount <3){
                    temp = getResources().getString(R.string.readingfp_str)+((10000-(ssend - ssart)))/1000 +"."+(1000-(ssend - ssart)%1000) +"s";
                    isfpon = false;
                    mtvMessage.setText(temp);
                    objHandler_fp.postDelayed(fpcharTasks, 10);
                }
                else{
                    temp =getResources().getString(R.string.Communicationerr_str);
                    Log.d(TAG, temp+": "+nRet);
                    mtvMessage.setText(temp);
                }
            }
            else
            {
                temp =getResources().getString(R.string.Communicationerr_str);
                Log.d(TAG, temp+"1: "+nRet);
                mtvMessage.setText(temp);
            }

        }
    };


    public void erollfp()
    {
        ssart = System.currentTimeMillis();
        ssend = System.currentTimeMillis();
        fpcharbuf= 1;
        isfpon = false;
        testcount = 0;
        objHandler_fp.postDelayed(fperollTasks, 0);
    }

    private final Runnable fperollTasks = new Runnable() {
        public void run()// 运行该服务执行此函数
        {
            String temp="";
            long timecount=0;
            ssend = System.currentTimeMillis();
            timecount = (ssend - ssart);
            if (timecount >10000)
            {
                temp =getResources().getString(R.string.readfptimeout_str)+"\r\n";
                mtvMessage.setText(temp);
                return;
            }
            if(fperoll){
                temp =getResources().getString(R.string.stoperoll_str)+"\r\n";
                mtvMessage.setText(temp);
                return;
            }
            int nRet = 0;
            nRet = a6.ZAZGetImage(DEV_ADDR);
            if(nRet  == 0)
            {
                if(isfpon){
                    temp =getResources().getString(R.string.pickupfinger_str);
                    mtvMessage.setText(temp);
                    ssart = System.currentTimeMillis();
                    objHandler_fp.postDelayed(fperollTasks, 100);
                    return;
                }
                if(isshowbmp)
                {
                    int[] len = { 0, 0 };
                    byte[] Image = new byte[256 * 360];
                    a6.ZAZUpImage(DEV_ADDR, Image, len);
                    @SuppressLint("SdCardPath") String str = "/mnt/sdcard/test.bmp";
                    a6.ZAZImgData2BMP(Image, str);
                    temp ="获取图像成功";
                    mtvMessage.setText(temp);
                    Bitmap bmpDefaultPic;
                    bmpDefaultPic = BitmapFactory.decodeFile(str,null);
                    mFingerprintIv.setImageBitmap(bmpDefaultPic);
                }
                nRet= a6.ZAZGenChar(DEV_ADDR, fpcharbuf);// != PS_OK) {
                if(nRet ==a6.PS_OK  )
                {
                    fpcharbuf++;
                    isfpon = true;
                    if(fpcharbuf > 2){
                        nRet = a6.ZAZRegModule(DEV_ADDR);
                        if(nRet != a6.PS_OK)
                        {
                            temp =getResources().getString(R.string.RegModulefail_str);
                            mtvMessage.setText(temp);
                        }
                        else{
                            nRet = a6.ZAZStoreChar(DEV_ADDR, 1, iPageID);
                            if(nRet == a6.PS_OK){
                                temp =getResources().getString(R.string.erollsuccess_str)+iPageID;
                                int[] iTempletLength = new int[1];
                                nRet=a6.ZAZUpChar(DEV_ADDR,1, pTempletbase, iTempletLength);
                                //System.arraycopy(pTemplet, 0, pTempletbase, 0, 2304);
                                mtvMessage.setText(temp);
                                iPageID++;
                            }
                            else
                            {
                                temp =getResources().getString(R.string.erollfail_str);
                                mtvMessage.setText(temp);
                            }
                        }
                    }
                    else
                    {
                        temp =getResources().getString(R.string.getfpsuccess_str);
                        mtvMessage.setText(temp);
                        ssart = System.currentTimeMillis();
                        objHandler_fp.postDelayed(fperollTasks, 500);
                    }
                }
                else
                {	temp =getResources().getString(R.string.getfailchar_str);
                    mtvMessage.setText(temp);
                    ssart = System.currentTimeMillis();
                    objHandler_fp.postDelayed(fperollTasks, 1000);
                }
            }
            else if(nRet==a6.PS_NO_FINGER){
                temp = getResources().getString(R.string.readingfp_str)+((10000-(ssend - ssart)))/1000 +"."+(1000-(ssend - ssart)%1000) +"s";
                isfpon = false;
                mtvMessage.setText(temp);
                objHandler_fp.postDelayed(fperollTasks, 10);
            }else if(nRet==a6.PS_GET_IMG_ERR){
                temp =getResources().getString(R.string.getimageing_str);
                Log.d(TAG, temp+": "+nRet);
                objHandler_fp.postDelayed(fperollTasks, 10);
                mtvMessage.setText(temp);
            }else if(nRet == -2)
            {
                testcount ++;
                if(testcount <3){
                    temp = getResources().getString(R.string.readingfp_str)+((10000-(ssend - ssart)))/1000 +"."+(1000-(ssend - ssart)%1000) +"s";
                    isfpon = false;
                    mtvMessage.setText(temp);
                    objHandler_fp.postDelayed(fperollTasks, 10);
                }
                else{
                    temp =getResources().getString(R.string.Communicationerr_str);
                    Log.d(TAG, temp+": "+nRet);
                    mtvMessage.setText(temp);

                }
            }
            else
            {
                temp =getResources().getString(R.string.Communicationerr_str);
                Log.d(TAG, temp+": "+nRet);
                mtvMessage.setText(temp);

            }

        }
    };


    public void searchfp()
    {
        ssart = System.currentTimeMillis();
        ssend = System.currentTimeMillis();
        fpcharbuf= 1;
        testcount = 0;
        objHandler_fp.postDelayed(fpsearchTasks, 0);
    }

    private final Runnable fpsearchTasks = new Runnable() {
        public void run()// 运行该服务执行此函数
        {
            String temp="";
            long st = System.currentTimeMillis();
            long sd = System.currentTimeMillis();
            long timecount=0;
            int[] id_iscore = new int[1];
            ssend = System.currentTimeMillis();
            timecount = (ssend - ssart);

            if (timecount >10000)
            {
                temp =getResources().getString(R.string.readfptimeout_str)+"\r\n";
                mtvMessage.setText(temp);
                return;
            }
            if(fpsearch){
                temp =getResources().getString(R.string.stopsearch_str)+"\r\n";
                mtvMessage.setText(temp);
                return;
            }
            int nRet = 0;
            nRet = a6.ZAZGetImage(DEV_ADDR);
            sd = System.currentTimeMillis();
            timecount = (sd - st);
            temp += getResources().getString(R.string.getimagesuccess_str);
            st = System.currentTimeMillis();
            if(nRet  == 0)
            {
                if(isshowbmp)
                {
                    int[] len = { 0, 0 };
                    byte[] Image = new byte[256 * 360];
                    a6.ZAZUpImage(DEV_ADDR, Image, len);
                    sd = System.currentTimeMillis();
                    timecount = (sd - st);
                    temp += getResources().getString(R.string.upimagesuccess_str) + "耗时:"+timecount+"ms\r\n";
                    st = System.currentTimeMillis();
                    @SuppressLint("SdCardPath") String str = "/mnt/sdcard/test.bmp";
                    a6.ZAZImgData2BMP(Image, str);
                    mtvMessage.setText(temp);
                    Bitmap bmpDefaultPic;
                    bmpDefaultPic = BitmapFactory.decodeFile(str,null);
                    mFingerprintIv.setImageBitmap(bmpDefaultPic);
                }
                nRet= a6.ZAZGenChar(DEV_ADDR, fpcharbuf );// != PS_OK) {
                if(nRet ==a6.PS_OK  )
                {
                    sd = System.currentTimeMillis();
                    timecount = (sd - st);
                 //  temp += getResources().getString(R.string.getcharsuccess_str);
                    st = System.currentTimeMillis();
                    st = System.currentTimeMillis();
                    nRet = a6.ZAZHighSpeedSearch(DEV_ADDR, 1, 0, 1000, id_iscore);
                    if(nRet == a6.PS_OK){
                        sd = System.currentTimeMillis();
                        timecount = (sd - st);
                      //  temp += getResources().getString(R.string.searchsuccess_str);
                        st = System.currentTimeMillis();
                    }
                    else
                    {
                        temp =getResources().getString(R.string.searchfail_str);
                    }
                    mtvMessage.setText(temp);

                }
                else
                {	temp =getResources().getString(R.string.getfailchar_str);
                    mtvMessage.setText(temp);
                    ssart = System.currentTimeMillis();
                    objHandler_fp.postDelayed(fpsearchTasks, 1000);

                }

            }
            else if(nRet==a6.PS_NO_FINGER){
                temp = getResources().getString(R.string.readingfp_str)+((10000-(ssend - ssart)))/1000 +"."+(1000-(ssend - ssart)%1000) +"s";
                mtvMessage.setText(temp);
                objHandler_fp.postDelayed(fpsearchTasks, 10);
            }else if(nRet==a6.PS_GET_IMG_ERR){
                temp =getResources().getString(R.string.getimageing_str);
                Log.d(TAG, temp+": "+nRet);
                objHandler_fp.postDelayed(fpsearchTasks, 10);
                mtvMessage.setText(temp);
            }else if(nRet == -2)
            {
                testcount ++;
                if(testcount <3){
                    temp = getResources().getString(R.string.readingfp_str)+((10000-(ssend - ssart)))/1000 +"."+(1000-(ssend - ssart)%1000) +"s";
                    isfpon = false;
                    mtvMessage.setText(temp);
                    objHandler_fp.postDelayed(fpsearchTasks, 10);
                }
                else{
                    temp =getResources().getString(R.string.Communicationerr_str);
                    Log.d(TAG, temp+": "+nRet);
                    mtvMessage.setText(temp);


                }
            }
            else
            {
                temp =getResources().getString(R.string.Communicationerr_str);
                Log.d(TAG, temp+": "+nRet);
                mtvMessage.setText(temp);

            }

        }
    };




    private void setflag(boolean value)
    {
        fpflag = value;
        fpcharflag = value;
        fperoll = value;
        fpsearch = value;


    }



    /*****************************************
     * 线程   end
     * ***************************************/


    private static String charToHexString(byte[] val,int len) {
        StringBuilder temp= new StringBuilder();
        for(int i=0;i<len;i++)
        {
            String hex = Integer.toHexString(0xff & val[i]);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            temp.append(hex.toUpperCase());
        }
        return temp.toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }





    public void LongDunD8800_CheckEuq()
    {
        Process process = null;
        DataOutputStream os = null;

        // for (int i = 0; i < 10; i++)
        // {
        String path = "/dev/bus/usb/00*/*";
        String path1 = "/dev/bus/usb/00*/*";
        File fpath = new File(path);
        Log.d("*** LongDun D8800 ***", " check path:" + path);
        // if (fpath.exists())
        // {
        String command = "chmod 777 " + path;
        String command1 = "chmod 777 " + path1;
        Log.d("*** LongDun D8800 ***", " exec command:" + command);
        try
        {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command+"\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        }
        catch (Exception e)
        {
            Log.d("*** DEBUG ***", "Unexpected error - Here is what I know: "+e.getMessage());
        }
        //  }
        //  }
    }



    private UsbManager mDevManager = null;
    private UsbDevice device = null;
    public int isusbfinshed = 0;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    public int getrwusbdevices() {

        mDevManager = ((UsbManager) this.getSystemService(Context.USB_SERVICE));
        PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        this.registerReceiver(mUsbReceiver, filter);
        //this.registerReceiver(mUsbReceiver, new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED));
        HashMap<String, UsbDevice> deviceList = mDevManager.getDeviceList();
        Log.e(TAG, "news:" + "mDevManager");


        for (UsbDevice tdevice : deviceList.values()) {
            Log.i(TAG,	tdevice.getDeviceName() + " "+ Integer.toHexString(tdevice.getVendorId()) + " "
                    + Integer.toHexString(tdevice.getProductId()));
            if (tdevice.getVendorId() == 0x2109 && (tdevice.getProductId() == 0x7638))
            {
                Log.e(TAG, " 指纹设备准备好了 ");
                mDevManager.requestPermission(tdevice, permissionIntent);
                return 1;
            }
        }
        Log.e(TAG, "news:" + "mDevManager  end");
        return 2;
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            context.unregisterReceiver(mUsbReceiver);
            isusbfinshed = 0;
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (context) {
                    device = (UsbDevice) intent	.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    Log.e("BroadcastReceiver","3333");
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            Log.e(TAG, "Authorize permission " + device);
                            isusbfinshed = 1;
                        }
                    }
                    else {
                        Log.e(TAG, "permission denied for device " + device);
                        device=null;
                        isusbfinshed = 2;

                    }
                }
            }
        }
    };

    public boolean WaitForInterfaces() {
        int i =0;
        while (device==null || isusbfinshed == 0) {
            i++;
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored){

            }
            if(i>2000){
                isusbfinshed = 2;break;
            }
            if(isusbfinshed == 2)break;
            if(isusbfinshed == 3)break;
        }
        if(isusbfinshed == 2)
            return false;
        return isusbfinshed != 3;
    }

    public int OpenDeviceInterfaces() {
        UsbDevice mDevice = device;
        Log.d(TAG, "setDevice " + mDevice);
        int fd = -1;
        if (mDevice == null) return -1;
        UsbDeviceConnection connection = mDevManager.openDevice(mDevice);
        if (!connection.claimInterface(mDevice.getInterface(0), true)) return -1;

        if (mDevice.getInterfaceCount() < 1) return -1;
        UsbInterface intf = mDevice.getInterface(0);

        if (intf.getEndpointCount() == 0) 	return -1;

        Log.e(TAG, "open connection success!");
        fd = connection.getFileDescriptor();
        return fd;
    }

}
