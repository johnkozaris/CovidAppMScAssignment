
package eu.kozaris.covidApp.models;


import com.google.gson.annotations.SerializedName;

//This is not a normal class, this is used by Google GSON as a contract or a mapping guide to convert JSON objects to Java Classes

public class CasesTimeSeries {

    @SerializedName("dailyconfirmed")
    private String dailyconfirmed;
    @SerializedName("dailydeceased")
    private String dailydeceased ;

    @SerializedName("dailyrecovered")
    private String dailyrecovered ;

    @SerializedName("date")
    private String date ;

    @SerializedName("totalconfirmed")
    private String totalconfirmed ;

    @SerializedName("totaldeceased")
    private String totaldeceased ;

    @SerializedName("totalrecovered")
    private String totalrecovered ;


    public String getDailyconfirmed() {
        return dailyconfirmed;
    }

    public void setDailyconfirmed(String dailyconfirmed) {
        this.dailyconfirmed = dailyconfirmed;
    }

    public String getDailydeceased() {
        return dailydeceased;
    }

    public void setDailydeceased(String dailydeceased) {
        this.dailydeceased = dailydeceased;
    }

    public String getDailyrecovered() {
        return dailyrecovered;
    }

    public void setDailyrecovered(String dailyrecovered) {
        this.dailyrecovered = dailyrecovered;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotalconfirmed() {
        return totalconfirmed;
    }

    public void setTotalconfirmed(String totalconfirmed) {
        this.totalconfirmed = totalconfirmed;
    }

    public String getTotaldeceased() {
        return totaldeceased;
    }

    public void setTotaldeceased(String totaldeceased) {
        this.totaldeceased = totaldeceased;
    }

    public String getTotalrecovered() {
        return totalrecovered;
    }

    public void setTotalrecovered(String totalrecovered) {
        this.totalrecovered = totalrecovered;
    }
}
