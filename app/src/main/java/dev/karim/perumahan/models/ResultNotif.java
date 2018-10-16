package dev.karim.perumahan.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mvryan on 03/08/18.
 */

public class ResultNotif {

    private String status;

    @SerializedName("data")
    private Notif notif;

    public String getStatus ()
    {
        return status;
    }

    public Notif getNotif ()
    {
        return notif;
    }

}
