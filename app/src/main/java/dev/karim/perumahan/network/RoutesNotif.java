package dev.karim.perumahan.network;

import dev.karim.perumahan.models.ResultNotif;
import dev.karim.perumahan.models.Viral;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by mvryan on 03/08/18.
 */

public interface RoutesNotif {

    @GET("viral/{id}")
    Call<ResultNotif> getNotif(
            @Path("id") Integer id
    );

    @POST("smsapi.php")
    Call<Viral> getViral(@Query("userkey") String userkey,
                         @Query("passkey") String passkey,
                         @Query("nohp") String nohp,
                         @Query("pesan") String pesan);
}
