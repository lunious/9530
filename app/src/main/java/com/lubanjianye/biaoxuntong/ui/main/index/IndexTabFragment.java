package com.lubanjianye.biaoxuntong.ui.main.index;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.flyco.tablayout.SlidingTabLayout;
import com.igexin.sdk.PushManager;
import com.lubanjianye.biaoxuntong.R;
import com.lubanjianye.biaoxuntong.base.BaseFragment;
import com.lubanjianye.biaoxuntong.database.DatabaseManager;
import com.lubanjianye.biaoxuntong.database.UserProfile;
import com.lubanjianye.biaoxuntong.eventbus.EventMessage;
import com.lubanjianye.biaoxuntong.api.BiaoXunTongApi;
import com.lubanjianye.biaoxuntong.sign.SignInActivity;
import com.lubanjianye.biaoxuntong.ui.citypicker.CityPicker;
import com.lubanjianye.biaoxuntong.ui.citypicker.adapter.OnPickListener;
import com.lubanjianye.biaoxuntong.ui.citypicker.model.City;
import com.lubanjianye.biaoxuntong.ui.citypicker.model.HotCity;
import com.lubanjianye.biaoxuntong.ui.citypicker.model.LocateState;
import com.lubanjianye.biaoxuntong.ui.citypicker.model.LocatedCity;
import com.lubanjianye.biaoxuntong.ui.main.index.search.IndexSearchActivity;
import com.lubanjianye.biaoxuntong.ui.main.index.sortcolumn.SortColumnActivity;
import com.lubanjianye.biaoxuntong.util.dialog.PromptButton;
import com.lubanjianye.biaoxuntong.util.dialog.PromptButtonListener;
import com.lubanjianye.biaoxuntong.util.dialog.PromptDialog;
import com.lubanjianye.biaoxuntong.util.sp.AppSharePreferenceMgr;
import com.lubanjianye.biaoxuntong.util.toast.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.lubanjianye.biaoxuntong.app.BiaoXunTong.getApplicationContext;

/**
 * 项目名:   AppLunious
 * 包名:     com.lubanjianye.biaoxuntong.ui.fragment
 * 文件名:   IndexTabFragment
 * 创建者:   lunious
 * 创建时间: 2017/12/9  0:33
 * 描述:     TODO
 */

public class IndexTabFragment extends BaseFragment implements View.OnClickListener, BDLocationListener {

    private SlidingTabLayout indexStlTab = null;
    private ViewPager indexVp = null;
    private LinearLayout llSearch = null;
    private ImageView ivAdd = null;


    private String clientID = PushManager.getInstance().getClientid(getApplicationContext());

    private IndexFragmentAdapter mAdapter = null;

    private final List<String> mList = new ArrayList<String>();


    private long userId = 0;
    private PromptDialog promptDialog;

    private Boolean IsToken = true;
    private boolean isInitCache = false;

    private LinearLayout ll_location = null;
    private AppCompatTextView tv_location = null;

    public LocationClient mLocationClient = null;


    @Override
    public Object setLayout() {
        return R.layout.fragment_main_index;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        //取消注册EventBus
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void initView() {

        //注册EventBus
        EventBus.getDefault().register(this);

        //创建对象
        promptDialog = new PromptDialog(getActivity());

        indexStlTab = getView().findViewById(R.id.index_stl_tab);
        indexVp = getView().findViewById(R.id.index_vp);
        llSearch = getView().findViewById(R.id.ll_search);
        ivAdd = getView().findViewById(R.id.iv_add);

        llSearch.setOnClickListener(this);
        ivAdd.setOnClickListener(this);

        ll_location = getView().findViewById(R.id.ll_location);
        tv_location = getView().findViewById(R.id.tv_location);
        ll_location.setOnClickListener(this);


        //定位相关
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(this);
        //注册监听函数
        LocationClientOption option = new LocationClientOption();

        option.setIsNeedAddress(true);
        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true
        mLocationClient.setLocOption(option);


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void XXXXXX(EventMessage message) {


        if (EventMessage.LOGIN_SUCCSS.equals(message.getMessage()) || EventMessage.LOGIN_OUT.equals(message.getMessage())
                || EventMessage.TAB_CHANGE.equals(message.getMessage())) {
            //更新UI
            if (indexStlTab != null) {
                indexStlTab.setCurrentTab(0);
                indexStlTab.setViewPager(indexVp);
                indexStlTab.notifyDataSetChanged();
            }
            requestData();
        }


    }

    @Override
    public void initData() {

        requestData();
        //开始定位
        mLocationClient.start();

        //热门城市
        hotCities = new ArrayList<>();
        hotCities.add(new HotCity("四川", "四川", null));
        hotCities.add(new HotCity("重庆", "重庆", null));

    }

    @Override
    public void initEvent() {

        if (AppSharePreferenceMgr.contains(getContext(), EventMessage.TOKEN_FALSE)) {

            final PromptButton cancel = new PromptButton("取      消", new PromptButtonListener() {
                @Override
                public void onClick(PromptButton button) {
                    AppSharePreferenceMgr.remove(getContext(), EventMessage.TOKEN_FALSE);
                }
            });
            cancel.setTextColor(Color.parseColor("#cccc33"));
            cancel.setTextSize(16);

            final PromptButton toLogin = new PromptButton("重新登陆", new PromptButtonListener() {
                @Override
                public void onClick(PromptButton button) {
                    AppSharePreferenceMgr.remove(getContext(), EventMessage.TOKEN_FALSE);
                    startActivity(new Intent(getActivity(), SignInActivity.class));
                }
            });
            toLogin.setTextColor(Color.parseColor("#00bfdc"));
            toLogin.setTextSize(16);
            promptDialog.getAlertDefaultBuilder().withAnim(false).cancleAble(false).touchAble(false);
            promptDialog.showWarnAlert("账号登陆过期、请重新登录!", toLogin, cancel, false);
        }

    }


    public void requestData() {

        Log.d("DASBDASDASD", "我被调用了");

        if (AppSharePreferenceMgr.contains(getContext(), EventMessage.LOGIN_SUCCSS)) {
            //得到用户userId
            List<UserProfile> users = DatabaseManager.getInstance().getDao().loadAll();
            for (int i = 0; i < users.size(); i++) {
                userId = users.get(0).getId();
            }

            OkGo.<String>post(BiaoXunTongApi.URL_INDEXTAB)
                    .params("userId", userId)
                    .params("clientId", clientID)
                    .cacheKey("index_tab_cache_login" + userId)
                    .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
                    .cacheTime(3600 * 48000)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {

                            final JSONObject object = JSON.parseObject(response.body());
                            String status = object.getString("status");
                            String message = object.getString("message");

                            if ("200".equals(status)) {
                                final JSONArray ownerList = object.getJSONArray("data");
                                if (mList.size() > 0) {
                                    mList.clear();
                                }

                                for (int i = 0; i < ownerList.size(); i++) {
                                    final JSONObject list = ownerList.getJSONObject(i);
                                    String name = list.getString("name");
                                    mList.add(name);
                                }

                                mAdapter = new IndexFragmentAdapter(getContext(), getFragmentManager(), mList);
                                indexVp.setAdapter(mAdapter);
                                indexStlTab.setViewPager(indexVp);
                                mAdapter.notifyDataSetChanged();

                            } else {
                                ToastUtil.shortToast(getContext(), message);
                            }
                        }

                        @Override
                        public void onCacheSuccess(Response<String> response) {
                            if (!isInitCache) {
                                final JSONObject object = JSON.parseObject(response.body());
                                String status = object.getString("status");
                                String message = object.getString("message");

                                if ("200".equals(status)) {
                                    final JSONArray ownerList = object.getJSONArray("data");
                                    if (mList.size() > 0) {
                                        mList.clear();
                                    }

                                    for (int i = 0; i < ownerList.size(); i++) {
                                        final JSONObject list = ownerList.getJSONObject(i);
                                        String name = list.getString("name");
                                        mList.add(name);
                                    }

                                    mAdapter = new IndexFragmentAdapter(getContext(), getFragmentManager(), mList);
                                    indexVp.setAdapter(mAdapter);
                                    indexStlTab.setViewPager(indexVp);
                                    mAdapter.notifyDataSetChanged();

                                } else {
                                    ToastUtil.shortToast(getContext(), message);
                                }
                                isInitCache = true;
                            }
                        }
                    });


        } else {
            OkGo.<String>post(BiaoXunTongApi.URL_INDEXTAB)
                    .params("clientId", clientID)
                    .cacheKey("index_tab_cache_no_login" + userId)
                    .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
                    .cacheTime(3600 * 48000)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            final JSONObject object = JSON.parseObject(response.body());
                            String status = object.getString("status");
                            String message = object.getString("message");

                            if ("200".equals(status)) {
                                final JSONArray ownerList = object.getJSONArray("data");
                                if (mList.size() > 0) {
                                    mList.clear();
                                }

                                for (int i = 0; i < ownerList.size(); i++) {
                                    final JSONObject list = ownerList.getJSONObject(i);
                                    String name = list.getString("name");
                                    mList.add(name);
                                }

                                mAdapter = new IndexFragmentAdapter(getContext(), getFragmentManager(), mList);
                                indexVp.setAdapter(mAdapter);
                                indexStlTab.setViewPager(indexVp);
                                mAdapter.notifyDataSetChanged();
                            } else {
                                ToastUtil.shortToast(getContext(), message);
                            }
                        }

                        @Override
                        public void onCacheSuccess(Response<String> response) {
                            if (!isInitCache) {
                                final JSONObject object = JSON.parseObject(response.body());
                                String status = object.getString("status");
                                String message = object.getString("message");

                                if ("200".equals(status)) {
                                    final JSONArray ownerList = object.getJSONArray("data");
                                    if (mList.size() > 0) {
                                        mList.clear();
                                    }

                                    for (int i = 0; i < ownerList.size(); i++) {
                                        final JSONObject list = ownerList.getJSONObject(i);
                                        String name = list.getString("name");
                                        mList.add(name);
                                    }

                                    mAdapter = new IndexFragmentAdapter(getContext(), getFragmentManager(), mList);
                                    indexVp.setAdapter(mAdapter);
                                    indexStlTab.setViewPager(indexVp);
                                    mAdapter.notifyDataSetChanged();

                                } else {
                                    ToastUtil.shortToast(getContext(), message);
                                }
                                isInitCache = true;
                            }
                        }
                    });

        }

    }

    private List<HotCity> hotCities;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_search:
                startActivity(new Intent(getActivity(), IndexSearchActivity.class));
                break;
            case R.id.iv_add:
                startActivity(new Intent(getActivity(), SortColumnActivity.class));
                break;
            case R.id.ll_location:
                CityPicker.getInstance()
                        .setFragmentManager(getFragmentManager())
                        .enableAnimation(true)
                        .setAnimationStyle(R.style.CustomAnim)
                        .setLocatedCity(new LocatedCity("成都", "浙江", "101210101"))
                        .setHotCities(hotCities)
                        .setOnPickListener(new OnPickListener() {
                            @Override
                            public void onPick(int position, City data) {
                                tv_location.setText(data == null ? "四川" : String.format("%s", data.getName()));
                            }

                            @Override
                            public void onLocate() {
                                //开始定位，这里模拟一下定位
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        CityPicker.getInstance()
                                                .locateComplete(new LocatedCity("成都", "广东", "101280601"),
                                                        LocateState.SUCCESS);
                                    }
                                }, 2000);
                            }
                        })
                        .show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        String city = bdLocation.getCity();
        Log.d("BDASBDASDA", city);
    }
}
