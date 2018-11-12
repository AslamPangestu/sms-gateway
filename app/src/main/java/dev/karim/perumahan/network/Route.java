package dev.karim.perumahan.network;

import dev.karim.perumahan.model.Counter;
import dev.karim.perumahan.model.Viral;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by mvryan on 03/08/18.
 */

public interface Route {

//    @GET("viral/{id}")
//    Call<ResultNotif> getNotif(
//            @Path("id") Integer id
//    );

    @POST("smsapi.php")
    Call<Viral> getViral(@Query("userkey") String userkey,
                         @Query("passkey") String passkey,
                         @Query("nohp") String nohp,
                         @Query("pesan") String pesan);

    @GET("counter/{id}")
    Call<Counter> getCounter(@Path("id") int id);

    @PUT("counter/{id}")
    @FormUrlEncoded
    Call<Counter> putCounter(@Path("id") int id,
                             @Field("counter") int counter);
}
