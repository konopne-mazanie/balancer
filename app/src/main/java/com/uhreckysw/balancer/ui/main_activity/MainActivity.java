package com.uhreckysw.balancer.ui.main_activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.backend.DateCommon;
import com.uhreckysw.balancer.backend.db.Database;
import com.uhreckysw.balancer.backend.db.Filter;
import com.uhreckysw.balancer.backend.db.Payment;
import com.uhreckysw.balancer.databinding.ActivityMainBinding;
import com.uhreckysw.balancer.ui.dialog.AddPaymentDialog;
import com.uhreckysw.balancer.ui.dialog.ChangeCategoryDialog;
import com.uhreckysw.balancer.ui.dialog.DeleteItemDialog;
import com.uhreckysw.balancer.ui.dialog.EditPaymentDialog;
import com.uhreckysw.balancer.ui.dialog.FiltersDialog;
import com.uhreckysw.balancer.ui.interfaces.IUpdatable;
import com.uhreckysw.balancer.ui.interfaces.ItemClickListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private ViewPager mPager;
    private PagerAdapter pagerAdapter;

    PaymentView paymentView = new PaymentView();
    LimitsView limitsView = new LimitsView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setViewmodel(this);

        mPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            public void onPageSelected(int position) {
                new Thread(() -> {
                    try {
                        Thread.sleep(320);
                    } catch (Exception e) {}
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (position == 0) {
                                if (paymentView.selectionMode) paymentView.cancelSelection();
                                paymentView.update();
                            }
                        }
                    });
                }).start();
            }
        });
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) return paymentView;
            else return limitsView;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
