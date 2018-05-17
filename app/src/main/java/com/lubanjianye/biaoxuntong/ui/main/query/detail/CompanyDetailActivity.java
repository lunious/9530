package com.lubanjianye.biaoxuntong.ui.main.query.detail;

import android.content.Intent;

import com.lubanjianye.biaoxuntong.base.BaseActivity;
import com.lubanjianye.biaoxuntong.base.BaseFragment;



public class CompanyDetailActivity extends BaseActivity {

    private String sfId = "";
    private String lxr = "";


    @Override
    public BaseFragment setRootFragment() {

        Intent intent = getIntent();
        if (intent != null) {
            sfId = intent.getStringExtra("sfId");
            lxr = intent.getStringExtra("lxr");
        }

        final CompanyDetailFragment fragment = CompanyDetailFragment.create(sfId,lxr);
        return fragment;

    }
}
