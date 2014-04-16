package com.ivideoi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;


public class AudioCapture
{
	public static int CAPTURERATE = 8000;
	
	public static AudioCapture _this = new AudioCapture();
	
	private AudioCapture()
	{
		_running = false;
		_mymic = new AudioRecord(MediaRecorder.AudioSource.MIC, CAPTURERATE, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, CAPTURERATE*5*2);		
		_audioDatas = new ArrayList<AudioData>();
		
		Thread work = new Thread(new Runnable()
		{
			public void run()
			{
				while (true)
				{
					if(_running)
					{
						AudioData ad = new AudioData();
						_mymic.read(ad._data, 0,ad._data.length);
						ad._time = new Date();
						for(int i=0;i<ad._data.length;i++)
						{
							ad._intensity += Math.abs(ad._data[i]);							
						}
						ad._intensity /= ad._data.length;
						synchronized(_audioDatas)
						{
							_audioDatas.add(ad);
						}
					}
					else
					{
						Utils.sleep(500);
					}
				}
			}
		});
		work.start();
	}
	
	public class AudioData
	{
		public short [] _data;
		public Date _time;
		public long _intensity;
		
		public AudioData()
		{
			_data = new short[CAPTURERATE];
			_intensity = 0;
		}
	}
	
	public void start()
	{
		_mymic.startRecording();
		_running = true;
	}
	
	private AudioRecord _mymic;
	public boolean _running;
	public List<AudioData> _audioDatas;
}
