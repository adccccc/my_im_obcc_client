package com.example.administrator.mycc.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.administrator.mycc.R;
import com.example.administrator.mycc.model.ChatTitle;
import com.example.administrator.mycc.model.UserInfo;
import com.example.administrator.mycc.utils.CacheUtils;
import com.example.administrator.mycc.utils.ImageUtils;
import com.example.administrator.mycc.utils.TimeUtils;

import java.util.List;

/**
 * @Author: obc
 * @Date: 2019/3/18 14:13
 * @Version 1.0
 */
public class ChatTitleAdapter extends CustomListViewAdapter<ChatTitle> {

    public ChatTitleAdapter(Context context, List<ChatTitle> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, ChatTitle chatTitle) {
        // 设置最新消息内容
        ((TextView)holder.getView(R.id.text_item_chat_message)).setText(chatTitle.getContent());
        // 设置时间
        ((TextView)holder.getView(R.id.text_item_chat_time)).setText(TimeUtils.timestampToChatTitleTime(chatTitle.getTimestamp()));

        if (chatTitle.getChatType() == 0) {     // 好友聊天
            UserInfo userInfo = CacheUtils.getInstance().getUserInfoMap().get(chatTitle.getId());
            if (userInfo != null) {
                ((TextView) holder.getView(R.id.text_item_chat_name)).setText(userInfo.getNickname());
                ((ImageView) holder.getView(R.id.image_item_chat_icon)).setImageResource(ImageUtils.getIconId(userInfo.getIcon()));
            }
        } else if (chatTitle.getChatType() == 1){       // 群聊天

        } else {    // 其他

        }
    }
}
