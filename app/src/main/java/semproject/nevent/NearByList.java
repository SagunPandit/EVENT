package semproject.nevent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static semproject.nevent.HomePage.staticadapter;
import static semproject.nevent.HomePage.staticeventRecyclerView;
import static semproject.nevent.ShowEvents.distance;
import static semproject.nevent.ShowEvents.la;
import static semproject.nevent.ShowEvents.ln;

import static semproject.nevent.Recent.extracteventCategory;
import static semproject.nevent.Recent.extracteventDate;
import static semproject.nevent.Recent.extracteventId;
import static semproject.nevent.Recent.extracteventList;
import static semproject.nevent.Recent.extracteventLocation;
import static semproject.nevent.Recent.extracteventOrganizer;
import static semproject.nevent.Recent.extractlatitude;
import static semproject.nevent.Recent.extractlongitude;
import static semproject.nevent.Recent.extractviewcount;


/**
 * Created by Aayush on 3/11/2017.
 */

public class NearByList extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener {
    private RecyclerView mRecyclerView;
    String STRING_TAG="NearByList";
    String username;
    public static List<String> neareventId=new ArrayList<>();
    public static List<String>neareventList=new ArrayList<>();
    public static List<String>neareventLocation=new ArrayList<>();
    public static List<String>neareventDate=new ArrayList<>();
    public static List<String>neareventCategory=new ArrayList<>();
    public static List<String>neareventOrganizer=new ArrayList<>();
    public static List<Integer>nearviewcount=new ArrayList<>();
    public static List<Double>nearlatitude=new ArrayList<>();
    public static List<Double>nearlongitude=new ArrayList<>();



    public NearByList() {
        // Required empty public constructor
        staticeventRecyclerView=new EventRecyclerView();
        staticadapter=new EventRecyclerView.AllItemAdapter();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        username = getArguments().getString("username");
        View rootView = inflater.inflate(R.layout.fragment_recent, container, false);

        // BEGIN_INCLUDE(initializeRecyclerView)
        RecyclerView.LayoutManager mLayoutManager;
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.all_recycler_view);
        if (mRecyclerView != null) {
            mRecyclerView.setHasFixedSize(true);
        }


        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        listenerFunction();
        return rootView;
    }
    public void retreiveFromDatabase(){
        float dist;
        Log.e(STRING_TAG,"database");
        if(checkConnection(getContext())){
            for (int i=0;i < neareventList.size();i++)
            {
                Log.i("CheckIndexRemove "+i," "+neareventList.get(i));
                dist=distance.get(i)/1000;
                DecimalFormat numberformat= new DecimalFormat("#.00");
                dist=Float.parseFloat(numberformat.format(dist));
                staticeventRecyclerView.initializeData(neareventId.get(i),neareventList.get(i),neareventCategory.get(i),neareventLocation.get(i),neareventDate.get(i),neareventOrganizer.get(i),nearviewcount.get(i),getContext(),dist);
                staticadapter = new EventRecyclerView.AllItemAdapter(getContext(), staticeventRecyclerView.getItem(),username,true);
                mRecyclerView.setAdapter(staticadapter);
            }
            neareventId=new ArrayList<>();
            neareventList=new ArrayList<>();
            neareventLocation=new ArrayList<>();
            neareventDate=new ArrayList<>();
            neareventCategory=new ArrayList<>();
            neareventOrganizer=new ArrayList<>();
            nearviewcount=new ArrayList<>();
            nearlatitude=new ArrayList<>();
            nearlongitude=new ArrayList<>();

        }

    }

    public void listenerFunction(){
        Log.e(STRING_TAG,"insideListiner");
        int out=0;
        for(double lat1: la){
            int inside=0;
            if(extractlatitude.isEmpty())
                Log.e("Insideextractby","Null");
            else{
                Log.e("InsideNearby","NOTNULL");
                for(double lat2:extractlatitude){
                    Log.e("CheckIndex ",Double.toString(lat1)+" "+Double.toString(lat2));
                    if((Double.compare(lat1,lat2)==0)&&(Double.compare(ln.get(out),extractlongitude.get(inside))==0)){
                        Log.e("InsideChck ",Integer.toString(inside)+" "+extracteventList.get(inside));
                        nearlatitude.add(extractlatitude.get(inside));
                        nearlongitude.add(extractlongitude.get(inside));
                        neareventList.add(extracteventList.get(inside));
                        neareventCategory.add(extracteventCategory.get(inside));
                        neareventDate.add(extracteventDate.get(inside));
                        neareventId.add(extracteventId.get(inside));
                        neareventLocation.add(extracteventLocation.get(inside));
                        neareventOrganizer.add(extracteventOrganizer.get(inside));
                        nearviewcount.add(extractviewcount.get(inside));
                    }
                    inside++;
                }
            }

            out++;
        }

        retreiveFromDatabase();
    }

    private boolean checkConnection(Context context) {
        Log.e(STRING_TAG,"checkConnection");
        boolean isConnected = ConnectivityReceiver.isConnected(context);
        if(!isConnected){
            Intent intent= new Intent(getContext(),InternetConnection.class);
            getActivity().finish();
            startActivity(intent);
        }
        return isConnected;
    }
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(isConnected){
            Intent intent= new Intent(getContext(),MainActivity.class);
            getActivity().finish();
            startActivity(intent);
        }
        else{
            Intent intent= new Intent(getContext(),InternetConnection.class);
            getActivity().finish();
            startActivity(intent);

        }
    }
}
