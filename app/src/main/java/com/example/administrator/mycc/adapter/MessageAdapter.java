package com.example.administrator.mycc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.administrator.mycc.R;
import com.example.administrator.mycc.model.BaseMessage;
import com.example.administrator.mycc.model.SingleMessage;
import com.example.administrator.mycc.model.UserInfo;
import com.example.administrator.mycc.utils.CacheUtils;
import com.example.administrator.mycc.utils.ImageUtils;
import com.example.administrator.mycc.utils.TimeUtils;

import java.util.List;

/**
 * @Author: obc
 * @Date: 2019/3/19 15:16
 * @Version 1.0
 */

/**
 * 聊天消息list适配器
 */
public class MessageAdapter extends CustomListViewAdapter<BaseMessage> {

    private static final int TYPE_TEXT_LEFT = 0;
    private static final int TYPE_TEXT_RIGHT = 1;
    private static final int TYPE_IMAGE_LEFT = 2;
    private static final int TYPE_IMAGE_RIGHT = 3;

    // 填充itemList的messageList
    private List<BaseMessage> mDatas;

    // 用来判断用左边item还是右边item
    private String username;

    public MessageAdapter(Context context, List<BaseMessage> datas) {
        // 这里由于是多个item布局，所以初始化时不传LayoutId，直接写死在方法
        super(context, datas, 0);
        this.mDatas = datas;
        this.username = CacheUtils.getInstance().getLoginInfo().getUsername();
    }

    /**
     * 多item布局
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        BaseMessage message = mDatas.get(position);

        if (message.getSenderId().equals(username)) {       // 右边item
            if (message.getMessageType() == 0) {    // 右边文字item
                return TYPE_TEXT_RIGHT;
            } else {
                return TYPE_IMAGE_RIGHT;    // 右边图片item
            }
        } else {        // 左边item
            if (message.getMessageType() == 0) {
                return TYPE_TEXT_LEFT;      // 左边文字item
            } else {
                return TYPE_IMAGE_LEFT;     // 左边图片item
            }
        }
    }

    /**
     * item布局种类数
     * @return
     */
    @Override
    public int getViewTypeCount() {
        return 4;
    }

    /**
     * 因为是多item布局 所以需要重写父类方法
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        BaseMessage message = getItem(position);
        String timeText = TimeUtils.timestampToMessageTime(message.getTimestamp());
        UserInfo userInfo = CacheUtils.getInstance().getUserInfoMap().get(message.getSenderId());
        String nicknameText;
        int icon_num;
        if (userInfo != null) {
            nicknameText = userInfo.getNickname();
            icon_num = userInfo.getIcon();
        } else {
            nicknameText = "unknown";
            icon_num = -1;
        }

        // 决定是否显示时间标签
        boolean showTimestamp = true;
        if (position > 0) {
            BaseMessage lastMessage = getItem(position - 1);
            // 两条消息间隔在2分钟内时， 不显示时间标签
            if (lastMessage != null && (message.getTimestamp() - lastMessage.getTimestamp()) <= 1000*60*2 ) {
                showTimestamp = false;
            }
        }

        switch (getItemViewType(position)) {
            case TYPE_TEXT_LEFT:        // 左边文字消息
                viewHolder = ViewHolder.get(mContext, convertView, parent, R.layout.item_message_list_single_word_left, position);
                ((TextView)viewHolder.getView(R.id.text_item_message_single_word_left_time)).setText(timeText);
                ((TextView)viewHolder.getView(R.id.text_item_message_single_word_left_name)).setText(nicknameText);
                ((ImageView)viewHolder.getView(R.id.image_item_message_single_word_left_icon)).setImageResource(ImageUtils.getIconId(icon_num));
                ((TextView)viewHolder.getView(R.id.text_item_message_single_word_left_content)).setText(message.getContent());
                if (!showTimestamp) {       // 隐藏时间标签
                    viewHolder.getView(R.id.text_item_message_single_word_left_time).setVisibility(View.GONE);
                } else {        // 因为viewHolder复用机制的关系  必须要有对应的else复原属性
                    viewHolder.getView(R.id.text_item_message_single_word_left_time).setVisibility(View.VISIBLE);
                }

                if (message.isSingleMessage()) {    // 好友信息隐藏nickname
                    viewHolder.getView(R.id.text_item_message_single_word_left_name).setVisibility(View.GONE);
                } else {
                    viewHolder.getView(R.id.text_item_message_single_word_left_name).setVisibility(View.VISIBLE);
                }
                break;

            case TYPE_TEXT_RIGHT:       // 右边文字消息
                viewHolder = ViewHolder.get(mContext, convertView, parent, R.layout.item_message_list_single_word_right, position);
                ((TextView)viewHolder.getView(R.id.text_item_message_single_word_right_time)).setText(timeText);
                ((ImageView)viewHolder.getView(R.id.image_item_message_single_word_right_icon)).setImageResource(ImageUtils.getIconId(icon_num));
                ((TextView)viewHolder.getView(R.id.text_item_message_single_word_right_content)).setText(message.getContent());
                if (!showTimestamp) {       // 隐藏时间标签
                    viewHolder.getView(R.id.text_item_message_single_word_right_time).setVisibility(View.GONE);
                } else {
                    viewHolder.getView(R.id.text_item_message_single_word_right_time).setVisibility(View.VISIBLE);
                }
                break;

            case TYPE_IMAGE_LEFT:       // 左边图片消息
                viewHolder = ViewHolder.get(mContext, convertView, parent, R.layout.item_message_list_single_image_left, position);
                // TODO:图片消息
                break;

            case TYPE_IMAGE_RIGHT:      // 右边图片消息
                viewHolder = ViewHolder.get(mContext, convertView, parent, R.layout.item_message_list_single_image_right, position);
                // TODO:图片消息
                break;

            default:
                return null;
        }
        return viewHolder.getConvertView();
    }

    // 本类中这个方法废弃，逻辑写在getView中
    @Override
    public void convert(ViewHolder holder, BaseMessage baseMessage) {

    }
}
