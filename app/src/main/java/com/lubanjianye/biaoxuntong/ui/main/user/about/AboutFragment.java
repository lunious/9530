package com.lubanjianye.biaoxuntong.ui.main.user.about;

import android.content.Intent;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.LinearLayout;
import com.lubanjianye.biaoxuntong.R;
import com.lubanjianye.biaoxuntong.base.BaseFragment;
import com.lubanjianye.biaoxuntong.ui.browser.BrowserActivity;
import com.lubanjianye.biaoxuntong.util.appinfo.AppApplicationMgr;


public class AboutFragment extends BaseFragment implements View.OnClickListener {

    private LinearLayout llIvBack = null;
    private AppCompatTextView mainBarName = null;
    private AppCompatTextView tvVersionName = null;
    private AppCompatTextView tvLuban = null;
    private LinearLayout llTj = null;

    @Override
    public Object setLayout() {
        return R.layout.fragment_about;
    }

    @Override
    public void initView() {
        llIvBack = getView().findViewById(R.id.ll_iv_back);
        mainBarName = getView().findViewById(R.id.main_bar_name);
        tvVersionName = getView().findViewById(R.id.tv_version_name);
        tvLuban = getView().findViewById(R.id.tv_luban);
        llTj = getView().findViewById(R.id.ll_tj);
        llIvBack.setOnClickListener(this);
        tvLuban.setOnClickListener(this);
        llTj.setOnClickListener(this);


    }

    @Override
    public void initData() {
        llIvBack.setVisibility(View.VISIBLE);
        tvVersionName.setText(AppApplicationMgr.getVersionName(getContext()));
        mainBarName.setText("关于我们");

    }

    @Override
    public void initEvent() {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.tv_luban:
                Intent intent = new Intent(getActivity(), BrowserActivity.class);
                intent.putExtra("url", "http://www.lubanjianye.com/");
                intent.putExtra("title", "鲁班建业通-招投标神器");
                startActivity(intent);
                break;
            case R.id.ll_tj:
                toShare(0, "我正在使用【鲁班标讯通】,推荐给你", "企业资质、人员资格、业绩、信用奖惩、经营风险、法律诉讼一键查询！", "http://api.lubanjianye.com/bxtajax/Entryajax/sharehtml?src=share");
                break;
            default:
                break;
        }
    }
}
