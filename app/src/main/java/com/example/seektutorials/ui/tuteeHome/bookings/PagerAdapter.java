package com.example.seektutorials.ui.tuteeHome.bookings;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TuteeBookingsPendingFragment();
            case 1:
                return new TuteeBookingsAcceptedFragment();
            case 2:
                return new TuteeBookingsFinishedFragment();
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return 3;
    }
}
