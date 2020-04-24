package com.uhreckysw.balancer.ui.main_activity;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.uhreckysw.balancer.Balancer;
import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.backend.db.Limit;

public class LimitUIElem extends BaseObservable {
    final public Limit limit;

    private final String name;
    private final String sum;
    private final String bottom_caption;
    private final int proggress;

    public LimitUIElem(Limit limit) {
        this.limit = limit;
        this.name = limit.name;
        this.sum = String.format("%.02f", limit.value - limit.spent);
        this.bottom_caption = limit.days + " " + Balancer.getContext().getResources().getString(R.string.days);
        this.proggress = (int)((limit.spent/limit.value) * 100);
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
    public int getProggress() {return proggress;}

}
