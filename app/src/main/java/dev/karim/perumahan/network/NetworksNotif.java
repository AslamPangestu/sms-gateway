package dev.karim.perumahan.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by mvryan on 03/08/18.
 */

public class NetworksNotif {

    public final static String NOTIF_URL = "http://192.168.100.3:8080/";//local_ip/route_name
    public final static String VIRAL_URL = "https://reguler.zenziva.net/apps/";//local_ip/route_name

    private static Retrofit retrofit = null;

    public static OkHttpClient client;

    private static void interceptor(){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new  OkHttpClient.Builder().addInterceptor(interceptor).build();
    }

    public static Retrofit notifRequest(){

        interceptor();

        retrofit = new Retrofit.Builder()
                .baseUrl(NOTIF_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

    public static Retrofit viralRequest(){

        interceptor();

        retrofit = new Retrofit.Builder()
                .baseUrl(VIRAL_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }
}
