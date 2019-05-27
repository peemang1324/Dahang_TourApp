package com.example.sns_project.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.sns_project.R;
import com.example.sns_project.adapter.GalleryAdapter;

import java.util.ArrayList;

import static com.example.sns_project.Util.GALLERY_IMAGE;
import static com.example.sns_project.Util.GALLERY_VIDEO;
import static com.example.sns_project.Util.INTENT_MEDIA;
import static com.example.sns_project.Util.showToast;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallay);

        if (ContextCompat.checkSelfPermission(GalleryActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(GalleryActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
            if (ActivityCompat.shouldShowRequestPermissionRationale(GalleryActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                showToast(GalleryActivity.this,getResources().getString(R.string.please_grant_gallery_permission));
            }
        } else {
            recycleInit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: { //접근권한 확인에서 받은 request code 1
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recycleInit();
                } else {
                    finish();
                    showToast(GalleryActivity.this,getResources().getString(R.string.please_grant_gallery_permission));
                }
                break;
            }
        }
    }

    private void recycleInit() {
        final int numberOfColumns = 3; //이미지가 한줄에 3개씩 출력되게 설정

        RecyclerView recyclerView = findViewById(R.id.rv_gallery);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        RecyclerView.Adapter mAdapter = new GalleryAdapter(this, getImagesPath(this));
        recyclerView.setAdapter(mAdapter);
    }

    public ArrayList<String> getImagesPath(Activity activity) { //이미지 경로를 가져오는 메소드
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        Cursor cursor;
        int column_index_data;
        String PathOfImage = null;
        String[] projection;
        Intent intent = getIntent(); //intent를 가져옴(사진인지, 동영상인지 구분하기 위해서)
        final int media = intent.getIntExtra(INTENT_MEDIA, GALLERY_IMAGE); //값을 넘기지 않으면 이미지로 처리

        if (media == GALLERY_VIDEO) {
            uri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            projection = new String[]{MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME};
        } else {
            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            projection = new String[]{MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        }

        cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(PathOfImage);
        }
        return listOfAllImages;
    }


}