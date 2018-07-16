package com.lubanjianye.biaoxuntong.ui.message;

import com.lubanjianye.biaoxuntong.base.BaseActivity1;
import com.lubanjianye.biaoxuntong.base.BaseFragment1;

/**
 * Created by 11645 on 2018/3/21.
 */

public class MessageActivity extends BaseActivity1 {
    @Override
    public BaseFragment1 setRootFragment() {
        return new MessageFragment();
    }
}
