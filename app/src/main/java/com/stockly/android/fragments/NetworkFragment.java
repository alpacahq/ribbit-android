package com.stockly.android.fragments;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.stockly.android.AppController;
import com.stockly.android.BuildConfig;
import com.stockly.android.R;
import com.stockly.android.apis.ApiServices;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.models.User;
import com.stockly.android.utils.ActivityUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.TimeoutException;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.HttpUrl;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * NetworkFragment
 * It represents Api calls over network using retrofit and fragment lifecycle, ApiService interface
 * It check fragment layout Id and initiate retrofit call when ready.
 * It handle Error and success cases and observable over network call.
 */
public abstract class NetworkFragment extends BaseFragment {
    private static final String TAG = ">>>>NetworkFragment";
    private final HashSet<Disposable> mDisposables = new HashSet<>();
    private boolean isAttached;
    private final SparseArray<CallBack<?>.PendingRunnable> mPendingCalls = new SparseArray<>();
    private View mProgressView;
    private int mRequestCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isAttached = true;
        return inflater.inflate(getLayoutId(), container, false);
    }

    /**
     * It returns layout Id of fragment attached.
     *
     * @return
     */
    @LayoutRes
    protected abstract int getLayoutId();

    @Override
    public void onResume() {
        super.onResume();
        if (mPendingCalls.size() > 0) {
            for (int i = 0; i < mPendingCalls.size(); i++) {
                mPendingCalls.get(mPendingCalls.keyAt(i)).run();
            }
            mPendingCalls.clear();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
        isAttached = false;
    }

    @Override
    public void onDestroy() {
        for (Disposable d : mDisposables) {
            if (d.isDisposed()) {
                d.dispose();
            }
        }
        mDisposables.clear();

        super.onDestroy();
    }

    boolean isRequestCountReset() {
        return mRequestCount <= 0;
    }

    public void setProgressView(@NonNull View progressView) {
        mProgressView = progressView;
    }

    /**
     * @return api service.
     */
    public ApiServices getApi() {
        ApiServices apiServices = AppController.getInstance(this).getApiServices();
        Log.d(TAG, "getApi() and Null? =" + (apiServices == null));
        return apiServices;
    }

    /**
     * Initiate Api call using Rxjava Observable.
     *
     * @param observable Observable Rxjava
     * @param callBack   Provide mechanism of success error of observer
     * @param <T>        parameter.
     * @see <a href="http://reactivex.io/documentation/observable.html">ReactiveX documentation: Observable</a>
     */
    public <T> void enqueue(Observable<T> observable, CallBack<T> callBack) {
        Log.d(TAG, "enqueue ()");
        if (!isAttached) {
            Log.e(TAG, "View Not created yet");
            return;
        }
        if (mRequestCount < 0) {
            mRequestCount = 0;
        }
        mRequestCount++;
        showHideProgress();
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(callBack);
    }


    private void showHideProgress() {
        if (mProgressView != null) {
            mProgressView.setVisibility(mRequestCount == 0 ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * It Provides mechanism of Observer with success or error cases
     * with overriding functions.
     *
     * @param <T> type of instance.
     */
    public abstract class CallBack<T> implements Observer<T> {

        boolean showProgressBar = true;

        @Override
        public void onSubscribe(@NonNull Disposable d) {
            mDisposables.add(d);
        }

        @Override
        public void onNext(@NonNull T t) {
            if (showProgressBar) {
                mRequestCount--;
                showHideProgress();
            }
            if (isAttached) {
                Log.d(TAG, "onNext: " + isAttached);
                onSuccess(t);
            } else {
                mPendingCalls.put(this.hashCode(), new PendingRunnable(t, null, false));
            }
        }

        @Override
        public final void onError(@NonNull Throwable throwable) {
            if (BuildConfig.DEBUG) {
                throwable.printStackTrace();
            }
            mRequestCount--;
            showHideProgress();
            boolean isInternetIssue = throwable instanceof TimeoutException || throwable instanceof IOException;
            if (isResumed() && isAdded()) {
                handleError(throwable, isInternetIssue);
            } else {
                mPendingCalls.append(this.hashCode(), new PendingRunnable(null, throwable, isInternetIssue));
            }
        }

        /**
         * Handle error thrown in Http request
         *
         * @param throwable       error/exception.
         * @param isInternetIssue tag to check internet issue.
         */
        private void handleError(Throwable throwable, boolean isInternetIssue) {
            throwable.printStackTrace();
            if (throwable instanceof HttpException) {
                HttpException exception = (HttpException) throwable;
                Response<?> response = exception.response();
                if (response == null) {
                    if (isInternetIssue) {
                        onError(new RetrofitError(getString(R.string.error_internet), exception.code()), isInternetIssue);
                        return;
                    }
                    onError(new RetrofitError(exception.message(), exception.code()), isInternetIssue);
                    return;
                }
                ResponseBody responseBody = response.errorBody();
                if (response.code() == 401 && !response.raw().request().url().toString().contains("login")) {
                    restartApp(requireActivity());
                    return;
                }
                if (responseBody != null) {
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(responseBody.string());
                        String msg = exception.getMessage();
                        if (jsonObject.has("error")) {
                            msg = jsonObject.getString("error");
                        }
                        if (jsonObject.has("message")) {
                            msg = jsonObject.getString("message");
                        }
                        RetrofitError error = new RetrofitError(msg, exception.code());
                        HttpUrl url = response.raw().request().url();
                        String path = url.encodedPath();
                        String forTouchUrl = url.topPrivateDomain();
                        Log.d(">>Path", path + " , " + forTouchUrl);

                        boolean isHandled = onError(error, isInternetIssue);
                        if (!isHandled) {
                            Log.e(">>>>Error", "Not Handled");
                        }

                    } catch (JSONException e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                        // Error parsing issue
                        onError(new RetrofitError("" + e.getMessage(), exception.code()), isInternetIssue);
                    } catch (IOException e) {
                        onError(new RetrofitError("" + e.getMessage(), exception.code()), isInternetIssue);
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (isInternetIssue) {
                        onError(new RetrofitError(getString(R.string.error_internet), exception.code()), isInternetIssue);
                        return;
                    }
                    onError(new RetrofitError(exception.message(), exception.code()), isInternetIssue);
                }
            } else {
                if (isInternetIssue) {
                    onError(new RetrofitError(getString(R.string.error_internet), -1), isInternetIssue);
                    return;
                }
                onError(new RetrofitError(throwable.getMessage(), -1), isInternetIssue);
            }
        }

        @Override
        public void onComplete() {

        }

        public abstract void onSuccess(T t);

        public boolean onError(RetrofitError error, boolean isInternetIssue) {
            if (error.message.contains("Failed to connect")) {
                Toast.makeText(requireContext(), getString(R.string.error_internet), Toast.LENGTH_SHORT).show();
                return false;
            }
            Toast.makeText(requireContext(), "" + error.message + "", Toast.LENGTH_SHORT).show();
            return false;
        }

        class PendingRunnable implements Runnable {
            final private T t;
            final private Throwable throwable;
            final private boolean isInternetIssue;

            private PendingRunnable(T t, Throwable throwable, boolean isInternetIssue) {
                this.t = t;
                this.throwable = throwable;
                this.isInternetIssue = isInternetIssue;
            }

            @Override
            public void run() {
                if (throwable == null) {
                    onSuccess(t);
                } else {
                    handleError(throwable, isInternetIssue);
                }
            }
        }
    }

    /**
     * Completable request using completable Observable for local
     * DB to access data.
     *
     * @param observable completable observer
     * @param callBack   Observer
     * @param <T>        params
     */
    public static <T> void requestCompletable(Completable observable, CallBackCompletable<T> callBack) {
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(callBack);
    }

    public <T> void requestObservable(Observable observable, CallBackObservable<T> callBack) {
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(callBack);
    }

    /**
     * Callback mechanism of Observer with success or error cases
     * with overriding functions.
     *
     * @param <T> type of instance.
     */
    public abstract class CallBackObservable<T> implements Observer<T> {

        @Override
        public void onSubscribe(@NotNull Disposable d) {
            mDisposables.add(d);
        }

        @Override
        public void onNext(@NotNull T t) {
            onSuccess(t);
        }

        @Override
        public void onError(@NotNull Throwable e) {

        }

        @Override
        public void onComplete() {

        }

        public abstract void onSuccess(T t);
    }

    public abstract static class CallBackCompletable<T> implements CompletableObserver {

        @Override
        public void onSubscribe(@NotNull Disposable d) {

        }


        @Override
        public void onError(@NotNull Throwable e) {

        }

        @Override
        public void onComplete() {

        }


    }

    /**
     * It Provides mechanism of Observable with single request
     * with overriding functions.
     *
     * @param <T>
     */
    public <T> void requestSingle(Single<T> observable, CallBackSingle<T> callBack) {
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(callBack);
    }


    public abstract class CallBackSingle<T> implements SingleObserver<T> {
        @Override
        public void onSubscribe(@NotNull Disposable d) {

        }

        @Override
        public void onSuccess(@NotNull T t) {

        }

        @Override
        public void onError(@NotNull Throwable e) {

        }
    }
}
