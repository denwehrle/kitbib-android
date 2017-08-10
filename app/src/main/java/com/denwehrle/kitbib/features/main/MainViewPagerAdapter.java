package com.denwehrle.kitbib.features.main;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.denwehrle.kitbib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dennis Wehrle
 */
public class MainViewPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private static Context mContext;

    public MainViewPagerAdapter(Context context, FragmentManager manager) {
        super(manager);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFrag(Fragment fragment) {
        mFragmentList.add(fragment);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return getCampusName(position);
    }

    private static String getCampusName(int position) {
        if (position == 1) {
            return mContext.getResources().getString(R.string.title_section_fees);
        } else if (position == 2) {
            return mContext.getResources().getString(R.string.title_section_learningPlaces);
        } else {
            return mContext.getResources().getString(R.string.title_section_summary);
        }
    }
}