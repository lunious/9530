package com.lubanjianye.biaoxuntong.ui.main.query;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lubanjianye.biaoxuntong.R;
import com.lubanjianye.biaoxuntong.bean.CompanySearchResultListBean;

import java.util.List;


public class CompanySearchResultListAdapter extends BaseQuickAdapter<CompanySearchResultListBean, BaseViewHolder> {
    public CompanySearchResultListAdapter(int layoutResId, @Nullable List<CompanySearchResultListBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CompanySearchResultListBean item) {

        helper.setText(R.id.tv_company_name, item.getQy());

        String lxr = item.getLxr();
        if (lxr != null) {
            helper.setText(R.id.tv_lxr, lxr);
        } else {
            helper.setText(R.id.tv_lxr, "暂未添加");
        }

        String provinceCode = item.getProvinceCode();
        String showSign = item.getShowSign();
        String areaType = item.getEntrySign();

        if ("1".equals(showSign)) {
            if ("0".equals(areaType)) {
                if ("510000".equals(provinceCode)) {
                    helper.setText(R.id.tv_area_type, "川内");
                } else if ("500000".equals(provinceCode)) {
                    helper.setText(R.id.tv_area_type, "渝内");
                }

            } else if ("1".equals(areaType)) {
                if ("510000".equals(provinceCode)) {
                    helper.setText(R.id.tv_area_type, "入川");
                } else if ("500000".equals(provinceCode)) {
                    helper.setText(R.id.tv_area_type, "入渝");
                }
            }
        } else {
            helper.setText(R.id.tv_area_type, "");
            helper.setVisible(R.id.tv_area_type, false);
        }

    }
}

