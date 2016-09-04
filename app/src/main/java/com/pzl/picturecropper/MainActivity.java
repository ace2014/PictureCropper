package com.pzl.picturecropper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.pzl.library.cropper.Cropper;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST_CODE = 0;
    private static final int ALBUM_REQUEST_CODE = 1;

    private Cropper cropper;
    private Bitmap src;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cropper = (Cropper) findViewById(R.id.cropper);
        //cropper.setImageResource(R.drawable.pic_test2);
        cropper.setPadding(20);
        cropper.setRatio(1, 1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_clip) {
            if (src == null ||src.isRecycled()) {
                Toast.makeText(this, "请选择图片", Toast.LENGTH_SHORT).show();
                return true;
            }
            Bitmap bitmap = cropper.getClipBitmap();
            Util.saveBitmap2file(bitmap, "裁剪.jpg");
            Intent intent = new Intent(this, PreviewActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_album) {
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, ALBUM_REQUEST_CODE);
        } else if (id == R.id.action_camera) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        cropper.setImageBitmap(null);
        if (src != null && src.isRecycled() == false) {
            src.recycle();
            src = null;
        }
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_CANCELED)
                return;
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                src = bundle.getParcelable("data");//todo 拿原图并且压缩，从文件拿
                cropper.setImageBitmap(src);
            }
        } else if (requestCode == ALBUM_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_CANCELED)
                return;
            Uri uri = data.getData();

            InputStream in = null;
            try {
                in = getContentResolver().openInputStream(uri);
                src = BitmapFactory.decodeStream(in);
                cropper.setImageBitmap(src);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


}
