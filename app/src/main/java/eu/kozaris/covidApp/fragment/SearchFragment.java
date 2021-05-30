package eu.kozaris.covidApp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import eu.kozaris.covidApp.R;
import eu.kozaris.covidApp.activity.InternetActivity;
import eu.kozaris.covidApp.adepter.CountryAdapter;
import eu.kozaris.covidApp.models.Country;
import eu.kozaris.covidApp.models.Summary;
import eu.kozaris.covidApp.retrofit.GetDataService;
import eu.kozaris.covidApp.retrofit.RetrofitClientInstance;
import eu.kozaris.covidApp.utils.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {


    public static SearchFragment searchFragment;
    RecyclerView recyclerView;
    CountryAdapter countryAdapter;
    List<Country> countryList;

    public static SearchFragment getInstance() {
        return searchFragment;
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);

        //Check for internet connection
        if (Utils.internetCheck(requireActivity())) {
            startActivity(new Intent(requireActivity(), InternetActivity.class));
            requireActivity().finish();
        }
        searchFragment = this;
        Bundle b = getArguments();
        if (b != null) {
            String keyword = b.getString("search");
            if (keyword.trim().length() != 0) {
                getSearch(keyword);
            }
        }
        setContain();


        return view;
    }

    //The parent activity notified us that the user has typed in the search edit text
    public void getSearch(String key) {
        Log.e("searchKey===", key + "");
        //Use the Filter in the country addapter to filter the list of ALL the countries based on the typed key
        countryAdapter.getFilter().filter(key);
    }


    private void setContain() {
        //Fill the recyclerview with 0 items and link it to the country adapter
        countryList = new ArrayList<>();
        countryAdapter = new CountryAdapter(requireActivity(), countryList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(requireActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(countryAdapter);

        //Use retrofit to get data from the API
        getAllData();
    }

    /**
     * Use retrofit to get the data from the API
     */
    private void getAllData() {
        /*Create handle for the RetrofitInstance interface*/
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<Summary> call = service.getAllCountries();
        call.enqueue(new Callback<Summary>() {
            @Override
            public void onResponse(@NotNull Call<Summary> call, @NotNull Response<Summary> response) {
                if (response.body() != null) {
                    Log.e("Response", "-->" + response.body().getGlobal().getNewConfirmed());
                }
                if (response.body().getCountries() != null) {
                    //Add all countries to the list
                    countryList.addAll(response.body().getCountries());
                    //Sort countries by the new confirmed covid cases
                    countryList.sort(new CustomComparator());
                    //notify the adapter that the dataset has changed and the recyclerview neeeds to change
                    countryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NotNull Call<Summary> call, @NotNull Throwable t) {
                Log.e("error", "-> " + t.getMessage());
                Toast.makeText(getActivity(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        searchFragment = null;
    }

    //Comparator that compares 2 countries based on the confirmed cases
    public static class CustomComparator implements Comparator<Country> {
        @Override
        public int compare(Country o1, Country o2) {
            return o2.getNewConfirmed().compareTo(o1.getNewConfirmed());
        }
    }
}
