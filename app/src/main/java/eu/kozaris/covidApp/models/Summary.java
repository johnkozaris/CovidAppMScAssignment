
package eu.kozaris.covidApp.models;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Summary {

    @SerializedName("Countries")
    private List<Country> mCountries;
    @SerializedName("Date")
    private String mDate;
    @SerializedName("Global")
    private Global mGlobal;
    private CountryInd countryTemp;

    public CountryInd getCountryTemp() {
        return countryTemp;
    }

    public void setCountryTemp(CountryInd countryTemp) {
        this.countryTemp = countryTemp;
    }

    public List<Country> getCountries() {
        return mCountries;
    }

    public void setCountries(List<Country> countries) {
        mCountries = countries;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public Global getGlobal() {
        return mGlobal;
    }

    public void setGlobal(Global global) {
        mGlobal = global;
    }

}
