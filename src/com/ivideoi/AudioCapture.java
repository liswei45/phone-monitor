package com.ivideoi;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.R.string;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class AudioCapture
{

	public static AudioCapture Instance()
	{
		if(_this == null)
		{
			_this = new AudioCapture();
		}
		return _this;
	}
	
	public AudioCapture()
	{
		_lock = new ReentrantLock(true);
        _audioBufferPos = 0;
		_mymic = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, 20*1024);
		_mymic.startRecording();

		//capture thread
		Thread audioth1 = new Thread(new Runnable()
		{
			public void run()
			{
				while (true)
				{
					MainActivity.sleep(200);
					_lock.lock();
					_audioBufferPos += _mymic.read(_auioBuffer, _audioBufferPos,_auioBuffer.length - _audioBufferPos);
					_lock.unlock();
				}
				//_mymic.stop();
			}
		});
		audioth1.start();
		
		//record thread
		Thread audioth2 = new Thread(new Runnable()
		{
			public void run()
			{
				while (true)
				{
					MainActivity.sleep(30000);
				
					_lock.lock();
					byte [] datasend = Arrays.copyOfRange(_auioBuffer, 0, _audioBufferPos);
					_audioBufferPos = 0;
					_lock.unlock();
					
					byte [] wav = MainActivity.GetWAV(datasend);
			    	DataIO.getInstance().uploadbyte(MainActivity.getTimeString()+".wav", wav);
					MainActivity.updateView(2,_count++);
				}
			}
		});
		audioth2.start();
	}
	

	byte[] _auioBuffer = new byte[960 * 1024 * 2];
	private Integer _audioBufferPos;
	private AudioRecord _mymic;
    private static AudioCapture _this;
    private int _count = 0;
    Lock _lock;
}
