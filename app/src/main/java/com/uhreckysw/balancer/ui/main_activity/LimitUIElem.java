package com.uhreckysw.balancer.ui.main_activity;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.uhreckysw.balancer.backend.DateCommon;
import com.uhreckysw.balancer.backend.db.Limit;
import com.uhreckysw.balancer.backend.db.Payment;
import com.uhreckysw.balancer.ui.interfaces.LambdaVoidInt;

public class LimitUIElem extends BaseObservable {
    final public Limit limit;

    private final String name;
    private final String sum;
    private final String bottom_caption;

    private boolean unpacked;

    public LimitUIElem(Limit limit) {
        this.limit = limit;
        this.name = limit.name;
        this.sum = String.format("%.02f", 0.00) + "/" + String.format("%.02f", limit.value);
        this.bottom_caption = limit.days + " DAYS | " + limit.category;
    }

    @Bindable
    public String getName() {
        return name;
    }

    @Bindable
    public String getSum() {
        return sum;
    }

    @Bindable
    public String getBottom_caption() {
        return bottom_caption;
    }

    @Bindable
    public boolean getUnpacked() {
        return unpacked;
    }

    public void setUnpacked(boolean unpacked) {
        this.unpacked = unpacked;
        notifyPropertyChanged(BR.unpacked);
    }

}
