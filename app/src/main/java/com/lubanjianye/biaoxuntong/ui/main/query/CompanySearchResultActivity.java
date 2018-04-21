package com.lubanjianye.biaoxuntong.ui.main.query;

import android.content.Intent;

import com.lubanjianye.biaoxuntong.base.BaseActivity;
import com.lubanjianye.biaoxuntong.base.BaseFragment;


public class CompanySearchResultActivity extends BaseActivity {

    private String provinceCode = "";
    private String qyIds = "";
    private String showSign = "";

    @Override
    public BaseFragment setRootFragment() {

        Intent intent = getIntent();
        if (intent != null) {
            provinceCode = intent.getStringExtra("provinceCode");
            qyIds = intent.getStringExtra("qyIds");
            showSign = intent.getStringExtra("showSign");
        }

        final CompanySearchResultFragment fragment = CompanySearchResultFragment.create(provinceCode, qyIds, showSign);
        return fragment;

    }
}
