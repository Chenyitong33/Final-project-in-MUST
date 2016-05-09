package com.example.handlephoto;

import com.edmodo.cropper.CropImageView;
import com.example.cropphoto.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class CropActivity extends Activity {

	private CropImageView cropimageview;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crop_activity);
		
		Intent intent=getIntent();
		byte[] bytes=intent.getByteArrayExtra("data");
		Bitmap map=BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		cropimageview = (CropImageView) findViewById(R.id.CropImageView);
		cropimageview.setFixedAspectRatio(false);
		cropimageview.setImageBitmap(map);
	   
	}

	public void ClickCrop(View v) {
		
		Intent intent=new Intent();
		Bitmap map=cropimageview.getCroppedImage();
		intent.putExtra("data", map);
		setResult(5,intent);
		finish();
	}

	public void ClickCancel(View v) {
		setResult(6);
		finish();
	}

}
