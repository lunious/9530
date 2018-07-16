package com.lubanjianye.biaoxuntong.ui.main.query.qybg;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lubanjianye.biaoxuntong.R;
import com.lubanjianye.biaoxuntong.app.BiaoXunTongApi;
import com.lubanjianye.biaoxuntong.base.BaseFragment1;
import com.lubanjianye.biaoxuntong.database.DatabaseManager;
import com.lubanjianye.biaoxuntong.database.UserProfile;
import com.lubanjianye.biaoxuntong.util.dialog.PromptDialog;
import com.lubanjianye.biaoxuntong.util.toast.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Author: lunious
 * Date: 2018/7/11 10:24
 * Description:
 */
public class QybgFragment extends BaseFragment1 implements View.OnClickListener {
    private LinearLayout llIvBack = null;
    private AppCompatTextView mainBarName = null;
    private TextView tvCompany = null;
    private EditText etEmail = null;
    private TextView tvSend = null;
    private PromptDialog promptDialog;


    private static final String ARG_SFID = "ARG_SFID";
    private static final String ARG_NAME = "ARG_NAME";
    private String sfId = "";
    private String companyName = "";
    private String email = "";
    private long userId = 0;
    private String mobile = "";
    private String token = "";

    /**
     * 正则表达式：验证邮箱
     */
    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";


    public static QybgFragment create(@NonNull String entity, String name) {
        final Bundle args = new Bundle();
        args.putString(ARG_SFID, entity);
        args.putString(ARG_NAME, name);
        final QybgFragment fragment = new QybgFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args != null) {
            sfId = args.getString(ARG_SFID);
            companyName = args.getString(ARG_NAME);
        }
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_qybg;
    }

    @Override
    public void initView() {
        llIvBack = getView().findViewById(R.id.ll_iv_back);
        mainBarName = getView().findViewById(R.id.main_bar_name);
        llIvBack.setOnClickListener(this);
        tvCompany = getView().findViewById(R.id.tv_bgcompany);
        etEmail = getView().findViewById(R.id.et_bgemail);
        tvSend = getView().findViewById(R.id.btn_sure_send);
        tvSend.setOnClickListener(this);
    }

    @Override
    public void initData() {
        llIvBack.setVisibility(View.VISIBLE);
        mainBarName.setText("确认企业报告");
        //创建对象
        promptDialog = new PromptDialog(getActivity());
        //设置自定义属性
        promptDialog.getDefaultBuilder().touchAble(true).round(3).loadingDuration(3000);
    }

    @Override
    public void initEvent() {
        tvCompany.setText(companyName);
        List<UserProfile> users = DatabaseManager.getInstance().getDao().loadAll();

        for (int i = 0; i < users.size(); i++) {
            userId = users.get(0).getId();
            token = users.get(0).getToken();
            mobile = users.get(0).getMobile();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.btn_sure_send:
                email = etEmail.getText().toString();

                Log.d("NBAJISDASDDASD",sfId+"___"+email+"__"+mobile+"__"+token+"____"+userId);

                if (Pattern.matches(REGEX_EMAIL, email)) {
                    promptDialog.showLoading("正在发送...");
                    OkGo.<String>post(BiaoXunTongApi.URL_SENDPDF)
                            .params("sfId",sfId)
                            .params("email", email)
                            .params("mobile", mobile)
                            .params("token", token)
                            .params("userId", userId)
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(Response<String> response) {
                                    final JSONObject object = JSON.parseObject(response.body());
                                    final String status = object.getString("status");
                                    final String message = object.getString("message");
                                    if ("200".equals(status)){
                                        promptDialog.dismissImmediately();
                                        ToastUtil.shortBottonToast(getContext(),"发送成功，请注意查收邮箱!");
                                        getActivity().onBackPressed();
                                    }else {
                                        promptDialog.dismissImmediately();
                                        ToastUtil.shortBottonToast(getContext(),"请检查邮箱地址是否正确!");
                                    }
                                }
                            });
                } else {

                    ToastUtil.shortBottonToast(getContext(), "请输入正确的邮箱地址");
                }
                break;

        }
    }
}
