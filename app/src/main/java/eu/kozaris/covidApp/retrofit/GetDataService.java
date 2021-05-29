package eu.kozaris.covidApp.retrofit;

import eu.kozaris.covidApp.models.Home;
import eu.kozaris.covidApp.models.Summary;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetDataService {

    @GET("/summary")
    Call<Summary> getAllCountries();

    @GET("/data.json")
    Call<Home> getHome();

}
