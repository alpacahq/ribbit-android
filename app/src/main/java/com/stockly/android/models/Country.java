package com.stockly.android.models;

import androidx.room.Entity;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * Country a class represents country/citizenship of kyc user
 * its attribute use to represent countries list.
 */
@Entity
public class Country {
    public long id;
    public String name;
    public String short_code;

    @Override
    public String toString() {
        return name;
    }
}
