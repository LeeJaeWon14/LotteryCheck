package com.example.ghkdw.lotterycheck;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by ghkdw on 2020-10-11.
 */

public class MyPagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 3;
    private Context context;
    String session;

    public
    MyPagerAdapter(FragmentManager fragmentManager, String session) {
        super(fragmentManager);
        this.session = session;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return ScanFragment.newInstance(0, "Scan", session);
            case 1:
                return TestFragmnet.newInstance(1, "Test", session);
            case 2:
                return RecordFragment.newInstance(2, "Record", session);
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "page" + position;
    }
}