package com.lubanjianye.biaoxuntong.ui.browser;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebConfig;
import com.just.agentweb.AgentWebUtils;
import com.just.agentweb.DefaultWebClient;
import com.just.agentweb.IAgentWebSettings;
import com.just.agentweb.LogUtils;
import com.lubanjianye.biaoxuntong.R;
import com.lubanjianye.biaoxuntong.base.BaseFragment;
import com.lubanjianye.biaoxuntong.util.toast.ToastUtil;

/**
 * Created by 11645 on 2018/3/22.
 */

public class BrowserSuitFragment extends BaseFragment implements View.OnClickListener {


    private LinearLayout mBack = null;
    private AppCompatTextView mainTitle = null;
    private LinearLayout ll_webview = null;
    private AgentWeb webView = null;
    private ImageView mImgBack = null;
    private ImageView mImgForward = null;
    private ImageView mImgRefresh = null;
    private ImageView mImgSystemBrowser = null;
    private android.webkit.WebSettings mWebSettings;


    private static final String ARG_URL = "ARG_URL";
    private static final String ARG_TITLE = "ARG_TITLE";

    public String mUrl = "";
    public String mTitle = "";

    public static BrowserSuitFragment create(@NonNull String url, String title) {
        final Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        args.putString(ARG_TITLE, title);
        final BrowserSuitFragment fragment = new BrowserSuitFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public Object setLayout() {
        return R.layout.fragment_browser;
    }

    @Override
    public void initView() {
        mBack = getView().findViewById(R.id.ll_iv_back);
        mainTitle = getView().findViewById(R.id.main_bar_name);
        ll_webview = getView().findViewById(R.id.ll_webview);
        mImgBack = getView().findViewById(R.id.browser_back);
        mImgForward = getView().findViewById(R.id.browser_forward);
        mImgRefresh = getView().findViewById(R.id.browser_refresh);
        mImgSystemBrowser = getView().findViewById(R.id.browser_system_browser);
        mBack.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
        mImgForward.setOnClickListener(this);
        mImgRefresh.setOnClickListener(this);
        mImgSystemBrowser.setOnClickListener(this);


    }

    @Override
    public void initData() {
        mBack.setVisibility(View.VISIBLE);
        final Bundle args = getArguments();
        if (args != null) {
            mUrl = args.getString(ARG_URL);
            mTitle = args.getString(ARG_TITLE);
        }

        if (!TextUtils.isEmpty(mTitle)) {
            mainTitle.setText(mTitle);
        } else {
            mainTitle.setText("鲁班标讯通");
        }
    }

    @Override
    public void initEvent() {
        webView = AgentWeb.with(this)
                .setAgentWebParent(ll_webview, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator(getResources().getColor(R.color.main_status_red), 3)
                .setAgentWebWebSettings(new IAgentWebSettings() {
                    @Override
                    public IAgentWebSettings toSetting(WebView webView) {
                        settings(webView);
                        return this;
                    }

                    @Override
                    public WebSettings getWebSettings() {
                        return mWebSettings;
                    }
                })
                .createAgentWeb()
                .ready()
                .go(mUrl);


    }

    private void settings(WebView webView) {


        mWebSettings = webView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setSupportZoom(true);
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setDisplayZoomControls(false);
        mWebSettings.setSavePassword(false);
        if (AgentWebUtils.checkNetwork(webView.getContext())) {
            //根据cache-control获取数据。
            mWebSettings.setCacheMode(android.webkit.WebSettings.LOAD_DEFAULT);
        } else {
            //没网，则从本地获取，即离线加载
            mWebSettings.setCacheMode(android.webkit.WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //适配5.0不允许http和https混合使用情况
            mWebSettings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

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


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.browser_back:
                webView.back();
                break;
            case R.id.browser_forward:
                //
                break;
            case R.id.browser_refresh:
                webView.getUrlLoader().reload();
                break;
            case R.id.browser_system_browser:
                try {
                    // 启用外部浏览器
                    Uri uri = Uri.parse(mUrl);
                    Intent it = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(it);
                } catch (Exception e) {
                    ToastUtil.shortToast(getContext(), "网页地址错误");
                }
                break;
            default:
                break;
        }
    }
}
