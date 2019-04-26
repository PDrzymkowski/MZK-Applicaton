package com.example.piotr.mzkapi15;


import android.content.Context;
import android.graphics.Typeface;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class PlaceAutoCompleteAdapter
        extends ArrayAdapter<AutocompletePrediction> implements Filterable {

    private static final String TAG = "...";
    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);

    private ArrayList<AutocompletePrediction> resultList;
    private LatLngBounds bounds;
    private AutocompleteFilter placeFilter;
    private GeoDataClient client;






    /**
     * Initializes with a resource for text rows and autocomplete query bounds.
     *
     * @see android.widget.ArrayAdapter#ArrayAdapter(android.content.Context, int)
     */
    public PlaceAutoCompleteAdapter(Context context, GeoDataClient geoDataClient,
                                    LatLngBounds bounds, AutocompleteFilter filter) {
        super(context, android.R.layout.simple_expandable_list_item_2, android.R.id.text1);
        placeFilter = filter;
        this.bounds = bounds;
        client = geoDataClient;

    }


    public void setBounds(LatLngBounds bounds) {
        bounds = bounds;
    }


    @Override
    public int getCount() {
        return resultList.size();
    }


    @Override
    public AutocompletePrediction getItem(int position) {
        return resultList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = super.getView(position, convertView, parent);

        // Sets the primary and secondary text for a row.
        // Note that getPrimaryText() and getSecondaryText() return a CharSequence that may contain
        // styling based on the given CharacterStyle.

        AutocompletePrediction item = getItem(position);

        TextView textView1 = (TextView) row.findViewById(android.R.id.text1);
        TextView textView2 = (TextView) row.findViewById(android.R.id.text2);
        textView1.setText(item.getPrimaryText(STYLE_BOLD));
        textView2.setText(item.getSecondaryText(STYLE_BOLD));

        return row;


    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                // We need a separate list to store the results, since
                // this is run asynchronously.
                ArrayList<AutocompletePrediction> filterData = new ArrayList<>();

                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    try{

                        filterData = getAutocomplete(constraint);
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                }

                results.values = filterData;
                if (filterData != null) {
                    results.count = filterData.size();
                } else {
                    results.count = 0;
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.


                   resultList = (ArrayList<AutocompletePrediction>) results.values;

                    List<Integer> positions = new ArrayList<>();

                    for(int i = 0; i<resultList.size(); i++) {

                        if( !resultList.get(i).getSecondaryText(STYLE_BOLD).toString().contains("Ełk") &&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Bajtkowo") &&
                                !resultList.get(i).getSecondaryText(STYLE_BOLD).toString().contains("Bajtkowo") &&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Barany")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Bartosze")&&
                                !resultList.get(i).getSecondaryText(STYLE_BOLD).toString().contains("Bartosze")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Białojany")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Chrzanowo")&&
                                !resultList.get(i).getSecondaryText(STYLE_BOLD).toString().contains("Chrzanowo")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Chełchy")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Chruściele")&&
                                !resultList.get(i).getSecondaryText(STYLE_BOLD).toString().contains("Chruściele")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Czerwonka")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Buniaki")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Guzki")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Janisze")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Judziki")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Juranda")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Kałęczyny")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Lega")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Liski")&&
                                !resultList.get(i).getSecondaryText(STYLE_BOLD).toString().contains("Malinówka")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Malinówka")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Miluki")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Mrozy")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Mostołty")&&
                                !resultList.get(i).getSecondaryText(STYLE_BOLD).toString().contains("Mostołty")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Nowa Wieś Ełcka")&&
                                !resultList.get(i).getSecondaryText(STYLE_BOLD).toString().contains("Nowa Wieś Ełcka")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Oracze")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Piaski")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Płociczno")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Ruska Wieś") &&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Regiel")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Siedliska")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Sędki")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Sajzy")&&
                                !resultList.get(i).getSecondaryText(STYLE_BOLD).toString().contains("Sajzy")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Rożyńsk")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Straduny")&&
                                !resultList.get(i).getSecondaryText(STYLE_BOLD).toString().contains("Straduny")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Szeligi")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Szarejki")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Wityny")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Woszczele")&&
                                !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Liski")&&
                           !resultList.get(i).getPrimaryText(STYLE_BOLD).toString().contains("Talusy")){
                            positions.add(i);
                        }
                    }

                    if(positions.size() >0) {
                        for (int n = positions.size() - 1; n >= 0; n--) {

                            resultList.remove((int) (positions.get(n)));

                        }
                    }
                    positions.clear();
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    notifyDataSetInvalidated();
                }
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                // Override this method to display a readable result in the AutocompleteTextView
                // when clicked.
                if (resultValue instanceof AutocompletePrediction) {
                    return ((AutocompletePrediction) resultValue).getFullText(null);
                } else {
                    return super.convertResultToString(resultValue);
                }
            }
        };
    }

    /**
     * Submits an autocomplete query to the Places Geo Data Autocomplete API.
     * Results are returned as frozen AutocompletePrediction objects, ready to be cached.
     * Returns an empty list if no results were found.
     * Returns null if the API client is not available or the query did not complete
     * successfully.
     * This method MUST be called off the main UI thread, as it will block until data is returned
     * from the API, which may include a network request.
     *
     * @param constraint Autocomplete query string
     * @return Results from the autocomplete API or null if the query was not successful.
     * @see GeoDataClient#getAutocompletePredictions(String, LatLngBounds, AutocompleteFilter)
     * @see AutocompletePrediction#freeze()
     */
    private ArrayList<AutocompletePrediction> getAutocomplete(CharSequence constraint) {

        // Submit the query to the autocomplete API and retrieve a PendingResult that will
        // contain the results when the query completes.

        Task<AutocompletePredictionBufferResponse> results =
                client.getAutocompletePredictions(constraint.toString(),bounds,
                        placeFilter);


        // This method should have been called off the main UI thread. Block and wait for at most
        // 60s for a result from the API.
        try {
            Tasks.await(results, 60, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }

        try {
            AutocompletePredictionBufferResponse autocompletePredictions = results.getResult();


            // Freeze the results immutable representation that can be stored safely.
            return DataBufferUtils.freezeAndClose(autocompletePredictions);
        } catch (RuntimeExecutionException e) {
            // If the query did not complete successfully return null
            Toast.makeText(getContext(), "Error contacting API: " + e.toString(),
                    Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error getting autocomplete prediction API call", e);
            return null;
        }
    }
}
