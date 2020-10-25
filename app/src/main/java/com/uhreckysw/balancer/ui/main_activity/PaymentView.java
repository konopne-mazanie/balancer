package com.uhreckysw.balancer.ui.main_activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.backend.DateCommon;
import com.uhreckysw.balancer.backend.db.Database;
import com.uhreckysw.balancer.backend.db.Filter;
import com.uhreckysw.balancer.databinding.PaymentViewBinding;
import com.uhreckysw.balancer.ui.dialog.AddPaymentDialog;
import com.uhreckysw.balancer.ui.dialog.ChangeCategoryDialog;
import com.uhreckysw.balancer.ui.dialog.DeleteItemDialog;
import com.uhreckysw.balancer.ui.dialog.EditPaymentDialog;
import com.uhreckysw.balancer.ui.dialog.FiltersDialog;
import com.uhreckysw.balancer.ui.interfaces.IUpdatable;
import com.uhreckysw.balancer.ui.interfaces.ItemClickListener;
import com.uhreckysw.balancer.ui.interfaces.LambdaVoidInt;
import com.uhreckysw.balancer.ui.scan_activity.ScanActivity;
import com.uhreckysw.balancer.ui.settings_activity.SettingsActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class PaymentView extends Fragment implements ItemClickListener, IUpdatable {


    private final int SCAN_ACTIVITY_REQ_CODE = 1;
    private final int SETTINGS_ACTIVITY_REQ_CODE = 2;

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
                PaymentView.this.nameFilter = findField.getText().toString();
                update();
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
        update(null);
    }
    public void update(AtomicBoolean finished) {
        if (view == null) return;
        new Thread(() -> {
            parentActivity.runOnUiThread(() -> view.setEnabled(false));

            if (filter.isDefault) filter = filtersDialog.defaultFilter(); //update default filter

            paymentListViewAdapter.mData = db.filterPayment(filter, findField.getText().toString()).stream().map((payment) -> new PaymentUIElem(payment, increaseSelectedCntFn)).collect(Collectors.toList());
            final double sum = paymentListViewAdapter.mData.stream().mapToDouble(p -> p.payment.price).reduce(0, Double::sum);
            binding.setLimitWarnOn(db.getAllLimits().stream().anyMatch(l -> l.spent > l.value));

            parentActivity.runOnUiThread(() -> {
                cancelUnpacked();
                sumCaption.setText("\u03A3 " + String.format("%.02f", sum));
                paymentListViewAdapter.notifyDataSetChanged();
                paymentListView.smoothScrollToPosition(0);
                view.setEnabled(true);
            });
            if (finished != null) finished.set(true);
        }).start();
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
        paymentListViewAdapter.mData.get(position).setChecked(true);
    }

    @Override
    public void onSettingsButtonClick(View view, int position) {
        editPaymentDialog.show();
        editPaymentDialog.setClickedItem(paymentListViewAdapter.mData.get(position));
    }

    @Override
    public void onCopyButtonClick(View view, int position) {
        addPaymentDialog.show();
        addPaymentDialog.setClickedItem(paymentListViewAdapter.mData.get(position));
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

    List<PaymentUIElem> getChecked() {
        return paymentListViewAdapter.mData.stream().filter(PaymentUIElem::getChecked).collect(Collectors.toList());
    }

    // ### OnButtonClick ###
    public void onSwitchLimis() {
        ((MainActivity) parentActivity).mPager.setCurrentItem(1, true);
    }

    public void onClearSearch() {
        findField.setText("");
        try {
            ((InputMethodManager) parentActivity.getSystemService(Activity.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(Optional.ofNullable(parentActivity.getCurrentFocus()).
                            orElse(new View(parentActivity)).getWindowToken(), 0);
        } catch (Exception e) {}
    }

    public void onAddItem() {
        addPaymentDialog.show();
    }

    public void onScanItem() {
        startActivityForResult(new Intent(parentActivity, ScanActivity.class), SCAN_ACTIVITY_REQ_CODE);
    }

    public void onSwitchSettings() {
        startActivityForResult(new Intent(parentActivity, SettingsActivity.class), SETTINGS_ACTIVITY_REQ_CODE);
    }

    public void onInvokeFilters() {
        if (db.isEmptyDb()) return;
        filtersDialog.show();
        if (filter != null) filtersDialog.setFilter(filter);
    }

    public void onChangeCategory() {
        List<PaymentUIElem> checked = getChecked();
        if (checked.isEmpty()) return;
        ChangeCategoryDialog changeCategoryDialog = new ChangeCategoryDialog(parentActivity, this, checked);
        changeCategoryDialog.show();
        if (checked.size() == 1) changeCategoryDialog.setCategory(checked.get(0).payment.category);
    }

    public void onDeleteItem() {
        List<PaymentUIElem> checked = getChecked();
        if (!checked.isEmpty()) new DeleteItemDialog(parentActivity, this, checked).show();
    }

    public void onCancelSelection() {
        cancelSelection();
    }

    public void onSelectAll() {
        for (PaymentUIElem elem : paymentListViewAdapter.mData) elem.setChecked(true);
    }

    // ###########################################################################################
    // Callbacks
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == SCAN_ACTIVITY_REQ_CODE) && (data != null)) {
            findField.setText(data.getStringExtra("receiptId"));
            AtomicBoolean updateFinished = new AtomicBoolean(false);
            update(updateFinished);
            new Thread(() -> {
                while (!updateFinished.get()) {}
                PaymentView.this.parentActivity.runOnUiThread(() -> {
                    switchSelection();
                    for (PaymentUIElem elem : paymentListViewAdapter.mData) elem.setChecked(true);
                    new ChangeCategoryDialog(parentActivity, this, paymentListViewAdapter.mData).show();
                });
            }).start();
        } else if (requestCode == SETTINGS_ACTIVITY_REQ_CODE) {
            update();
        }
    }

}
