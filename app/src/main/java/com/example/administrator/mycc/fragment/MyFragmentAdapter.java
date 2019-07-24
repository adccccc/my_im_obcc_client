package com.example.administrator.mycc.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * @Author: obc
 * @Date: 2019/3/13 9:52
 * @Version 1.0
 */
public class MyFragmentAdapter extends FragmentStatePagerAdapter {

    Context context;
    List<Fragment> fragmentList;

    public MyFragmentAdapter(FragmentManager fm, Context context, List<Fragment> list) {
        super(fm);
        this.context = context;
        this.fragmentList = list;
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
