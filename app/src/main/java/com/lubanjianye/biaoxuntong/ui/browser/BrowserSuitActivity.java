package com.lubanjianye.biaoxuntong.ui.browser;

import android.content.Intent;

import com.lubanjianye.biaoxuntong.base.BaseActivity;
import com.lubanjianye.biaoxuntong.base.BaseFragment;

/**
 * Created by 11645 on 2018/3/22.
 */

public class BrowserSuitActivity extends BaseActivity {
    private String mUrl = "";
    private String mTitle = "";

    @Override
    public BaseFragment setRootFragment() {
        Intent intent = getIntent();
        if (intent != null) {
            mUrl = intent.getStringExtra("url");
            mTitle = intent.getStringExtra("title");
        }

        final BrowserSuitFragment fragment = BrowserSuitFragment.create(mUrl, mTitle);
        return fragment;
    }
}
