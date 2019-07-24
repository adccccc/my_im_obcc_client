package com.example.administrator.mycc.adapter;

import android.content.Context;

import android.widget.ImageView;
import android.widget.TextView;
import com.example.administrator.mycc.R;
import com.example.administrator.mycc.model.UserInfo;
import com.example.administrator.mycc.utils.CacheUtils;
import com.example.administrator.mycc.utils.ImageUtils;

import java.util.List;

/**
 * @Author: obc
 * @Date: 2019/3/17 15:41
 * @Version 1.0
 */

/**
 * 联系人list适配器
 */
public class FriendAdapter extends CustomListViewAdapter<String> {

    public FriendAdapter(Context context, List<String> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, String friend) {
        UserInfo userInfo = CacheUtils.getInstance().getUserInfoMap().get(friend);
        if (userInfo != null) {
            ((TextView) holder.getView(R.id.text_item_friend_name)).setText(userInfo.getNickname());
            ((ImageView) holder.getView(R.id.image_item_friend_icon)).setImageResource(ImageUtils.getIconId(userInfo.getIcon()));
        }
    }
}
