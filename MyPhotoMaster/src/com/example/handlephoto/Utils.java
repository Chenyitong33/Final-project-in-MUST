package com.example.handlephoto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public class Utils {

	public static void saveImageToGallery(Activity context, Bitmap bmp) {
		MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp,
				"title", "description");
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri
				.parse("file://" + Environment.getExternalStorageDirectory())));
	}
	//ice filter
	public static Bitmap ice(Bitmap bmp) {

		int width = bmp.getWidth();
		int height = bmp.getHeight();

		int dst[] = new int[width * height];
		bmp.getPixels(dst, 0, width, 0, 0, width, height);

		int R, G, B, pixel;
		int pos, pixColor;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				pos = y * width + x;
				pixColor = dst[pos]; // 获取图片当前点的像素值

				R = Color.red(pixColor); // 获取RGB三原色
				G = Color.green(pixColor);
				B = Color.blue(pixColor);

				pixel = R - G - B;
				pixel = pixel * 3 / 2;
				if (pixel < 0)
					pixel = -pixel;
				if (pixel > 255)
					pixel = 255;
				R = pixel; // 计算后重置R值，以下类同

				pixel = G - B - R;
				pixel = pixel * 3 / 2;
				if (pixel < 0)
					pixel = -pixel;
				if (pixel > 255)
					pixel = 255;
				G = pixel;

				pixel = B - R - G;
				pixel = pixel * 3 / 2;
				if (pixel < 0)
					pixel = -pixel;
				if (pixel > 255)
					pixel = 255;
				B = pixel;
				dst[pos] = Color.rgb(R, G, B);  // reset current point's pixel
			} // x
		} // y
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(dst, 0, width, 0, 0, width, height);
		return bitmap;
	}
}
