package com.lubanjianye.biaoxuntong.ui.main.user.avater;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.lubanjianye.biaoxuntong.R;
import com.lubanjianye.biaoxuntong.app.BiaoXunTongApi;
import com.lubanjianye.biaoxuntong.base.BaseFragment;
import com.lubanjianye.biaoxuntong.database.DatabaseManager;
import com.lubanjianye.biaoxuntong.database.UserProfile;
import com.lubanjianye.biaoxuntong.eventbus.EventMessage;
import com.lubanjianye.biaoxuntong.ui.main.user.company.BindCompanyActivity;
import com.lubanjianye.biaoxuntong.util.toast.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 项目名:   LBBXT
 * 包名:     com.lubanjianye.biaoxuntong.ui.main.fragment.user.avater
 * 文件名:   AvaterFragment
 * 创建者:   lunious
 * 创建时间: 2017/12/15  13:05
 * 描述:     TODO
 */

public class AvaterFragment extends BaseFragment implements View.OnClickListener {

    private LinearLayout llIvBack = null;
    private AppCompatTextView mainBarName = null;
    private AppCompatTextView tvUserName = null;
    private AppCompatTextView tvUserCompany = null;
    private AppCompatTextView tvUserMobile = null;
    private AppCompatTextView tvUserSex = null;
    private AppCompatTextView tvUserArea = null;
    private CircleImageView imgUserAvatar = null;


    private long id = 0;
    private String mobile = "";
    private String nickName = "";
    private String token = "";
    private String comid = "";
    private String companyName = "";
    private String headUrl = "";
    private String sex = "";
    private String diqu = "";


    @Override
    public Object setLayout() {
        return R.layout.fragment_avater;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        //取消注册EventBus
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void initView() {

        //注册EventBus
        EventBus.getDefault().register(this);

        llIvBack = getView().findViewById(R.id.ll_iv_back);
        mainBarName = getView().findViewById(R.id.main_bar_name);
        tvUserName = getView().findViewById(R.id.tv_user_name);
        tvUserCompany = getView().findViewById(R.id.tv_user_company);
        tvUserMobile = getView().findViewById(R.id.tv_user_mobile);
        tvUserSex = getView().findViewById(R.id.tv_user_sex);
        tvUserArea = getView().findViewById(R.id.tv_user_area);
        imgUserAvatar = getView().findViewById(R.id.img_user_avatar);
        tvUserArea.setOnClickListener(this);
        tvUserSex.setOnClickListener(this);
        llIvBack.setOnClickListener(this);
        tvUserCompany.setOnClickListener(this);
        tvUserMobile.setOnClickListener(this);
        imgUserAvatar.setOnClickListener(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void XXXXXX(EventMessage message) {

        if (EventMessage.BIND_MOBILE_SUCCESS.equals(message.getMessage())) {
            //绑定手机号成功后更新UI
            List<UserProfile> users = DatabaseManager.getInstance().getDao().loadAll();
            for (int i = 0; i < users.size(); i++) {
                mobile = users.get(0).getMobile();
            }
            if (!TextUtils.isEmpty(mobile)) {
                tvUserMobile.setText(mobile);
            } else {
                tvUserMobile.setText("点击绑定手机号");
            }

        }
        if (EventMessage.BIND_COMPANY_SUCCESS.equals(message.getMessage())) {
            //绑定企业成功后更新UI
            List<UserProfile> users = DatabaseManager.getInstance().getDao().loadAll();
            for (int i = 0; i < users.size(); i++) {
                companyName = users.get(0).getCompanyName();
            }

            if (!TextUtils.isEmpty(companyName)) {
                tvUserCompany.setText(companyName);
            } else {
                tvUserCompany.setText("点击绑定企业");
            }
        }
    }

    @Override
    public void initData() {
        llIvBack.setVisibility(View.VISIBLE);
        mainBarName.setText("个人中心");
    }

    @Override
    public void initEvent() {
        requestData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_user_avatar:
                ToastUtil.shortToast(getContext(),"更换头像");
                break;
            case R.id.tv_user_sex:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                String[] strarr = {"男","女"};
                builder.setItems(strarr, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        String sex = "";
                        if (arg1 == 0) {
                            //男
                            sex = "男";
                        }else {
                            //女
                            sex = "女";
                        }

                        OkGo.<String>post(BiaoXunTongApi.URL_CHANGEUSER)
                                .params("Id",id)
                                .params("sex",sex)
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(Response<String> response) {
                                        final JSONObject userInfo = JSON.parseObject(response.body());
                                        final String status = userInfo.getString("status");
                                        final String message = userInfo.getString("message");

                                        if ("200".equals(status)){
                                            requestData();
                                            ToastUtil.shortBottonToast(getContext(),"修改成功");
                                        }else {
                                            ToastUtil.shortBottonToast(getContext(),message);
                                        }
                                    }
                                });
                    }
                });
                builder.show();
                break;
            case R.id.tv_user_area:

                break;
            case R.id.ll_iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.tv_user_company:

                if ("点击绑定企业".equals(tvUserCompany.getText().toString().trim())) {

                    if (!TextUtils.isEmpty(mobile)) {
                        //进入绑定企业界面
                        startActivity(new Intent(getActivity(), BindCompanyActivity.class));
                    } else {
                        ToastUtil.shortBottonToast(getContext(), "请先绑定手机号！");
                    }

                } else {
                    ToastUtil.shortBottonToast(getContext(),"暂不支持修改已绑定企业");
                }
                break;
            case R.id.tv_user_mobile:
                if ("点击绑定手机号".equals(tvUserMobile.getText().toString().trim())) {
                    //进入绑定手机号界面
                    startActivity(new Intent(getActivity(), BindMobileActivity.class));
                    getActivity().onBackPressed();
                } else {
                    ToastUtil.shortBottonToast(getContext(),"暂不支持修改已绑定手机号");
                }
                break;
            default:
                break;
        }
    }


    public void requestData() {

        List<UserProfile> users = DatabaseManager.getInstance().getDao().loadAll();
        for (int i = 0; i < users.size(); i++) {
            id = users.get(0).getId();
            headUrl = users.get(0).getImageUrl();

        }

        OkGo.<String>post(BiaoXunTongApi.URL_GETUSERINFO)
                .params("Id",id)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        final JSONObject userInfo = JSON.parseObject(response.body());
                        final String status = userInfo.getString("status");
                        final String message = userInfo.getString("message");


                        if ("200".equals(status)){
                            final JSONObject data = userInfo.getJSONObject("data");
                            final JSONObject qy = data.getJSONObject("qy");
                            final JSONObject user = data.getJSONObject("user");

                            companyName = qy.getString("qy");
                            if (!TextUtils.isEmpty(companyName)) {
                                tvUserCompany.setText(companyName);
                            } else {
                                tvUserCompany.setText("点击绑定企业");
                            }
                            mobile = user.getString("mobile");
                            if (!TextUtils.isEmpty(mobile)) {
                                tvUserMobile.setText(mobile);
                            } else {
                                tvUserMobile.setText("点击绑定手机号");
                            }
                            nickName = user.getString("nickName");
                            if (!TextUtils.isEmpty(nickName)) {
                                tvUserName.setText(nickName);
                            } else {
                                tvUserName.setText("点击设置");
                            }
                            sex = user.getString("sex");
                            if (!TextUtils.isEmpty(sex)) {
                                tvUserSex.setText(sex);
                            } else {
                                tvUserSex.setText("点击设置");
                            }
                            diqu = user.getString("diqu");
                            if (!TextUtils.isEmpty(diqu)) {
                                tvUserArea.setText(diqu);
                            } else {
                                tvUserArea.setText("点击设置");
                            }

                            String newHeardUrl = user.getString("headUrl");
                            if (!TextUtils.isEmpty(newHeardUrl)){
                                Glide.with(getActivity()).load(newHeardUrl).into(imgUserAvatar);
                            }else if (!TextUtils.isEmpty(headUrl)){
                                Glide.with(getActivity()).load(headUrl).into(imgUserAvatar);
                            }else {
                                imgUserAvatar.setImageResource(R.mipmap.moren_touxiang);
                            }

                        }else {
                            ToastUtil.shortToast(getContext(),message);
                        }
                    }
                });


    }
}
