package com.uhreckysw.balancer.ui.dialog;

import android.app.Activity;
import android.util.Pair;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;

import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.library.baseAdapters.BR;

import com.uhreckysw.balancer.R;
import com.uhreckysw.balancer.backend.DateCommon;
import com.uhreckysw.balancer.backend.db.Filter;
import com.uhreckysw.balancer.databinding.DialogFilterBinding;
import com.uhreckysw.balancer.ui.MyArrayAdapter;
import com.uhreckysw.balancer.ui.interfaces.IFiltersDialog;
import com.uhreckysw.balancer.ui.interfaces.IUpdatable;

import org.joda.time.DateTimeComparator;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static java.lang.Math.abs;

public final class FiltersDialog extends MyDialog implements IFiltersDialog {
    final protected SeekBar minSeek;
    final protected SeekBar maxSeek;

    private String dateMin;
    private String dateMax;
    private float priceMin;
    private float priceMax;
    private String category = "";

    private CategorySelectDialog categorySelectDialog;

    public void bind() {
        DialogFilterBinding binding = DataBindingUtil.inflate(parentActivity.getLayoutInflater(), R.layout.dialog_filter, null, false);
        binding.setViewmodel(this);
        dialogLayout = binding.getRoot();
    }

    public FiltersDialog(Activity parentActivity, IUpdatable parent) {
        super(parentActivity, parent);
        minSeek = dialogLayout.findViewById(R.id.value_price_min);
        maxSeek = dialogLayout.findViewById(R.id.value_price_max);
        categorySelectDialog = new CategorySelectDialog(parentActivity, parent, this);
    }

    public void setFilter(com.uhreckysw.balancer.backend.db.Filter f) {
        setCategory(f.category);
        setDateMin(DateCommon.dateFormatGUI.format(f.dateMin));
        setDateMax(DateCommon.dateFormatGUI.format(f.dateMax));
        setPriceMin(f.priceMin);
        setPriceMax(f.priceMax);
    }

    private void updatePriceBoundaries(Date from, Date to) {
        Pair<Float, Float> priceBoundaries = db.getPriceBoundaries(from, to, category);
        maxSeek.setMax((int) (priceBoundaries.second*100));
        minSeek.setMax((int) (priceBoundaries.second*100));
    }

    public Filter defaultFilter() {
        if (db.paymentsEmpty()) return new Filter().makeDefault();
        
        Pair<Date, Date> dateBoundaries;
        try {
            dateBoundaries = db.getDateBoundaries();
        } catch (ParseException e) {
            dateBoundaries = new Pair<>(new Date(), new Date());
        }
        Pair<Float, Float> priceBoundaries = db.getPriceBoundaries(dateBoundaries.first, dateBoundaries.second, "");
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -31);
       return new Filter()
                .setPriceMin(priceBoundaries.first)
                .setPriceMax(priceBoundaries.second)
                .setDateMin(calendar.getTime())
                .setDateMax(dateBoundaries.second)
                .makeDefault();
    }

    @Override
    void update() {
        Filter defaultFilter = defaultFilter();
        maxSeek.setMax((int) (defaultFilter.priceMax*100));
        minSeek.setMax((int) (defaultFilter.priceMax*100));
        setFilter(defaultFilter);
    }

    public void onCancel() {
        dialog.cancel();
    }

    public void onConfirm() {
        Filter f = new Filter()
                .setPriceMin(priceMin)
                .setPriceMax(priceMax)
                .setDateMin(DateCommon.parseDateGUI(getDateMin()))
                .setDateMax(DateCommon.parseDateGUI(getDateMax()))
                .setCategory(getCategory());
        Filter defaultFilter = defaultFilter();
        if (        defaultFilter.category.equals(f.category)
                && (DateTimeComparator.getDateOnlyInstance().compare(defaultFilter.dateMin, f.dateMin) == 0)
                && (DateTimeComparator.getDateOnlyInstance().compare(defaultFilter.dateMax, f.dateMax) == 0)
                && (abs(defaultFilter.priceMin - f.priceMin) < 0.5)
                && (abs(defaultFilter.priceMax - f.priceMax) < 0.5)) parent.setFilter(defaultFilter);
        else parent.setFilter(f);
        parent.update();
        dialog.cancel();
    }

    public void onClear() {
        update();
    }

    public void onFromDatePick() {
        try {
            DateCommon.pickDate(DateCommon.dateFormatGUI.parse(getDateMin()), (String s) -> setDateMin(s), parentActivity);
        } catch (Exception e) {}
    }
    public void onToDatePick() {
        try {
            DateCommon.pickDate(DateCommon.dateFormatGUI.parse(getDateMax()), (String s) -> setDateMax(s), parentActivity);
        } catch (Exception e) {}
    }

    public void onSelectCategory() {
        categorySelectDialog.show();
    }

    // #########################################################################################
    public String intToStringConverter(int i) {
        return String.format("%.02f", ((float) i)/100);
    }
    // #########################################################################################
    @Bindable
    public String getDateMin() {
        return dateMin;
    }

    public void setDateMin(String value) {
        this.dateMin = value;
        notifyPropertyChanged(BR.dateMin);
        if (dateMax != null) {
            try {
                updatePriceBoundaries(DateCommon.parseDateGUI(dateMin), DateCommon.parseDateGUI(dateMax));
            } catch (Exception e) {
            }
        }
    }
    @Bindable
    public String getDateMax() {
        return dateMax;
    }

    public void setDateMax(String dateMax) {
        this.dateMax = dateMax;
        notifyPropertyChanged(BR.dateMax);
        if (dateMin != null) {
            try {
                updatePriceBoundaries(DateCommon.parseDateGUI(dateMin), DateCommon.parseDateGUI(dateMax));
            } catch (Exception e) {
            } //in case of wrong date format
        }
    }

    @Bindable
    public int getPriceMin() {
        return (int) (priceMin*100);
    }

    public void setPriceMin(int priceMin) {
        this.priceMin = ((float) priceMin)/100;
        notifyPropertyChanged(BR.priceMin);
    }
    private void setPriceMin(float priceMin) {
        this.priceMin = priceMin;
        notifyPropertyChanged(BR.priceMin);
    }
    @Bindable
    public int getPriceMax() {
        return (int) (priceMax*100);
    }

    public void setPriceMax(int priceMax) {
        this.priceMax = ((float) priceMax)/100;
        notifyPropertyChanged(BR.priceMax);
    }
    private void setPriceMax(float priceMax) {
        this.priceMax = priceMax;
        notifyPropertyChanged(BR.priceMax);
    }

    @Bindable
    public String getCategory() {
        return category;
    }

    public void setCategory(String value) {
        category = value;
        notifyPropertyChanged(BR.category);
        if ((dateMax != null) && (dateMin != null)) {
            try {
                updatePriceBoundaries(DateCommon.parseDateGUI(dateMin), DateCommon.parseDateGUI(dateMax));
            } catch (Exception e) {
            }
        }
    }
}
