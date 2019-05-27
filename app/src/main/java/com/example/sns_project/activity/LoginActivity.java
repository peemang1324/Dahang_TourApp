package com.example.sns_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.sns_project.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.sns_project.Util.showToast;

public class LoginActivity extends BasicActivity implements  GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG ="LoginActivity";
    private FirebaseAuth mAuth; //FirebaseAuth 인스턴스 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 상태바 삭제
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance(); //FirebaseAuth 인스턴스 초기화

        findViewById(R.id.bt_login).setOnClickListener(onClickListener); //로그인 버튼이 클릭된 경우
        findViewById(R.id.bt_forget_password).setOnClickListener(onClickListener); //비밀번호 초기화 버튼
        findViewById(R.id.bt_singup).setOnClickListener(onClickListener); //회원가입 버튼

    }

    //버튼별 클릭 메소드
    View.OnClickListener onClickListener = v -> {
        switch (v.getId()){
            case R.id.bt_login:
                login();
                break;
            case R.id.bt_forget_password:
                myStartActivity(PasswordReset.class);
                break;
            case R.id.bt_singup:
                myStartActivity(SignUpActivity.class);
                break;

        }
    };

    private void login(){ //회원가입 버튼을 눌렀을 경우 실행되는 메소드
        EditText emailFocus = findViewById(R.id.et_email);
        EditText passwordFocus = findViewById(R.id.et_password);

        String email = ((EditText) findViewById(R.id.et_email)).getText().toString(); //EditText에서 Email값 String으로 가져옴
        String password = ((EditText) findViewById(R.id.et_password)).getText().toString(); //EditText에서 Password값 String으로 가져옴

        if(email.length() > 0 ) {//이메일과 비밀번호를 입력한 경우
            if(password.length() > 0){
                final RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);
                loaderLayout.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            loaderLayout.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                // 성공 했을 경우 UI
                                myStartActivity(MainActivity.class); //MainActivity로 이동
                            } else {
                                // 실패 했을 경우 UI
                                if (task.getException() != null) {
                                    showToast(LoginActivity.this, task.getException().toString());
                                }
                            }
                        });
            } else{
               showToast(LoginActivity.this,"비밀번호를 입력해주세요.");
                passwordFocus.requestFocus();
            }
        } else { //이메일과 비밀번호가 입력되지 않은 경우
           showToast(LoginActivity.this,"이메일을 입력해주세요.");
           emailFocus.requestFocus();
        }
    }

    @Override

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void myStartActivity(Class c){ //원하는 Activity로 이동시켜주는 메소드
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}
