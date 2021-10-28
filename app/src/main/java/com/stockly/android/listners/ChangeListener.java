package com.stockly.android.listners;

import org.jetbrains.annotations.NotNull;

import io.reactivex.disposables.Disposable;

/**
 * Checked change callback listener to listen to changes favourite/un-favourite
 * and return some data for updates.
 *
 * @param <T>
 */
public interface ChangeListener<T> {
    void checkedChangeListener(T t, int pos, boolean isChecked);
}
