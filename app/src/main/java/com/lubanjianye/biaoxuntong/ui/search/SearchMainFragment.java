package com.lubanjianye.biaoxuntong.ui.search;

import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lubanjianye.biaoxuntong.R;
import com.lubanjianye.biaoxuntong.app.BiaoXunTongApi;
import com.lubanjianye.biaoxuntong.base.BaseFragment;
import com.lubanjianye.biaoxuntong.util.aes.AesUtil;
import com.lubanjianye.biaoxuntong.util.toast.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import rx.functions.Action1;

public class SearchMainFragment extends BaseFragment implements View.OnClickListener {


    private ViewPager viewPager;
    private AppBarLayout mAppBarLayout;
    private EditText mSerachEt;
    private ImageView mClearIv;
    private LinearLayout mHeadLl;
    private FrameLayout mHistoryLayout;
    private View mViewSpace;
    private View mStopView;
    private AppCompatTextView mSearch;
    private ImageView mBack;
    private MSGView mMsgView;

    public RxManager mRxManager;


    private BaseFragmentStateAdapter fragmentAdapter;
    List<Fragment> mNewsFragmentList = new ArrayList<>();
    LinkedHashMap<String, String> mSearchResult = new LinkedHashMap<>();
    private SearchHistoryFragment mSearchHistoryFragment;


    @Override
    public Object setLayout() {
        return R.layout.fragment_search_main;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mRxManager != null) {
            mRxManager.clear();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                getActivity().onBackPressed();
                break;
            case R.id.atv_search:
                if (StringUtils.isNullOrEmpty(mSerachEt.getText().toString())) {
                    ToastUtil.shortToast(getContext(), "请输入内容");
                } else {
                    search(mSerachEt.getText().toString());
                    if (mSearchHistoryFragment != null) {
                        mSearchHistoryFragment.addHistory(mSerachEt.getText().toString());
                    }
                }
                break;
            case R.id.iv_search_clear:
                mSerachEt.setText("");
                break;
            default:
                break;
        }
    }

    @Override
    public void initView() {
        mRxManager = new RxManager();
        viewPager = getView().findViewById(R.id.view_pager);
        mAppBarLayout = getView().findViewById(R.id.abl_layout);
        mSerachEt = getView().findViewById(R.id.et_search_content);
        mClearIv = getView().findViewById(R.id.iv_search_clear);
        mHeadLl = getView().findViewById(R.id.ll_head);
        mHistoryLayout = getView().findViewById(R.id.fl_history);
        mViewSpace = getView().findViewById(R.id.view_sapce);
        mStopView = getView().findViewById(R.id.view_stop);
        mBack = getView().findViewById(R.id.iv_back);
        mSearch = getView().findViewById(R.id.atv_search);
        mMsgView = getView().findViewById(R.id.msg_view);
        mBack.setOnClickListener(this);
        mSearch.setOnClickListener(this);
        mClearIv.setOnClickListener(this);

    }

    @Override
    public void initData() {
        mRxManager.on(GlobalConstant.RxBus.SERACH_HISTORY_CLICK, new Action1<String>() {
            @Override
            public void call(String s) {
                if (StringUtils.isNullOrEmpty(s)) {
                    return;
                }
                mSerachEt.setText(s);
                mSerachEt.setSelection(s.length());
                search(s);
            }
        });

        mRxManager.on(GlobalConstant.RxBus.SEARCH_TOP_SHOW_HIDE, new Action1<Boolean>() {

            @Override
            public void call(Boolean hideOrShow) {
                if (hideOrShow) {
                    RxBus.getInstance().post(GlobalConstant.RxBus.MENU_SHOW_HIDE, true);
                }
            }
        });

    }

    @Override
    public void initEvent() {
        mMsgView.setVisibility(View.GONE);
        mStopView.setVisibility(View.GONE);
        mMsgView.setErrorClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtils.isNullOrEmpty(mSerachEt.getText().toString())) {
                    search(mSerachEt.getText().toString());
                } else {
                    ToastUtil.shortToast(getContext(), "请输入内容");
                }

            }
        });

        mSerachEt.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mSerachEt.addTextChangedListener(new SearchResultWatcher());
        mSerachEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH || (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if (StringUtils.isNullOrEmpty(mSerachEt.getText().toString())) {
                        ToastUtil.shortToast(getContext(), "请输入内容");
                        return true;
                    }
                    search(mSerachEt.getText().toString());
                    if (mSearchHistoryFragment != null) {
                        mSearchHistoryFragment.addHistory(mSerachEt.getText().toString());
                    }
                    return true;
                }
                return false;
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mAppBarLayout.setExpanded(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mRxManager.on(GlobalConstant.RxBus.TOP_SHOW_HIDE, new Action1<Boolean>() {

            @Override
            public void call(Boolean hideOrShow) {
                if (hideOrShow) {
                    //fab.setVisibility(View.VISIBLE);
                }
            }
        });

        mRxManager.on(GlobalConstant.RxBus.SERACH_MAIN_HEAD_EXPLAND, new Action1<Boolean>() {

            @Override
            public void call(Boolean hideOrShow) {
                if (hideOrShow) {

                } else {

                }
                headIsScroll(hideOrShow);
            }
        });
        mSearchHistoryFragment = SearchHistoryFragment.newInstance();
        getChildFragmentManager().beginTransaction().add(R.id.fl_history, mSearchHistoryFragment).commit();
        showHistory(true);

    }

    private void showHistory(boolean isShow) {
        mViewSpace.setVisibility(isShow ? View.VISIBLE : View.VISIBLE);
        mHistoryLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);
        headIsScroll(!isShow);
        if (isShow) {
            mMsgView.dismiss();
        }
    }

    private void initFragment(String content) {

        showHistory(false);
        mNewsFragmentList.clear();
        viewPager.setAdapter(null);
        List<String> values = new ArrayList<>();

        mNewsFragmentList.add(createSearchFragment(content));
        fragmentAdapter = new BaseFragmentStateAdapter(getChildFragmentManager(), mNewsFragmentList, values);
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setVisibility(View.VISIBLE);
    }

    private Fragment createSearchFragment(String content) {
        SearchListFragment search = SearchListFragment.newInstance(content);
        return search;
    }

    private void headIsScroll(boolean yes) {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mHeadLl.getLayoutParams();
        if (yes) {
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
        } else {
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
        }
        mHeadLl.setLayoutParams(params);
    }

    private void setSearchRsult(String content) {
        mSearchResult.clear();

        if (content != null) {
            initFragment(content);
        }

    }


    private void search(final String content) {
        mStopView.setVisibility(View.VISIBLE);
        hideSoftInput();
        mAppBarLayout.setExpanded(true);
        mMsgView.showLoading();

        OkGo.<String>post(BiaoXunTongApi.URL_GETRESULTLIST)
                .params("keyWord", content)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        mMsgView.dismiss();
                        mStopView.setVisibility(View.GONE);

                        String jiemi = AesUtil.aesDecrypt(response.body(), BiaoXunTongApi.PAS_KEY);

                        final JSONObject object = JSON.parseObject(jiemi);
                        final JSONObject data = object.getJSONObject("data");
                        final JSONArray array = data.getJSONArray("list");

                        if (array.size() > 0) {
                            setSearchRsult(content);
                        } else {
                            mMsgView.showSearchEmpty();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        mMsgView.showError();
                        mStopView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFinish() {
                        mStopView.setVisibility(View.GONE);
                    }
                });
    }

    private class SearchResultWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() > 0) {
                mClearIv.setVisibility(View.VISIBLE);
            } else {
                mClearIv.setVisibility(View.GONE);
                showHistory(true);
            }
        }
    }

}
