package com.stockly.android.listners;

/**
 * Data change callback listener to listen to changes like added/removed
 * and return its updates.
 *
 * @param <T> generic data type.
 */
public interface DataListener<T> {
    void onDataListener(T t);
}
