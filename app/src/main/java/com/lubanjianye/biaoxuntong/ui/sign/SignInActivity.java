package com.lubanjianye.biaoxuntong.ui.sign;

import com.lubanjianye.biaoxuntong.base.BaseActivity1;
import com.lubanjianye.biaoxuntong.base.BaseFragment1;

/**
 * 项目名:   AppLunious
 * 包名:     com.lubanjianye.biaoxuntong.sign
 * 文件名:   SignInActivity
 * 创建者:   lunious
 * 创建时间: 2017/12/13  0:40
 * 描述:     TODO
 */

public class SignInActivity extends BaseActivity1 {
    @Override
    public BaseFragment1 setRootFragment() {
        return new SignInFragment();
    }
}
