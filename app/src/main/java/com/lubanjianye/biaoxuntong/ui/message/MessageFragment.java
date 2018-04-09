package com.lubanjianye.biaoxuntong.ui.message;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.flyco.tablayout.SlidingTabLayout;
import com.lubanjianye.biaoxuntong.R;
import com.lubanjianye.biaoxuntong.app.BiaoXunTongApi;
import com.lubanjianye.biaoxuntong.base.BaseFragment;
import com.lubanjianye.biaoxuntong.database.DatabaseManager;
import com.lubanjianye.biaoxuntong.database.UserProfile;
import com.lubanjianye.biaoxuntong.eventbus.EventMessage;
import com.lubanjianye.biaoxuntong.util.toast.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 11645 on 2018/3/21.
 */

public class MessageFragment extends BaseFragment implements View.OnClickListener {

    private LinearLayout llIvBack = null;
    private AppCompatTextView mainBarName = null;
    private AppCompatTextView atv_yjyd = null;

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
        atv_yjyd = getView().findViewById(R.id.atv_yjyd);
        resulttStlTab = getView().findViewById(R.id.message_stl_tab);
        resultVp = getView().findViewById(R.id.message_vp);

        llIvBack.setOnClickListener(this);
        atv_yjyd.setOnClickListener(this);
    }

    @Override
    public void initData() {
        llIvBack.setVisibility(View.VISIBLE);
        mainBarName.setText("消息中心");

        mList.add("历史推送");
        mList.add("告知消息");

    }

    @Override
    public void initEvent() {
        mAdapter = new MessageAdapter(mList, getFragmentManager());
        resultVp.setAdapter(mAdapter);
        resulttStlTab.setViewPager(resultVp);

    }

    private long id = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.atv_yjyd:
                String type = resulttStlTab.getCurrentTab() + 1 + "";

                List<UserProfile> users = DatabaseManager.getInstance().getDao().loadAll();

                for (int i = 0; i < users.size(); i++) {
                    id = users.get(0).getId();
                }
                OkGo.<String>post(BiaoXunTongApi.URL_YJYD)
                        .params("type", type)
                        .params("userId", id)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {

                                final JSONObject object = JSON.parseObject(response.body());
                                final String status = object.getString("status");
                                final String message = object.getString("message");

                                if ("200".equals(status)) {
                                    ToastUtil.shortBottonToast(getContext(), message);
                                    //改变已读未读状态
                                    EventBus.getDefault().post(new EventMessage(EventMessage.READ_STATUS));
                                } else {
                                    ToastUtil.shortBottonToast(getContext(), message);
                                }

                            }
                        });
                break;
            default:
                break;
        }
    }
}
