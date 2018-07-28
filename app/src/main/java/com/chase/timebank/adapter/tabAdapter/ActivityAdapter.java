package com.chase.timebank.adapter.tabAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.chase.timebank.fragment.detail.ActivityListFragment;
import com.chase.timebank.fragment.detail.ActivityMyFragment;

/**
 * Created by chase on 2018/4/16.
 */

public class ActivityAdapter extends FragmentPagerAdapter {
    String[] _title;
    private ActivityListFragment activityListFragment;
    private ActivityMyFragment activityMyFragment;
    public ActivityAdapter(FragmentManager fm,String[] title) {
        super(fm);
        _title = title;
    }

    @Override
    public int getCount() {
        return _title.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return _title[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (activityListFragment == null) {
                    activityListFragment = new ActivityListFragment();
                }
                return activityListFragment;
            case 1:
                if (activityMyFragment == null) {
                    activityMyFragment = new ActivityMyFragment();
                }
                return activityMyFragment;

            default:
                return null;
        }
    }


}
