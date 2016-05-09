package com.example.handlephoto;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.example.cropphoto.R;

import HaoRan.ImageFilter.ColorToneFilter;
import HaoRan.ImageFilter.IImageFilter;
import HaoRan.ImageFilter.Image;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private SelectPhoto menuWindow;
	private Bitmap bitmap, yuanbitmap;
	private ImageView img;
	private float startx, starty;
	private Matrix matric = new Matrix();
	private int index = 0;
	private int staryAngle = 0;
	private Boolean isni = false;
	private WindowManager winmanager;
	private float x0,y0;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				img.setImageBitmap(bitmap);
			}
		};
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/*winmanager=(WindowManager) getSystemService(Context.WINDOW_SERVICE);
		 x0=(float) (winmanager.getDefaultDisplay().getWidth()/2.0);
		 y0=(float) (winmanager.getDefaultDisplay().getHeight()/2.0);*/
		img = (ImageView) findViewById(R.id.main_img);
		bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.a1);
		// original picture
		yuanbitmap = bitmap;
		img.setImageBitmap(bitmap);
	}

	public void ClickFilterImage(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("Please select an image filter");
		final String[] lvj = { "original", "icy", "elegent", "green screen" };

		builder.setSingleChoiceItems(lvj, index,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Toast.makeText(MainActivity.this, lvj[arg1],
								Toast.LENGTH_LONG).show();
						if (arg1 == 0) {
							img.setImageBitmap(yuanbitmap);
						} else if (arg1 == 1) {
							bitmap = yuanbitmap;
							bitmap = Utils.ice(bitmap);
							img.setImageBitmap(bitmap);
						} else if (arg1 == 2) {
							// elegent
							IImageFilter filter = new ColorToneFilter(Color
									.rgb(33, 168, 254), 192);
							bitmap = yuanbitmap;
							Image imgage = new Image(bitmap);
							imgage = filter.process(imgage);
							imgage.copyPixelsFromBuffer();
							bitmap = imgage.getImage();
							img.setImageBitmap(bitmap);
						} else {
							// green
							IImageFilter filter = new ColorToneFilter(0x00FF00,
									192);
							bitmap = yuanbitmap;
							Image imgage = new Image(bitmap);
							imgage = filter.process(imgage);
							imgage.copyPixelsFromBuffer();
							bitmap = imgage.getImage();
							img.setImageBitmap(bitmap);
						}
						index = arg1;
						arg0.dismiss();
					}
				});
		builder.create().show();

	}

	public void ClickCropImage(View v) {
		Intent intent = new Intent(MainActivity.this, CropActivity.class);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] bytes = stream.toByteArray();
		intent.putExtra("data", bytes);
		startActivityForResult(intent, 5);
	}
	/*
	public void ClickCropImage(View v) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        //可以选择图片类型，如果是*表明所有类型的图片
        //intent.setDataAndType(v, "image/*");
        // 下面这个crop = true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例，这里设置的是正方形（长宽比为1:1）
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 1000);
        intent.putExtra("outputY", 1000);
        //裁剪时是否保留图片的比例，这里的比例是1:1
        intent.putExtra("scale", true);
        //是否是圆形裁剪区域，设置了也不一定有效
        //intent.putExtra("circleCrop", true);
        //设置输出的格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        //是否将数据保留在Bitmap中返回
        intent.putExtra("return-data", true);

        startActivityForResult(intent, 5);
    }*/
	public void SaveImage(View v) {
		Matrix ma = new Matrix();
		// rotate
		ma.postRotate(staryAngle);
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), ma, true);
		Utils.saveImageToGallery(MainActivity.this, bitmap);
	}

	public void ClickShowSheet(View v) {
		menuWindow = new SelectPhoto(MainActivity.this, itemsOnClick);
		// display the window
		menuWindow.showAtLocation(
				MainActivity.this.findViewById(R.id.main_showsheet),
				Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); 
	}

	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			menuWindow.dismiss();
			int id = v.getId();
			if (id == R.id.btn_take_photo) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, 0);
			} else if (id == R.id.btn_pick_photo) {
				Intent intent1 = new Intent();
				intent1.setType("image/*");
				intent1.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent1, 1);
			} else {
			}
		}
	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			Uri selectedImage = data.getData();
			/*
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			bitmap = BitmapFactory.decodeFile(picturePath);*/
		    bitmap = decodeUri(data.getData());
			yuanbitmap = bitmap;
			handler.sendEmptyMessage(0);
		} else if (requestCode == 0) {
			Bitmap photo = data.getParcelableExtra("data");
			bitmap = photo;
			yuanbitmap = bitmap;
			handler.sendEmptyMessage(0);
		} else if (requestCode == 5) {
			// cropped stuff
			if (resultCode != 6) {
				bitmap = data.getParcelableExtra("data");
				handler.sendEmptyMessage(0);
			}
		}
	};

	public Bitmap decodeUri(Uri uri) {
	    ParcelFileDescriptor parcelFD = null;
	    try {
	        parcelFD = getContentResolver().openFileDescriptor(uri, "r");
	        FileDescriptor imageSource = parcelFD.getFileDescriptor();

	        // Decode image size
	        BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;
	        BitmapFactory.decodeFileDescriptor(imageSource, null, o);

	        // the new size we want to scale to
	        final int REQUIRED_SIZE = 1024;

	        // Find the correct scale value. It should be the power of 2.
	        int width_tmp = o.outWidth, height_tmp = o.outHeight;
	        int scale = 1;
	        while (true) {
	            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE) {
	                break;
	            }
	            width_tmp /= 2;
	            height_tmp /= 2;
	            scale *= 2;
	        }

	        // decode with inSampleSize
	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize = scale;
	        bitmap = BitmapFactory.decodeFileDescriptor(imageSource, null, o2);

	        //imageview.setImageBitmap(bitmap);

	    } catch (FileNotFoundException e) {
	        // handle errors
	    } catch (IOException e) {
	        // handle errors
	    } finally {
	        if (parcelFD != null)
	            try {
	                parcelFD.close();
	            } catch (IOException e) {
	                // ignored
	            }
	    }
		return bitmap;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			startx = x;
			starty = y;// record original (x, y)
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if (Dis(x, y, startx, starty) > 10) {

				float startWidthX=startx-x0;
				float startHeightY=starty-y0;
				float startka=startHeightY/startWidthX;
				
				float endWithX=x-x0;
				float endHeightY=y-y0;
				float endka=endHeightY/endWithX;
				
				if(startka<endka){
					RotateAnimation rotateAnimation = new RotateAnimation(
							staryAngle, staryAngle + 10,
							Animation.RELATIVE_TO_SELF, 0.5f,
							Animation.RELATIVE_TO_SELF, 0.5f);// 顺时针
					rotateAnimation.setDuration(10);
					rotateAnimation.setFillAfter(true);
					img.clearAnimation();
					img.startAnimation(rotateAnimation);
					staryAngle += 10;
				}else{
					RotateAnimation rotateAnimation = new RotateAnimation(
							staryAngle, staryAngle - 10,
							Animation.RELATIVE_TO_SELF, 0.5f,
							Animation.RELATIVE_TO_SELF, 0.5f);// 逆时针
					rotateAnimation.setDuration(10);
					rotateAnimation.setFillAfter(true);
					img.clearAnimation();
					img.startAnimation(rotateAnimation);
					staryAngle -= 10;
				}
			
				startx = x;
				starty = y;
			}

		}

		return true;
	}
	//求距离
	public int Dis(float x1, float y1, float x2, float y2) {
		float dis = (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));

		return (int) dis;
	}
}
