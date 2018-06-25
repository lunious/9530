package com.lubanjianye.biaoxuntong.ui.main.user.avater;

import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.LinearLayout;

import com.lubanjianye.biaoxuntong.R;
import com.lubanjianye.biaoxuntong.base.BaseFragment;

/**
 * Author: lunious
 * Date: 2018/6/25 14:57
 * Description:
 */
public class AccountFragment extends BaseFragment implements View.OnClickListener {
    private LinearLayout llIvBack = null;
    private AppCompatTextView mainBarName = null;

    @Override
    public Object setLayout() {
        return R.layout.fragment_account;
    }

    @Override
    public void initView() {
        llIvBack = getView().findViewById(R.id.ll_iv_back);
        mainBarName = getView().findViewById(R.id.main_bar_name);
        llIvBack.setOnClickListener(this);
    }

    @Override
    public void initData() {
        llIvBack.setVisibility(View.VISIBLE);
        mainBarName.setText("我的账户");
    }

    @Override
    public void initEvent() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_iv_back:
                getActivity().onBackPressed();
                break;
        }

    }
}
