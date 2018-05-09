package com.lubanjianye.biaoxuntong.ui.main.query.detail;

import android.content.Intent;

import com.lubanjianye.biaoxuntong.base.BaseActivity;
import com.lubanjianye.biaoxuntong.base.BaseFragment;


public class CompanySgyjListActivity extends BaseActivity {

    private String sfId = "";
    @Override
    public BaseFragment setRootFragment() {
        Intent intent = getIntent();
        if (intent != null) {
            sfId = intent.getStringExtra("sfId");
        }

        final CompanySgyjListFragment fragment = CompanySgyjListFragment.create(sfId);
        return fragment;
    }
}
