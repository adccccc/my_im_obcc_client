package com.example.administrator.mycc.fragment;

/**
 * @Author: obc
 * @Date: 2019/3/13 10:24
 * @Version 1.0
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.administrator.mycc.R;
import com.example.administrator.mycc.activity.LoginActivity;
import com.example.administrator.mycc.dao.UserInfoDao;
import com.example.administrator.mycc.retrofit.LoginService;
import com.example.administrator.mycc.retrofit.response.RespObj;
import com.example.administrator.mycc.model.UserInfo;
import com.example.administrator.mycc.retrofit.AddTokenInterceptor;
import com.example.administrator.mycc.retrofit.UserInfoService;
import com.example.administrator.mycc.utils.CacheUtils;
import com.example.administrator.mycc.utils.ConstantUtils;
import com.example.administrator.mycc.utils.ImageUtils;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 个人信息页
 */
public class MyInfoFragment extends Fragment {

    String username;
    String token;
    UserInfo userInfo;
    // UI
    private ImageView iconImage;
    private TextView usernameText;
    private TextView nicknameText;
    private Button exitButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_me, container, false);
        iconImage = (ImageView)view.findViewById(R.id.image_fragment_me_icon);
        usernameText = (TextView)view.findViewById(R.id.text_fragment_me_username);
        nicknameText = (TextView)view.findViewById(R.id.text_fragment_me_nickname);
        exitButton = view.findViewById(R.id.button_fragment_sign_out);

        username = CacheUtils.getInstance().getLoginInfo().getUsername();
        token = CacheUtils.getInstance().getLoginInfo().getToken();

        userInfo = CacheUtils.getInstance().getUserInfoMap().get(username);

        if (userInfo != null) {
            usernameText.setText(userInfo.getUsername());
            nicknameText.setText(userInfo.getNickname());
            iconImage.setImageResource(ImageUtils.getIconId(userInfo.getIcon()));
        }

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOutByRetrofit();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        getUserInfoByRetrofit();

        return view;
    }

    /**
     * HTTP注销登录
     */
    private void signOutByRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ConstantUtils.HTTP_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient.Builder()
                        .connectTimeout(ConstantUtils.HTTP_CONNECT_TIME_OUT, TimeUnit.SECONDS)
                        .addInterceptor(new AddTokenInterceptor(token))
                        .build())
                .build();
        LoginService service = retrofit.create(LoginService.class);
        service.signOut(username).subscribeOn(Schedulers.io()).subscribe();
    }

    @SuppressWarnings("unchecked")
    private void getUserInfoByRetrofit() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ConstantUtils.HTTP_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient.Builder()
                        .connectTimeout(ConstantUtils.HTTP_CONNECT_TIME_OUT, TimeUnit.SECONDS)
                        .addInterceptor(new AddTokenInterceptor(token))
                        .build())
                .build();
        UserInfoService service = retrofit.create(UserInfoService.class);

        service.getUserInfo(username)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<RespObj>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {

                    }

                    @Override
                    public void onNext(RespObj respObj) {
                        if (respObj == null || respObj.getCode() != 0) {
                            Toast.makeText(getActivity(), "请求用户信息失败", Toast.LENGTH_SHORT).show();
                        } else {
                            Map<String, Object> userInfo = (Map<String, Object>) respObj.getData().get("userInfo");
                            String username = userInfo.get("username").toString();
                            String nickname = userInfo.get("nickname").toString();
                            String icon = userInfo.get("icon").toString();
                            int icon_num = (int)Double.parseDouble(icon);

                            // 更新UI
                            usernameText.setText(userInfo.get("username").toString());
                            nicknameText.setText(userInfo.get("nickname").toString());
                            iconImage.setImageResource(ImageUtils.getIconId(icon_num));

                            // 更新缓存中的个人信息
                            UserInfo info = new UserInfo(username, nickname, icon_num);
                            UserInfo lastInfo = CacheUtils.getInstance().getUserInfoMap().get(username);
                            if (lastInfo == null || !lastInfo.equals(info)) {
                                CacheUtils.getInstance().getUserInfoMap().put(username, info);
                                // 更新db
                                UserInfoDao.getInstance(getContext()).insertOrUpdateUserInfo(info);
                            }

                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                        Toast.makeText(getActivity(), "请求用户信息异常", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }



}
