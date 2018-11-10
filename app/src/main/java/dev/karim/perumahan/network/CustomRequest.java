package dev.karim.perumahan.network;

import android.support.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomRequest {

    public static <T> void request(Call<T> request, final NetworkResponse<T> res) {
        request.enqueue(new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                if (response.isSuccessful()) {
                    res.onSuccess(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                res.onError(t);
            }
        });
    }

}
