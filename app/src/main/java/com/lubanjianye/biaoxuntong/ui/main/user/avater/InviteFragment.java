package com.lubanjianye.biaoxuntong.ui.main.user.avater;

import android.view.View;
import android.widget.LinearLayout;

import com.lubanjianye.biaoxuntong.R;
import com.lubanjianye.biaoxuntong.base.BaseFragment;
import com.lubanjianye.biaoxuntong.util.toast.ToastUtil;

/**
 * Author: lunious
 * Date: 2018/6/25 16:35
 * Description:
 */
public class InviteFragment extends BaseFragment implements View.OnClickListener {
    private LinearLayout llBack = null;
    private LinearLayout llWenhao = null;

    @Override
    public Object setLayout() {
        return R.layout.fragment_invite;
    }

    @Override
    public void initView() {
        llBack = getView().findViewById(R.id.ll_back);
        llWenhao = getView().findViewById(R.id.ll_wenhao);
        llBack.setOnClickListener(this);
        llWenhao.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                getActivity().onBackPressed();
                break;
            case R.id.ll_wenhao:
                ToastUtil.shortBottonToast(getContext(), "说明");
                break;
        }
    }
}
