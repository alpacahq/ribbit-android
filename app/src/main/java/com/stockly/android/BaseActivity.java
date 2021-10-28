package com.stockly.android;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.stockly.android.dao.BankAccountDao;
import com.stockly.android.databinding.LogoutCustomDialogBinding;
import com.stockly.android.fragments.kyc.MainFragment;
import com.stockly.android.utils.PrefUtils;

import java.util.List;

import javax.inject.Inject;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * BaseActivity parent class that have multiple functions
 * defined to be used by child class for re-usability.
 */

public class BaseActivity extends AppCompatActivity {

    //    private final Handler handler = new Handler();
//    private Runnable r = new Runnable() {
//        @Override
//        public void run() {
//            // TODO Auto-generated method stub
////            Toast.makeText(BaseActivity.this, "user is inactive from last 2 minutes", Toast.LENGTH_SHORT).show();
//            isTimeUp = true;
//            if (!isFinishing() && !isPaused) {
////                Toast.makeText(getApplicationContext(), "not background", Toast.LENGTH_SHORT).show();
//                createDialog();
//            }
//        }
//    };
//
//    boolean isTimeUp = false;
//    boolean isPaused = false;

    @Inject
    BankAccountDao userDao;
    @Inject
    BankAccountDao accountDao;
    @Inject
    BankAccountDao profileDao;

    public void setUpToolBar(@NonNull Toolbar toolBar, boolean isHomeUp) {
        setSupportActionBar(toolBar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(isHomeUp);
            supportActionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        startHandler();
        super.onCreate(savedInstanceState);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(">>>", "onOptionsItemSelected: Base Activity");
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void replaceFragment(Fragment fragment, boolean addToStack) {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
////        isPaused = false;
//        stopHandler();//stop first and then start
//        startHandler();
//        Log.d(">>>", "createDialog: " + isTimeUp);
//        if (isTimeUp) {
//            Log.d(">>>", "onResume: ");
//            if (!isFinishing() && !isPaused) {
//                createDialog();
//            }
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        isPaused = true;
//        isTimeUp = false;
//        Log.d(">>>", "onPause: ");
//        stopHandler();//stop first and then start
//        startHandler();
//    }
//
//    @Override
//    public void onUserInteraction() {
//        super.onUserInteraction();
//        if (!isTimeUp) {
//            Log.d(">>>", "onUserInteraction: ");
//            stopHandler();//stop first and then start
//            startHandler();
//        }
//    }
//
//    public void startHandler() {
//        handler.postDelayed(r, 60 * 1000);
//    }
//
//    public void stopHandler() {
//        handler.removeCallbacks(r);
//    }
//
//    public void createDialog() {
//        @NonNull LogoutCustomDialogBinding customDialogBinding = LogoutCustomDialogBinding.inflate(LayoutInflater.from(BaseActivity.this), null, false);
//        isTimeUp = false;
//        Log.d(">>>", "createDialog: " + isTimeUp);
//        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(BaseActivity.this);
//        // Building the Alert dialog using materialAlertDialogBuilder instance
//        materialAlertDialogBuilder.setView(customDialogBinding.getRoot())
//                .setTitle("Still there?")
//                .setMessage("You have been Inactive for while.\n Do you want to continue?")
//                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Log.d(">>>", "onClick: ");
//                        stopHandler();
//                        startHandler();
//                        dialog.dismiss();
//                    }
//                });
//        try {
//            checkLogout(customDialogBinding.time);
//            materialAlertDialogBuilder.show();
//        } catch (WindowManager.BadTokenException e) {
//            e.printStackTrace();
//        }
//
//
//    }
//
//    public void checkLogout(TextView textView) {
//        new CountDownTimer(30000, 1000) {
//            public void onTick(long millisUntilFinished) {
//                textView.setText("You will be logged out\n in: " + millisUntilFinished / 1000 + " sec");
//                //here you can have your logic to set text to edittext
//            }
//
//            public void onFinish() {
////                flushDB();
//            }
//
//        }.start();
//    }
//
//
//    private boolean isAppIsInBackground(Context context) {
//        boolean isInBackground = true;
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
//            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
//            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
//                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                    for (String activeProcess : processInfo.pkgList) {
//                        if (activeProcess.equals(context.getPackageName())) {
//                            isInBackground = false;
//                        }
//                    }
//                }
//            }
//        } else {
//            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
//            ComponentName componentInfo = taskInfo.get(0).topActivity;
//            if (componentInfo.getPackageName().equals(context.getPackageName())) {
//                isInBackground = false;
//            }
//        }
//
//        return isInBackground;
//    }

    public void flushDB() {
        PrefUtils.clear(BaseActivity.this);
        userDao.deleteAll();
        accountDao.deleteAll();
        profileDao.deleteAll();
    }
}
