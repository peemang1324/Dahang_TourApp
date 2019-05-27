package com.example.sns_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.sns_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

import static com.example.sns_project.Util.showToast;

public class SignUpActivity extends BasicActivity {
    private FirebaseAuth mAuth; //FirebaseAuth 인스턴스 선언
    private static final String TAG = "SingUpActivity";
    CheckBox signupCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance(); //FirebaseAuth 인스턴스 초기화

        findViewById(R.id.bt_singup_check).setOnClickListener(onClickListener); //회원가입 버튼이 클릭된 경우
        findViewById(R.id.tv_terms_service).setOnClickListener(onClickListener); //이용약관 보기

        signupCheck = findViewById(R.id.cb_signup); //이용약관 동의 확인

    }

    @Override
    public void onBackPressed() { //뒤로가기 버튼을 눌렀을 경우 실행되는 메소드
        super.onBackPressed();

        //App 종료
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    //버튼별 클릭 메소드
    View.OnClickListener onClickListener = v -> {
        switch (v.getId()) {
            case R.id.bt_singup_check: //회원가입
                signUp();
                break;
            case R.id.tv_terms_service:
               // myStartActivity(TemsOfService.class);
        }

    };

    private void signUp() { //회원가입 버튼을 눌렀을 경우 실행되는 메소드
        EditText email = findViewById(R.id.et_email);
        EditText password = findViewById(R.id.et_password);

        String emailStr = email.getText().toString(); //EditText에서 Email값 String으로 가져옴
        String passwordStr = password.getText().toString(); //EditText에서 Password값 String으로 가져옴
        String password_check = ((EditText) findViewById(R.id.et_password_check)).getText().toString(); //EditText에서 Password값 String으로 가져옴

        if (emailStr.length() > 0) { //이메일과 비밀번호를 입력한 경우
            if (passwordStr.length() > 0) {
                if (passwordStr.equals(password_check)) { //비밀번호와 비밀번호 확인이 일치할 경우
                    if (signupCheck.isChecked()) {
                        final RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);
                        loaderLayout.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(emailStr, passwordStr)
                                .addOnCompleteListener(this, task -> {
                                    loaderLayout.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        // 성공 했을 경우 UI
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        showToast(SignUpActivity.this, "회원가입이 완료되었습니다.");
                                        myStartActivity(MainActivity.class); //MainActivity로 이동
                                    } else {
                                        // 실패 했을 경우 UI
                                        if (task.getException() != null) {
                                            showToast(SignUpActivity.this, task.getException().toString());
                                        }
                                    }
                                });
                    } else {
                        showToast(SignUpActivity.this, "이용약관에 동의해 주세요.");
                    }

                } else { //비밀번호와 비밀번호 확인이 일치하지 않을 경우
                    showToast(SignUpActivity.this, "비밀번호가 일치하지 않습니다.");
                    password.requestFocus(); //패스워드 입력칸에 포커스 두기
                }
            } else {
                showToast(SignUpActivity.this, "비밀번호를 입력해주세요");
                password.requestFocus(); //패스워드 입력칸에 포커스 두기
            }
        } else { //이메일과 비밀번호가 입력되지 않은 경우
            showToast(SignUpActivity.this, "이메일을 입력해주세요.");
            email.requestFocus(); //이메일 입력칸에 포커스 두기

        }
    }

    private void myStartActivity(Class c) { //원하는 Activity로 이동시켜주는 메소드
        Intent intent = new Intent(this, c);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); //로그인 화면 이동 후 이전화면 intent 삭제
        startActivity(intent);
    }
}
