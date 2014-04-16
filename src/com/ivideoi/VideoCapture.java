package com.ivideoi;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class VideoCapture implements Camera.PictureCallback 
{
	public static VideoCapture Instance()
	{
		if(_this == null)
		{
			_this = new VideoCapture();
		}
		return _this;
	}
	
	public VideoCapture()
	{
		_lock = new ReentrantLock(true);
        Thread th = new Thread(
        new Runnable()
		{
			public void run()
			{
				while (true)
				{
					Utils.sleep(8000);
					_lock.lock();
			        _mycamera = Camera.open();
			        Camera.Parameters parameters = _mycamera.getParameters();
			        parameters.setPictureFormat(android.graphics.ImageFormat.JPEG);
			        _mycamera.startPreview();
			        Utils.sleep(1000);
					_mycamera.takePicture(null, null, VideoCapture.this);
					_lock.unlock();
				}
			}
		}
        );
        th.start();
	}
	
    @Override
    public void onPictureTaken(byte[] data, Camera camera) 
    {
    	_lock.lock();
    	_mycamera.release();
    	DataIO.getInstance().uploadbyte(Utils.getTimeString()+".jpg", data);
		//MainActivity.updateView(1,_count++);
		_lock.unlock();
    }
	
    Lock _lock;
	Camera _mycamera;
    private int _count = 0;
    private static VideoCapture _this = null;
}
