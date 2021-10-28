package com.stockly.android.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.github.ramiz.nameinitialscircleimageview.NameInitialsCircleImageView;
import com.stockly.android.BuildConfig;
import com.stockly.android.MainActivity;
import com.stockly.android.R;
import com.stockly.android.adapter.MyStocksAdapter;
import com.stockly.android.adapter.MyWatchListAdapter;
import com.stockly.android.constants.CommonKeys;
import com.stockly.android.dao.TradingProfileDao;
import com.stockly.android.databinding.FragmentProfileBinding;
import com.stockly.android.fragments.plaid.BankIntroFragment;
import com.stockly.android.fragments.wallet.AddFundsFragment;
import com.stockly.android.listners.ItemClickListener;
import com.stockly.android.models.Assets;
import com.stockly.android.models.BankAccount;
import com.stockly.android.models.Payment;
import com.stockly.android.models.Positions;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.models.ShareableLink;
import com.stockly.android.models.TradingProfile;
import com.stockly.android.models.User;
import com.stockly.android.models.WatchList;
import com.stockly.android.session.UserSession;
import com.stockly.android.utils.ActivityUtils;
import com.stockly.android.utils.CommonUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.Single;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 * <p>
 * ProfileFragment
 * It represents profile of user's and its list of favourite
 * stock ticker/assets
 */
@AndroidEntryPoint
public class ProfileFragment extends NetworkFragment {
    private FragmentProfileBinding mBinding;
    private MyWatchListAdapter mWatchListAdapter;
    private String code;
    private String fb, twitter, insta;
    @Inject
    UserSession mUserSession;
    @Inject
    TradingProfileDao profileDao;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleBackPressActivity(this, MainActivity.class);
        // set status bar color.
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#FFFFFF"));
        setHasOptionsMenu(true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_profile;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentProfileBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar, true);
        mBinding.toolbar.save.setVisibility(View.GONE);
        mBinding.toolbar.title.setText(R.string.my_profile);

        //setup adapter
        mBinding.watchlistItems.setLayoutManager(new LinearLayoutManager(requireActivity()));
        mWatchListAdapter = new MyWatchListAdapter(requireActivity(), (s, position) -> {

        });
        mBinding.watchlistItems.setAdapter(mWatchListAdapter);

//        getShareableLink();


//        mBinding.share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!TextUtils.isEmpty(mBinding.shareableLink.getText().toString()))
//                    shareLink(mBinding.shareableLink.getText().toString(), CommonUtils.getValue(code));
//            }
//        });
//        mBinding.fbIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                newFacebookIntent(CommonUtils.getValue(fb));
//            }
//        });
//
//        mBinding.twitterIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                twitterIntent(CommonUtils.getValue(twitter));
//            }
//        });
//
//        mBinding.instaIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                instaIntent(CommonUtils.getValue(insta));
//            }
//        });


    }

    public void shareLink(String link, String code) {

        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Candy Stock");
            String shareMessage = "\nUse my referral code for application\n" + code + "\n";
            shareMessage = shareMessage + link;
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Choose one"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * get watchListed assets/ticker from server.
     */
    private void getWatchlist() {
        mBinding.progressBar.progressBar.setVisibility(View.VISIBLE);
        enqueue(getApi().getWatchlist(), new NetworkFragment.CallBack<WatchList>() {
            @Override
            public void onSuccess(WatchList list) {

                mWatchListAdapter.setData(list.assets, true);
                if (list.assets.size() == 0) {
                    mBinding.noDataWatch.setVisibility(View.VISIBLE);
                } else {
                    mBinding.noDataWatch.setVisibility(View.GONE);
                }
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                mBinding.noDataWatch.setVisibility(View.VISIBLE);
                return super.onError(error, isInternetIssue);
            }
        });
    }


//    private void getShareableLink() {
//        mBinding.progressBar.progressBar.setVisibility(View.VISIBLE);
//        enqueue(getApi().getShareableLink(), new NetworkFragment.CallBack<ShareableLink>() {
//            @Override
//            public void onSuccess(ShareableLink link) {
//                mBinding.shareableLink.setText(link.url);
//                code = link.code;
//                mBinding.progressBar.progressBar.setVisibility(View.GONE);
//            }
//
//            @Override
//            public boolean onError(RetrofitError error, boolean isInternetIssue) {
//                mBinding.progressBar.progressBar.setVisibility(View.GONE);
//                return super.onError(error, isInternetIssue);
//            }
//        });
//    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater
            inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.setting) {
            ActivityUtils.launchFragment(requireActivity(), EditProfileFragment.class);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        getUser();
    }

    /**
     * get user from local DB.
     */
    public void getUser() {
        // DB User
        Single<User> userById = userDao.getUserById(mUserSession.getUserID());
        requestSingle(userById, new CallBackSingle<User>() {
            @Override
            public void onSuccess(@NotNull User user) {
                updateUI(user);
                if (user.account_status.equalsIgnoreCase("APPROVED")) {
                    getWatchlist();
                } else {
                    mBinding.noDataWatch.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(@NotNull Throwable e) {
                Log.d(">>>", "onError: " + e.getMessage());
                super.onError(e);
            }
        });
    }

    /**
     * update UI with user's data.
     *
     * @param user object.
     */
    public void updateUI(User user) {
        if (user != null) {
            mBinding.userName.setText(String.format("%s %s", user.firstName, user.lastName));
            mBinding.bio.setText(user.bio);
            updateImage(user);
            fb = user.facebookUrl;
            twitter = user.twitterUrl;
            insta = user.instagramUrl;
        }
    }

    /**
     * set image with name initials or with uploaded image url from
     *
     * @param user object.
     */
    public void updateImage(User user) {

        NameInitialsCircleImageView.ImageInfo imageInfo = new NameInitialsCircleImageView.ImageInfo
                .Builder("" + user.firstName.charAt(0) + user.lastName.charAt(0))
                .setTextColor(R.color.colorWhite)
                .setTextFont(R.font.inter_semibold)
//                .setImageUrl(imageUrl)
                .setCircleBackgroundColorRes(R.color.colorPurpleLight)
                .build();
        mBinding.profileImg.setImageInfo(imageInfo);
        if (!TextUtils.isEmpty(user.avatar) && isAdded()) {
            Glide.with(ProfileFragment.this).load(BuildConfig.BASE_URL + "file/users/" + user.avatar).error(imageInfo).into(mBinding.profileImg);
        }
    }

    public String getFacebookPageURL(Context context, String url) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + url;
            } else { //older versions of fb app
                return "fb://page/" + url;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return url; //normal web url
        }
    }

    //facebook
    public void facebookIntent(String url) {
        if (!TextUtils.isEmpty(url)) {
            Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
            String facebookUrl = getFacebookPageURL(requireActivity(), url);
            facebookIntent.setData(Uri.parse(facebookUrl));
            startActivity(facebookIntent);
        }
    }

    // twitter
    public void twitterIntent(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (!url.contains("twitter")) {
                url = "twitter://" + url;
            }
            Intent intent = null;
            try {
                // get the Twitter app if possible
                requireActivity().getPackageManager().getPackageInfo("com.twitter.android", 0);
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            } catch (Exception e) {
                // no Twitter app, revert to browser
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            }
            this.startActivity(intent);
        }
    }

    public void instaIntent(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (!url.contains("instagram")) {
                url = "http://instagram" + url;
            }
            Uri uri = Uri.parse(url);
            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

            likeIng.setPackage("com.instagram.android");

            try {
                startActivity(likeIng);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://instagram.com/xxx")));
            }
        }

    }

}
