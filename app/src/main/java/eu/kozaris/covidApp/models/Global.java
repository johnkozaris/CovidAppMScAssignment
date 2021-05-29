
package eu.kozaris.covidApp.models;

import com.google.gson.annotations.SerializedName;

public class Global {

    @SerializedName("NewConfirmed")
    private Long mNewConfirmed;
    @SerializedName("NewDeaths")
    private Long mNewDeaths;
    @SerializedName("NewRecovered")
    private Long mNewRecovered;
    @SerializedName("TotalConfirmed")
    private Long mTotalConfirmed;
    @SerializedName("TotalDeaths")
    private Long mTotalDeaths;
    @SerializedName("TotalRecovered")
    private Long mTotalRecovered;

    public Long getNewConfirmed() {
        return mNewConfirmed;
    }

    public void setNewConfirmed(Long newConfirmed) {
        mNewConfirmed = newConfirmed;
    }

    public Long getNewDeaths() {
        return mNewDeaths;
    }

    public void setNewDeaths(Long newDeaths) {
        mNewDeaths = newDeaths;
    }

    public Long getNewRecovered() {
        return mNewRecovered;
    }

    public void setNewRecovered(Long newRecovered) {
        mNewRecovered = newRecovered;
    }

    public Long getTotalConfirmed() {
        return mTotalConfirmed;
    }

    public void setTotalConfirmed(Long totalConfirmed) {
        mTotalConfirmed = totalConfirmed;
    }

    public Long getTotalDeaths() {
        return mTotalDeaths;
    }

    public void setTotalDeaths(Long totalDeaths) {
        mTotalDeaths = totalDeaths;
    }

    public Long getTotalRecovered() {
        return mTotalRecovered;
    }

    public void setTotalRecovered(Long totalRecovered) {
        mTotalRecovered = totalRecovered;
    }

}
