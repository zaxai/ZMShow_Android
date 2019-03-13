package com.zaxai.zmshow;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class NetworkFragment extends Fragment {
    private View mContentView;
    private ViewGroup mFullScreen;
    private ViewGroup mToolbar;
    private ProgressBar mProgressBar;
    private WebView mWebView;
    private EditText mEditText;
    private ImageButton mBtnBack;
    private ImageButton mBtnForward;
    private ImageButton mBtnBrowse;
    private ImageButton mBtnRefresh;
    private String mUrl;
    private WebChromeClient.CustomViewCallback mCallback;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView=inflater.inflate(R.layout.network_fragment,container,false);
        initView();
        return mContentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mWebView.destroy();
    }

    public void initView(){
        mFullScreen=mContentView.findViewById(R.id.network_fullscreen);
        if(Build.VERSION.SDK_INT>=19) {
            mFullScreen.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }else{
            mFullScreen.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        mToolbar=mContentView.findViewById(R.id.network_toolbar);
        mProgressBar=mContentView.findViewById(R.id.network_progressbar);
        mWebView=mContentView.findViewById(R.id.network_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new NetworkWebViewClient());
        mWebView.setWebChromeClient(new NetworkWebChromeClient());
        mEditText=mContentView.findViewById(R.id.network_url);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId==EditorInfo.IME_ACTION_GO){
                    String url=mEditText.getText().toString();
                    if (url.indexOf("http://") != 0 && url.indexOf("https://") != 0) {
                        url = "http://" + url;
                    }
                    mWebView.loadUrl(url);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm!=null)
                        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
                }
                return false;
            }
        });
        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    mBtnBrowse.setVisibility(View.VISIBLE);
                    mBtnRefresh.setVisibility(View.GONE);
                }else{
                    mBtnBrowse.setVisibility(View.GONE);
                    mBtnRefresh.setVisibility(View.VISIBLE);
                }
            }
        });
        if(TextUtils.isEmpty(mUrl)){
            SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(getContext());
            mUrl=preferences.getString("network_default_url","www.iqiyi.com");
            if (mUrl.indexOf("http://") != 0 && mUrl.indexOf("https://") != 0) {
                mUrl = "http://" + mUrl;
            }
        }
        mWebView.loadUrl(mUrl);
        mEditText.setText(mUrl);
        mBtnBack=mContentView.findViewById(R.id.network_back);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.goBack();
            }
        });
        mBtnForward=mContentView.findViewById(R.id.network_forward);
        mBtnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.goForward();
            }
        });
        mBtnBrowse=mContentView.findViewById(R.id.network_browse);
        mBtnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url=mEditText.getText().toString();
                if (url.indexOf("http://") != 0 && url.indexOf("https://") != 0) {
                    url = "http://" + url;
                }
                mWebView.loadUrl(url);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm!=null)
                    imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
            }
        });
        mBtnRefresh=mContentView.findViewById(R.id.network_refresh);
        mBtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.reload();
            }
        });
    }

    class NetworkWebViewClient extends WebViewClient{
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mProgressBar.setVisibility(View.VISIBLE);
            mEditText.setText(url);
            mUrl=url;
            if(mWebView.canGoBack()){
                mBtnBack.setImageResource(R.drawable.ic_zmshow_btn_surf_back);
            }else{
                mBtnBack.setImageResource(R.drawable.ic_zmshow_btn_surf_back_disabled);
            }
            if(mWebView.canGoForward()){
                mBtnForward.setImageResource(R.drawable.ic_zmshow_btn_surf_forward);
            }else{
                mBtnForward.setImageResource(R.drawable.ic_zmshow_btn_surf_forward_disabled);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    class NetworkWebChromeClient extends WebChromeClient{
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            mProgressBar.setProgress(newProgress);
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
            BottomNavigationView navigation = (BottomNavigationView) ((MainActivity)getActivity()).findViewById(R.id.navigation);
            navigation.setVisibility(View.GONE);
            mToolbar.setVisibility(View.GONE);
            mWebView.setVisibility(View.GONE);
            mFullScreen.setVisibility(View.VISIBLE);
            mFullScreen.addView(view);
            mCallback=callback;
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
            if(mCallback!=null)
                mCallback.onCustomViewHidden();
            mFullScreen.removeAllViews();
            mFullScreen.setVisibility(View.GONE);
            mToolbar.setVisibility(View.VISIBLE);
            mWebView.setVisibility(View.VISIBLE);
            BottomNavigationView navigation = (BottomNavigationView) ((MainActivity)getActivity()).findViewById(R.id.navigation);
            navigation.setVisibility(View.VISIBLE);
        }
    }
}
