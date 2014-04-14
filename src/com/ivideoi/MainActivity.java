package com.ivideoi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Properties;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//keep screen on
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
        //init var
		_this = this;
		_uiHandler = new UIHandler();
	    DataIO.isSaveToLocal = false;
	    DataIO.LocalPath = "/temp/";
		try
		{
			File f = new File(Environment.getExternalStorageDirectory().getPath() + "/monitor.conf");
			InputStream inputStream = new FileInputStream(f);
			Properties p = new Properties();
			p.load(inputStream);
			DataIO.ServerPath = (String)p.get("ip");
			inputStream.close();
		}
		catch (Exception e)
		{
			log(e);
		}

		
        //Timer Thread
        //Thread th = new Thread(new Runnable()
        //{
		//	@Override
		//	public void run()
		//	{
		//		while(true)
		//		{
		//			sleep(5000);
		//	    	Message msg = new Message();
		//	    	msg.arg1 = 3;
		//	    	_this._uiHandler.sendMessage(msg);
		//		}
		//	}
        //});
        //th.start();
        
		VideoCapture.Instance();
		AudioCapture.Instance();
	}
	
    public static void sleep(int ms)
    {
		try
		{
			Thread.sleep(ms);
		} catch (InterruptedException e)
		{
			log(e);
		}
    }
    
    public static String getTimeString()
    {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("ddHHmmss",java.util.Locale.CHINA);
		return sDateFormat.format(new java.util.Date());
    }
    
    public static void updateView(int target, int i)
    {
    	Message msg = new Message();
    	msg.arg1 = target;
    	msg.arg2 = i;
    	_this._uiHandler.sendMessage(msg);
    }
    
	public static byte[] InttoByteArray(int iSource)
	{
		byte[] bLocalArr = new byte[4];
		for (int i = 0; i < 4; i++)
		{
			bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
		}
		return bLocalArr;
	}
	public static byte[] ShorttoByteArray(short iSource)
	{
		byte[] bLocalArr = new byte[2];
		for (int i = 0; i < 2; i++)
		{
			bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
		}
		return bLocalArr;
	}
	
	public static void log(String words)
	{
    	File log = new File(Environment.getExternalStorageDirectory(),"monitor.log");
        try
        {
			PrintStream fos = new PrintStream(new FileOutputStream(log, true));
    		SimpleDateFormat sDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss:S",java.util.Locale.CHINA);
			fos.print("[" + sDateFormat.format(new java.util.Date()) + "]" + words + "\n");
            fos.close();
        }
        catch(Exception ee)
        {
        	ee.printStackTrace();
        }
    }
	
    public static void log(Exception e)
	{
		File log = new File(Environment.getExternalStorageDirectory(),"monitor.log");
	    try
	    {
	    	PrintStream fos = new PrintStream(new FileOutputStream(log,true));
    		SimpleDateFormat sDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss:S",java.util.Locale.CHINA);
			fos.print("[" + sDateFormat.format(new java.util.Date()) + "]\n");
	    	e.printStackTrace();
	        e.printStackTrace(fos);
	        fos.close();
	    }
	    catch(Exception ee)
	    {
	    	ee.printStackTrace();
	    }
	}

	public static byte [] GetWAV(byte [] datas)
    {
		byte [] RIFF = {'R','I','F','F'}; 			//"RIFF"标志
		int filelength = 44 + datas.length;			//文件长度
		byte [] WAVE = {'W','A','V','E'};			//"WAVE"标志
		byte [] tmf = {'f','m','t',' '};			//"fmt"标志
		byte [] unused = {16,0,0,0};				//过渡字节（不定）
		byte [] format ={1,0};						// 格式类别
		short channel = 0x0001;						//声道数
		int rate = 8000;							//采样率
		int speed = 16000;							//位速
		short block = 2;							//一个采样多声道数据块大小
		short width = 16;							//一个采样占的bit数
		byte [] data = {'d','a','t','a'};			//数据标记符＂data＂
		int datalen = filelength - 36;				//语音数据的长度，比文件长度小36

		byte [] header = new byte[44+datas.length];
		System.arraycopy(RIFF, 0, header, 0, RIFF.length);
		System.arraycopy(InttoByteArray(filelength), 0, header, 4, InttoByteArray(filelength).length);
		System.arraycopy(WAVE, 0, header, 8, WAVE.length);
		System.arraycopy(tmf, 0, header, 12, tmf.length);
		System.arraycopy(unused, 0, header, 16, unused.length);
		System.arraycopy(format, 0, header, 20, format.length);
		System.arraycopy(ShorttoByteArray(channel), 0, header, 22, ShorttoByteArray(channel).length);
		System.arraycopy(InttoByteArray(rate), 0, header, 24, InttoByteArray(rate).length);
		System.arraycopy(InttoByteArray(speed), 0, header, 28, InttoByteArray(speed).length);
		System.arraycopy(ShorttoByteArray(block), 0, header, 32, ShorttoByteArray(block).length);
		System.arraycopy(ShorttoByteArray(width), 0, header, 34, ShorttoByteArray(width).length);
		System.arraycopy(data, 0, header, 36, data.length);
		System.arraycopy(InttoByteArray(datalen), 0, header, 40, InttoByteArray(datalen).length);
		System.arraycopy(datas, 0, header, 44, datas.length);
		return header;
    }

	private class UIHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch (msg.arg1)
			{
			case 1:
			{
				SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",java.util.Locale.CHINA);
				TextView view1 = (TextView) (MainActivity.this.findViewById(R.id.textView1));
				view1.setText(String.valueOf(msg.arg2)+"    :"+sDateFormat.format(new java.util.Date()));
			}
				break;
			case 2:
			{
				SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",java.util.Locale.CHINA);
				TextView view2 = (TextView) (MainActivity.this.findViewById(R.id.textView2));
				view2.setText(String.valueOf(msg.arg2)+"    :"+sDateFormat.format(new java.util.Date()));
			}
				break;
			}
		}
	}
	
	@Override
	public void onClick(View arg0)
	{
		//EditText edit = (EditText) findViewById(R.id.editText1);
	}	
    
    public UIHandler _uiHandler;
    private static MainActivity _this;
}
