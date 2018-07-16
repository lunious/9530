package com.lubanjianye.biaoxuntong.base;


import com.lubanjianye.biaoxuntong.eventbus.EventMessage;
import com.lubanjianye.biaoxuntong.util.sp.AppSharePreferenceMgr;
import com.lubanjianye.biaoxuntong.util.toast.ToastUtil;


public class MainTabFragment extends BaseFragment1 {


    @Override
    public Object setLayout() {
        return null;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {

    }


    // 再点一次退出程序时间设置
    private static final long WAIT_TIME = 3000L;
    private long TOUCH_TIME = 0;

    /**
     * 处理回退事件
     *
     * @return
     */
    @Override
    public boolean onBackPressedSupport() {
        if (!AppSharePreferenceMgr.contains(getContext(), EventMessage.DOUBLE_CLICK_EXIT)) {
            if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
                _mActivity.finish();
            } else {
                TOUCH_TIME = System.currentTimeMillis();
                ToastUtil.shortToast(getContext(), "再按一次退出鲁班标讯通");
            }
        } else {
            _mActivity.finish();
        }
        return true;
    }
}
