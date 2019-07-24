package com.example.administrator.mycc.fragment;

/**
 * @Author: obc
 * @Date: 2019/3/13 10:47
 * @Version 1.0
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.example.administrator.mycc.R;
import com.example.administrator.mycc.activity.ChatActivity;
import com.example.administrator.mycc.adapter.ChatTitleAdapter;
import com.example.administrator.mycc.dao.ChatTitleDao;
import com.example.administrator.mycc.model.ChatTitle;
import com.example.administrator.mycc.model.MessageEvent;
import com.example.administrator.mycc.others.MyThreadPoolManager;
import com.example.administrator.mycc.utils.CacheUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 消息页面
 */
public class ChatFragment extends Fragment {

    String username;
    // UI
    private ListView listView;
    private ChatTitleAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username = CacheUtils.getInstance().getLoginInfo().getUsername();
        // 注册EventBus监听
        EventBus.getDefault().register(this);
        loadChatDataAsync();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 注销EventBus监听
        EventBus.getDefault().unregister(this);
    }

    private void loadChatDataAsync() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {

                // 只在TitleList为空时从db加载(即第一次进入主界面时)
                if (CacheUtils.getInstance().getChatTitleList().size() > 0) {
                    observableEmitter.onComplete();
                    return;
                }

                List<ChatTitle> list = null;
                ChatTitleDao chatTitleDao = ChatTitleDao.getInstance(getContext());
                try {
                    list = chatTitleDao.getAllChatTitles(username);
                } catch (Exception e) {
                    e.printStackTrace();
                    observableEmitter.onError(e);
                    return;
                }
                if (list != null) {
                    CacheUtils.getInstance().getChatTitleList().addAll(list);
                    // 按时间戳降序排列
                    Collections.sort(CacheUtils.getInstance().getChatTitleList());
                }
                observableEmitter.onComplete();
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {

                    }

                    @Override
                    public void onNext(Integer integer) {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Toast.makeText(getContext(), "加载聊天列表失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        // 加载完成，重绘list
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void initView(View view) {
        listView = view.findViewById(R.id.list_chat);
        adapter = new ChatTitleAdapter(getContext(), CacheUtils.getInstance().getChatTitleList(), R.layout.item_chat_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {     // 点击跳转到聊天界面
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final ChatTitle chatTitle = adapter.getItem(i);
                // 未读消息计数清零
                chatTitle.setMsgCount(0);
                // 更新db
                final Context context = getContext();
                MyThreadPoolManager.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        ChatTitleDao.getInstance(context).insertOrUpdateChatTitle(username, chatTitle);
                    }
                });

                Intent intent = new Intent(getActivity(), ChatActivity.class);
                if (chatTitle.getChatType() == 0) {     // 好友聊天
                    intent.putExtra("friendChat", true);
                    intent.putExtra("friendId", chatTitle.getId());
                } else {        // 群聊天
                    intent.putExtra("friendChat", false);
                    intent.putExtra("groupId", chatTitle.getId());
                }
                startActivity(intent);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getType()) {
            case UPDATE_VIEW:
                adapter.notifyDataSetChanged();
                break;
            default:
                break;

        }
    }
}
