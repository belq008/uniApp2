package com.xshell.xshelllib.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.viewpagerindicator.IconPagerAdapter;
import com.xshell.xshelllib.ui.TestFragment;

import java.io.File;

public class TestFragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter {

	public static int c;
	public static int posi;
	Context mContext;
	String webIndex;
	String imageurl;

	public TestFragmentAdapter(FragmentManager fm, Context context, String webindex,String imageurl) {
		super(fm);
		mContext = context;
		webIndex = webindex;
		this.imageurl = imageurl;
	}

	@Override
	public Fragment getItem(int position) {
		posi = position;
		Log.e("position---", position + "");
		return TestFragment.newInstance(position);
	}

	/**
	 * 获得引导页的数量
	 * @return 引导页的数量
	 */
	public int getCc() {
		File f = new File(imageurl);
		String[] name = f.list();
		if (name != null) {
			c = name.length;
			return name.length;
		} else {
			return 0;
		}
	}

	@Override
	public int getCount() {
		return getCc();
	}


	public static int getC() {
		return c;
	}

	public static void setC(int c) {
		TestFragmentAdapter.c = c;

	}

	@Override
	public int getIconResId(int index) {
		// TODO Auto-generated method stub
		return index;
	}

}