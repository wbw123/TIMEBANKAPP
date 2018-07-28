package com.chase.timebank.adapter.tabAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.chase.timebank.fragment.detail.ServiceListFragment;
import com.chase.timebank.fragment.detail.RequestMyFragment;
import com.chase.timebank.fragment.detail.ServiceMyFragment;

/**
 * Created by chase on 2018/4/16.
 */

public class RequestAdapter extends FragmentPagerAdapter {
    String[] _title;
    private ServiceListFragment serviceListFragment;
    private RequestMyFragment requestMyFragment;
    private ServiceMyFragment serviceMyFragment;
    public RequestAdapter(FragmentManager fm, String[] title) {
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
                if (serviceListFragment == null) {
                    serviceListFragment = new ServiceListFragment();
                    Bundle bundle0 = new Bundle();
                    bundle0.putString("serviceListFragment",_title[0]);
                    serviceListFragment.setArguments(bundle0);
                }
                return serviceListFragment;
            case 1:
                if (requestMyFragment == null) {
                    requestMyFragment = new RequestMyFragment();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("requestMyFragment",_title[1]);
                    requestMyFragment.setArguments(bundle1);
                }
                return requestMyFragment;
            case 2:
                if (serviceMyFragment == null) {
                    serviceMyFragment = new ServiceMyFragment();
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("serviceMyFragment",_title[2]);
                    serviceMyFragment.setArguments(bundle2);
                }
                return serviceMyFragment;
            default:
                return null;
        }
    }


}
