package com.example.administrator.mycc.fragment;

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
import com.example.administrator.mycc.adapter.FriendAdapter;
import com.example.administrator.mycc.dao.FriendDao;
import com.example.administrator.mycc.dao.UserInfoDao;
import com.example.administrator.mycc.others.MyThreadPoolManager;
import com.example.administrator.mycc.retrofit.response.FriendsListResponse;
import com.example.administrator.mycc.model.UserInfo;
import com.example.administrator.mycc.retrofit.AddTokenInterceptor;
import com.example.administrator.mycc.retrofit.FriendService;
import com.example.administrator.mycc.utils.CacheUtils;
import com.example.administrator.mycc.utils.ConstantUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author: obc
 * @Date: 2019/3/13 10:47
 * @Version 1.0
 */

/**
 * 联系人页面
 */
public class ContactFragment extends Fragment {

    String username;
    String token;
    // UI
    private ListView listView;
    private FriendAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        username = CacheUtils.getInstance().getLoginInfo().getUsername();
        token = CacheUtils.getInstance().getLoginInfo().getToken();

        initView(view);
        loadFriendsDataAsync();
        getFriendsListByRetrofit();

        return view;
    }

    // 异步从db加载联系人信息
    private void loadFriendsDataAsync() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {

                // 只在首次进入时加载
                if (CacheUtils.getInstance().getFriendsList().size() > 0) {
                    observableEmitter.onComplete();
                    return;
                }
                FriendDao friendDao = FriendDao.getInstance(getContext());
                List<String> friendsList = null;
                try {
                    friendsList = friendDao.getAllFriends(username);
                } catch (Exception e) {
                    observableEmitter.onError(e);
                    return;
                }
                if (friendsList != null) {
                    CacheUtils.getInstance().getFriendsList().addAll(friendsList);
                }
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
                        Toast.makeText(getContext(), "联系人加载失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    // http更新联系人信息
    @SuppressWarnings("unchecked")
    private void getFriendsListByRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ConstantUtils.HTTP_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient.Builder()
                        .connectTimeout(ConstantUtils.HTTP_CONNECT_TIME_OUT, TimeUnit.SECONDS)
                        .addInterceptor(new AddTokenInterceptor(token))
                        .build())
                .build();
        FriendService service = retrofit.create(FriendService.class);

        service.getFriendsList(username)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<FriendsListResponse>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {

                    }

                    @Override
                    public void onNext(FriendsListResponse respObj) {

                        if (respObj == null || respObj.getCode() != 0) {
                            Toast.makeText(getActivity(), "请求好友列表失败", Toast.LENGTH_SHORT).show();
                        } else {
                            List<UserInfo> latestList = respObj.getData().getFriendList();
                            Set<String> friendSet = new HashSet<>();

                            List<String> cacheList = CacheUtils.getInstance().getFriendsList();
                            final FriendDao friendDao = FriendDao.getInstance(getContext());
                            final UserInfoDao userInfoDao = UserInfoDao.getInstance(getContext());

                            for (final UserInfo userInfo : latestList) {
                                friendSet.add(userInfo.getUsername());
                                // 向好友列表中添加新的好友并更新db
                                if (!cacheList.contains(userInfo.getUsername())) {
                                    cacheList.add(userInfo.getUsername());
                                    // 子线程更新db
                                    MyThreadPoolManager.getInstance().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            friendDao.insertFriend(username, userInfo.getUsername());
                                        }
                                    });
                                }
                                // 更新用户信息Map
                                UserInfo lastInfo = CacheUtils.getInstance().getUserInfoMap().get(userInfo.getUsername());
                                if (lastInfo == null || !lastInfo.equals(userInfo)) {
                                    CacheUtils.getInstance().getUserInfoMap().put(userInfo.getUsername(), userInfo);
                                    MyThreadPoolManager.getInstance().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            userInfoDao.insertOrUpdateUserInfo(userInfo);
                                        }
                                    });
                                }

                            }

                            // 向缓存列表中移除好友并更新db
                            Iterator<String> iterator = cacheList.iterator();
                            while (iterator.hasNext()) {
                                final String friend = iterator.next();
                                if (!friendSet.contains(friend)) {
                                    iterator.remove();
                                    // 子线程更新db
                                    MyThreadPoolManager.getInstance().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            friendDao.deleteFriendByUsername(username, friend);
                                        }
                                    });
                                }
                            }

                            // 重绘listview
                            adapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                        Toast.makeText(getActivity(), "请求好友列表异常", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void initView(View view) {
        listView = view.findViewById(R.id.list_contact);
        adapter = new FriendAdapter(getContext(), CacheUtils.getInstance().getFriendsList(), R.layout.item_friend_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("friendChat", true);
                intent.putExtra("friendId", adapter.getItem(i));
                startActivity(intent);
            }
        });

    }

}
