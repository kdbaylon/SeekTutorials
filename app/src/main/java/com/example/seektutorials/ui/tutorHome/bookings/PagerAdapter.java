package com.example.seektutorials.ui.tutorHome.bookings;

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
                TutorBookingsPendingFragment tab1 = new TutorBookingsPendingFragment();
                return tab1;
            case 1:
                TutorBookingsAcceptedFragment tab2 = new TutorBookingsAcceptedFragment();
                return tab2;
            case 2:
                TutorBookingsFinishedFragment tab3 = new TutorBookingsFinishedFragment();
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
