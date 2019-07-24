package com.example.administrator.mycc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.administrator.mycc.R;
import com.example.administrator.mycc.retrofit.response.RespObj;
import com.example.administrator.mycc.retrofit.LoginService;
import com.example.administrator.mycc.utils.ConstantUtils;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.*;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @Author: obc
 * @Date: 2019/3/11 19:23
 * @Version 1.0
 */

/**
 * 注册页面
 */
public class SignUpActivity extends BaseActivity {

    // UI references.
    private AutoCompleteTextView mUsernameView;
    private EditText mNicknameView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private View mProgressView;
    private View mLoginFormView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mUsernameView = (AutoCompleteTextView)findViewById(R.id.text_username);
        mNicknameView = (EditText)findViewById(R.id.text_nickname);
        mPasswordView = (EditText)findViewById(R.id.text_pwd);
        mConfirmPasswordView = (EditText)findViewById(R.id.text_pwd_confirm);

        Button mSignUpButton = (Button)findViewById(R.id.btn_sign_up);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();
            }
        });

    }

    private void attemptSignUp() {

        mUsernameView.setError(null);
        mNicknameView.setError(null);
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);

        String username = mUsernameView.getText().toString();
        String nickname = mNicknameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirmPassword = mConfirmPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_sign_up_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isNicknameValid(nickname)) {
            mNicknameView.setError(getString(R.string.error_sign_up_invalid_nickname));
            focusView = mNicknameView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_sign_up_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isConfirmPasswordValid(password, confirmPassword)) {
            mConfirmPasswordView.setError(getString(R.string.error_sign_up_invalid_confirm_password));
            focusView = mConfirmPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            doSignUpByRetrofit(username, nickname, password);
        }

    }

    private boolean isUsernameValid(String username) {
        if (username == null) return false;
        return Pattern.matches("[A-Za-z][0-9a-zA-Z_]{4,15}", username);
    }

    private boolean isNicknameValid(String nickname) {
        return nickname != null && nickname.length() > 0;
    }

    private boolean isPasswordValid(String password) {
        return password != null && Pattern.matches("[0-9a-zA-Z_]{6,16}", password);
    }

    private boolean isConfirmPasswordValid(String pwd, String confirmPwd) {
        return pwd.equals(confirmPwd);
    }

    private void doSignUpByRetrofit(String username, String nickname, String password) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ConstantUtils.HTTP_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient.Builder()
                        .connectTimeout(ConstantUtils.HTTP_CONNECT_TIME_OUT, TimeUnit.SECONDS)
                        .build())
                .build();

        LoginService loginService = retrofit.create(LoginService.class);

        loginService.signUp(username, password, nickname)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<RespObj>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {

                    }

                    @Override
                    public void onNext(RespObj respObj) {
                        if (respObj.getCode() != 0) {
                            if (respObj.getCode() == 100001) {
                                mUsernameView.setError(getString(R.string.error_sign_up_duplicated_username));
                                mUsernameView.requestFocus();

                            }
                        } else {
                            Toast.makeText(SignUpActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Toast.makeText(SignUpActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
