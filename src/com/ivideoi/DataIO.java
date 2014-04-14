package com.ivideoi;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;

import android.os.Environment;

public class DataIO
{
	public static Boolean isRPC = false;
	public static Boolean isSaveToLocal = true;
	public static String LocalPath = "/temp/";
	public static String ServerPath = "192.168.1.215";
	
	private static DataIO _this;
	
	private DataIO(){};
	
	public static DataIO getInstance()
	{
		if(_this == null)
		{
			_this = new DataIO();
		}
		return _this;
	}
	
	
	public synchronized boolean uploadbyte(String file, byte[] data) 
	{
		//sdcard save
		if(isSaveToLocal)
		{
	    	File picture = new File(Environment.getExternalStorageDirectory()+LocalPath,file);
	        try
	        {
	            FileOutputStream fos = new FileOutputStream(picture);
	            fos.write(data);
	            fos.close();
				return true;
	        }
	        catch(Exception e)
	        {
				MainActivity.log(e);
				return false;
	        }
		}
		else if (isRPC) // json rpc
		{
			try
			{
				JSONRPC2Session mySession = new JSONRPC2Session(new URL("http://" + ServerPath + ":8080/rpc/JosnRPCServer"));
				List<Object> param = new ArrayList();
				param.add(file);
				param.add(data);
				JSONRPC2Request request = new JSONRPC2Request("uploadFile", param, 2);
				JSONRPC2Response response = mySession.send(request);
				if (response.indicatesSuccess())
				{
					Boolean succ = (Boolean) (response.getResult());
					return succ;
				}
				else
				{
					System.out.println(response.getError().getMessage());
					return false;
				}
			}
			catch (Exception e)
			{
				MainActivity.log(e);
				return false;
			}
		}
		else	// http post
		{
			try
			{
				URL url = new URL("http://" + ServerPath + ":8080/rpc/uploadfile");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true); // ����������
				conn.setDoOutput(true); // ���������
				conn.setUseCaches(false); // ������ʹ�û���
				conn.setRequestMethod("POST"); // ����ʽ
				conn.setRequestProperty("connection", "keep-alive");
				conn.setRequestProperty("filename",file);
				conn.getOutputStream().write(data);
				int res = conn.getResponseCode();
				if (res == 200)
				{
					return true;
				}
				else
				{
					return false;
				}
			}
			catch (Exception e)
			{
				MainActivity.log(e);
				return false;
			}
		}
		
	}

	public static Boolean uploadFile(String serverurl,String filename, byte[] data) throws Exception 
	{
		String encode = "GB2312";
		String BOUNDARY = UUID.randomUUID().toString(); // �߽��ʶ �������
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // ��������

		URL url = new URL(serverurl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(3000);
		conn.setConnectTimeout(3000);
		conn.setDoInput(true); // ����������
		conn.setDoOutput(true); // ���������
		conn.setUseCaches(false); // ������ʹ�û���
		conn.setRequestMethod("POST"); // ����ʽ
        conn.setRequestProperty("Charset", encode); // ���ñ���

		conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

		DataOutputStream dos = new DataOutputStream(conn.getOutputStream());


		StringBuffer sb = new StringBuffer();
		sb.append(PREFIX);
		sb.append(BOUNDARY);
		sb.append(LINE_END);
		/**
		 * �����ص�ע�⣺ name�����ֵΪ����������Ҫkey ֻ�����key �ſ��Եõ���Ӧ���ļ� filename���ļ������֣�������׺����
		 * ����:abc.png
		 */
		sb.append("Content-Disposition: form-data; name=\"dat\"; filename=\"" + filename + "\"" + LINE_END);
		sb.append("Content-Type: application/octet-stream; charset=" + encode + LINE_END);
		sb.append(LINE_END);
		dos.write(sb.toString().getBytes());
		dos.write(data);
		dos.write(LINE_END.getBytes());
		byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();

		dos.write(end_data);
		dos.flush();
		/**
		 * ��ȡ��Ӧ�� 200=�ɹ� ����Ӧ�ɹ�����ȡ��Ӧ����
		 */
		int res = conn.getResponseCode();
		if (res == 200)
		{
			// InputStream input = conn.getInputStream();
			// StringBuffer sb1 = new StringBuffer();
			// int ss;
			// while ((ss = input.read()) != -1)
			// {
			// sb1.append((char) ss);
			// }
			// result = sb1.toString();
			return true;
		}
		else
		{
			return false;
		}
	}
}
