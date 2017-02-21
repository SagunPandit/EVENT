package semproject.nevent;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import java.util.ArrayList;
import java.util.Arrays;
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

    public void initializeData(String eventid,String eventname,String eventcategory,String eventlocation,String eventdate,String organizer,Integer viewcount,Context context) {
        items.add(new Item(eventid,eventname, eventcategory,eventlocation,eventdate,organizer,viewcount,context));

    }

    public List getItem(){
        return items;
    }

    private class Item {
        private String eventId,eventLabel,eventLocation,eventDate,eventOrganizer,eventCategory;
        private Context context;
        private int viewcount;

        Item(String eventid,String eventname,String eventcategory,String eventlocation,String eventdate,String eventOrganizer,Integer count,Context context) {
            this.eventId=eventid;
            this.eventLabel=eventname;
            this.eventLocation=eventlocation;
            this.eventDate=eventdate;
            this.eventOrganizer=eventOrganizer;
            this.eventCategory=eventcategory;
            this.context=context;
            viewcount=count;
        }
    }

    private static void listenerFunction(String eventname,Integer viewcount,final Context context){
        Log.e("EventRecyclerView","insideListiner");
        Response.Listener<String> responseListener= new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("EventRecyclerView","try");
                    JSONObject jsonObject=new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if(success){
                        Log.e("EventRecyclerView","insideSuccess");
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
            CountRequest countRequest=new CountRequest(eventname,viewcount,"incr",responseListener);
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(countRequest);
    }


    // Creating an Adapter i.e to add each items in recyclerView
    public static class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> implements ConnectivityReceiver.ConnectivityReceiverListener {
        String STRING_TAG= "ItemAdapter";
        /* private instance variable to store Layout of each item. */
        private LayoutInflater inflater;
        /* Store data */
        List<Item> items = Collections.emptyList();
        Item currentItem;
        private boolean checkConnection() {
            Log.e(STRING_TAG,"checkConnection");
            boolean isConnected = ConnectivityReceiver.isConnected(currentItem.context);
            if(!isConnected){
                Intent intent= new Intent(currentItem.context,InternetConnection.class);
                ((Activity)currentItem.context).finish();
                currentItem.context.startActivity(intent);
            }
            return isConnected;
        }

        @Override
        public void onNetworkConnectionChanged(boolean isConnected) {
            if(isConnected){
                Intent intent= new Intent(currentItem.context,MainActivity.class);
                ((Activity)currentItem.context).finish();
                currentItem.context.startActivity(intent);
            }
            else{
                Intent intent= new Intent(currentItem.context,InternetConnection.class);
                ((Activity)currentItem.context).finish();
                currentItem.context.startActivity(intent);
            }
        }

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
        public void onBindViewHolder(final ItemViewHolder holder, final int position) {
            Log.v(LOG_TAG, "onBindViewHolder called.");
            String defaultLabel="Activity";
            currentItem = items.get(position);

            if(holder.eventLabel.getText().equals(" "))
                holder.eventLabel.setText(defaultLabel);
            else
                holder.eventLabel.setText(currentItem.eventLabel);
            holder.eventLocation.setText(currentItem.eventLocation);
            holder.eventDate.setText(currentItem.eventDate);
            holder.eventCategory.setText(currentItem.eventCategory);
            holder.eventOrganizer.setText(currentItem.eventOrganizer);
            holder.eventId.setText(currentItem.eventId);
            holder.eventLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkConnection()){
                        Intent intent=new Intent (currentItem.context,EventDetails.class);
                        intent.putExtra("eventId",holder.eventId.getText().toString());
                        intent.putExtra("eventLabel",holder.eventLabel.getText().toString());
                        intent.putExtra("eventLocation",holder.eventLocation.getText().toString());
                        intent.putExtra("eventDate",holder.eventDate.getText().toString());
                        intent.putExtra("eventCategory",holder.eventCategory.getText().toString());
                        intent.putExtra("eventOrganizer",holder.eventOrganizer.getText().toString());
                        currentItem.context.startActivity(intent);
                    }

                }

            });
            holder.eventDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(LOG_TAG, "Item Clicked.");
                    Log.v(LOG_TAG,holder.eventLabel.getText().toString());
                    Log.v(LOG_TAG+" delete",Integer.toString(position));
                    removeAt(position,currentItem,holder);
                }
            });
            // click event handler when Item in RecyclerView is clicked

        }
        public void removeAt(final int position, final Item item,final ItemViewHolder holder ) {
            final Context context=item.context;
            AlertDialog.Builder builder= new AlertDialog.Builder(context);
            builder.setMessage("Do you really want to delete this event?")
                    .setTitle("Confirmation")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            items.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, items.size());
                            listenerFunction(context,holder);
                        }
                    })
                    .setNegativeButton("NO",null)
                    .create()
                    .show();

        }

        public void listenerFunction(final Context context,final ItemViewHolder holder){
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
            DeleteRequest deleteRequest=new DeleteRequest(holder.eventOrganizer.getText().toString()
                    ,holder.eventLabel.getText().toString()
                    ,holder.eventDate.getText().toString()
                    ,holder.eventCategory.getText().toString()
                    ,holder.eventLocation.getText().toString()
                    ,responseListener);
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(deleteRequest);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        /* ViewHolder for this adapter */
        class ItemViewHolder extends RecyclerView.ViewHolder {
            LinearLayout eventLinear;
            TextView eventLabel;
            TextView eventLocation;
            TextView eventDate;
            TextView eventOrganizer;
            ImageButton eventDelete;
            TextView eventCategory;
            TextView eventId;

            public ItemViewHolder(View itemView) {
                super(itemView);
                eventLinear=(LinearLayout) itemView.findViewById(R.id.linear1);
                eventId=(TextView) itemView.findViewById(R.id.eventId);
                eventCategory=(TextView) itemView.findViewById(R.id.eventCategory);
                eventLabel = (TextView) itemView.findViewById(R.id.eventLabel);
                eventLocation = (TextView) itemView.findViewById(R.id.eventLocation);
                eventDate=(TextView) itemView.findViewById(R.id.eventDate);
                eventOrganizer=(TextView) itemView.findViewById(R.id.eventOrganizer);
                eventDelete=(ImageButton) itemView.findViewById(R.id.eventDelete);
            }
        }
    }

    //For all users
    public static class AllItemAdapter extends RecyclerView.Adapter<AllItemAdapter.AllItemViewHolder>implements ConnectivityReceiver.ConnectivityReceiverListener{
        String STRING_TAG= "ItemAdapter";
        String[] admin={"Aayush","Sagun","Pratyush","Avash","Prabin"};
        /* private instance variable to store Layout of each item. */
        private LayoutInflater inflater;
        /* Store data */
        List<Item> items = Collections.emptyList();
        Item currentItem;
        String username;


        // Constructor to inflate layout of each item in RecyclerView
        public AllItemAdapter(Context context, List<Item> items, String name) {
            inflater = LayoutInflater.from(context);
            this.items = items;
            username=name;
        }

        private boolean checkConnection() {
            Log.e(STRING_TAG,"checkConnection");
            boolean isConnected = ConnectivityReceiver.isConnected(currentItem.context);
            if(!isConnected){
                Intent intent= new Intent(currentItem.context,InternetConnection.class);
                ((Activity)currentItem.context).finish();
                currentItem.context.startActivity(intent);
            }
            return isConnected;
        }

        @Override
        public void onNetworkConnectionChanged(boolean isConnected) {
            if(isConnected){
                Intent intent= new Intent(currentItem.context,MainActivity.class);
                currentItem.context.startActivity(intent);
                ((Activity)currentItem.context).finish();
            }
            else{
                Intent intent= new Intent(currentItem.context,InternetConnection.class);
                currentItem.context.startActivity(intent);
                ((Activity)currentItem.context).finish();
            }
        }

        //create a view holder of items
        @Override
        public AllItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.v(LOG_TAG, "onCreateViewHolder called.");
            View view = inflater.inflate(R.layout.allevents_details, parent, false);

            AllItemAdapter.AllItemViewHolder holder = new AllItemAdapter.AllItemViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(final AllItemViewHolder holder,final int position) {
            Log.v(LOG_TAG, "onBindViewHolder called.");
            String defaultLabel="Activity";
            currentItem = items.get(position);

            if(holder.eventLabel.getText().equals(" "))
                holder.eventLabel.setText(defaultLabel);
            else
                holder.eventLabel.setText(currentItem.eventLabel);
            holder.eventLocation.setText(currentItem.eventLocation);
            holder.eventDate.setText(currentItem.eventDate);
            holder.eventCategory.setText(currentItem.eventCategory);
            holder.eventOrganizer.setText(currentItem.eventOrganizer);
            holder.eventView.setText(String.valueOf(currentItem.viewcount));
            holder.eventId.setText(currentItem.eventId);
            holder.eventLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkConnection()){
                        Log.e("Count",holder.eventView.getText().toString());
                        int views=Integer.decode(holder.eventView.getText().toString());
                        views++;
                        Log.v("Count",String.valueOf(views));
                        listenerFunction(holder.eventLabel.getText().toString(),views,currentItem.context);
                        Intent intent=new Intent (currentItem.context,EventDetails.class);
                        intent.putExtra("eventId",holder.eventId.getText().toString());
                        intent.putExtra("eventLabel",holder.eventLabel.getText().toString());
                        intent.putExtra("eventLocation",holder.eventLocation.getText().toString());
                        intent.putExtra("eventDate",holder.eventDate.getText().toString());
                        intent.putExtra("eventCategory",holder.eventCategory.getText().toString());
                        intent.putExtra("eventOrganizer",holder.eventOrganizer.getText().toString());
                        currentItem.context.startActivity(intent);
                    }
                }
            });
            Log.v(STRING_TAG,username);
            if (Arrays.asList(admin).contains(username)) {
                holder.eventDelete.setVisibility(View.VISIBLE);
                holder.eventDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v(LOG_TAG, "Item Clicked.");
                        Log.v(LOG_TAG,holder.eventLabel.getText().toString());
                        removeAt(position, currentItem,holder);
                    }
                });
            }
            // click event handler when Item in RecyclerView is clicked

        }
        public void removeAt(final int position, final Item item,final AllItemViewHolder holder ) {
            final Context context=item.context;
            AlertDialog.Builder builder= new AlertDialog.Builder(context);
            builder.setMessage("Do you really want to delete this event?")
                    .setTitle("Confirmation")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            items.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, items.size());
                            deleteFunction(context,holder);
                        }
                    })
                    .setNegativeButton("NO",null)
                    .create()
                    .show();

        }

        public void deleteFunction(final Context context,final AllItemViewHolder holder){
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
            DeleteRequest deleteRequest=new DeleteRequest(holder.eventOrganizer.getText().toString()
                    ,holder.eventLabel.getText().toString()
                    ,holder.eventDate.getText().toString()
                    ,holder.eventCategory.getText().toString()
                    ,holder.eventLocation.getText().toString()
                    ,responseListener);
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(deleteRequest);
        }




        @Override
        public int getItemCount() {
            return items.size();
        }

        /* ViewHolder for this adapter */
        class AllItemViewHolder extends RecyclerView.ViewHolder {
            LinearLayout eventLinear;
            TextView eventLabel;
            TextView eventLocation;
            TextView eventDate;
            TextView eventOrganizer;
            TextView eventCategory;
            TextView eventId;
            TextView eventView;
            ImageButton eventDelete;

            public AllItemViewHolder(View itemView) {
                super(itemView);
                eventLinear=(LinearLayout) itemView.findViewById(R.id.alllinear1);
                eventId=(TextView) itemView.findViewById(R.id.alleventId);
                eventCategory=(TextView) itemView.findViewById(R.id.alleventCategory);
                eventLabel = (TextView) itemView.findViewById(R.id.alleventLabel);
                eventLocation = (TextView) itemView.findViewById(R.id.alleventLocation);
                eventDate=(TextView) itemView.findViewById(R.id.alleventDate);
                eventOrganizer=(TextView) itemView.findViewById(R.id.alleventOrganizer);
                eventView= (TextView) itemView.findViewById(R.id.alleventView);
                eventDelete=(ImageButton) itemView.findViewById(R.id.alleventDelete);
            }
        }
    }
}

