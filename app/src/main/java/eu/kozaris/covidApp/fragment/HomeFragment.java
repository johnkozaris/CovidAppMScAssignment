package eu.kozaris.covidApp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.os.ConfigurationCompat;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import eu.kozaris.covidApp.R;
import eu.kozaris.covidApp.activity.InternetActivity;
import eu.kozaris.covidApp.models.CountryInd;
import eu.kozaris.covidApp.models.Summary;
import eu.kozaris.covidApp.retrofit.GetDataService;
import eu.kozaris.covidApp.retrofit.RetrofitClientInstance;
import eu.kozaris.covidApp.utils.Utils;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements View.OnClickListener {

    TextView txtGreece;
    TextView txtGlobal;
    TextView txtAffected;
    TextView txtDeath;
    TextView txtRecovered;
    TextView txtTotalRecovered;
    TextView txtActive;
    TextView txtLiveLabel;
    ImageView imgDot;


    Summary summary;
    public  HomeFragment homeFragment;

    //This is boilerplate
    public  HomeFragment getInstance() {
        return homeFragment;
    }

    /**
     * This method changes between Greek and global data and fills the appropriate textviews
     * @param isType if true then greece is selected else global is selected
     */
    private void setData(boolean isType) {
        Locale currentLocale = ConfigurationCompat.getLocales(getResources().getConfiguration()).get(0);
        if (isType) {
            txtAffected.setText(String.format(summary.getCountryTemp().getNewConfirmed().toString(),currentLocale));
            txtDeath.setText(String.format(summary.getCountryTemp().getTotalDeaths().toString(),currentLocale));
            txtRecovered.setText(String.format(summary.getCountryTemp().getNewRecovered().toString(),currentLocale));
            txtActive.setText(String.format(summary.getCountryTemp().getTotalConfirmed().toString(),currentLocale));
            txtTotalRecovered.setText(String.format(summary.getCountryTemp().getTotalRecovered().toString(),currentLocale));
        } else {
            txtAffected.setText(String.format(summary.getGlobal().getNewConfirmed().toString(),currentLocale));
            txtDeath.setText(String.format(summary.getGlobal().getTotalDeaths().toString(),currentLocale));
            txtRecovered.setText(String.format(summary.getGlobal().getNewRecovered().toString(),currentLocale));
            txtActive.setText(String.format(summary.getGlobal().getTotalConfirmed().toString(),currentLocale));
            txtTotalRecovered.setText(String.format(summary.getGlobal().getTotalRecovered().toString(),currentLocale));
        }

    }
    public HomeFragment() {
        // Required empty public constructor
    }

    //This is also boilerplate in case we need to pass vars when calling the fragment
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        homeFragment = this;
        //Inflate Views
        txtGreece = view.findViewById(R.id.txt_greece);
        txtGlobal = view.findViewById(R.id.txt_global);
        txtAffected = view.findViewById(R.id.txt_affected);
        txtDeath = view.findViewById(R.id.txt_deaths);
        txtRecovered = view.findViewById(R.id.txt_recovered);
        txtTotalRecovered = view.findViewById(R.id.txt_total_recovered);
        txtActive = view.findViewById(R.id.txt_active);
        txtLiveLabel = view.findViewById(R.id.txt_live_label);
        imgDot = view.findViewById(R.id.img_dot);

        //These textviews change the API Results from Greece to Global
        //PS The greek Results are not correct from the API we chose
        txtGreece.setOnClickListener(this);
        txtGlobal.setOnClickListener(this);

        setContain();
        return view;
    }


    private void setContain() {
        //This summary is a JSON representes as a Java Class this is how retrofit works
        summary = new Summary();
        //Check for internet connection
        if (Utils.internetCheck(requireContext())) {
            startActivity(new Intent(getActivity(), InternetActivity.class));
            requireActivity().finish();
        }
        getSummaryData();
        blink();
    }

    /**
     * This method starts a thread that makes the red dot net to Live Statistics blink
     */
    private void blink() {
        final Handler handler = new Handler();
        new Thread(() -> {
            int timeToBlink = 600;    //in milissegunds
            try {
                Thread.sleep(timeToBlink);
            } catch (Exception e) {
                Log.e("error", "-> " + e.getMessage());
            }
            handler.post(() -> {
                if (imgDot.getVisibility() == View.VISIBLE) {
                    imgDot.setVisibility(View.INVISIBLE);
                } else {
                    imgDot.setVisibility(View.VISIBLE);
                }
                blink();
            });
        }).start();
    }

    //Listens to textview click to change from Global to Greece
   @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.txt_greece) {
            txtGreece.setBackgroundResource(R.drawable.rounded_search);
            txtGlobal.setBackgroundResource(R.drawable.rounded_tranf);
            txtGreece.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorBlack));
            txtGlobal.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorWhite));
            setData(true);
        } else if (id == R.id.txt_global) {
            txtGlobal.setBackgroundResource(R.drawable.rounded_search);
            txtGreece.setBackgroundResource(R.drawable.rounded_tranf);
            txtGreece.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorWhite));
            txtGlobal.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorBlack));
            setData(false);
        }
    }

    /**
     * Gets data from the API using Retrofit
     */
    private void getSummaryData() {
        //Create handle for the RetrofitInstance interface
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<Summary> call = service.getAllCountries();
        //This is the callback in which we receive the data for ALL the countries
        call.enqueue(new Callback<Summary>() {
            @Override
            public void onResponse(@NotNull Call<Summary> call, @NotNull Response<Summary> response) {
                if (response.body() != null) {
                    Log.e("Response", ":" + response.body().getGlobal().getNewConfirmed());
                }
                if (response.body() != null) {
                    //get all the countries data from the response
                    summary.setGlobal(response.body().getGlobal());

                    //Go through all the countries
                    for (int i = 0; i <= response.body().getCountries().size(); i++) {
                        //When Greece is found
                        if (response.body().getCountries().get(i).getCountry().equalsIgnoreCase("Greece")) {
                            CountryInd country = new CountryInd();
                            country.setCountry(response.body().getCountries().get(i).getCountry());
                            country.setNewConfirmed(response.body().getCountries().get(i).getNewConfirmed());
                            country.setNewDeaths(response.body().getCountries().get(i).getNewDeaths());
                            country.setNewRecovered(response.body().getCountries().get(i).getNewRecovered());
                            country.setTotalConfirmed(response.body().getCountries().get(i).getTotalConfirmed());
                            country.setTotalDeaths(response.body().getCountries().get(i).getTotalDeaths());
                            country.setTotalRecovered(response.body().getCountries().get(i).getTotalRecovered());
                            //set the country as the selectected country for home
                            summary.setCountryTemp(country);
                            //inform the UI to change the textviews
                            setData(true);
                            return;
                        }
                    }
                }
            }
            @Override
            public void onFailure(@NotNull Call<Summary> call, @NotNull Throwable t) {
                Log.e("error", "-> " + t.getMessage());
                Toast.makeText(getActivity(), "Something went wrong. try later!", Toast.LENGTH_SHORT).show();

            }
        });
    }


}
