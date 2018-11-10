package dev.karim.perumahan.network;

import android.support.annotation.NonNull;

import retrofit2.Response;

public interface NetworkResponse<M> {
    void onSuccess(@NonNull Response<M> res);
    void onError(Throwable error);
}