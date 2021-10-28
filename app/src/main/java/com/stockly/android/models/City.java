package com.stockly.android.models;

import androidx.room.Entity;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * City a class represents cities required by user kyc.
 */

@Entity
public class City {
    public long id;
    public String name;
    public String ascii;
    public String lat;
    public String lng;

    @Override
    public String toString() {
        return name;
    }
}
