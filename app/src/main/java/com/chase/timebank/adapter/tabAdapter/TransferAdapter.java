package com.chase.timebank.adapter.tabAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.chase.timebank.fragment.detail.TransferGatherFragment;
import com.chase.timebank.fragment.detail.TransferRemitFragment;

/**
 * Created by chase on 2018/4/16.
 */

public class TransferAdapter extends FragmentPagerAdapter {
    String[] _title;
    private TransferGatherFragment transferGatherFragment;
    private TransferRemitFragment transferRemitFragment;
    public TransferAdapter(FragmentManager fm, String[] title) {
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
                if (transferGatherFragment == null) {
                    transferGatherFragment = new TransferGatherFragment();
                    Bundle bundle0 = new Bundle();
                    bundle0.putString("transferGatherFragment",_title[0]);
                    transferGatherFragment.setArguments(bundle0);
                }
                return transferGatherFragment;
            case 1:
                if (transferRemitFragment == null) {
                    transferRemitFragment = new TransferRemitFragment();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("transferRemitFragment",_title[1]);
                    transferRemitFragment.setArguments(bundle1);
                }
                return transferRemitFragment;

            default:
                return null;
        }
    }


}
