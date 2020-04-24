package com.uhreckysw.balancer.ui.dialog;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.uhreckysw.balancer.BR;

public class CategorySelectDialogUiElem extends BaseObservable {
    private final String name;
    private boolean checked;

    public CategorySelectDialogUiElem(String name) {
       this.name = name;
    }

    @Bindable
    public String getName() {
        return name;
    }

    @Bindable
    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        notifyPropertyChanged(BR.checked);
    }

}
