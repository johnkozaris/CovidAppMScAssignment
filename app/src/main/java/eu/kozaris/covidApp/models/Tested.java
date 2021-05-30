
package eu.kozaris.covidApp.models;

import com.google.gson.annotations.SerializedName;
//This is not a normal class, this is used by Google GSON as a contract or a mapping guide to convert JSON objects to Java Classes

public class Tested {

    @SerializedName("positivecasesfromsamplesreported")
    private String mPositivecasesfromsamplesreported;
    @SerializedName("samplereportedtoday")
    private String mSamplereportedtoday;
    @SerializedName("source")
    private String mSource;
    @SerializedName("testsconductedbyprivatelabs")
    private String mTestsconductedbyprivatelabs;
    @SerializedName("totalindividualstested")
    private String mTotalindividualstested;
    @SerializedName("totalpositivecases")
    private String mTotalpositivecases;
    @SerializedName("totalsamplestested")
    private String mTotalsamplestested;
    @SerializedName("updatetimestamp")
    private String mUpdatetimestamp;

    public String getPositivecasesfromsamplesreported() {
        return mPositivecasesfromsamplesreported;
    }

    public void setPositivecasesfromsamplesreported(String positivecasesfromsamplesreported) {
        mPositivecasesfromsamplesreported = positivecasesfromsamplesreported;
    }

    public String getSamplereportedtoday() {
        return mSamplereportedtoday;
    }

    public void setSamplereportedtoday(String samplereportedtoday) {
        mSamplereportedtoday = samplereportedtoday;
    }

    public String getSource() {
        return mSource;
    }

    public void setSource(String source) {
        mSource = source;
    }

    public String getTestsconductedbyprivatelabs() {
        return mTestsconductedbyprivatelabs;
    }

    public void setTestsconductedbyprivatelabs(String testsconductedbyprivatelabs) {
        mTestsconductedbyprivatelabs = testsconductedbyprivatelabs;
    }

    public String getTotalindividualstested() {
        return mTotalindividualstested;
    }

    public void setTotalindividualstested(String totalindividualstested) {
        mTotalindividualstested = totalindividualstested;
    }

    public String getTotalpositivecases() {
        return mTotalpositivecases;
    }

    public void setTotalpositivecases(String totalpositivecases) {
        mTotalpositivecases = totalpositivecases;
    }

    public String getTotalsamplestested() {
        return mTotalsamplestested;
    }

    public void setTotalsamplestested(String totalsamplestested) {
        mTotalsamplestested = totalsamplestested;
    }

    public String getUpdatetimestamp() {
        return mUpdatetimestamp;
    }

    public void setUpdatetimestamp(String updatetimestamp) {
        mUpdatetimestamp = updatetimestamp;
    }

}
