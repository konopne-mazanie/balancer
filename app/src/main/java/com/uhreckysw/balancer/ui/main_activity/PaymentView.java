package com.uhreckysw.balancer.ui.main_activity;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.backend.DateCommon;
import com.uhreckysw.balancer.backend.db.Database;
import com.uhreckysw.balancer.backend.db.Filter;
import com.uhreckysw.balancer.backend.db.Payment;
import com.uhreckysw.balancer.databinding.PaymentViewBinding;
import com.uhreckysw.balancer.ui.dialog.AddPaymentDialog;
import com.uhreckysw.balancer.ui.dialog.ChangeCategoryDialog;
import com.uhreckysw.balancer.ui.dialog.DeleteItemDialog;
import com.uhreckysw.balancer.ui.dialog.EditPaymentDialog;
import com.uhreckysw.balancer.ui.dialog.FiltersDialog;
import com.uhreckysw.balancer.ui.interfaces.IUpdatable;
import com.uhreckysw.balancer.ui.interfaces.ItemClickListener;
import com.uhreckysw.balancer.ui.interfaces.LambdaVoidInt;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

public class PaymentView extends Fragment implements ItemClickListener, IUpdatable {


    public static PaymentView newInstance() {
        return new PaymentView();
    }

    Database db;
    String nameFilter = "";
    Filter filter;
    public boolean selectionMode = false;
    LambdaVoidInt increaseSelectedCntFn;

    PaymentListViewAdapter paymentListViewAdapter;
    RecyclerView paymentListView;
    TextView sumCaption;
    EditText findField;

    AddPaymentDialog addPaymentDialog;
    FiltersDialog filtersDialog;
    EditPaymentDialog editPaymentDialog;
    private PaymentViewBinding binding;
    Activity parentActivity;
    View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        parentActivity = getActivity();
        binding = DataBindingUtil.inflate(
                inflater, R.layout.payment_view, container, false);
        binding.setViewmodel(this);
        view = binding.getRoot();
        increaseSelectedCntFn = (int num) -> binding.setSelectedCnt(binding.getSelectedCnt() + num);

        db = Database.getInstance();

        //create big dialogs in advance
        addPaymentDialog = new AddPaymentDialog(parentActivity, this);
        editPaymentDialog = new EditPaymentDialog(parentActivity, this);
        filtersDialog = new FiltersDialog(parentActivity, this);

        // set up the RecyclerView
        paymentListView = view.findViewById(R.id.item_list);
        LinearLayoutManager layout_manager = new LinearLayoutManager(parentActivity);
        paymentListView.setLayoutManager(layout_manager);
        ArrayList<PaymentUIElem> payments = new ArrayList<>();
        paymentListViewAdapter = new PaymentListViewAdapter(parentActivity, payments);
        paymentListViewAdapter.setClickListener(this);
        paymentListView.setAdapter(paymentListViewAdapter);

        sumCaption = view.findViewById(R.id.prices_sum_caption);
        PaymentView thisView = this;
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!Pattern.compile("^[\\w|\\s]*$").matcher(findField.getText()).find())
                    findField.setError(parentActivity.getString(R.string.illegal_chars), null);
                else {
                    thisView.nameFilter = findField.getText().toString();
                    update();
                }
                if (findField.getText().toString().isEmpty()) binding.setIsSearching(false);
                else binding.setIsSearching(true);
            }
        };
        findField = view.findViewById(R.id.find_item_field);
        findField.addTextChangedListener(afterTextChangedListener);
        filter = filtersDialog.defaultFilter();
        binding.setSelectedCnt(0);
        update();
        return view;
    }

    @Override
    public void update() {
        view.setEnabled(false);
        if (filter.isDefault) filter = filtersDialog.defaultFilter(); //update default filter
        cancelUnpacked();
        paymentListViewAdapter.clear();

        for (Payment payment : db.filterPayment(filter, findField.getText().toString())) paymentListViewAdapter.add(new PaymentUIElem(payment, increaseSelectedCntFn));

        paymentListViewAdapter.notifyDataSetChanged();

        float sum = 0;
        for (int i = 0; i < paymentListViewAdapter.getItemCount(); i++) sum+= paymentListViewAdapter.getItem(i).payment.price;
        sumCaption.setText("\u03A3 " + String.format("%.02f", sum));
        paymentListView.smoothScrollToPosition(0);
        view.setEnabled(true);
    }

    @Override
    public void setFilter(Filter f) {
        filter = f;
    }

    // ###########################################################################################

    @Override
    public void onItemClick(View view, int position) {
        PaymentUIElem elem = paymentListViewAdapter.mData.get(position);
        if (selectionMode) elem.setChecked(!elem.getChecked());
        else elem.setUnpacked(!elem.getUnpacked());
    }

    @Override
    public void onItemLongClick(View view, int position) {
        if (selectionMode) return;
        switchSelection();
        PaymentUIElem elem = paymentListViewAdapter.mData.get(position);
        elem.setChecked(true);
    }

    @Override
    public void onSettingsButtonClick(View view, int position) {
        editPaymentDialog.setClickedItem(paymentListViewAdapter.getItem(position));
        editPaymentDialog.show();
    }

    @Override
    public void onCopyButtonClick(View view, int position) {
        addPaymentDialog.show();
        addPaymentDialog.setClickedItem(paymentListViewAdapter.getItem(position));
        addPaymentDialog.setItemDateFieldText(DateCommon.dateFormatGUI.format(new Date()));
    }

    // ###########################################################################################

    void switchSelection() {
        cancelUnpacked();
        selectionMode = !selectionMode;
        binding.setSelectionMode(selectionMode);
        for (PaymentUIElem elem : paymentListViewAdapter.mData) elem.setSelection_mode(selectionMode);
    }

    public void cancelSelection() {
        for (PaymentUIElem elem : paymentListViewAdapter.mData) elem.setChecked(false);
        switchSelection();
    }

    public void cancelUnpacked() {
        for (PaymentUIElem elem : paymentListViewAdapter.mData) elem.setUnpacked(false);
    }

    ArrayList<PaymentUIElem> getChecked() {
        ArrayList<PaymentUIElem> ret = new ArrayList<>();
        for (PaymentUIElem elem : paymentListViewAdapter.mData) {
            if (elem.getChecked()) ret.add(elem);
        }
        return ret;
    }

    // ### OnButtonClick ###
    public void onClearSearch() {
        findField.setText("");
        findField.setError(null);
    }

    public void onAddItem() {
        addPaymentDialog.show();
    }

    public void onInvokeFilters() {
        if (db.isEmptyDb()) return;
        filtersDialog.show();
        if (filter != null) filtersDialog.setFilter(filter);
    }

    public void onChangeCategory() {
        ArrayList<PaymentUIElem> checked = getChecked();
        if (checked.isEmpty()) return;
        ChangeCategoryDialog changeCategoryDialog = new ChangeCategoryDialog(parentActivity, this, checked);
        changeCategoryDialog.show();
    }

    public void onDeleteItem() {
        ArrayList<PaymentUIElem> checked = getChecked();
        if (checked.isEmpty()) return;
        DeleteItemDialog deleteItemDialog = new DeleteItemDialog(parentActivity, this, checked);
        deleteItemDialog.show();
    }

    public void onCancelSelection() {
        cancelSelection();
    }

    public void onSelectAll() {
        for (PaymentUIElem elem : paymentListViewAdapter.mData) elem.setChecked(true);
    }

}
