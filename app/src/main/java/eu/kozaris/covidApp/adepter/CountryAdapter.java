package eu.kozaris.covidApp.adepter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.core.os.ConfigurationCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import eu.kozaris.covidApp.R;
import eu.kozaris.covidApp.models.Country;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.MyViewHolder>
        implements Filterable {

    private final List<Country> CountryList;
    private List<Country> CountryListFiltered;
    private final Context callingContext;


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtConfirmed;
        TextView txtNewDeaths;
        TextView txtActive;
        TextView txtDeaths;
        TextView txtRecovered;
        TextView txtTotalRecovered;
        TextView txtCountry;

        public MyViewHolder(View view) {
            super(view);
            txtConfirmed = view.findViewById(R.id.txt_confirmed);
            txtNewDeaths = view.findViewById(R.id.txt_new_deaths);
            txtActive = view.findViewById(R.id.txt_active);
            txtDeaths = view.findViewById(R.id.txt_deaths);
            txtRecovered = view.findViewById(R.id.txt_recovered);
            txtTotalRecovered = view.findViewById(R.id.txt_total_recovered_listitem);
            txtCountry = view.findViewById(R.id.txt_country);
        }
    }


    public CountryAdapter(Context context, List<Country> contactList) {
        this.callingContext = context;
        this.CountryList = contactList;
        this.CountryListFiltered = contactList;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_country, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, final int position) {
        final Country country = CountryListFiltered.get(position);
        //TODO I cant ge the context here so that i can get a Locale
        Locale currentLocale = ConfigurationCompat.getLocales(callingContext.getResources().getConfiguration()).get(0);
        viewHolder.txtConfirmed.setText(String.format(country.getNewConfirmed().toString(),currentLocale));
        viewHolder.txtNewDeaths.setText(String.format(country.getNewDeaths().toString(),currentLocale));
        viewHolder.txtActive.setText(String.format(country.getTotalConfirmed().toString(),currentLocale));
        viewHolder.txtDeaths.setText(String.format(country.getTotalDeaths().toString(),currentLocale));
        viewHolder.txtRecovered.setText(String.format(country.getNewRecovered().toString(),currentLocale));
        viewHolder.txtTotalRecovered.setText(String.format(country.getTotalRecovered().toString(),currentLocale));
        viewHolder.txtCountry.setText(country.getCountry());


    }

    @Override
    public int getItemCount() {
        return CountryListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    CountryListFiltered = CountryList;
                } else {
                    List<Country> filteredList = new ArrayList<>();
                    for (Country row : CountryList) {
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getCountry().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    CountryListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = CountryListFiltered;
                return filterResults;
            }
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                CountryListFiltered = (ArrayList<Country>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ContactsAdapterListener {
        void onContactSelected(Country contact);
    }
}