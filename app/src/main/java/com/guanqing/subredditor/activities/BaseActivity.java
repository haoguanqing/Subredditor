package com.guanqing.subredditor.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.guanqing.subredditor.MyApplication;

/**
 * Created by Guanqing on 2015/11/24.
 */
public class BaseActivity extends AppCompatActivity{


    protected MyApplication mMyApplication;
    protected Resources mResources;
    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initConfigure();
    }

    private void initConfigure() {
        mContext = this;
        if (null == mMyApplication) {
            mMyApplication = MyApplication.getInstance();
        }
        mResources = getResources();
    }

    /**
     * start Activity
     *
     * @param context
     * @param targetActivity
     * @param bundle
     */
    public void jumpToActivity(Context context, Class<?> targetActivity, Bundle bundle) {
        Intent intent = new Intent(context, targetActivity);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * startActivityForResult
     *
     * @param context
     * @param targetActivity
     * @param requestCode
     * @param bundle
     */
    public void jumpToActivityForResult(Context context, Class<?> targetActivity, int requestCode, Bundle bundle) {
        Intent intent = new Intent(context, targetActivity);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

}
