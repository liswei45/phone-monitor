package com.ivideoi;

import java.util.ArrayList;
import java.util.List;
import android.hardware.Camera;

public class VideoCapture implements Camera.PictureCallback 
{
	Camera _mycamera;
	public List<byte[]> _videoDatas;
	
	static public VideoCapture _this = new VideoCapture();
	private VideoCapture()
	{
        _mycamera = Camera.open();
        Camera.Parameters parameters = _mycamera.getParameters();
        parameters.setPictureFormat(android.graphics.ImageFormat.JPEG);
        _mycamera.startPreview();
        _videoDatas = new ArrayList<byte[]>();
	}
	
	public void takePhoto()
	{
		_mycamera.takePicture(null, null, VideoCapture.this);
	}
	
    @Override
    public void onPictureTaken(byte[] data, Camera camera) 
    {
    	synchronized(_videoDatas)
    	{
    		_videoDatas.add(data);
    	}
    }
	

}
