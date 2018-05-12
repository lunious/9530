package com.lubanjianye.biaoxuntong.ui.browser;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.just.agentweb.AgentWeb;
import com.lubanjianye.biaoxuntong.R;
import com.lubanjianye.biaoxuntong.app.BiaoXunTongApi;
import com.lubanjianye.biaoxuntong.base.BaseFragment;
import com.lubanjianye.biaoxuntong.database.DatabaseManager;
import com.lubanjianye.biaoxuntong.database.UserProfile;
import com.lubanjianye.biaoxuntong.eventbus.EventMessage;
import com.lubanjianye.biaoxuntong.ui.sign.SignInActivity;
import com.lubanjianye.biaoxuntong.ui.share.Share;
import com.lubanjianye.biaoxuntong.util.aes.AesUtil;
import com.lubanjianye.biaoxuntong.util.netStatus.AppSysMgr;
import com.lubanjianye.biaoxuntong.util.sp.AppSharePreferenceMgr;
import com.lubanjianye.biaoxuntong.util.toast.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


public class BrowserDetailFragment extends BaseFragment implements View.OnClickListener {


    private LinearLayout mBack = null;
    private AppCompatTextView mainTitle = null;
    private LinearLayout ll_webview = null;
    private AgentWeb webView = null;
    private ImageView ivFav = null;
    private LinearLayout llFav = null;
    private LinearLayout llShare = null;
    private static final String ARG_URL = "ARG_URL";
    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ENTITY = "ENTITY";
    private static final String ENTITYID = "ENTITYID";

    public String mApi = "";
    public String mUrl = "";
    public String mTitle = "";
    private int mEntityId = -1;
    private String mEntity = "";
    private int myFav = -1;
    private String mDiqu = "";
    private String deviceId = AppSysMgr.getPsuedoUniqueID();
    private String ajaxType = "0";


    public static BrowserDetailFragment create(@NonNull String api, String title, String entity, int entityid) {
        final Bundle args = new Bundle();
        args.putString(ARG_URL, api);
        args.putString(ARG_TITLE, title);
        args.putString(ENTITY, entity);
        args.putInt(ENTITYID, entityid);
        final BrowserDetailFragment fragment = new BrowserDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public Object setLayout() {
        return R.layout.fragment_browser_detail;
    }

    @Override
    public void initView() {
        mBack = getView().findViewById(R.id.ll_iv_back);
        mainTitle = getView().findViewById(R.id.main_bar_name);
        ll_webview = getView().findViewById(R.id.ll_webview);
        ivFav = getView().findViewById(R.id.iv_fav);
        llFav = getView().findViewById(R.id.ll_fav);
        llShare = getView().findViewById(R.id.ll_share);
        mBack.setOnClickListener(this);
        llShare.setOnClickListener(this);
        llFav.setOnClickListener(this);

        if (AppSharePreferenceMgr.contains(getContext(), EventMessage.LOCA_AREA)) {
            mDiqu = (String) AppSharePreferenceMgr.get(getContext(), EventMessage.LOCA_AREA, "");
        }


    }

    @Override
    public void initData() {
        mBack.setVisibility(View.VISIBLE);
        mainTitle.setText("标讯详情");
        final Bundle args = getArguments();
        if (args != null) {
            mApi = args.getString(ARG_URL);
            mTitle = args.getString(ARG_TITLE);
            mEntity = args.getString(ENTITY);
            mEntityId = args.getInt(ENTITYID);
        }

        requestData();

    }

    @Override
    public void initEvent() {

    }

    private long id = 0;

    private void requestData() {
        if (AppSharePreferenceMgr.contains(getContext(), EventMessage.LOGIN_SUCCSS)) {
            List<UserProfile> users = DatabaseManager.getInstance().getDao().loadAll();

            for (int i = 0; i < users.size(); i++) {
                id = users.get(0).getId();
            }

            OkGo.<String>post(mApi)
                    .params("entityId", mEntityId)
                    .params("entity", mEntity)
                    .params("userid", id)
                    .params("diqu", mDiqu)
                    .params("deviceId", deviceId)
                    .params("ajaxlogtype", ajaxType)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            String jiemi = AesUtil.aesDecrypt(response.body(), BiaoXunTongApi.PAS_KEY);

                            //判断是否收藏过
                            final JSONObject object = JSON.parseObject(jiemi);
                            String status = object.getString("status");
                            int favorite = object.getInteger("favorite");
                            if (favorite == 1) {
                                myFav = 1;
                                ivFav.setImageResource(R.mipmap.ic_faved_pressed);
                            } else if (favorite == 0) {
                                myFav = 0;
                                ivFav.setImageResource(R.mipmap.ic_fav_pressed);
                            }

                            if ("200".equals(status)) {
                                final JSONObject data = object.getJSONObject("data");
                                mUrl = data.getString("url");


                                webView = AgentWeb.with(BrowserDetailFragment.this)
                                        .setAgentWebParent(ll_webview, new LinearLayout.LayoutParams(-1, -1))
                                        .useDefaultIndicator(getResources().getColor(R.color.main_status_red), 3)
                                        .createAgentWeb()
                                        .ready()
                                        .go(mUrl);

                            } else {

                            }
                        }
                    });

        } else {

            OkGo.<String>post(mApi)
                    .params("entityId", mEntityId)
                    .params("entity", mEntity)
                    .params("diqu", mDiqu)
                    .params("deviceId", deviceId)
                    .params("ajaxlogtype", ajaxType)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            String jiemi = AesUtil.aesDecrypt(response.body(), BiaoXunTongApi.PAS_KEY);

                            final JSONObject object = JSON.parseObject(jiemi);
                            String status = object.getString("status");

                            if ("200".equals(status)) {
                                final JSONObject data = object.getJSONObject("data");
                                mUrl = data.getString("url");

                                webView = AgentWeb.with(BrowserDetailFragment.this)
                                        .setAgentWebParent(ll_webview, new LinearLayout.LayoutParams(-1, -1))
                                        .useDefaultIndicator(getResources().getColor(R.color.main_status_red), 3)
                                        .createAgentWeb()
                                        .ready()
                                        .go(mUrl);
                            } else {

                            }
                        }
                    });
        }
    }


    private Share mShare = new Share();

    @Override
    public void onClick(View view) {
        mShare.setAppName("鲁班标讯通");
        mShare.setAppShareIcon(R.mipmap.ic_share);
        if (mShare.getBitmapResID() == 0) {
            mShare.setBitmapResID(R.mipmap.ic_share);
        }
        mShare.setTitle(mTitle);
        mShare.setContent(mTitle);
        mShare.setSummary(mTitle);
        mShare.setDescription(mTitle);
        mShare.setImageUrl(null);
        mShare.setUrl(mUrl);
        switch (view.getId()) {
            case R.id.ll_iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.ll_share:
                toShare(mEntityId, mTitle, mTitle, mUrl);
                break;
            case R.id.ll_fav:

                if (AppSharePreferenceMgr.contains(getContext(), EventMessage.LOGIN_SUCCSS)) {
                    //已登录处理事件
                    List<UserProfile> users = DatabaseManager.getInstance().getDao().loadAll();
                    long id = 0;
                    for (int i = 0; i < users.size(); i++) {
                        id = users.get(0).getId();
                    }

                    if (myFav == 1) {

                        OkGo.<String>post(BiaoXunTongApi.URL_DELEFAV)
                                .params("entityid", mEntityId)
                                .params("entity", mEntity)
                                .params("userid", id)
                                .params("diqu", mDiqu)
                                .params("deviceId", deviceId)
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(Response<String> response) {
                                        final JSONObject object = JSON.parseObject(response.body());
                                        String status = object.getString("status");
                                        if ("200".equals(status)) {
                                            myFav = 0;
                                            ivFav.setImageResource(R.mipmap.ic_fav_pressed);
                                            ToastUtil.shortToast(getContext(), "取消收藏");
                                            EventBus.getDefault().post(new EventMessage(EventMessage.CLICK_FAV));
                                        } else if ("500".equals(status)) {
                                            ToastUtil.shortToast(getContext(), "服务器异常");
                                        }
                                    }
                                });

                    } else if (myFav == 0) {

                        OkGo.<String>post(BiaoXunTongApi.URL_ADDFAV)
                                .params("entityid", mEntityId)
                                .params("entity", mEntity)
                                .params("userid", id)
                                .params("diqu", mDiqu)
                                .params("deviceId", deviceId)
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(Response<String> response) {
                                        final JSONObject object = JSON.parseObject(response.body());
                                        String status = object.getString("status");
                                        if ("200".equals(status)) {
                                            myFav = 1;
                                            ivFav.setImageResource(R.mipmap.ic_faved_pressed);
                                            ToastUtil.shortToast(getContext(), "收藏成功");
                                            EventBus.getDefault().post(new EventMessage(EventMessage.CLICK_FAV));
                                        } else if ("500".equals(status)) {
                                            ToastUtil.shortToast(getContext(), "服务器异常");
                                        }
                                    }
                                });

                    } else {
                        ToastUtil.shortToast(getContext(), "未知收藏状态");
                    }
                } else {
                    //未登录去登陆
                    startActivity(new Intent(getActivity(), SignInActivity.class));
                }
                break;
            default:
                break;
        }
    }
}
