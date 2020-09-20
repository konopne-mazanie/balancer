package com.uhreckysw.balancer.ui.main_activity;

import android.webkit.WebView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;

import com.uhreckysw.balancer.BR;
import com.uhreckysw.balancer.backend.DateCommon;
import com.uhreckysw.balancer.backend.db.Payment;
import com.uhreckysw.balancer.ui.interfaces.LambdaVoidInt;

public class PaymentUIElem extends BaseObservable {

    private static final String description_web_pre_html =
            "<html>" +
            "<body style=\"margin: 0px; padding: 0px; background-color: transparent;\">" +
            "<table style=\"margin-left: -10px; padding: 0px; border-spacing: 10px 0;" +
                    "color: rgba(0, 0, 0, 0.6); font-family: 'Roboto'; font-size: 0.97em;\"" +
                    " cellspacing=\"0\" cellpadding=\"0\">";

    private final String item;
    private final String price;
    private final String description;
    private final String description_web;
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
        this.description = payment.description;

        if (payment.receipt != null) {
            StringBuilder description_web = new StringBuilder(description_web_pre_html);
            payment.receipt.items.forEach((item) ->
                description_web.append(String.format("<tr><td>%s<td>%s<td>%s", item.name, item.quantity, item.price))
            );
            this.description_web = description_web.append("</table></body></html>").toString();
        } else description_web = "";
    }

    public PaymentUIElem(Payment payment, LambdaVoidInt fn) {
        this(payment);
        increaseSelectedCntFn = fn;
    }

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

    @Bindable
    public String getDescription_web() {
        return description_web;
    }
    @BindingAdapter({ "app:loadUrl" })
    public static void description_web_adapter(WebView view, String data) {
        view.loadData(data, "text/html; charset=utf-8", "UTF-8");
        view.setBackgroundColor(0x00000000); // transparent bg hack
        view.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
    }

}
