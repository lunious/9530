package com.lubanjianye.biaoxuntong.ui.main.user.avater;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.lubanjianye.biaoxuntong.R;
import com.lubanjianye.biaoxuntong.app.BiaoXunTongApi;
import com.lubanjianye.biaoxuntong.base.BaseFragment1;
import com.lubanjianye.biaoxuntong.database.DatabaseManager;
import com.lubanjianye.biaoxuntong.database.UserProfile;
import com.lubanjianye.biaoxuntong.eventbus.EventMessage;
import com.lubanjianye.biaoxuntong.ui.main.user.company.BindCompanyActivity;
import com.lubanjianye.biaoxuntong.ui.media.SelectImageActivity;
import com.lubanjianye.biaoxuntong.ui.media.config.SelectOptions;
import com.lubanjianye.biaoxuntong.ui.media.util.ImageUtils;
import com.lubanjianye.biaoxuntong.util.dialog.PromptDialog;
import com.lubanjianye.biaoxuntong.util.picker.Area;
import com.lubanjianye.biaoxuntong.util.picker.SinglePicker;
import com.lubanjianye.biaoxuntong.util.picker.WheelView;
import com.lubanjianye.biaoxuntong.util.rx.RxDialogEditSureCancel;
import com.lubanjianye.biaoxuntong.util.toast.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 个人中心
 */

public class AvaterFragment extends BaseFragment1 implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

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
    private String imageUrl = "";


    private PromptDialog promptDialog = null;


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
        tvUserName.setOnClickListener(this);


        //创建对象
        promptDialog = new PromptDialog(getActivity());
        //设置自定义属性
        promptDialog.getDefaultBuilder().touchAble(true).round(3).loadingDuration(3000);

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
                tvUserMobile.setText("点击认证");
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
                tvUserCompany.setText("点击绑定");
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
    public void onSupportInvisible() {
        super.onSupportInvisible();
        if (promptDialog != null) {
            promptDialog.dismissImmediately();
        }

    }

    /**
     * update the new picture
     */
    private void uploadNewPhoto(File file) {
        // 获取头像缩略图
        if (file == null || !file.exists() || file.length() == 0) {
            ToastUtil.shortBottonToast(getContext(), "图像不存在，上传失败");
        } else {
            OkGo.<String>post(BiaoXunTongApi.URL_UPTOUXIANG)
                    .params("userId", id)
                    .params("file", file)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            final JSONObject userPhoto = JSON.parseObject(response.body());
                            final String status = userPhoto.getString("status");
                            final String message = userPhoto.getString("message");

                            if ("200".equals(status)) {
                                final JSONObject data = userPhoto.getJSONObject("data");
                                final JSONArray urls = data.getJSONArray("urls");
                                String url = urls.get(0).toString();

                                OkGo.<String>post(BiaoXunTongApi.URL_CHANGEUSER)
                                        .params("Id", id)
                                        .params("headUrl", "http://api.lubanjianye.com/bxtajax/" + url)
                                        .execute(new StringCallback() {
                                            @Override
                                            public void onSuccess(Response<String> response) {
                                                final JSONObject userInfo = JSON.parseObject(response.body());
                                                final String status = userInfo.getString("status");
                                                final String message = userInfo.getString("message");

                                                if ("200".equals(status)) {
                                                    requestData();
                                                    ToastUtil.shortBottonToast(getContext(), "修改成功");
                                                } else {
                                                    ToastUtil.shortBottonToast(getContext(), message);
                                                }
                                            }
                                        });
                            } else {
                                ToastUtil.shortBottonToast(getContext(), message);
                            }

                        }

                    });
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_user_avatar:
                SelectImageActivity.show(getContext(), new SelectOptions.Builder()
                        .setSelectCount(1)
                        .setHasCam(true)
                        .setCrop(680, 680)
                        .setCallback(new SelectOptions.Callback() {
                            @Override
                            public void doSelected(String[] images) {
                                String path = images[0];
                                uploadNewPhoto(new File(path));
                            }
                        }).build());
                break;
            case R.id.tv_user_name:
                final RxDialogEditSureCancel rxDialogEditSureCancel = new RxDialogEditSureCancel(getContext());
                rxDialogEditSureCancel.getTitleView().setText("请输入想要设置的昵称");
                rxDialogEditSureCancel.getSureView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String nick = rxDialogEditSureCancel.getEditText().getText().toString();
                        if (!TextUtils.isEmpty(nick)) {
                            OkGo.<String>post(BiaoXunTongApi.URL_CHANGEUSER)
                                    .params("Id", id)
                                    .params("nickName", nick)
                                    .execute(new StringCallback() {
                                        @Override
                                        public void onSuccess(Response<String> response) {
                                            final JSONObject userInfo = JSON.parseObject(response.body());
                                            final String status = userInfo.getString("status");
                                            final String message = userInfo.getString("message");

                                            if ("200".equals(status)) {
                                                requestData();
                                                ToastUtil.shortBottonToast(getContext(), "修改成功");
                                            } else {
                                                ToastUtil.shortBottonToast(getContext(), message);
                                            }
                                        }
                                    });
                            rxDialogEditSureCancel.cancel();
                        } else {
                            ToastUtil.shortBottonToast(getContext(), "内容不能为空");
                        }

                    }
                });
                rxDialogEditSureCancel.getCancelView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rxDialogEditSureCancel.cancel();
                    }
                });
                rxDialogEditSureCancel.show();
                break;
            case R.id.tv_user_sex:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                String[] strarr = {"男", "女"};
                builder.setItems(strarr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        String sex = "";
                        if (arg1 == 0) {
                            //男
                            sex = "男";
                        } else {
                            //女
                            sex = "女";
                        }

                        OkGo.<String>post(BiaoXunTongApi.URL_CHANGEUSER)
                                .params("Id", id)
                                .params("sex", sex)
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(Response<String> response) {
                                        final JSONObject userInfo = JSON.parseObject(response.body());
                                        final String status = userInfo.getString("status");
                                        final String message = userInfo.getString("message");

                                        if ("200".equals(status)) {
                                            requestData();
                                            ToastUtil.shortBottonToast(getContext(), "修改成功");
                                        } else {
                                            ToastUtil.shortBottonToast(getContext(), message);
                                        }
                                    }
                                });
                    }
                });
                builder.show();
                break;
            case R.id.tv_user_area:

                List<Area> data = new ArrayList<>();
                data.add(new Area(1, "北京"));
                data.add(new Area(2, "天津"));
                data.add(new Area(3, "河北"));
                data.add(new Area(4, "山西"));
                data.add(new Area(5, "内蒙古"));
                data.add(new Area(6, "辽宁"));
                data.add(new Area(7, "吉林"));
                data.add(new Area(8, "黑龙江"));
                data.add(new Area(9, "上海"));
                data.add(new Area(10, "江苏"));
                data.add(new Area(11, "浙江"));
                data.add(new Area(12, "安徽"));
                data.add(new Area(13, "福建"));
                data.add(new Area(14, "江西"));
                data.add(new Area(15, "山东"));
                data.add(new Area(16, "广东"));
                data.add(new Area(17, "广西"));
                data.add(new Area(18, "海南"));
                data.add(new Area(19, "河南"));
                data.add(new Area(20, "湖北"));
                data.add(new Area(21, "湖南"));
                data.add(new Area(22, "重庆"));
                data.add(new Area(23, "四川"));
                data.add(new Area(24, "贵州"));
                data.add(new Area(25, "云南"));
                data.add(new Area(26, "西藏"));
                data.add(new Area(27, "陕西"));
                data.add(new Area(28, "甘肃"));
                data.add(new Area(29, "青海"));
                data.add(new Area(30, "宁夏"));
                data.add(new Area(31, "新疆"));
                data.add(new Area(32, "香港"));
                data.add(new Area(33, "澳门"));
                data.add(new Area(34, "台湾"));
                SinglePicker picker = new SinglePicker<>(getActivity(), data);
                picker.setCycleDisable(true);
                picker.setTopBackgroundColor(0xFFEEEEEE);
                picker.setTopHeight(42);
                picker.setTopLineColor(0xFFEEEEEE);
                picker.setTopLineHeight(1);
                picker.setTitleText("请选择地区");
                picker.setTitleTextColor(0xFF999999);
                picker.setTitleTextSize(14);
                picker.setCancelTextColor(getResources().getColor(R.color.main_theme_color));
                picker.setCancelTextSize(15);
                picker.setSubmitTextColor(getResources().getColor(R.color.main_theme_color));
                picker.setSubmitTextSize(15);
                picker.setTextColor(getResources().getColor(R.color.main_theme_color), 0xFF999999);
                WheelView.DividerConfig config = new WheelView.DividerConfig();
                config.setColor(getResources().getColor(R.color.main_theme_color));
                config.setAlpha(140);
                config.setRatio((float) (1.0 / 8.0));
                picker.setDividerConfig(config);
                picker.setBackgroundColor(getResources().getColor(R.color.main_status_white));
                picker.setSelectedIndex(22);
                picker.setCanceledOnTouchOutside(true);
                picker.setOnItemPickListener(new SinglePicker.OnItemPickListener<Area>() {
                    @Override
                    public void onItemPicked(int index, Area item) {


                        String diqu = item.getName();

                        OkGo.<String>post(BiaoXunTongApi.URL_CHANGEUSER)
                                .params("Id", id)
                                .params("diqu", diqu)
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(Response<String> response) {
                                        final JSONObject userInfo = JSON.parseObject(response.body());
                                        final String status = userInfo.getString("status");
                                        final String message = userInfo.getString("message");

                                        if ("200".equals(status)) {
                                            requestData();
                                            ToastUtil.shortBottonToast(getContext(), "修改成功");
                                        } else {
                                            ToastUtil.shortBottonToast(getContext(), message);
                                        }
                                    }
                                });
                    }
                });
                picker.show();
                break;
            case R.id.ll_iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.tv_user_company:

                if ("点击绑定".equals(tvUserCompany.getText().toString().trim())) {

                    if (!TextUtils.isEmpty(mobile)) {
                        //进入绑定企业界面
                        startActivity(new Intent(getActivity(), BindCompanyActivity.class));
                    } else {
                        ToastUtil.shortBottonToast(getContext(), "请先认证手机");
                    }

                } else {
                    ToastUtil.shortBottonToast(getContext(), "已绑定企业，暂不支持修改");
                }


                break;
            case R.id.tv_user_mobile:
                if ("点击认证".equals(tvUserMobile.getText().toString().trim())) {
                    //进入绑定手机号界面
                    startActivity(new Intent(getActivity(), BindMobileActivity.class));
                    getActivity().onBackPressed();
                } else {
                    ToastUtil.shortBottonToast(getContext(), "已认证手机，暂不支持修改");
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
            imageUrl = users.get(0).getImageUrl();
            token = users.get(0).getToken();
            nickName = users.get(0).getNickName();
            mobile = users.get(0).getMobile();
            comid = users.get(0).getComid();
            companyName = users.get(0).getCompanyName();

        }

        OkGo.<String>post(BiaoXunTongApi.URL_GETUSERINFO)
                .params("Id", id)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

                        final JSONObject userInfo = JSON.parseObject(response.body());
                        final String status = userInfo.getString("status");
                        final String message = userInfo.getString("message");


                        if ("200".equals(status)) {

                            final JSONObject data = userInfo.getJSONObject("data");
                            final JSONObject qy = data.getJSONObject("qy");
                            final JSONObject user = data.getJSONObject("user");


                            if (qy != null) {
                                companyName = qy.getString("qy");
                            }
                            if (!TextUtils.isEmpty(companyName)) {
                                tvUserCompany.setText(companyName);
                            } else {
                                tvUserCompany.setText("点击绑定");
                            }

                            String phone = user.getString("mobile");
                            if (!TextUtils.isEmpty(phone)) {
                                mobile = phone;
                                tvUserMobile.setText(phone);
                            } else {
                                tvUserMobile.setText("点击认证");
                            }

                            String name = user.getString("nickName");
                            if (!TextUtils.isEmpty(name)) {
                                nickName = name;
                                tvUserName.setText(name);
                            } else {
                                tvUserName.setText(nickName);
                            }

                            String xingbie = user.getString("sex");
                            if (!TextUtils.isEmpty(xingbie)) {
                                tvUserSex.setText(xingbie);
                            } else {
                                tvUserSex.setText("点击设置");
                            }
                            String area = user.getString("diqu");
                            if (!TextUtils.isEmpty(area)) {
                                tvUserArea.setText(area);
                            } else {
                                tvUserArea.setText("点击设置");
                            }

                            String newHeardUrl = user.getString("headUrl");
                            if (!TextUtils.isEmpty(newHeardUrl)) {
                                imageUrl = newHeardUrl;
                                Glide.with(getActivity()).load(imageUrl).into(imgUserAvatar);
                            } else if (!TextUtils.isEmpty(imageUrl)) {
                                Glide.with(getActivity()).load(imageUrl).into(imgUserAvatar);
                            } else {
                                imgUserAvatar.setImageResource(R.mipmap.moren_touxiang);
                            }

                            final UserProfile profile = new UserProfile(id, mobile, nickName, token, comid, imageUrl, companyName);
                            DatabaseManager.getInstance().getDao().update(profile);

                            EventBus.getDefault().post(new EventMessage(EventMessage.USER_INFO_CHANGE));


                        } else {
                            ToastUtil.shortToast(getContext(), message);
                        }
                    }
                });


    }

    /**
     * take photo
     */
    private void startTakePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,
                ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        try {
            startTakePhoto();
        } catch (Exception e) {
            Toast.makeText(this.getContext(), R.string.permissions_camera_error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this.getContext(), R.string.permissions_camera_error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions
            , @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
