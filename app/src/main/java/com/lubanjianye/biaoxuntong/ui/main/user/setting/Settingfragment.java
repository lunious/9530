package com.lubanjianye.biaoxuntong.ui.main.user.setting;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lubanjianye.biaoxuntong.R;
import com.lubanjianye.biaoxuntong.app.BiaoXunTong;
import com.lubanjianye.biaoxuntong.base.BaseFragment;
import com.lubanjianye.biaoxuntong.database.DatabaseManager;
import com.lubanjianye.biaoxuntong.database.UserProfile;
import com.lubanjianye.biaoxuntong.eventbus.EventMessage;
import com.lubanjianye.biaoxuntong.app.BiaoXunTongApi;
import com.lubanjianye.biaoxuntong.ui.sign.SignInActivity;
import com.lubanjianye.biaoxuntong.ui.main.user.opinion.OpinionActivity;
import com.lubanjianye.biaoxuntong.ui.update.UpdateAppBean;
import com.lubanjianye.biaoxuntong.ui.update.UpdateAppManager;
import com.lubanjianye.biaoxuntong.ui.update.UpdateCallback;
import com.lubanjianye.biaoxuntong.ui.update.utils.CProgressDialogUtils;
import com.lubanjianye.biaoxuntong.ui.update.utils.OkGoUpdateHttpUtil;
import com.lubanjianye.biaoxuntong.ui.view.ToggleButton;
import com.lubanjianye.biaoxuntong.util.appinfo.AppApplicationMgr;
import com.lubanjianye.biaoxuntong.util.cache.AppCleanMgr;
import com.lubanjianye.biaoxuntong.util.dialog.DialogHelper;
import com.lubanjianye.biaoxuntong.util.sp.AppSharePreferenceMgr;
import com.lubanjianye.biaoxuntong.util.toast.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class Settingfragment extends BaseFragment implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private LinearLayout llBack = null;
    private AppCompatTextView mainBarName = null;
    private AppCompatTextView llCancel = null;
    private LinearLayout llUpdate = null;
    private LinearLayout llCacheSize = null;
    private LinearLayout llOpinion = null;
    private ToggleButton mDoubleExit = null;
    private ToggleButton mLeftBack = null;


    //存储权限
    private static final int RC_EXTERNAL_STORAGE = 0x04;

    @Override
    public Object setLayout() {
        return R.layout.fragment_setting;
    }

    @Override
    public void initView() {
        llBack = getView().findViewById(R.id.ll_iv_back);
        mainBarName = getView().findViewById(R.id.main_bar_name);
        llCancel = getView().findViewById(R.id.ll_cancel);
        llUpdate = getView().findViewById(R.id.ll_update);
        llCacheSize = getView().findViewById(R.id.ll_cache_size);
        llOpinion = getView().findViewById(R.id.ll_opinion);
        mDoubleExit = getView().findViewById(R.id.tb_double_click_exit);
        mLeftBack = getView().findViewById(R.id.tb_left_back);
        llOpinion.setOnClickListener(this);
        llBack.setOnClickListener(this);
        llCancel.setOnClickListener(this);
        llUpdate.setOnClickListener(this);
        llCacheSize.setOnClickListener(this);
        mDoubleExit.setOnClickListener(this);
        mLeftBack.setOnClickListener(this);

    }

    @Override
    public void initData() {
        llBack.setVisibility(View.VISIBLE);
        mainBarName.setText("系统设置");
        String cacheSize = AppCleanMgr.getAppClearSize(getContext());


        if (AppSharePreferenceMgr.contains(getContext(), EventMessage.LOGIN_SUCCSS)) {
            llCancel.setVisibility(View.VISIBLE);
        } else {
            llCancel.setVisibility(View.INVISIBLE);
        }

        if (AppSharePreferenceMgr.contains(getContext(), EventMessage.DOUBLE_CLICK_EXIT)) {
            mDoubleExit.setToggleOff();
        } else {
            mDoubleExit.setToggleOn();
        }

        if (AppSharePreferenceMgr.contains(getContext(), EventMessage.LEFT_BACK)) {
            mLeftBack.setToggleOff();
        } else {
            mLeftBack.setToggleOn();
        }


    }

    @Override
    public void initEvent() {
        mDoubleExit.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on) {
                    AppSharePreferenceMgr.remove(getContext(), EventMessage.DOUBLE_CLICK_EXIT);
                } else {
                    AppSharePreferenceMgr.put(getContext(), EventMessage.DOUBLE_CLICK_EXIT, "off");
                }
            }
        });
        mLeftBack.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on) {
                    AppSharePreferenceMgr.remove(getContext(), EventMessage.LEFT_BACK);
                } else {
                    AppSharePreferenceMgr.put(getContext(), EventMessage.LEFT_BACK, "off");
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.ll_cache_size:
                AppCleanMgr.cleanInternalCache(getContext());
                ToastUtil.shortToast(getContext(), "缓存清理成功");
                break;
            case R.id.ll_opinion:
                //意见反馈界面
                if (AppSharePreferenceMgr.contains(getContext(), EventMessage.LOGIN_SUCCSS)) {
                    startActivity(new Intent(getContext(), OpinionActivity.class));
                } else {
                    //未登录去登陆
                    ToastUtil.shortBottonToast(getContext(), "请先登录");
                    startActivity(new Intent(getContext(), SignInActivity.class));
                }
                break;
            case R.id.ll_cancel:

                DialogHelper.getConfirmDialog(getActivity(), "退出确认", "退出当前账号，将不能同步收藏和查看公司详情", "确认退出", "取消", false, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<UserProfile> users = DatabaseManager.getInstance().getDao().loadAll();
                        long id = 0;
                        for (int j = 0; j < users.size(); j++) {
                            id = users.get(0).getId();
                        }
                        OkGo.<String>post(BiaoXunTongApi.URL_LOGOUT)
                                .params("id", id)
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(Response<String> response) {
                                        if (llCacheSize != null) {
                                            llCancel.setVisibility(View.INVISIBLE);
                                        }
                                        DatabaseManager.getInstance().getDao().deleteAll();
                                        AppSharePreferenceMgr.remove(getContext(), EventMessage.LOGIN_SUCCSS);
                                        AppSharePreferenceMgr.remove(getContext(), EventMessage.NO_CHANGE_AREA);
                                        AppSharePreferenceMgr.remove(getContext(), EventMessage.IF_ASK_LOCATION);
                                        AppSharePreferenceMgr.remove(getContext(), EventMessage.TOKEN_FALSE);
                                        EventBus.getDefault().post(new EventMessage(EventMessage.LOGIN_OUT));
                                        ToastUtil.shortBottonToast(getContext(), "退出成功");
                                    }
                                });
                    }
                }, null).show();

                break;
            case R.id.ll_update:
                //更新界面
                requestExternalStorage();
                updateDiy();
                break;
            case R.id.tb_double_click_exit:
                mDoubleExit.toggle();
                break;
            case R.id.tb_left_back:
                mLeftBack.toggle();
                break;
            default:
                break;
        }
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        DialogHelper.getConfirmDialog(getActivity(), "温馨提示", "需要开启鲁班标讯通对您手机的存储权限才能下载安装，是否现在开启", "去开启", "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
            }
        }).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @SuppressLint("InlinedApi")
    @AfterPermissionGranted(RC_EXTERNAL_STORAGE)
    public void requestExternalStorage() {
        if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
        } else {
            EasyPermissions.requestPermissions(this, "", RC_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    /**
     * 自定义接口协议
     */
    public void updateDiy() {

        int versionCode = AppApplicationMgr.getVersionCode(BiaoXunTong.getApplicationContext());

        Map<String, String> params = new HashMap<String, String>();

        params.put("versionCode", String.valueOf(versionCode));


        new UpdateAppManager
                .Builder()
                //必须设置，当前Activity
                .setActivity(getActivity())
                //必须设置，实现httpManager接口的对象
                .setHttpManager(new OkGoUpdateHttpUtil())
                //必须设置，更新地址
                .setUpdateUrl(BiaoXunTongApi.URL_UPDATE)

                //以下设置，都是可选
                //设置请求方式，默认get
                .setPost(true)
                //不显示通知栏进度条
//                .dismissNotificationProgress()
                //是否忽略版本
//                .showIgnoreVersion()
                //添加自定义参数，默认version=1.0.0（app的versionName）；apkKey=唯一表示（在AndroidManifest.xml配置）
                .setParams(params)
                //设置点击升级后，消失对话框，默认点击升级后，对话框显示下载进度
                .hideDialogOnDownloading(false)
                //设置头部，不设置显示默认的图片，设置图片后自动识别主色调，然后为按钮，进度条设置颜色
//                .setTopPic(R.mipmap.top_8)
                //为按钮，进度条设置颜色。
                .setThemeColor(0xffffac5d)
                //设置apk下砸路径，默认是在下载到sd卡下/Download/1.0.0/test.apk
//                .setTargetPath(path)
                //设置appKey，默认从AndroidManifest.xml获取，如果，使用自定义参数，则此项无效
//                .setAppKey("ab55ce55Ac4bcP408cPb8c1Aaeac179c5f6f")

                .build()
                //检测是否有新版本
                .checkNewApp(new UpdateCallback() {
                    /**
                     * 解析json,自定义协议
                     *
                     * @param json 服务器返回的json
                     * @return UpdateAppBean
                     */
                    @Override
                    protected UpdateAppBean parseJson(String json) {
                        final JSONObject object = JSON.parseObject(json);
                        String status = object.getString("status");
                        UpdateAppBean updateAppBean = new UpdateAppBean();

                        if ("200".equals(status)) {
                            final JSONObject data = object.getJSONObject("data");
                            String name = data.getString("name");
                            String content = data.getString("content");
                            String url = data.getString("downloadUrl");


                            String doUrl = "http://openbox.mobilem.360.cn/index/d/sid/3958155";

                            updateAppBean
                                    //（必须）是否更新Yes,No
                                    .setUpdate("Yes")
                                    //（必须）新版本号，
                                    .setNewVersion(name)
                                    //（必须）下载地址
                                    .setApkFileUrl(doUrl)
                                    //（必须）更新内容
                                    .setUpdateLog(content)
                                    //是否强制更新，可以不设置
                                    .setConstraint(false);

                        }
                        return updateAppBean;
                    }

                    @Override
                    protected void hasNewApp(UpdateAppBean updateApp, UpdateAppManager updateAppManager) {
                        updateAppManager.showDialogFragment();
                    }

                    /**
                     * 网络请求之前
                     */
                    @Override
                    public void onBefore() {
                        CProgressDialogUtils.showProgressDialog(getActivity(), "检查新版本...");
                    }

                    /**
                     * 网路请求之后
                     */
                    @Override
                    public void onAfter() {
                        CProgressDialogUtils.cancelProgressDialog(getActivity());
                    }

                    /**
                     * 没有新版本
                     */
                    @Override
                    public void noNewApp() {
                        ToastUtil.shortToast(getActivity(), "当前已是最新版本");
                    }
                });

    }


}
