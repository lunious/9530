package com.lubanjianye.biaoxuntong.ui.message;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.LinearLayout;

import com.flyco.tablayout.SlidingTabLayout;
import com.lubanjianye.biaoxuntong.R;
import com.lubanjianye.biaoxuntong.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 11645 on 2018/3/21.
 */

public class MessageFragment extends BaseFragment implements View.OnClickListener {

    private LinearLayout llIvBack = null;
    private AppCompatTextView mainBarName = null;


    private SlidingTabLayout resulttStlTab = null;
    private ViewPager resultVp = null;


    private final List<String> mList = new ArrayList<String>();
    private MessageAdapter mAdapter;

    @Override
    public Object setLayout() {
        return R.layout.fragment_message;
    }

    @Override
    public void initView() {
        llIvBack = getView().findViewById(R.id.ll_iv_back);
        mainBarName = getView().findViewById(R.id.main_bar_name);

        resulttStlTab = getView().findViewById(R.id.message_stl_tab);
        resultVp = getView().findViewById(R.id.message_vp);

        llIvBack.setOnClickListener(this);
    }

    @Override
    public void initData() {
        llIvBack.setVisibility(View.VISIBLE);
        mainBarName.setText("消息中心");

        mList.add("普通消息");
        mList.add("关注推送");

    }

    @Override
    public void initEvent() {
        mAdapter = new MessageAdapter(mList, getFragmentManager());
        resultVp.setAdapter(mAdapter);
        resulttStlTab.setViewPager(resultVp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_iv_back:
                getActivity().finish();
                break;
                default:
                    break;
        }
    }
}
