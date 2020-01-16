package com.uhreckysw.balancer.ui.interfaces;

import com.uhreckysw.balancer.backend.db.Filter;

public interface IUpdatable {
    void update();
    void cancelSelection();
    void setFilter(Filter f);
}
