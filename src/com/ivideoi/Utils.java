package com.ivideoi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;

import android.os.Environment;

public class Utils
{
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
	
    public static void sleep(int ms)
    {
		try
		{
			Thread.sleep(ms);
		} catch (InterruptedException e)
		{
			Utils.log(e);
		}
    }
    
    public static String getTimeString()
    {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("ddHHmmss",java.util.Locale.CHINA);
		return sDateFormat.format(new java.util.Date());
    }
}
