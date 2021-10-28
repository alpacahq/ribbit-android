package com.stockly.android.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.ramiz.nameinitialscircleimageview.NameInitialsCircleImageView;
import com.stockly.android.BuildConfig;
import com.stockly.android.R;
import com.stockly.android.dao.BankAccountDao;
import com.stockly.android.dao.UserDao;
import com.stockly.android.databinding.FragmentEditProfileBinding;
import com.stockly.android.listners.WrapperTextWatcher;
import com.stockly.android.models.Avatar;
import com.stockly.android.models.RetrofitError;
import com.stockly.android.models.User;
import com.stockly.android.session.UserSession;
import com.stockly.android.utils.CommonUtils;
import com.stockly.android.validation.Validation;
import com.stockly.android.validation.Validator;


import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.Single;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;

/**
 * EditProfileFragment
 * It represents User's to update their profiles.
 */
@AndroidEntryPoint
public class EditProfileFragment extends NetworkFragment {

    private FragmentEditProfileBinding mBinding;
    private String public_portfolio;
    private final int RequestCode = 100;
    private boolean isValidBio = false;
    @Inject
    UserDao userDao;
    @Inject
    UserSession mUserSession;
    private User mUser;
    @Inject
    BankAccountDao accountDao;
    @Inject
    BankAccountDao profileDao;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_edit_profile;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = FragmentEditProfileBinding.bind(view);
        setUpToolBar(mBinding.toolbar.toolbar);
        mBinding.toolbar.save.setVisibility(View.GONE);

        mBinding.bio.addTextChangedListener(new WrapperTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    if (!Validation.isValidLongInput(charSequence.toString())) {
                        mBinding.errorMessage.setVisibility(View.VISIBLE);
                    } else {
                        mBinding.errorMessage.setVisibility(View.GONE);
                    }
                } else {
                    mBinding.errorMessage.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
            }
        });

        // local db user
        getUser();

//        mBinding.publicPortfolio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    public_portfolio = "true";
//                } else {
//                    public_portfolio = "false";
//                }
//            }
//        });

        mBinding.logoutLayout.setOnClickListener(v -> restartApp(requireActivity()));

        // get result of image and pass uri to server for update
        ActivityResultLauncher<Intent> launcher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Uri uri = result.getData().getData();
//                      Toast.makeText(requireActivity(), "uri" + uri, Toast.LENGTH_SHORT).show();
                        File file = new File(uri.getPath());
                        RequestBody requestFile =
                                RequestBody.create(MediaType.parse("multipart/form-data"), file);
//                        Log.d(">>>", "onActivityResult: " + file);
                        // MultipartBody.Part is used to send also the actual file name
                        MultipartBody.Part body =
                                MultipartBody.Part.createFormData("file", file.getName(), requestFile);


                        updateProfileImage(body);
                        // Use the uri to load the image
                    } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                        // Use ImagePicker.Companion.getError(result.getData()) to show an error
                        Toast.makeText(requireActivity(), "err", Toast.LENGTH_SHORT).show();
                    }
                });

        // open image picker
        mBinding.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(requireActivity())
                        .crop()
                        .cropFreeStyle()
                        .maxResultSize(512, 512, true)
                        .createIntentFromDialog((Function1) (new Function1() {
                            public Object invoke(Object var1) {
                                this.invoke((Intent) var1);
                                return Unit.INSTANCE;
                            }

                            public final void invoke(@NotNull Intent it) {
                                Intrinsics.checkNotNullParameter(it, "it");
                                launcher.launch(it);
                            }
                        }));
            }
        });

        mBinding.delete.setOnClickListener(v -> deleteProfileImage());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_save, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.save_it) {
            Validator validator = new Validator();
            if (!TextUtils.isEmpty(mBinding.bio.getText().toString())) {
                isValidBio = Validation.isValidLongInput(mBinding.bio.getText().toString());
            } else {
                isValidBio = true;
            }
//&& validator.isValidFbUrl(requireActivity(), mBinding.fbLink) && validator.isValidInstaUrl(requireActivity(), mBinding.instaLink) && validator.isValidTwitterUrl(requireActivity(), mBinding.twitterLink)
//                , CommonUtils.getValue(mBinding.fbLink.getEditText().getText().toString()),
//                        CommonUtils.getValue(mBinding.twitterLink.getEditText().getText().toString()), CommonUtils.getValue(mBinding.instaLink.getEditText().getText().toString()), public_portfolio
            if (isValidBio) {
                updateProfile(CommonUtils.getValue(mBinding.bio.getText().toString()));
            }
        }
        return super.onOptionsItemSelected(item);

    }

    /**
     * Update profile with updated values
     *
     * @param bio input value of user's bio
     */
    //, String facebookUrl, String twitterUrl, String instaUrl, String public_portfolio
    private void updateProfile(String bio) {
        mBinding.progressBar.progressBar.setVisibility(View.VISIBLE);
        HashMap<String, Object> body = new HashMap<>();
        body.put("bio", CommonUtils.getValue(bio));
//        body.put("facebook_url", CommonUtils.getValue(facebookUrl));
//        body.put("twitter_url", CommonUtils.getValue(twitterUrl));
//        body.put("instagram_url", CommonUtils.getValue(instaUrl));
//        body.put("public_portfolio", CommonUtils.getValue(public_portfolio));
        enqueue(getApi().updateProfile(body), new NetworkFragment.CallBack<User>() {
            @Override
            public void onSuccess(User user) {
                updateUser(user);
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                requireActivity().finish();
                Log.d(">>>", "onSuccess: " + user.firstName);

            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                return super.onError(error, isInternetIssue);

            }
        });
    }

    /**
     * Upload image to server and update in user's object.
     *
     * @param file multipart file of image uri.
     */
    private void updateProfileImage(MultipartBody.Part file) {
        mBinding.progressBar.progressBar.setVisibility(View.VISIBLE);
        Log.d(">>>", "updateProfileImage: " + file);
        enqueue(getApi().updateProfileImage(file), new NetworkFragment.CallBack<Avatar>() {
            @Override
            public void onSuccess(Avatar avatar) {
                mUser.avatar = avatar.avatar;
                updateUser(mUser);
//                Log.d(">>>", "updateUI: " + BuildConfig.BASE_URL + "file/users/" + avatar.avatar);
                updateImage(mUser, avatar.avatar);
                mBinding.delete.setClickable(!TextUtils.isEmpty(avatar.avatar));
                mBinding.delete.setEnabled(!TextUtils.isEmpty(avatar.avatar));
                mBinding.progressBar.progressBar.setVisibility(View.GONE);

            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                return super.onError(error, isInternetIssue);

            }
        });
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
                mUser = user;
                mBinding.delete.setClickable(!TextUtils.isEmpty(user.avatar));
                mBinding.delete.setEnabled(!TextUtils.isEmpty(user.avatar));
                updateUI(user);
            }

            @Override
            public void onError(@NotNull Throwable e) {
                Log.d(">>>", "onError: " + e.getMessage());
                super.onError(e);
            }
        });

    }

    /**
     * Update Ui with user's record.
     *
     * @param user data object.
     */
    public void updateUI(User user) {
        if (user != null) {
            updateImage(user, user.avatar);
            mBinding.firstName.getEditText().setText(String.format("%s %s", user.firstName, user.lastName));
            mBinding.bio.setText(user.bio);
//            mBinding.fbLink.getEditText().setText(user.facebookUrl);
//            mBinding.twitterLink.getEditText().setText(user.twitterUrl);
//            mBinding.instaLink.getEditText().setText(user.instagramUrl);
//            if (!TextUtils.isEmpty(user.publicPortfolio)) {
//                if (user.publicPortfolio.equalsIgnoreCase("true")) {
//                    public_portfolio = "true";
//                    mBinding.publicPortfolio.setChecked(true);
//                } else if (user.publicPortfolio.equalsIgnoreCase("false")) {
//                    public_portfolio = "false";
//                    mBinding.publicPortfolio.setChecked(false);
//                }
//            } else {
//                public_portfolio = "true";
//            }
        }
    }

    /**
     * Update image with name initials else show image with Url.
     *
     * @param user   data object.
     * @param avatar image url.
     */
    public void updateImage(User user, String avatar) {

        NameInitialsCircleImageView.ImageInfo imageInfo = new NameInitialsCircleImageView.ImageInfo
                .Builder("" + user.firstName.charAt(0) + user.lastName.charAt(0))
                .setTextColor(R.color.light_red)
                .setTextFont(R.font.inter_semibold)
                .setImageUrl(avatar)
                .setCircleBackgroundColorRes(R.color.holo_light_red)
                .build();
        mBinding.profileImg.setImageInfo(imageInfo);
        if (!TextUtils.isEmpty(avatar)) {
            Glide.with(this).load(BuildConfig.BASE_URL + "file/users/" + avatar).error(imageInfo).into(mBinding.profileImg);
        }
    }

    /**
     * Delete image by calling api from server and update user's object.
     */
    private void deleteProfileImage() {
        mBinding.progressBar.progressBar.setVisibility(View.VISIBLE);
        enqueue(getApi().deleteProfileImage(), new NetworkFragment.CallBack<Avatar>() {
            @Override
            public void onSuccess(Avatar avatar) {
                mUser.avatar = "";
                updateUser(mUser);
                updateImage(mUser, mUser.avatar);
                mBinding.delete.setClickable(!TextUtils.isEmpty(mUser.avatar));
                mBinding.delete.setEnabled(!TextUtils.isEmpty(mUser.avatar));
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                Toast.makeText(requireActivity(), "" + avatar.message, Toast.LENGTH_SHORT).show();

            }

            @Override
            public boolean onError(RetrofitError error, boolean isInternetIssue) {
                mBinding.progressBar.progressBar.setVisibility(View.GONE);
                return super.onError(error, isInternetIssue);

            }
        });
    }


}