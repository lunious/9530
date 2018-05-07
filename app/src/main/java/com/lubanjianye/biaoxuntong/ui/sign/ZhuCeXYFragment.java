package com.lubanjianye.biaoxuntong.ui.sign;

import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.LinearLayout;

import com.lubanjianye.biaoxuntong.R;
import com.lubanjianye.biaoxuntong.base.BaseFragment;


public class ZhuCeXYFragment extends BaseFragment implements View.OnClickListener {

    private LinearLayout llIvBack = null;
    private AppCompatTextView mainBarName = null;

    @Override
    public Object setLayout() {
        return R.layout.fragment_zhucexieyi;
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
        mainBarName.setText("服务协议");
    }

    @Override
    public void initEvent() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_iv_back:
                getActivity().finish();
                break;
            default:
                break;
        }
    }
}
