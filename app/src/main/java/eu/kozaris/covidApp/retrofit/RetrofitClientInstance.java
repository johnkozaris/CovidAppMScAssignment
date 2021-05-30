package eu.kozaris.covidApp.retrofit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A retrofit client connecting to a specific API and making HTTP calls in this case "https://api.covid19api.com"
 */
public class RetrofitClientInstance {
    private static final String BASE_URL = "https://api.covid19api.com";
    public static Retrofit getRetrofitInstance() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);

        return new Retrofit.Builder()
            .baseUrl(BASE_URL)
                //Retrofit will use GSON lib to convert Json to Java object
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build();
    }

}
