package com.example.administrator.mycc.activity;

/**
 * @Author: obc
 * @Date: 2019/3/13 10:36
 * @Version 1.0
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.administrator.mycc.R;
import com.example.administrator.mycc.dao.ChatTitleDao;
import com.example.administrator.mycc.dao.FriendDao;
import com.example.administrator.mycc.fragment.ChatFragment;
import com.example.administrator.mycc.fragment.ContactFragment;
import com.example.administrator.mycc.fragment.MyFragmentAdapter;
import com.example.administrator.mycc.fragment.MyInfoFragment;
import com.example.administrator.mycc.model.ChatTitle;
import com.example.administrator.mycc.model.LoginInfo;
import com.example.administrator.mycc.model.UserInfo;
import com.example.administrator.mycc.others.MyThreadPoolManager;
import com.example.administrator.mycc.service.SocketService;
import com.example.administrator.mycc.utils.CacheUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 核心页面
 */
public class AppMainActivity extends BaseActivity {

    private ViewPager viewPager;
    BottomNavigationView navigationView;
    List<Fragment> fragmentList;

    private String username;
    private String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_main);

        // 应用被杀，重新恢复页面信息
        if (savedInstanceState != null) {
            String username = savedInstanceState.getString("username");
            String token = savedInstanceState.getString("token");
            CacheUtils.getInstance().setLoginInfo(new LoginInfo(username, token));
        }

        username = CacheUtils.getInstance().getLoginInfo().getUsername();
        token = CacheUtils.getInstance().getLoginInfo().getToken();

        initView();

    }

    /**
     * 启动Service
     */
    @Override
    protected void onStart() {
        super.onStart();
        initService();
    }

    /**
     * 应用在后台被kill掉时，保存好登录信息
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("username", username);
        outState.putString("token", token);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

    /**
     * 启动netty服务
     */
    private void initService() {
        Intent intent = new Intent(AppMainActivity.this, SocketService.class);
        startService(intent);
    }

    private void initView() {
        viewPager = (ViewPager)findViewById(R.id.view_pager_main);
        navigationView = (BottomNavigationView)findViewById(R.id.navigation_main);

        MyInfoFragment myInfoFragment = new MyInfoFragment();

        fragmentList = new ArrayList<>();
        fragmentList.add(new ChatFragment());
        fragmentList.add(new ContactFragment());
        fragmentList.add(myInfoFragment);
        MyFragmentAdapter adapter = new MyFragmentAdapter(getSupportFragmentManager(), this, fragmentList);
        viewPager.setAdapter(adapter);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_chat:
                        viewPager.setCurrentItem(0, true);
                        return true;
                    case R.id.navigation_friend_list:
                        viewPager.setCurrentItem(1, true);
                        return true;
                    case R.id.navigation_me:
                        viewPager.setCurrentItem(2, true);
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                //注意这个方法滑动时会调用多次，下面是参数解释：
                //i:当前所处页面索引,滑动调用的最后一次绝对是滑动停止所在页面
                //v:表示从位置的页面偏移的[0,1]的值。
                //i1:以像素为单位的值，表示与位置的偏移
            }

            @Override
            public void onPageSelected(int i) {
                navigationView.getMenu().getItem(i).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    /**
     * 关闭Tcp服务,清空缓存信息
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(AppMainActivity.this, SocketService.class);
        stopService(intent);
        CacheUtils.getInstance().clearCache();
    }
}
