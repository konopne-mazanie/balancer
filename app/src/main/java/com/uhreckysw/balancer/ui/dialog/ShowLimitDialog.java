package com.uhreckysw.balancer.ui.dialog;

import android.app.Activity;
import android.util.Pair;

import androidx.databinding.DataBindingUtil;

import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.backend.db.Filter;
import com.uhreckysw.balancer.databinding.DialogShowLimitBinding;
import com.uhreckysw.balancer.ui.interfaces.IUpdatable;
import com.uhreckysw.balancer.ui.main_activity.LimitUIElem;
import com.uhreckysw.balancer.ui.main_activity.MainActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public final class ShowLimitDialog extends MyDialog {
    public LimitUIElem item;
    DialogShowLimitBinding binding;

    public void bind() {
        binding = DataBindingUtil.inflate(parentActivity.getLayoutInflater(), R.layout.dialog_show_limit, null, false);
        binding.setViewmodel(this);
        dialogLayout = binding.getRoot();
    }

    public ShowLimitDialog(Activity parentActivity, IUpdatable parent) {
        super(parentActivity, parent);
    }

    public void onConfirm() {
        Date currrentDate = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(currrentDate);
        calendar.add(Calendar.DATE, -1 * (item.limit.days - 1));
        Pair<Float, Float> priceBoundaries = db.getPriceBoundaries(calendar.getTime(), currrentDate, item.limit.category);
        ((MainActivity) parentActivity).paymentView.setFilter(
                new Filter()
                .setPriceMin(priceBoundaries.first)
                .setPriceMax(priceBoundaries.second)
                .setDateMin(calendar.getTime())
                .setDateMax(currrentDate)
                .setCategory(item.limit.category)
        );
        dialog.cancel();
        parentActivity.onBackPressed();
    }

    public void setClickedItem(LimitUIElem item) {
        this.item = item;
    }

    @Override
    public void update() {
        binding.setViewmodel(binding.getViewmodel());
    }

}
