package com.lubanjianye.biaoxuntong.ui.main.index;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
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
import com.lubanjianye.biaoxuntong.app.BiaoXunTong;
import com.lubanjianye.biaoxuntong.base.BaseFragment;
import com.lubanjianye.biaoxuntong.database.DatabaseManager;
import com.lubanjianye.biaoxuntong.database.UserProfile;
import com.lubanjianye.biaoxuntong.eventbus.EventMessage;
import com.lubanjianye.biaoxuntong.app.BiaoXunTongApi;
import com.lubanjianye.biaoxuntong.ui.main.result.detail.sichuan.ResultSggjyzbjgDetailActivity;
import com.lubanjianye.biaoxuntong.ui.search.activity.SearchActivity;
import com.lubanjianye.biaoxuntong.ui.sign.SignInActivity;
import com.lubanjianye.biaoxuntong.ui.citypicker.CityPicker;
import com.lubanjianye.biaoxuntong.ui.citypicker.adapter.OnPickListener;
import com.lubanjianye.biaoxuntong.ui.citypicker.model.City;
import com.lubanjianye.biaoxuntong.ui.citypicker.model.HotCity;
import com.lubanjianye.biaoxuntong.ui.citypicker.model.LocatedCity;
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

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.lubanjianye.biaoxuntong.app.BiaoXunTong.getApplicationContext;

/**
 * 项目名:   AppLunious
 * 包名:     com.lubanjianye.biaoxuntong.ui.fragment
 * 文件名:   IndexTabFragment
 * 创建者:   lunious
 * 创建时间: 2017/12/9  0:33
 * 描述:     TODO
 */

public class IndexTabFragment extends BaseFragment implements View.OnClickListener, BDLocationListener, EasyPermissions.PermissionCallbacks {

    private SlidingTabLayout indexStlTab = null;
    private ViewPager indexVp = null;
    private LinearLayout llSearch = null;
    private ImageView ivAdd = null;

    private List<HotCity> hotCities;

    public LocationClient mLocationClient = null;

    private String clientID = PushManager.getInstance().getClientid(getApplicationContext());

    private IndexFragmentAdapter mAdapter = null;

    private final List<String> mList = new ArrayList<String>();


    private long userId = 0;
    private PromptDialog promptDialog;

    private boolean isInitCache = false;

    private LinearLayout ll_location = null;
    private AppCompatTextView tv_location = null;


    private String locationArea = "";


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


        if (AppSharePreferenceMgr.contains(getContext(), EventMessage.LOCA_AREA)) {
            String area = (String) AppSharePreferenceMgr.get(getContext(), EventMessage.LOCA_AREA, "");
            tv_location.setText(area);

        } else {
            tv_location.setText("四川");
        }


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
            requestData(true);
        }


    }

    @Override
    public void initData() {

        requestData(false);


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

    @Override
    public void initEvent() {
        //热门城市
        hotCities = new ArrayList<>();
        hotCities.add(new HotCity("四川", "四川", null));
        hotCities.add(new HotCity("重庆", "重庆", null));


        BiaoXunTong.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //检查定位
                locationTask();
            }
        }, 4000);


    }


    public void requestData(final boolean isRefresh) {

        String mDiqu = tv_location.getText().toString();


        //保存地区
        AppSharePreferenceMgr.put(getContext(), EventMessage.LOCA_AREA, mDiqu);

        if (AppSharePreferenceMgr.contains(getContext(), EventMessage.LOGIN_SUCCSS)) {
            //得到用户userId
            List<UserProfile> users = DatabaseManager.getInstance().getDao().loadAll();
            for (int i = 0; i < users.size(); i++) {
                userId = users.get(0).getId();
            }

            if (isRefresh){
                OkGo.<String>post(BiaoXunTongApi.URL_INDEXTAB)
                        .params("userId", userId)
                        .params("clientId", clientID)
                        .params("diqu", mDiqu)
                        .cacheKey("index_tab_cache_login" + userId + mDiqu)
                        .cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)
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

                                    setUI(mList);

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

                                        setUI(mList);

                                    } else {
                                        ToastUtil.shortToast(getContext(), message);
                                    }
                                    isInitCache = true;
                                }
                            }
                        });
            }else {

                OkGo.<String>post(BiaoXunTongApi.URL_INDEXTAB)
                        .params("userId", userId)
                        .params("clientId", clientID)
                        .params("diqu", mDiqu)
                        .cacheKey("index_tab_cache_login" + userId + mDiqu)
                        .cacheMode(CacheMode.IF_NONE_CACHE_REQUEST)
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

                                    setUI(mList);

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

                                        setUI(mList);

                                    } else {
                                        ToastUtil.shortToast(getContext(), message);
                                    }
                                    isInitCache = true;
                                }
                            }
                        });
            }



        } else {

            if (isRefresh){
                OkGo.<String>post(BiaoXunTongApi.URL_INDEXTAB)
                        .params("clientId", clientID)
                        .params("diqu", mDiqu)
                        .cacheKey("index_tab_cache_no_login" + userId + mDiqu)
                        .cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)
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

                                    setUI(mList);
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

                                        setUI(mList);

                                    } else {
                                        ToastUtil.shortToast(getContext(), message);
                                    }
                                    isInitCache = true;
                                }
                            }
                        });
            }else {
                OkGo.<String>post(BiaoXunTongApi.URL_INDEXTAB)
                        .params("clientId", clientID)
                        .params("diqu", mDiqu)
                        .cacheKey("index_tab_cache_no_login" + userId + mDiqu)
                        .cacheMode(CacheMode.IF_NONE_CACHE_REQUEST)
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

                                    setUI(mList);
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

                                        setUI(mList);

                                    } else {
                                        ToastUtil.shortToast(getContext(), message);
                                    }
                                    isInitCache = true;
                                }
                            }
                        });
            }


        }

    }


    private void setUI(List<String> mList) {
        mAdapter = new IndexFragmentAdapter(getContext(), getFragmentManager(), mList);
        indexVp.setAdapter(mAdapter);
        indexStlTab.setViewPager(indexVp);
        mAdapter.notifyDataSetChanged();


        BiaoXunTong.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (promptDialog != null) {
                    promptDialog.dismissImmediately();
                }
            }
        }, 1000);


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_search:
                //点击搜素
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("searchTye", 1);
                startActivity(intent);
                break;
            case R.id.iv_add:
                startActivity(new Intent(getActivity(), SortColumnActivity.class));
                break;
            case R.id.ll_location:
                if (!TextUtils.isEmpty(locationArea)) {
                    CityPicker.getInstance()
                            .setFragmentManager(getFragmentManager())
                            .enableAnimation(true)
                            .setAnimationStyle(R.style.CustomAnim)
                            .setLocatedCity(new LocatedCity(locationArea, "", ""))
                            .setHotCities(hotCities)
                            .setOnPickListener(new OnPickListener() {
                                @Override
                                public void onPick(int position, City data) {


                                    if (data != null) {

                                        if (!data.getName().equals(tv_location.getText().toString())) {
                                            tv_location.setText(String.format("%s", data.getName()));
                                            if (indexStlTab != null) {
                                                indexStlTab.setCurrentTab(0);
                                                indexStlTab.setViewPager(indexVp);
                                                indexStlTab.notifyDataSetChanged();
                                            }
                                            promptDialog.showLoading("请稍后");
                                            requestData(true);
                                            EventBus.getDefault().post(new EventMessage(EventMessage.LOCA_AREA_CHANGE));
                                        }


                                    } else {
                                        tv_location.setText(tv_location.getText().toString());
                                    }


                                }

                                @Override
                                public void onLocate() {
                                    //开始定位，这里模拟一下定位
                                    locationTask();

                                }
                            })
                            .show();
                } else {
                    CityPicker.getInstance()
                            .setFragmentManager(getFragmentManager())
                            .enableAnimation(true)
                            .setAnimationStyle(R.style.CustomAnim)
                            .setHotCities(hotCities)
                            .setOnPickListener(new OnPickListener() {
                                @Override
                                public void onPick(int position, City data) {


                                    if (data != null) {
                                        if (!data.getName().equals(tv_location.getText().toString())) {
                                            tv_location.setText(String.format("%s", data.getName()));
                                            if (indexStlTab != null) {
                                                indexStlTab.setCurrentTab(0);
                                                indexStlTab.setViewPager(indexVp);
                                                indexStlTab.notifyDataSetChanged();
                                            }
                                            promptDialog.showLoading("请稍后");
                                            requestData(true);
                                            EventBus.getDefault().post(new EventMessage(EventMessage.LOCA_AREA_CHANGE));
                                        }

                                    } else {
                                        tv_location.setText(tv_location.getText().toString());
                                    }


                                }

                                @Override
                                public void onLocate() {
                                    //开始定位，这里模拟一下定位
                                    locationTask();

                                }
                            })
                            .show();
                }
                break;
            default:
                break;
        }
    }

    private static final int RC_LOCATION_PERM = 123;

    @AfterPermissionGranted(RC_LOCATION_PERM)
    public void locationTask() {
        if (EasyPermissions.hasPermissions(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Have permission, do the thing!
            //开始定位
            mLocationClient.start();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.rationale_location),
                    RC_LOCATION_PERM,
                    android.Manifest.permission.ACCESS_FINE_LOCATION);

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

    }


    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        String province = bdLocation.getProvince();
        locationArea = province.substring(0, province.length() - 1);


        String area = tv_location.getText().toString();

        if (!area.equals(locationArea)) {
            //是否切换地区
            final PromptButton cancel = new PromptButton("取      消", new PromptButtonListener() {
                @Override
                public void onClick(PromptButton button) {

                }
            });
            cancel.setTextColor(getResources().getColor(R.color.main_status_yellow));
            cancel.setTextSize(16);

            final PromptButton sure = new PromptButton("切      换", new PromptButtonListener() {
                @Override
                public void onClick(PromptButton button) {
                    //确认切换地区，刷新数据
                    tv_location.setText(locationArea);

                    //更新UI
                    if (indexStlTab != null) {
                        indexStlTab.setCurrentTab(0);
                        indexStlTab.setViewPager(indexVp);
                        indexStlTab.notifyDataSetChanged();
                    }

                    promptDialog.showLoading("请稍后");
                    requestData(true);

                    EventBus.getDefault().post(new EventMessage(EventMessage.LOCA_AREA_CHANGE));
                }
            });
            sure.setTextColor(getResources().getColor(R.color.main_status_blue));
            sure.setTextSize(16);
            promptDialog.getAlertDefaultBuilder().withAnim(true).cancleAble(false).touchAble(false)
                    .round(8).loadingDuration(200);
            promptDialog.showWarnAlert("当前定位为" + locationArea + "," + "是否切换到" + locationArea + "?", cancel, sure, true);
        }


    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.d("AJSNBDASDA", "同意");
        mLocationClient.start();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.d("AJSNBDASDA", "拒绝");

        Log.d("HBDAHUBSDASDA", "requestCode==" + requestCode + "\n" + "perms==" + perms.size());
    }
}
