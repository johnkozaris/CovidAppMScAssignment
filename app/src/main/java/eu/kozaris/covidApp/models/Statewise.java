
package eu.kozaris.covidApp.models;


import com.google.gson.annotations.SerializedName;
//This is not a normal class, this is used by Google GSON as a contract or a mapping guide to convert JSON objects to Java Classes

public class Statewise {

    @SerializedName("active")
    private String active;

    @SerializedName("confirmed")
    private Integer confirmed;

    @SerializedName("deaths")
    private String deaths;

    @SerializedName("deltaconfirmed")
    private String deltaconfirmed;

    @SerializedName("deltadeaths")
    private String deltadeaths;

    @SerializedName("deltarecovered")
    private String deltarecovered;

    @SerializedName("lastupdatedtime")
    private String lastupdatedtime;

    @SerializedName("recovered")
    private String recovered;

    @SerializedName("state")
    private String state;

    @SerializedName("statecode")
    private String statecode;

    @SerializedName("statenotes")
    private String statenotes;


    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public Integer getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Integer confirmed) {
        this.confirmed = confirmed;
    }

    public String getDeaths() {
        return deaths;
    }

    public void setDeaths(String deaths) {
        this.deaths = deaths;
    }

    public String getDeltaconfirmed() {
        return deltaconfirmed;
    }

    public void setDeltaconfirmed(String deltaconfirmed) {
        this.deltaconfirmed = deltaconfirmed;
    }

    public String getDeltadeaths() {
        return deltadeaths;
    }

    public void setDeltadeaths(String deltadeaths) {
        this.deltadeaths = deltadeaths;
    }

    public String getDeltarecovered() {
        return deltarecovered;
    }

    public void setDeltarecovered(String deltarecovered) {
        this.deltarecovered = deltarecovered;
    }

    public String getLastupdatedtime() {
        return lastupdatedtime;
    }

    public void setLastupdatedtime(String lastupdatedtime) {
        this.lastupdatedtime = lastupdatedtime;
    }

    public String getRecovered() {
        return recovered;
    }

    public void setRecovered(String recovered) {
        this.recovered = recovered;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStatecode() {
        return statecode;
    }

    public void setStatecode(String statecode) {
        this.statecode = statecode;
    }

    public String getStatenotes() {
        return statenotes;
    }

    public void setStatenotes(String statenotes) {
        this.statenotes = statenotes;
    }
}
