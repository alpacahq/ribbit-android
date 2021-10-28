package com.stockly.android.listners;

/**
 * Item click Listener callback to listen to click events in adapters
 * like moving next page and return its object and position.
 *
 * @param <T> generic data type.
 */
public interface ItemClickListener<T> {
    void onItemClick(T t, int position);
}
