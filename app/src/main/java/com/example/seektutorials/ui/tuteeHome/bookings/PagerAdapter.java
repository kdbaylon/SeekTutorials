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
                TuteeBookingsPendingFragment tab1 = new TuteeBookingsPendingFragment();
                return tab1;
            case 1:
                TuteeBookingsAcceptedFragment tab2 = new TuteeBookingsAcceptedFragment();
                return tab2;
            case 2:
                TuteeBookingsFinishedFragment tab3 = new TuteeBookingsFinishedFragment();
                return tab3;
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
