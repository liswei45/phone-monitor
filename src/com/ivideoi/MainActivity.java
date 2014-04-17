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

public class MainActivity extends Activity //implements OnClickListener
{
    public static MainActivity _this;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		_this = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//keep screen on
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
        //get config
	    DataIO.isSaveToLocal = false;
	    DataIO.LocalPath = "/Monitor/";
//		try
//		{
//			File f = new File(Environment.getExternalStorageDirectory().getPath() + "/monitor.conf");
//			InputStream inputStream = new FileInputStream(f);
//			Properties p = new Properties();
//			p.load(inputStream);
//			DataIO.ServerPath = (String)p.get("ip");
//			inputStream.close();
//		}
//		catch (Exception e)
//		{
//			log(e);
//		}

		VideoCapture._this.takePhoto();
		AudioCapture._this.start();
	}
    
    public Handler _uiHandler = new Handler()
    {  
		@Override
		public void handleMessage(Message msg)
		{
//			super.handleMessage(msg);
//			switch (msg.arg1)
//			{
//			case 1:
//			{
//				SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",java.util.Locale.CHINA);
//				TextView view1 = (TextView) (MainActivity.this.findViewById(R.id.textView1));
//				view1.setText(String.valueOf(msg.arg2)+"    :"+sDateFormat.format(new java.util.Date()));
//			}
//				break;
//			case 2:
//			{
//				SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",java.util.Locale.CHINA);
//				TextView view2 = (TextView) (MainActivity.this.findViewById(R.id.textView2));
//				view2.setText(String.valueOf(msg.arg2)+"    :"+sDateFormat.format(new java.util.Date()));
//			}
//				break;
//			}
		}
    };
    
    public enum COMMAND 
    	{
    	  UPDATE_TEXTVIEW1,
    	  UPDATE_TEXTVIEW2,
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
}
