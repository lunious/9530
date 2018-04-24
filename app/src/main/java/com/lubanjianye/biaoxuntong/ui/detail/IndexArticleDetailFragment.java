package com.lubanjianye.biaoxuntong.ui.detail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.classic.common.MultipleStatusView;
import com.just.agentweb.AgentWebConfig;
import com.just.agentweb.AgentWebUtils;
import com.lubanjianye.biaoxuntong.R;
import com.lubanjianye.biaoxuntong.app.BiaoXunTongApi;
import com.lubanjianye.biaoxuntong.base.BaseFragment;
import com.lubanjianye.biaoxuntong.database.DatabaseManager;
import com.lubanjianye.biaoxuntong.database.UserProfile;
import com.lubanjianye.biaoxuntong.eventbus.EventMessage;
import com.lubanjianye.biaoxuntong.ui.browser.BrowserSuitActivity;
import com.lubanjianye.biaoxuntong.ui.share.OpenBuilder;
import com.lubanjianye.biaoxuntong.ui.share.OpenConstant;
import com.lubanjianye.biaoxuntong.ui.share.Share;
import com.lubanjianye.biaoxuntong.ui.sign.SignInActivity;
import com.lubanjianye.biaoxuntong.util.aes.AesUtil;
import com.lubanjianye.biaoxuntong.util.netStatus.AppSysMgr;
import com.lubanjianye.biaoxuntong.util.netStatus.NetUtil;
import com.lubanjianye.biaoxuntong.util.sp.AppSharePreferenceMgr;
import com.lubanjianye.biaoxuntong.util.toast.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


public class IndexArticleDetailFragment extends BaseFragment implements View.OnClickListener, OpenBuilder.Callback {


    private LinearLayout llIvBack = null;
    private AppCompatTextView mainBarName = null;
    private MultipleStatusView loadingStatus = null;
    private WebView webView = null;
    private LinearLayout ll_content = null;

    private NestedScrollView detailNsv = null;
    private AppCompatTextView tv_title = null;
    private ImageView ivFav = null;
    private LinearLayout llShare = null;
    private LinearLayout llBrowser = null;
    private LinearLayout llYw = null;
    private LinearLayout llFav = null;
    private AppCompatTextView tv_data_time = null;
    private AppCompatTextView tv_status = null;
    private AppCompatTextView tv_dead_time = null;
    private AppCompatTextView tv_area = null;
    private AppCompatTextView tv_type = null;

    private LinearLayout llWeiBoShare = null;
    private LinearLayout llQQBoShare = null;
    private LinearLayout llWeixinBoShare = null;
    private LinearLayout llPyqShare = null;

    private LinearLayout llWeiBoShare_bottom = null;
    private LinearLayout llQQBoShare_bottom = null;
    private LinearLayout llWeixinBoShare_bottom = null;
    private LinearLayout llPyqShare_bottom = null;

    private AppCompatTextView atv_gz = null;
    private AppCompatTextView atv_jg = null;


    private static final String ARG_ENTITYID = "ARG_ENTITYID";
    private static final String ARG_ENTITY = "ARG_ENTITY";
    private static final String ARG_AJAXTYPE = "ARG_AJAXTYPE";

    private int myFav = -1;
    private int mEntityId = -1;
    private String mEntity = "";
    private String ajaxlogtype = "";

    private String title = "";
    private String sysTime = "";
    private String sign_status = "";
    private String dead_time = "";
    private String area = "";
    private String type = "";
    private String url = "";
    private String entityUrl = "";

    private String deviceId = AppSysMgr.getPsuedoUniqueID();


    public static IndexArticleDetailFragment create(@NonNull int entityId, String entity, String ajaxlogtype) {
        final Bundle args = new Bundle();
        args.putInt(ARG_ENTITYID, entityId);
        args.putString(ARG_ENTITY, entity);
        args.putString(ARG_AJAXTYPE, ajaxlogtype);
        final IndexArticleDetailFragment fragment = new IndexArticleDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args != null) {
            mEntityId = args.getInt(ARG_ENTITYID);
            mEntity = args.getString(ARG_ENTITY);
            ajaxlogtype = args.getString(ARG_AJAXTYPE);
        }
    }


    @Override
    public Object setLayout() {
        return R.layout.fragment_index_article_detail;
    }

    @Override
    public void initView() {
        llIvBack = getView().findViewById(R.id.ll_iv_back);
        mainBarName = getView().findViewById(R.id.main_bar_name);
        loadingStatus = getView().findViewById(R.id.detail_status_view);
        webView = getView().findViewById(R.id.wv_content);
        detailNsv = getView().findViewById(R.id.detail_nsv);
        tv_title = getView().findViewById(R.id.tv_main_title);
        ivFav = getView().findViewById(R.id.iv_fav);
        llShare = getView().findViewById(R.id.ll_share);
        llBrowser = getView().findViewById(R.id.ll_browser);
        llYw = getView().findViewById(R.id.ll_yw);
        llFav = getView().findViewById(R.id.ll_fav);
        tv_data_time = getView().findViewById(R.id.tv_data_time);
        tv_status = getView().findViewById(R.id.tv_status);
        ll_content = getView().findViewById(R.id.ll_content);
        tv_dead_time = getView().findViewById(R.id.tv_dead_time);
        llWeiBoShare = getView().findViewById(R.id.ll_weibo_share);
        llQQBoShare = getView().findViewById(R.id.ll_qq_share);
        llWeixinBoShare = getView().findViewById(R.id.ll_chat_share);
        llPyqShare = getView().findViewById(R.id.ll_pyq_share);
        tv_area = getView().findViewById(R.id.tv_area);
        tv_type = getView().findViewById(R.id.tv_type);

        llWeiBoShare_bottom = getView().findViewById(R.id.ll_weibo_share_bottom);
        llQQBoShare_bottom = getView().findViewById(R.id.ll_qq_share_bottom);
        llWeixinBoShare_bottom = getView().findViewById(R.id.ll_chat_share_bottom);
        llPyqShare_bottom = getView().findViewById(R.id.ll_pyq_share_bottom);

        atv_gz = getView().findViewById(R.id.atv_gz);
        atv_jg = getView().findViewById(R.id.atv_jg);

        atv_gz.setOnClickListener(this);
        atv_jg.setOnClickListener(this);

        llWeiBoShare.setOnClickListener(this);
        llQQBoShare.setOnClickListener(this);
        llWeixinBoShare.setOnClickListener(this);
        llPyqShare.setOnClickListener(this);

        llWeiBoShare_bottom.setOnClickListener(this);
        llQQBoShare_bottom.setOnClickListener(this);
        llWeixinBoShare_bottom.setOnClickListener(this);
        llPyqShare_bottom.setOnClickListener(this);

        llIvBack.setOnClickListener(this);
        llShare.setOnClickListener(this);
        llBrowser.setOnClickListener(this);
        llYw.setOnClickListener(this);
        llFav.setOnClickListener(this);

    }

    @Override
    public void initData() {
        requestData();

    }


    @Override
    public void initEvent() {
        initNsv();
        initWebView();
    }

    private long id = 0;

    private void requestData() {

        if ("1".equals(ajaxlogtype)) {
            //改变已读未读状态
            EventBus.getDefault().post(new EventMessage(EventMessage.READ_STATUS));
        }

        if (!NetUtil.isNetworkConnected(getActivity())) {
            loadingStatus.showNoNetwork();
        } else {
            loadingStatus.showLoading();
            mainBarName.setText("加载中...");

            if (AppSharePreferenceMgr.contains(getContext(), EventMessage.LOGIN_SUCCSS)) {
                List<UserProfile> users = DatabaseManager.getInstance().getDao().loadAll();
                for (int i = 0; i < users.size(); i++) {
                    id = users.get(0).getId();
                }

                OkGo.<String>post(BiaoXunTongApi.URL_GETCOLLECTIONLISTDETAIL)
                        .params("entityId", mEntityId)
                        .params("entity", mEntity)
                        .params("userid", id)
                        .params("deviceId", deviceId)
                        .params("ajaxlogtype", ajaxlogtype)
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
                                    //判断是否有更正和结果
                                    //1、判断有误更正
                                    final JSONArray arrayGz = data.getJSONArray("listGzUrl");
                                    //2、判断有无结果
                                    final JSONArray arrayJg = data.getJSONArray("listJgId");
                                    if (arrayGz != null) {

                                    } else {
                                        atv_gz.setText("无");
                                        atv_gz.setTextColor(getResources().getColor(R.color.main_text_color));
                                    }
                                    if (arrayJg != null) {
                                        atv_jg.setVisibility(View.VISIBLE);
                                        JSONObject list = arrayJg.getJSONObject(arrayJg.size() - 1);
                                    } else {
                                        atv_jg.setVisibility(View.GONE);
                                    }
                                    String mTitle = data.getString("entryName");
                                    if (!TextUtils.isEmpty(mTitle)) {
                                        title = mTitle;
                                    } else {
                                        title = data.getString("reportTitle");
                                    }
                                    tv_title.setText(title);
                                    url = data.getString("url");
                                    entityUrl = data.getString("entityUrl");
                                    webView.loadUrl(entityUrl);
                                    sysTime = data.getString("sysTime");
                                    tv_data_time.setText(sysTime);
                                    sign_status = data.getString("signStauts");
                                    if ("正在报名".equals(sign_status)) {
                                        tv_status.setText(sign_status);
                                        tv_status.setTextColor(android.graphics.Color.parseColor("#21a9ff"));
                                    } else if ("报名结束".equals(status)) {
                                        tv_status.setText(sign_status);
                                        tv_status.setTextColor(android.graphics.Color.parseColor("#ff6666"));
                                    } else if ("待报名".equals(sign_status)) {
                                        tv_status.setText(sign_status);
                                        tv_status.setTextColor(android.graphics.Color.parseColor("#00bfdc"));
                                    }
                                    dead_time = data.getString("deadTime");
                                    if (!TextUtils.isEmpty(dead_time)) {
                                        tv_dead_time.setText(dead_time);
                                    } else {
                                        tv_dead_time.setText("暂无");
                                    }
                                    area = data.getString("area");
                                    if (!TextUtils.isEmpty(area)) {
                                        tv_area.setText(area);
                                    } else {
                                        tv_area.setVisibility(View.GONE);
                                    }
                                    type = data.getString("type");
                                    tv_type.setText(type);

                                    loadingStatus.showContent();
                                } else {
                                    loadingStatus.showError();
                                }
                            }
                        });

            } else {

                OkGo.<String>post(BiaoXunTongApi.URL_GETCOLLECTIONLISTDETAIL)
                        .params("entityId", mEntityId)
                        .params("entity", mEntity)
                        .params("deviceId", deviceId)
                        .params("ajaxlogtype", ajaxlogtype)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {

                                String jiemi = AesUtil.aesDecrypt(response.body(), BiaoXunTongApi.PAS_KEY);

                                final JSONObject object = JSON.parseObject(jiemi);
                                String status = object.getString("status");

                                if ("200".equals(status)) {
                                    final JSONObject data = object.getJSONObject("data");
                                    //判断是否有更正和结果
                                    //1、判断有误更正
                                    final JSONArray arrayGz = data.getJSONArray("listGzUrl");
                                    //2、判断有无结果
                                    final JSONArray arrayJg = data.getJSONArray("listJgId");
                                    if (arrayGz != null) {

                                    } else {
                                        atv_gz.setText("无");
                                        atv_gz.setTextColor(getResources().getColor(R.color.main_text_color));
                                    }
                                    if (arrayJg != null) {
                                        atv_jg.setVisibility(View.VISIBLE);
                                        JSONObject list = arrayJg.getJSONObject(arrayJg.size() - 1);

                                    } else {
                                        atv_jg.setVisibility(View.GONE);
                                    }


                                    String mTitle = data.getString("entryName");
                                    if (!TextUtils.isEmpty(mTitle)) {
                                        title = mTitle;
                                    } else {
                                        title = data.getString("reportTitle");
                                    }
                                    tv_title.setText(title);
                                    url = data.getString("url");
                                    entityUrl = data.getString("entityUrl");
                                    webView.loadUrl(entityUrl);
                                    sysTime = data.getString("sysTime");
                                    tv_data_time.setText(sysTime);
                                    sign_status = data.getString("signStauts");
                                    if ("正在报名".equals(sign_status)) {
                                        tv_status.setText(sign_status);
                                        tv_status.setTextColor(android.graphics.Color.parseColor("#21a9ff"));
                                    } else if ("报名结束".equals(status)) {
                                        tv_status.setText(sign_status);
                                        tv_status.setTextColor(android.graphics.Color.parseColor("#ff6666"));
                                    } else if ("待报名".equals(sign_status)) {
                                        tv_status.setText(sign_status);
                                        tv_status.setTextColor(android.graphics.Color.parseColor("#00bfdc"));
                                    }
                                    dead_time = data.getString("deadTime");
                                    if (!TextUtils.isEmpty(dead_time)) {
                                        tv_dead_time.setText(dead_time);
                                    } else {
                                        tv_dead_time.setText("暂无");
                                    }
                                    area = data.getString("area");
                                    if (!TextUtils.isEmpty(area)) {
                                        tv_area.setText(area);
                                    } else {
                                        tv_area.setVisibility(View.GONE);
                                    }
                                    type = data.getString("type");
                                    tv_type.setText(type);

                                    loadingStatus.showContent();
                                } else {
                                    loadingStatus.showError();
                                }
                            }
                        });

            }

        }

    }

    private void initNsv() {
        detailNsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    // 向下滑动
                    mainBarName.setText(title);
                }

                if (scrollY < oldScrollY) {
                    // 向上滑动
                    mainBarName.setText(title);
                }

                if (scrollY == 0) {
                    // 顶部
                    mainBarName.setText("标讯详情");
                }

                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    // 底部
                    mainBarName.setText(title);
                }
            }
        });
    }

    private void initWebView() {
        WebSettings mWebSettings = webView.getSettings();
        mWebSettings = webView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setSupportZoom(true);
        mWebSettings.setBuiltInZoomControls(false);
        mWebSettings.setSavePassword(false);
        if (AgentWebUtils.checkNetwork(webView.getContext())) {
            //根据cache-control获取数据。
            mWebSettings.setCacheMode(android.webkit.WebSettings.LOAD_DEFAULT);
        } else {
            //没网，则从本地获取，即离线加载
            mWebSettings.setCacheMode(android.webkit.WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            //适配5.0不允许http和https混合使用情况
//            mWebSettings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        }

        webView.setLayerType(View.LAYER_TYPE_NONE, null);

        mWebSettings.setTextZoom(100);
        mWebSettings.setDatabaseEnabled(true);
        mWebSettings.setAppCacheEnabled(true);
        mWebSettings.setLoadsImagesAutomatically(true);
        mWebSettings.setSupportMultipleWindows(false);
        mWebSettings.setBlockNetworkImage(false);//是否阻塞加载网络图片  协议http or https
        mWebSettings.setAllowFileAccess(true); //允许加载本地文件html  file协议
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mWebSettings.setAllowFileAccessFromFileURLs(false); //通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
            mWebSettings.setAllowUniversalAccessFromFileURLs(false);//允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
        }
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            mWebSettings.setLayoutAlgorithm(android.webkit.WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        } else {
            mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setNeedInitialFocus(true);
        mWebSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        mWebSettings.setDefaultFontSize(16);
        mWebSettings.setMinimumFontSize(12);//设置 WebView 支持的最小字体大小，默认为 8
        mWebSettings.setGeolocationEnabled(true);
        //
        String dir = AgentWebConfig.getCachePath(webView.getContext());

        //设置数据库路径  api19 已经废弃,这里只针对 webkit 起作用
        mWebSettings.setGeolocationDatabasePath(dir);
        mWebSettings.setDatabasePath(dir);
        mWebSettings.setAppCachePath(dir);

        //缓存文件最大值
        mWebSettings.setAppCacheMaxSize(Long.MAX_VALUE);

        webView.setWebViewClient(new MyWebViewClient());


    }


    private Share mShare = new Share();

    @Override
    public void onClick(View v) {
        mShare.setAppName("鲁班标讯通");
        mShare.setAppShareIcon(R.mipmap.ic_share);
        if (mShare.getBitmapResID() == 0) {
            mShare.setBitmapResID(R.mipmap.ic_share);
        }
        mShare.setTitle(title);
        mShare.setContent(title);
        mShare.setSummary(title);
        mShare.setDescription(title);
        mShare.setImageUrl(null);
        mShare.setUrl(BiaoXunTongApi.SHARE_URL + entityUrl);

        Intent intent = null;
        switch (v.getId()) {
            case R.id.atv_gz:
                ToastUtil.shortToast(getContext(), "相关更正公告");
                break;
            case R.id.atv_jg:
                ToastUtil.shortToast(getContext(), "相关结果公告");
                break;
            case R.id.ll_weibo_share:
                OpenBuilder.with(getActivity())
                        .useWeibo(OpenConstant.WB_APP_KEY)
                        .share(mShare, new OpenBuilder.Callback() {
                            @Override
                            public void onFailed() {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                break;
            case R.id.ll_qq_share:
                OpenBuilder.with(getActivity())
                        .useTencent(OpenConstant.QQ_APP_ID)
                        .share(mShare, new IUiListener() {
                            @Override
                            public void onComplete(Object o) {
                                ToastUtil.shortToast(getContext(), "分享成功");
                            }

                            @Override
                            public void onError(UiError uiError) {
                                ToastUtil.shortToast(getContext(), "分享失败");
                            }

                            @Override
                            public void onCancel() {
                                ToastUtil.shortToast(getContext(), "分享取消");
                            }
                        }, this);
                break;
            case R.id.ll_chat_share:
                OpenBuilder.with(getActivity())
                        .useWechat(OpenConstant.WECHAT_APP_ID)
                        .shareSession(mShare, new OpenBuilder.Callback() {
                            @Override
                            public void onFailed() {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                break;
            case R.id.ll_pyq_share:
                OpenBuilder.with(getActivity())
                        .useWechat(OpenConstant.WECHAT_APP_ID)
                        .shareTimeLine(mShare, new OpenBuilder.Callback() {
                            @Override
                            public void onFailed() {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                break;
            case R.id.ll_weibo_share_bottom:
                OpenBuilder.with(getActivity())
                        .useWeibo(OpenConstant.WB_APP_KEY)
                        .share(mShare, new OpenBuilder.Callback() {
                            @Override
                            public void onFailed() {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                break;
            case R.id.ll_qq_share_bottom:
                OpenBuilder.with(getActivity())
                        .useTencent(OpenConstant.QQ_APP_ID)
                        .share(mShare, new IUiListener() {
                            @Override
                            public void onComplete(Object o) {
                                ToastUtil.shortToast(getContext(), "分享成功");
                            }

                            @Override
                            public void onError(UiError uiError) {
                                ToastUtil.shortToast(getContext(), "分享失败");
                            }

                            @Override
                            public void onCancel() {
                                ToastUtil.shortToast(getContext(), "分享取消");
                            }
                        }, this);
                break;
            case R.id.ll_chat_share_bottom:
                OpenBuilder.with(getActivity())
                        .useWechat(OpenConstant.WECHAT_APP_ID)
                        .shareSession(mShare, new OpenBuilder.Callback() {
                            @Override
                            public void onFailed() {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                break;
            case R.id.ll_pyq_share_bottom:
                OpenBuilder.with(getActivity())
                        .useWechat(OpenConstant.WECHAT_APP_ID)
                        .shareTimeLine(mShare, new OpenBuilder.Callback() {
                            @Override
                            public void onFailed() {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                break;
            case R.id.ll_iv_back:
                getActivity().onBackPressed();
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

                    }
                } else {
                    //未登录去登陆
                    startActivity(new Intent(getActivity(), SignInActivity.class));
                }
                break;
            case R.id.ll_share:
                toShare(mEntityId, title, title, BiaoXunTongApi.SHARE_URL + entityUrl);
                break;
            case R.id.ll_browser:
                try {
                    // 启用外部浏览器
                    Uri uri = Uri.parse(url);
                    Intent it = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(it);
                } catch (Exception e) {
                    ToastUtil.shortToast(getContext(), "网页地址错误");
                }
                break;
            case R.id.ll_yw:
                intent = new Intent(getActivity(), BrowserSuitActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("title", title);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onFailed() {

    }

    @Override
    public void onSuccess() {

    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url == null) {
                return false;
            }
            try {
                if (url.startsWith("weixin://") //微信
                        || url.startsWith("mailto://") //邮件
                        || url.startsWith("tel://")//电话
                        || url.startsWith("tel:")//电话
                        //其他自定义的scheme
                        || url.startsWith("mqqwpa://")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
            } catch (Exception e) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                return true;//没有安装该app时，返回true，表示拦截自定义链接，但不跳转，避免弹出上面的错误页面
            }

            //处理http和https开头的url
            if (url.startsWith("http://") || url.startsWith("https://")) {
//                webView.loadUrl(url);
                return false;
            }
            return true;

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //加载完成
            ll_content.setVisibility(View.GONE);
            mainBarName.setText("标讯详情");
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //加载开始
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            //加载失败
        }

    }


}
