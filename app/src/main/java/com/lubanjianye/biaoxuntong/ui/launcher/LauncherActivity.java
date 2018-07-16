package com.lubanjianye.biaoxuntong.ui.launcher;

import com.lubanjianye.biaoxuntong.base.BaseActivity1;
import com.lubanjianye.biaoxuntong.base.BaseFragment1;


public class LauncherActivity extends BaseActivity1 {
    @Override
    public BaseFragment1 setRootFragment() {
        return new LauncherFragment();
    }

}
