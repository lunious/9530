package com.lubanjianye.biaoxuntong.ui.main.user.avater;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.lubanjianye.biaoxuntong.base.BaseActivity1;
import com.lubanjianye.biaoxuntong.base.BaseFragment1;



@Route(path = "/com/AvaterActivity")
public class AvaterActivity extends BaseActivity1 {
    @Override
    public BaseFragment1 setRootFragment() {
        return new AvaterFragment();
    }
}
