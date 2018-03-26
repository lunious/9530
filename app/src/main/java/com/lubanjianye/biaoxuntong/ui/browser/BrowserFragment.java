package com.lubanjianye.biaoxuntong.ui.browser;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.just.agentweb.AgentWeb;
import com.lubanjianye.biaoxuntong.R;
import com.lubanjianye.biaoxuntong.base.BaseFragment;
import com.lubanjianye.biaoxuntong.util.toast.ToastUtil;

/**
 * Created by 11645 on 2018/3/22.
 */

public class BrowserFragment extends BaseFragment implements View.OnClickListener {


    private LinearLayout mBack = null;
    private AppCompatTextView mainTitle = null;
    private LinearLayout ll_webview = null;
    private AgentWeb webView = null;
    private ImageView mImgBack = null;
    private ImageView mImgForward = null;
    private ImageView mImgRefresh = null;
    private ImageView mImgSystemBrowser = null;


    private static final String ARG_URL = "ARG_URL";
    private static final String ARG_TITLE = "ARG_TITLE";

    public String mUrl = "";
    public String mTitle = "";

    public static BrowserFragment create(@NonNull String url, String title) {
        final Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        args.putString(ARG_TITLE, title);
        final BrowserFragment fragment = new BrowserFragment();
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
                .useDefaultIndicator(getResources().getColor(R.color.main_status_red),3)
                .createAgentWeb()
                .ready()
                .go(mUrl);

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
