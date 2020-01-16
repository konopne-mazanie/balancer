package com.uhreckysw.balancer.ui.interfaces;

import android.view.View;

public interface ItemClickListener {
    void onItemClick(View view, int position);
    void onItemLongClick(View view, int position);
    void onSettingsButtonClick(View view, int position);
    void onCopyButtonClick(View view, int position);
}
