package com.example.sns_project.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.sns_project.info.MemberInfo;
import com.example.sns_project.R;
import com.example.sns_project.info.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import static com.example.sns_project.Util.INTENT_PATH;
import static com.example.sns_project.Util.showToast;

public class MemberInitActivity extends BasicActivity {

    private FirebaseAuth auth; //FirebaseAuth 인스턴스 선언
    private FirebaseUser user; //Firebase 사용자 정보를 담을 변수
    private static final String TAG = "MemberInitActivity";
    private RelativeLayout loaderLayout;
    private ImageView profileImageView; //프로필 이미지 넣기
    private String profilePath; //프로필 사진 경로를 저장할 변수
    User realtimeUserInfo; //realtimeDB에 저장할 user 객체

    private DatabaseReference mDatabase;// ...
    Uri downloadUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);


        loaderLayout = findViewById(R.id.loaderLayout);
        profileImageView = findViewById(R.id.iv_picture); //사진촬영 버튼이 클릭된 경우
        profileImageView.setOnClickListener(onClickListener);

        findViewById(R.id.bt_check_member).setOnClickListener(onClickListener); //로그인 버튼이 클릭된 경우
        findViewById(R.id.ib_gallery).setOnClickListener(onClickListener);
    }

    @Override
    public void onBackPressed() { //뒤로가기 버튼을 눌렀을 경우 종료
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    profilePath = data.getStringExtra(INTENT_PATH);
                    Glide.with(this).load(profilePath).centerCrop().override(600).into(profileImageView);
                }
                break;
        }
    }

    //버튼별 클릭 메소드
    View.OnClickListener onClickListener = v -> {
        switch (v.getId()) {
            case R.id.bt_check_member: //입력 버튼을 클릭한 경우
                storageUploader();
                break;
            case R.id.iv_picture: //사진 아이콘을 클릭한 경우

                myStartActivity(CameraActivity.class);
                break;
            case R.id.ib_gallery: //갤러리 아이콘을 클릭한 경우
                myStartActivity(GalleryActivity.class);
                break;
        }
    };

    private void storageUploader() { //firebase storage 업로드 메소드
        /*각 텍스트창 입력 값을 가져옴*/
        final String name = ((EditText) findViewById(R.id.et_name)).getText().toString();
        final String birthday = ((EditText) findViewById(R.id.et_birthday)).getText().toString();
        final String phone_number = ((EditText) findViewById(R.id.et_phone_number)).getText().toString();
        final String address = ((EditText) findViewById(R.id.et_address)).getText().toString();


        if (name.length() > 0 && phone_number.length() > 9 && birthday.length() > 5 && address.length() > 0) {
            loaderLayout.setVisibility(View.VISIBLE);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            user = FirebaseAuth.getInstance().getCurrentUser();
            final StorageReference mountainImagesRef = storageRef.child("users/" + user.getUid() + "/profileImage.jpg");

            if (profilePath == null) { //프로필 이미지를 넣지 않았을 경우
                MemberInfo memberInfo = new MemberInfo(name, phone_number, birthday, address);
                storeUploader(memberInfo); //firebase DB에 등록
            } else {
                try {
                    InputStream stream = new FileInputStream(new File(profilePath));
                    UploadTask uploadTask = mountainImagesRef.putStream(stream);
                    uploadTask.continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return mountainImagesRef.getDownloadUrl();
                    }).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            downloadUri = task.getResult();
                            MemberInfo memberInfo = new MemberInfo(name, phone_number, birthday, address, downloadUri.toString());
                            storeUploader(memberInfo);
                        } else {
                            showToast(MemberInitActivity.this, "회원 정보를 보내는데 실패하였습니다.");
                        }
                    });
                } catch (FileNotFoundException e) {
                    Log.e("로그", "에러: " + e.toString());
                }
            }
        } else {
            showToast(MemberInitActivity.this, "회원정보를 입력해주세요.");
        }
    }

    private void storeUploader(MemberInfo memberInfo) { //fireStore DB에 업로드 메소드
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).set(memberInfo)
                .addOnSuccessListener(aVoid ->{
                    final String name = ((EditText) findViewById(R.id.et_name)).getText().toString();

                    /*User 정보 RealTime DB 등록 */
                    auth = FirebaseAuth.getInstance();
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    assert firebaseUser != null;
                    String userId = firebaseUser.getUid();

                    mDatabase = FirebaseDatabase.getInstance().getReference();

                    if(downloadUri != null){ //이미지를 넣었을 경우
                        realtimeUserInfo = new User(userId, name, downloadUri.toString(), "offline", name.toLowerCase());
                    }else{ //이미지를 넣지 않았을 경우
                        realtimeUserInfo = new User(userId, name, "default", "offline", name.toLowerCase());
                    }

                    mDatabase.child("Users").child(userId).setValue(realtimeUserInfo);

                    showToast(MemberInitActivity.this, "회원정보 등록을 성공하였습니다.");
                    loaderLayout.setVisibility(View.GONE);
                    finish();
                })
                .addOnFailureListener(e -> {
                    showToast(MemberInitActivity.this, "회원정보 등록에 실패하였습니다.");
                    loaderLayout.setVisibility(View.GONE);
                    Log.w(TAG, "Error writing document", e);
                });
    }

    private void myStartActivity(Class c) { //원하는 Activity로 이동시켜주는 메소드
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);
    }
}