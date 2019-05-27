package com.example.sns_project.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.sns_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.sns_project.Util.showToast;

public class PasswordReset extends BasicActivity {
    private FirebaseAuth mAuth; //FirebaseAuth 인스턴스 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        mAuth = FirebaseAuth.getInstance(); //FirebaseAuth 인스턴스 초기화

        findViewById(R.id.bt_reset_password).setOnClickListener(onClickListener); //로그인 버튼이 클릭된 경우
    }

    //버튼별 클릭 메소드
    View.OnClickListener onClickListener = v -> {
        switch (v.getId()) {
            case R.id.bt_reset_password:
                sendPassword();
                break;
        }
    };

    private void sendPassword() { //회원가입 버튼을 눌렀을 경우 실행되는 메소드
        String email = ((EditText) findViewById(R.id.et_email)).getText().toString(); //EditText에서 Email값 String으로 가져옴
        EditText emailFocus = findViewById(R.id.et_email);

        if (email.length() > 0) { //이메일을 입력한 경우
            final RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);
            loaderLayout.setVisibility(View.VISIBLE);
            FirebaseUser user = mAuth.getCurrentUser();

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        loaderLayout.setVisibility(View.GONE);
                        if (task.isSuccessful()) { //이메일에 비밀번호 전송이 성공한 경우
                            showToast(PasswordReset.this,"입력한 이메일에 비밀번호를 전송했습니다.");
                            finish();
                        }
                    });
        } else { //이메일과 비밀번호가 입력되지 않은 경우
            showToast(PasswordReset.this, "이메일을 입력해 주세요.");
            emailFocus.requestFocus();
        }
    }
}