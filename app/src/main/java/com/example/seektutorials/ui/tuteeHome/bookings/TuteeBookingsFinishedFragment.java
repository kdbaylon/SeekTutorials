package com.example.seektutorials.ui.tuteeHome.bookings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.seektutorials.R;


/**
 * Tutee bookings finished fragment
 */
public class TuteeBookingsFinishedFragment extends Fragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tutee_bookings_finished, null);

    }
}

