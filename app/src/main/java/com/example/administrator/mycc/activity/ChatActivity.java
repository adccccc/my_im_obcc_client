package com.example.administrator.mycc.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import com.example.administrator.mycc.R;
import com.example.administrator.mycc.adapter.MessageAdapter;
import com.example.administrator.mycc.dao.MessageDao;
import com.example.administrator.mycc.model.BaseMessage;
import com.example.administrator.mycc.model.MessageEvent;
import com.example.administrator.mycc.model.UserInfo;
import com.example.administrator.mycc.others.AndroidBug5497Workaround;
import com.example.administrator.mycc.proto.CcPacket;
import com.example.administrator.mycc.utils.CacheUtils;
import com.example.administrator.mycc.utils.MsgIdUtils;
import okhttp3.Cache;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.util.Date;
import java.util.List;

/**
 * @Author: obc
 * @Date: 2019/3/19 15:02
 * @Version 1.0
 */

/**
 * 聊天页面
 */
public class ChatActivity extends BaseActivity {

    UserInfo user;
    UserInfo friendInfo;
    List<BaseMessage> messageList;
    boolean friendChat;

    // UI
    private ListView mMessageList;
    private MessageAdapter messageAdapter;
    private TextView mNameText;
    private ImageButton mBackButton;
    private ImageButton mImageButton;
    private EditText mInputText;
    private Button mSendButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        user = CacheUtils.getInstance().getUserInfoMap().get(CacheUtils.getInstance().getLoginInfo().getUsername());
        Intent intent = getIntent();
        friendChat = intent.getBooleanExtra("friendChat", true);
        if (friendChat) {
            friendInfo = CacheUtils.getInstance().getUserInfoMap().get(intent.getStringExtra("friendId"));
            messageList = CacheUtils.getInstance().getSingleMessageMap().get(friendInfo.getUsername());
            if (messageList == null) {
                // UI线程读db   TODO: 改成子线程
                messageList = MessageDao.getInstance(this).getSingleMessageList(user.getUsername(), friendInfo.getUsername());
                CacheUtils.getInstance().getSingleMessageMap().put(friendInfo.getUsername(), messageList);
            }

        } else {
            // TODO: 群聊天
        }

        initView();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        // 处理聊天框页面上移
        AndroidBug5497Workaround.assistActivity(this);

        mMessageList = findViewById(R.id.list_activity_chat_message);
        mNameText = findViewById(R.id.text_activity_chat_friend);
        mInputText = findViewById(R.id.text_activity_chat_input);
        mBackButton = findViewById(R.id.button_activity_chat_back);
        mImageButton = findViewById(R.id.button_activity_chat_image);
        mSendButton = findViewById(R.id.button_activity_chat_send);
        messageAdapter = new MessageAdapter(this, messageList);

        mMessageList.setAdapter(messageAdapter);
        // 移到最底部
        mMessageList.setSelection(messageAdapter.getCount()-1);

        if (friendChat) {
            mNameText.setText(friendInfo.getNickname());
        } else {
            // TODO
        }
        mInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mInputText.getText().length() > 0) {    // 输入栏非空时可以发送
                    mSendButton.setClickable(true);
                } else {    // 输入栏为空时，无法点击发送按钮
                    mSendButton.setClickable(false);
                }
            }
        });
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatActivity.super.onBackPressed();
            }
        });
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
            }
        });
        mSendButton.setClickable(false);    // 初始时无法点击
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInputText.getText().length() == 0) {
                    mSendButton.setClickable(false);
                    return;
                }

                CcPacket.SingleChatPacket.Builder builder = CcPacket.SingleChatPacket.newBuilder();
                builder.setMessageId(MsgIdUtils.getUUID())
                        .setTimestamp(new Date().getTime())
                        .setSenderId(user.getUsername())
                        .setReceiverId(friendInfo.getUsername())
                        .setType(CcPacket.SingleChatPacket.MessageType.WORD)
                        .setContent(mInputText.getText().toString());
                CcPacket.SingleChatPacket packet = builder.build();

                // 将消息交给service发送
                MessageEvent event = new MessageEvent(MessageEvent.EventType.SEND_SINGLE_MESSAGE);
                event.setMessage(packet);
                EventBus.getDefault().post(event);

                mInputText.setText("");
                mSendButton.setClickable(false);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getType()) {
            case UPDATE_VIEW:
                System.out.println("ChatActivity更新View");
                messageAdapter.notifyDataSetChanged();
                // 滚动到底部
                mMessageList.setSelection(messageAdapter.getCount() -1);
                break;
            default:
                break;
        }
    }

}
