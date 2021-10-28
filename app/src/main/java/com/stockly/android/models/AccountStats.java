package com.stockly.android.models;

import androidx.room.Entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by m.imran
 * Senior Software Engineer at
 * BhimSoft on 3/11/21.
 */

/**
 * user's account stats for home screen
 * number of user's invited by him and rewards
 */

public class AccountStats {
    @SerializedName("people_invited")
    public String peopleInvited;
    @SerializedName("reward_earned")
    public String rewardEarned;
}
