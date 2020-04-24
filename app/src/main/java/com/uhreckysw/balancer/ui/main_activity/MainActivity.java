package com.uhreckysw.balancer.ui.main_activity;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.databinding.ActivityMainBinding;

public class MainActivity extends FragmentActivity {
    public ViewPager2 mPager;

    public PaymentView paymentView = new PaymentView();
    public LimitsView limitsView = new LimitsView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ActivityMainBinding) (DataBindingUtil.setContentView(this, R.layout.activity_main))).setViewmodel(this);

        mPager = findViewById(R.id.pager);
        mPager.setAdapter(new ScreenSlidePagerAdapter(this));
        mPager.setUserInputEnabled(false);
        mPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                new Thread(() -> {
                    try {Thread.sleep(320);}
                    catch (Exception ignored) {}

                    if (position == 0) {
                        if (paymentView.selectionMode) paymentView.cancelSelection();
                        paymentView.update();
                    } else limitsView.update();
                }).start();
            }
        });
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            if (position == 0) return paymentView;
            else return limitsView;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() != 0) mPager.setCurrentItem(0, true);
        else if (paymentView.selectionMode) paymentView.cancelSelection();
        else super.onBackPressed();
    }
}
