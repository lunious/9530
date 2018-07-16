package com.lubanjianye.biaoxuntong.ui.main.query.qybg;

import android.content.Intent;

import com.lubanjianye.biaoxuntong.base.BaseActivity1;
import com.lubanjianye.biaoxuntong.base.BaseFragment1;

/**
 * Author: lunious
 * Date: 2018/7/11 10:24
 * Description:
 */
public class QybgActivity extends BaseActivity1 {

    private String sfId = "";
    private String companyName = "";

    @Override
    public BaseFragment1 setRootFragment() {
        Intent intent = getIntent();
        if (intent != null) {
            sfId = intent.getStringExtra("sfId");
            companyName = intent.getStringExtra("companyName");
        }

        final QybgFragment fragment = QybgFragment.create(sfId,companyName);
        return fragment;
    }
}
