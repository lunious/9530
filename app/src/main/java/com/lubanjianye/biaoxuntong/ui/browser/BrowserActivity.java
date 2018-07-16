package com.lubanjianye.biaoxuntong.ui.browser;

import android.content.Intent;

import com.lubanjianye.biaoxuntong.base.BaseActivity1;
import com.lubanjianye.biaoxuntong.base.BaseFragment1;

/**
 * Created by 11645 on 2018/3/22.
 */

public class BrowserActivity extends BaseActivity1 {
    private String mUrl = "";
    private String mTitle = "";

    @Override
    public BaseFragment1 setRootFragment() {
        Intent intent = getIntent();
        if (intent != null) {
            mUrl = intent.getStringExtra("url");
            mTitle = intent.getStringExtra("title");
        }

        final BrowserFragment fragment = BrowserFragment.create(mUrl, mTitle);
        return fragment;
    }
}
