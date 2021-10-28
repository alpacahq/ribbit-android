package com.stockly.android.models;

import androidx.room.Entity;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * A class represent data of country's state.
 */
@Entity
public class State {
    public long id;
    public String name;
    public String short_code;

    @Override
    public String toString() {
        return name;
    }
}
