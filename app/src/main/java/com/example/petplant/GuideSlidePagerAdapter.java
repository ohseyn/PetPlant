package com.example.petplant;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class GuideSlidePagerAdapter extends FragmentStateAdapter {

    public GuideSlidePagerAdapter(FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return GuideSlidePageFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return 9; // 총 9개의 페이지
    }
}
