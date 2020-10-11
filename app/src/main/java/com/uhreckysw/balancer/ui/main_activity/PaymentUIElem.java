package com.uhreckysw.balancer.ui.main_activity;


import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.uhreckysw.balancer.BR;
import com.uhreckysw.balancer.backend.DateCommon;
import com.uhreckysw.balancer.backend.Tools;
import com.uhreckysw.balancer.backend.db.Payment;
import com.uhreckysw.balancer.backend.db.Receipt;
import com.uhreckysw.balancer.ui.interfaces.LambdaVoidInt;

import java.util.ArrayList;

public class PaymentUIElem extends BaseObservable {

    private final String item;
    private final String price;
    private final String description;
    private final String bottom_caption;

    private boolean checked;
    private boolean unpacked;
    private boolean selection_mode;

    private LambdaVoidInt increaseSelectedCntFn;

    final public Payment payment;

    public PaymentUIElem(Payment payment) {
        this.payment = payment;
        this.item = payment.item;
        this.price = String.format("%.02f", payment.price);
        this.bottom_caption = DateCommon.dateFormatGUI.format(payment.date_of_buy) + "  |  " + payment.category;

        StringBuilder description = new StringBuilder(payment.description.isEmpty() ? "" : (payment.description + "\n\n"));
        if (payment.receipt != null) {
            String tableFormat = getReceiptTableFormat(payment.receipt.items);
            payment.receipt.items.forEach((item) -> description.append(String.format(tableFormat, item.name, item.quantity, item.price)));
            description.append("\n").append(payment.receipt.id);
        }
        this.description = description.toString();
    }

    public PaymentUIElem(Payment payment, LambdaVoidInt fn) {
        this(payment);
        increaseSelectedCntFn = fn;
    }

    // ############################################################################################

    @Bindable
    public String getItem() {
        return item;
    }

    @Bindable
    public String getPrice() {
        return price;
    }

    @Bindable
    public String getDescription() {
        return description;
    }

    @Bindable
    public String getBottom_caption() {
        return bottom_caption;
    }

    @Bindable
    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        if (this.checked == checked) return;
        this.checked = checked;
        if (increaseSelectedCntFn != null) increaseSelectedCntFn.fun(checked ? 1 : -1);
        notifyPropertyChanged(BR.checked);
    }

    @Bindable
    public boolean getUnpacked() {
        return unpacked;
    }

    public void setUnpacked(boolean unpacked) {
        this.unpacked = unpacked;
        notifyPropertyChanged(BR.unpacked);
    }

    @Bindable
    public boolean getSelection_mode() {
        return selection_mode;
    }

    public void setSelection_mode(boolean selection_mode) {
        this.selection_mode = selection_mode;
        notifyPropertyChanged(BR.selection_mode);
    }

    // ############################################################################################

    String getReceiptTableFormat(ArrayList<Receipt.Item> items) {
        int max_name_length, max_quantity_length, max_price_length;
        max_name_length = max_quantity_length = max_price_length = 0;

        for (Receipt.Item item : payment.receipt.items) {
            if (item.name.length() > max_name_length) max_name_length = item.name.length();
            int quantity_length = Tools.int_length(item.quantity);
            if (quantity_length > max_quantity_length) max_quantity_length = quantity_length;
            int price_length = Tools.int_length((int) item.price);
            if (price_length > max_price_length) max_price_length = price_length;
        }
        return("%-" + max_name_length + "s" + "  %-" + max_quantity_length + "d" + "  %-" + max_price_length + ".02f\n");
    }
}
