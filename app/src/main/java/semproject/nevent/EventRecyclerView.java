package semproject.nevent;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by User on 12/26/2016.
 */

public class EventRecyclerView {
    String STRING_TAG= "EventRecyclerView";
    private final static String LOG_TAG = EventRecyclerView.class.getSimpleName();
    private final static String LOG_TAGS="Holder name down";
    private List<Item> items = new ArrayList<>();

    public EventRecyclerView() {

    }

    public void initializeData(String eventid,String eventname,String eventcategory,String eventlocation,String eventdate,String organizer,Integer viewcount,Context context) {
        items.add(new Item(eventid,eventname, eventcategory,eventlocation,eventdate,organizer,viewcount,context));
        Log.e(STRING_TAG,eventname+" data initialized");

    }

    public List getItem(){
        return items;
    }

    public class Item {
        public String eventLabel;
        private String eventId,eventLocation,eventDate,eventOrganizer,eventCategory;
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
        String eventnamedelete;
        private static final String SERVER_ADDRESS="http://avashadhikari.com.np/";
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
            String eventname;
            currentItem = items.get(position);

            if(holder.eventLabel.getText().equals(" "))
                holder.eventLabel.setText(defaultLabel);
            else


            if(currentItem.eventLabel.contains(" "))
            {
                eventname = currentItem.eventLabel.replaceAll(" ", "_");
                holder.downloadedimage.setTag(eventname);
            }
            else {
                holder.downloadedimage.setTag(currentItem.eventLabel);
            }
            holder.downloadedimage.setVisibility(View.GONE);
            new Downloadimage(holder, currentItem.eventLabel, position).execute();
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
                    holder.downloadedimage.setImageBitmap(null);
                }
            });
            // click event handler when Item in RecyclerView is clicked

        }
        public void removeAt(final int position, final Item item,final ItemViewHolder holder ) {
            final Context context=item.context;
            AlertDialog.Builder builder= new AlertDialog.Builder(item.context);
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
            if(holder.eventLabel.getText().toString().contains(" "))
            {
                eventnamedelete = holder.eventLabel.getText().toString().replaceAll(" ", "_");
                new Deleteimage(eventnamedelete).execute();
            }
            else
            {
                new Deleteimage(holder.eventLabel.getText().toString()).execute();
            }


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
            ImageView downloadedimage;
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
                downloadedimage=(ImageView) itemView.findViewById(R.id.downloadedpicture);
            }
        }


        //For retrieving the image of event.
        private class Downloadimage extends AsyncTask<Void, Void, Bitmap>
        {
            String event_name;
            int position;
            ItemViewHolder holder;
            public Downloadimage(ItemViewHolder holder, String name, int position)
            {
                this.position=position;
                this.holder=holder;
                this.event_name=name;
            }
            @Override
            protected Bitmap doInBackground(Void... params) {
                if(event_name.contains(" "))
                {
                    event_name = event_name.replaceAll(" ", "_");
                }
                String url=SERVER_ADDRESS+"pictures/eventimages/"+event_name+".JPG";
                try{
                    URLConnection connection=new URL(url).openConnection();
                    connection.setConnectTimeout(1000*30);
                    connection.setReadTimeout(1000*30);
                    return BitmapFactory.decodeStream((InputStream) connection.getContent(),null,null);

                }catch(Exception e){
                    e.printStackTrace();
                    return null;
                }

            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if(bitmap!=null && holder.downloadedimage.getTag().toString().equals(event_name))
                {
                    Log.v(LOG_TAGS, "Photo received.");

                    holder.downloadedimage.setVisibility(View.VISIBLE);
                    holder.downloadedimage.setImageBitmap(bitmap);
                }
                else
                {
                    holder.downloadedimage.setVisibility(View.GONE);
                }
            }
        }

        private class Deleteimage extends AsyncTask<Void,Void,Void>
        {
            String name;
            public Deleteimage(String name)
            {
                this.name=name;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

            }

            @Override
            protected Void doInBackground(Void... params) {

                ArrayList<NameValuePair> datatosend=new ArrayList<>();
                datatosend.add(new BasicNameValuePair("name",name));

                HttpParams httpRequestParams=getHttpRequestParams();
                HttpClient client=new DefaultHttpClient(httpRequestParams);
                HttpPost post=new HttpPost(SERVER_ADDRESS + "Deletephoto.php");

                try{
                    post.setEntity(new UrlEncodedFormEntity(datatosend));
                    client.execute(post);

                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                return null;
            }
        }


        private HttpParams getHttpRequestParams()
        {
            HttpParams httpRequestParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams,1000*30);
            HttpConnectionParams.setSoTimeout(httpRequestParams,1000*30);
            return httpRequestParams;
        }
    }

    //For all events
    public static class AllItemAdapter extends RecyclerView.Adapter<AllItemAdapter.AllItemViewHolder>implements ConnectivityReceiver.ConnectivityReceiverListener{
        String STRING_TAG= "ItemAdapter";

        private static final String SERVER_ADDRESS="http://avashadhikari.com.np/";
        String[] admin={"Aayush","Sagun","Pratyush","Avash","Prabin"};
        /* private instance variable to store Layout of each item. */
        private LayoutInflater inflater;
        /* Store data */
        List<Item> items = Collections.emptyList();
        Item currentItem;
        String username;
        private int check=10;

        public AllItemAdapter(){
           /* Log.e(STRING_TAG,Integer.toString(check));
            check++;*/
        }
        // Constructor to inflate layout of each item in RecyclerView
        public AllItemAdapter(Context context, List<Item> items, String name) {
            inflater = LayoutInflater.from(context);
            this.items = items;
            username=name;
           /* Log.e(STRING_TAG,"itemadpter "+Integer.toString(check));*/

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
            String eventname;
            currentItem = items.get(position);

            if(holder.eventLabel.getText().equals(" "))
                holder.eventLabel.setText(defaultLabel);
            else

            if(currentItem.eventLabel.contains(" "))
            {
                eventname = currentItem.eventLabel.replaceAll(" ", "_");
                holder.downloadedimage.setTag(eventname);
            }
            else {
                holder.downloadedimage.setTag(currentItem.eventLabel);
            }

            holder.downloadedimage.setVisibility(View.GONE);
            new Downloadimage(holder, currentItem.eventLabel, position).execute();
            holder.eventLabel.setText(currentItem.eventLabel);
            holder.eventLocation.setText(currentItem.eventLocation);
            holder.eventDate.setText(currentItem.eventDate);
            holder.eventCategory.setText(currentItem.eventCategory);
            holder.eventOrganizer.setText(currentItem.eventOrganizer);
            holder.eventView.setText(String.valueOf(currentItem.viewcount));
            holder.eventId.setText(currentItem.eventId);
            Log.v("Holder name up", holder.eventLabel.getText().toString());

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
                        Log.v(LOG_TAG+" delete",Integer.toString(position));
                        removeAt(position,currentItem,holder);
                        holder.downloadedimage.setImageBitmap(null);
                    }
                });
            }
            // click event handler when Item in RecyclerView is clicked

        }
        public void removeAt(final int position, final Item item,final AllItemViewHolder holder ) {
            final Context context=item.context;
            AlertDialog.Builder builder= new AlertDialog.Builder(item.context);
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
       /* public void removeAt(final int position, final Item item) {
            AlertDialog.Builder builder= new AlertDialog.Builder(item.context);
            builder.setMessage("Do you really want to delete this event?")
                    .setTitle("Confirmation")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            items.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, items.size());
                            deleteFunction(item);
                        }
                    })
                    .setNegativeButton("NO",null)
                    .create()
                    .show();

        }*/

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
                            String toastMesg = "You have sucessfully deleted an event.";
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
            DeleteRequest deleteRequest=new DeleteRequest(holder.eventOrganizer.getText().toString(),
                    holder.eventLabel.getText().toString(),
                    holder.eventDate.getText().toString(),
                    holder.eventCategory.getText().toString(),
                    holder.eventLocation.getText().toString(), responseListener);
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
            ImageView downloadedimage;
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
                downloadedimage=(ImageView) itemView.findViewById(R.id.alldownloadedimage);

            }
        }

        public void setFilter(List<Item> searchitem){
            if(items.isEmpty())
                Log.e(STRING_TAG,"SetFilter empty");
            //Log.e(STRING_TAG,"SetFilter "+Integer.toString(check));
            items=Collections.emptyList();
            items=searchitem;

            notifyDataSetChanged();
        }

        //For retrieving the image of event.
        private class Downloadimage extends AsyncTask<Void, Void, Bitmap>
        {
            String name;
            int position;
            AllItemViewHolder holder;
            public Downloadimage(AllItemViewHolder holder,String name, int position)

            {
                this.position=position;
                this.holder=holder;
                this.name=name;
            }
            @Override
            protected Bitmap doInBackground(Void... params) {
                if(name.contains(" "))
                {
                    name = name.replaceAll(" ", "_");
                }

                String url=SERVER_ADDRESS+"pictures/eventimages/"+name+".JPG";

                try{
                    URLConnection connection=new URL(url).openConnection();
                    connection.setConnectTimeout(1000*30);
                    connection.setReadTimeout(1000*30);
                    return BitmapFactory.decodeStream((InputStream) connection.getContent(),null,null);

                }catch(Exception e){

                    return null;
                }

            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                Log.e("Holder set tag name _",holder.downloadedimage.getTag().toString());
                if(bitmap!=null && holder.downloadedimage.getTag().toString().equals(name))
                {

                    Log.v(LOG_TAGS, holder.eventLabel.getText().toString());

                    //holder.downloadedimage.getLayoutParams().height = 90;
                    holder.downloadedimage.setVisibility(View.VISIBLE);
                    holder.downloadedimage.setImageBitmap(bitmap);

                }
                else
                {
                    holder.downloadedimage.setVisibility(View.GONE);
                }

            }
        }

    }
}

