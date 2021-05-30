package eu.kozaris.covidApp.adepter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import eu.kozaris.covidApp.R;

/**
 * This adapter takes Contact items and fills the recent contacts recyclerview with a list of contact points
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private final List<String> mListIds;
    private final List<String> mListLocations;
    private final List<String> mListStatuses;
    private final LayoutInflater mInflater;

    //Lists data is passed (from Firestore probably) into the constructor
  public  ContactsAdapter(Context context, List<String> listIds,List<String> listLocations,List<String> listStatuses) {
        this.mInflater = LayoutInflater.from(context);
        this.mListIds = listIds;
        this.mListLocations = listLocations;
        this.mListStatuses = listStatuses;
    }

    //This Inflate the row layout from the xml
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_row_contact, parent, false);
        return new ViewHolder(view);
    }

    // This binds the data to the TextViews in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String id = mListIds.get(position);
        String location = mListLocations.get(position);
        String status = mListStatuses.get(position);
        holder.myTextViewIDs.setText(id);
        holder.myTextViewLocations.setText(location);

        //The status variable will come as true or false, so we make this bool human readable for the textview
        if (status.equals("true")){
            holder.myTextViewStatuses.setText("Infected");
            holder.myTextViewStatuses.setTextColor(Color.RED);
        }else {
            holder.myTextViewStatuses.setText("Covid Free");
            holder.myTextViewStatuses.setTextColor(Color.GREEN);

        }
    }

    // Total number of rows in the Recyclerview
    @Override
    public int getItemCount() {
        return mListIds.size();
    }


    // Stores and Recycler's views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView myTextViewIDs;
        TextView myTextViewLocations;
        TextView myTextViewStatuses;

        ViewHolder(View itemView) {
            super(itemView);
            myTextViewIDs = itemView.findViewById(R.id.textViewContactID);
            myTextViewLocations = itemView.findViewById(R.id.textViewContactLocation);
            myTextViewStatuses = itemView.findViewById(R.id.textViewContactStatus);
        }
    }
}