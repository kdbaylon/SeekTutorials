package com.example.seektutorials.ui.tuteeHome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;


import com.example.seektutorials.R;
import com.example.seektutorials.ui.tuteeHome.bookings.PagerAdapter;
import com.google.android.material.tabs.TabLayout;


/**
 * Tutee bookings fragment
 */
public class TuteeBookingsFragment extends Fragment{
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tutee_bookings, null);
        TabLayout tabLayout = (TabLayout)rootView.findViewById(R.id.tabs);
        final ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        final PagerAdapter adapter = new PagerAdapter
                (getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        return rootView;
    }
}

