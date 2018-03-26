package com.lubanjianye.biaoxuntong.ui.message;

import com.lubanjianye.biaoxuntong.base.BaseActivity;
import com.lubanjianye.biaoxuntong.base.BaseFragment;

/**
 * Created by 11645 on 2018/3/21.
 */

public class MessageActivity extends BaseActivity {
    @Override
    public BaseFragment setRootFragment() {
        return new MessageFragment();
    }
}
