package gr.georkouk.inmyfridge.network;

/*
 * Created by geoDev on 4/3/2018.
 */

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RestClient {

    private static final String BASE_URL = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {

        if (retrofit == null) {

            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }

        return retrofit;
    }

}
