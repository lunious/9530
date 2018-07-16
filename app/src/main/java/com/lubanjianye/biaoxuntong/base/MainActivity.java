package com.lubanjianye.biaoxuntong.base;


public class MainActivity extends BaseActivity1 {

    @Override
    public BaseFragment1 setRootFragment() {
        setSwipeBackEnable(false);
        return new MainFragment();
    }

}
