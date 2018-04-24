package com.lubanjianye.biaoxuntong.ui.launcher;

import com.lubanjianye.biaoxuntong.base.BaseActivity;
import com.lubanjianye.biaoxuntong.base.BaseFragment;


public class LauncherActivity extends BaseActivity {
    @Override
    public BaseFragment setRootFragment() {
        return new LauncherFragment();
    }

}
