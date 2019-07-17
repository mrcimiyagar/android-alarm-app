package kasper_external_apps.android.alarmer.front.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import kasper_external_apps.android.alarmer.front.fragments.base.AlarmFragment;

public class PageAdapter extends FragmentStatePagerAdapter {

    private AlarmFragment[] fragments;

    public PageAdapter(FragmentManager fm, AlarmFragment[] fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {

        return fragments[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {

        if (position == 0) {
            return "در انتظار";
        }
        else if (position == 1) {
            return "انجام شده";
        }
        else {
            return "نشان دار";
        }
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}