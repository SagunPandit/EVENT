package semproject.nevent;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by User on 12/26/2016.
 */

public class EventRecyclerView {
    String STRING_TAG= "EventRecyclerView";
    private final static String LOG_TAG = EventRecyclerView.class.getSimpleName();
    private List<Item> items = new ArrayList<>();

    public EventRecyclerView() {

    }

    public void initializeData(String eventname,String eventcategory,String eventlocation,String eventdate,String organizer,Context context) {
        items.add(new Item(eventname, eventcategory,eventlocation,eventdate,organizer,context));

    }

    public List getItem(){
        return items;
    }
    private class Item {
        private String eventLabel,eventLocation,eventDate,eventOrganizer,eventCategory;
        private Context context;

        Item(String eventname,String eventcategory,String eventlocation,String eventdate,String eventOrganizer,Context context) {
            this.eventLabel=eventname;
            this.eventLocation=eventlocation;
            this.eventDate=eventdate;
            this.eventOrganizer=eventOrganizer;
            this.eventCategory=eventcategory;
            this.context=context;
        }
    }

    // Creating an Adapter i.e to add each items in recyclerView
    public static class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder>{
        String STRING_TAG= "ItemAdapter";
        /* private instance variable to store Layout of each item. */
        private LayoutInflater inflater;
        /* Store data */
        List<Item> items = Collections.emptyList();

        // Constructor to inflate layout of each item in RecyclerView
        public ItemAdapter(Context context, List<Item> items) {
            inflater = LayoutInflater.from(context);
            this.items = items;
        }

        //create a view holder of items
        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.v(LOG_TAG, "onCreateViewHolder called.");
            View view = inflater.inflate(R.layout.events_details, parent, false);

            ItemViewHolder holder = new ItemViewHolder(view);

            return holder;
        }

        //binds all the views from view holder to form a single view and show the binded view
        @Override
        public void onBindViewHolder(ItemViewHolder holder, final int position) {
            Log.v(LOG_TAG, "onBindViewHolder called.");
            String defaultLabel="Activity";
            final Item currentItem = items.get(position);

            if(holder.eventLabel.getText().equals(" "))
                holder.eventLabel.setText(defaultLabel);
            else
                holder.eventLabel.setText(currentItem.eventLabel);
            holder.eventLocation.setText(currentItem.eventLocation);
            holder.eventDate.setText(currentItem.eventDate);
            holder.eventCategory.setText(currentItem.eventCategory);
            holder.eventOrganizer.setText(currentItem.eventOrganizer);
            holder.eventDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(LOG_TAG, "Item Clicked.");
                    removeAt(position,currentItem);
                }
            });
            // click event handler when Item in RecyclerView is clicked

        }
        public void removeAt(final int position, final Item item) {
            AlertDialog.Builder builder= new AlertDialog.Builder(item.context);
            builder.setMessage("Do you really want to delete this event?")
                    .setTitle("Confirmation")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            items.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, items.size());
                            listenerFunction(item);
                        }
                    })
                    .setNegativeButton("NO",null)
                    .create()
                    .show();

        }

        public void listenerFunction(Item item){
            final Context context=item.context;
            Log.e(STRING_TAG,"insideListiner");
            Response.Listener<String> responseListener= new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.e(STRING_TAG,"try");
                        JSONObject jsonObject=new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        if(success){
                            Log.e(STRING_TAG,"insideSuccess");
                            String toastMesg = "You have deleted your event from database";
                            Toast toast = Toast.makeText(context, toastMesg, Toast.LENGTH_SHORT);
                            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                            if (v != null) v.setGravity(Gravity.CENTER);
                            toast.show();
                        }
                        else {
                            AlertDialog.Builder builder= new AlertDialog.Builder(context);
                            builder.setMessage("Connection Failed")
                                    .setNegativeButton("Retry",null)
                                    .create()
                                    .show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            DeleteRequest deleteRequest=new DeleteRequest(item.eventOrganizer,item.eventLabel,item.eventDate,item.eventCategory,item.eventLocation, responseListener);
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(deleteRequest);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        /* ViewHolder for this adapter */
        class ItemViewHolder extends RecyclerView.ViewHolder {

            TextView eventLabel;
            TextView eventLocation;
            TextView eventDate;
            TextView eventOrganizer;
            ImageButton eventDelete;
            TextView eventCategory;

            public ItemViewHolder(View itemView) {
                super(itemView);

                eventCategory=(TextView) itemView.findViewById(R.id.eventCategory);
                eventLabel = (TextView) itemView.findViewById(R.id.eventLabel);
                eventLocation = (TextView) itemView.findViewById(R.id.eventLocation);
                eventDate=(TextView) itemView.findViewById(R.id.eventDate);
                eventOrganizer=(TextView) itemView.findViewById(R.id.eventOrganizer);
                eventDelete=(ImageButton) itemView.findViewById(R.id.eventDelete);
            }
        }
    }
}

