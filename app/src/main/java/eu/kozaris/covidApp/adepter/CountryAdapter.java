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

/**
 * This adapter fills the Country Recycler View in the Search fragment
 * It uses a country card as items in the recycler View
 */
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
        //TODO The locale call is probably useless as we wont move the string to the xml. But keeps android studio from complaining
        Locale currentLocale = ConfigurationCompat.getLocales(callingContext.getResources().getConfiguration()).get(0);
        viewHolder.txtConfirmed.setText(String.format(country.getNewConfirmed().toString(), currentLocale));
        viewHolder.txtNewDeaths.setText(String.format(country.getNewDeaths().toString(), currentLocale));
        viewHolder.txtActive.setText(String.format(country.getTotalConfirmed().toString(), currentLocale));
        viewHolder.txtDeaths.setText(String.format(country.getTotalDeaths().toString(), currentLocale));
        viewHolder.txtRecovered.setText(String.format(country.getNewRecovered().toString(), currentLocale));
        viewHolder.txtTotalRecovered.setText(String.format(country.getTotalRecovered().toString(), currentLocale));
        viewHolder.txtCountry.setText(country.getCountry());
    }

    @Override
    public int getItemCount() {
        return CountryListFiltered.size();
    }

    /**
     * we use this to get a filtered list from the big countries list accordning to what the user typed in the search edit text
     *
     * @return A filteres list to the Search Fragment
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            // filter according to the charachters received
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    CountryListFiltered = CountryList;
                } else {
                    List<Country> filteredList = new ArrayList<>();
                    for (Country row : CountryList) {

                        // Match the charachters that the user types to the whole list and get the filtered result
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
                //TODO this is an unchecked cast but i could not find any other way to do it
                CountryListFiltered = (ArrayList<Country>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ContactsAdapterListener {
        void onContactSelected(Country contact);
    }
}