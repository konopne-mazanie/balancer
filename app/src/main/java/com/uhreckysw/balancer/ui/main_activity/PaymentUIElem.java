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
            int[] fieldSize = getReceiptTableFieldSize(payment.receipt.items);
            String tableFormat = "  %-" + fieldSize[1] + "d" + "  %-" + fieldSize[2] + ".02f\n";
            payment.receipt.items.forEach((item) -> description.append(receiptTableFitString(item.name.trim().replaceAll(" +", " "), fieldSize[0]))
                                                                .append(String.format(tableFormat, item.quantity, item.price)));
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

    private static int[] getReceiptTableFieldSize(ArrayList<Receipt.Item> items) {
        int max_name_length, max_quantity_length, max_price_length;
        max_name_length = max_quantity_length = max_price_length = 0;

        for (Receipt.Item item : items) {
            max_name_length = Math.max(max_name_length, item.name.length());
            max_quantity_length = Math.max(max_quantity_length, Tools.int_length(item.quantity));
            max_price_length = Math.max(max_price_length, Tools.int_length((int) item.price));
        }

        return (new int[]{
                (int) ((max_name_length + max_price_length + max_quantity_length + 4)/1.8),
                max_quantity_length,
                (max_price_length + 5)
        });
    }

    private static String receiptTableFitString(String input, int maxlenght) {
        if (input.length() > maxlenght) {
            int splitpos = input.substring(0, maxlenght).lastIndexOf(' ');
            if (splitpos < 1) splitpos = maxlenght;
            return (receiptTableFitString(input.substring(0, splitpos), maxlenght)
                    + "\n"
                    + receiptTableFitString(input.substring(splitpos), maxlenght));
        }
        return String.format("%-" + maxlenght + "s", input);
    }
}
