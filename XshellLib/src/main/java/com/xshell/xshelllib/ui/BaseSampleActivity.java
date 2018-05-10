package com.xshell.xshelllib.ui;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.viewpagerindicator.PageIndicator;
import com.xshell.xshelllib.adapter.TestFragmentAdapter;

import java.util.Random;

public abstract class BaseSampleActivity extends FragmentActivity {
    private static final Random RANDOM = new Random();

    TestFragmentAdapter mAdapter;
    ViewPager mPager;
    PageIndicator mIndicator;
   
}
