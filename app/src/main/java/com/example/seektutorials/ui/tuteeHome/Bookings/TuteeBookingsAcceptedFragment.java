package com.example.seektutorials.ui.tuteeHome.Bookings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.seektutorials.R;


/**
 * Tutee bookings accepted fragment
 */
public class TuteeBookingsAcceptedFragment extends Fragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tutee_bookings_accepted, null);

    }
}

