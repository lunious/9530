package com.lubanjianye.biaoxuntong.ui.search.activity;

import android.content.Intent;

import com.lubanjianye.biaoxuntong.base.BaseActivity;
import com.lubanjianye.biaoxuntong.base.BaseFragment;
import com.lubanjianye.biaoxuntong.ui.main.result.detail.sichuan.ResultSggjyzbjgDetailFragment;
import com.lubanjianye.biaoxuntong.ui.search.fragment.SearchFragment;

public class SearchActivity extends BaseActivity {

    private int searchTye = -1;

    @Override
    public BaseFragment setRootFragment() {

        Intent intent = getIntent();
        if (intent != null) {
            searchTye = intent.getIntExtra("searchTye", -1);
        }

        final SearchFragment fragment = SearchFragment.create(searchTye);
        return fragment;
    }
}
