package banyan.com.anilcrm.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import banyan.com.anilcrm.R;
import banyan.com.anilcrm.adapter.Agency_Adapter;
import banyan.com.anilcrm.global.SessionManager;


/**
 * Created by Jo on 05/03/2018.
 */
public class Tab_Enquiry_Agency_Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;

    String TAG = "reg";
    public static final String TAG_AGENCY_ID = "agencies_id";
    public static final String TAG_AGENCY_NAME = "agencies_name";
    public static final String TAG_LOCATION_NAME = "location_name";
    public static final String TAG_AGENCY_OUTLET = "agencies_outlet";

    String str_user_name, str_user_id;

    ProgressDialog pDialog;
    public static RequestQueue queue;

    static ArrayList<HashMap<String, String>> complaint_list;

    HashMap<String, String> params = new HashMap<String, String>();

    public Agency_Adapter adapter;

    // Session Manager Class
    SessionManager session;

    String str_name, str_id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.tab_succeed_enquiry_layout, null);

        session = new SessionManager(getActivity());

        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
        str_name = user.get(SessionManager.KEY_USER);
        str_id = user.get(SessionManager.KEY_USER_ID);

        listView = (ListView) rootview.findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.swipe_refresh_layout);

        // Hashmap for ListView
        complaint_list = new ArrayList<HashMap<String, String>>();

        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        try {
                                            pDialog = new ProgressDialog(getActivity());
                                            pDialog.setMessage("Please wait...");
                                            pDialog.setCancelable(true);
                                            queue = Volley.newRequestQueue(getActivity());
                                            GetMyAgency();

                                        } catch (Exception e) {
                                            // TODO: handle exception
                                        }
                                    }
                                }
        );

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


            }

        });

        return rootview;
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        try {
            complaint_list.clear();
            queue = Volley.newRequestQueue(getActivity());
            GetMyAgency();

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /*****************************
     * GET REQ
     ***************/

    public void GetMyAgency() {

        String tag_json_obj = "json_obj_req";

        System.out.println("CAME DA 1" + AppConfig.url_list_location_individual);

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_list_location_individual, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("success");

                    if (success == 1) {

                        JSONArray arr;

                        arr = obj.getJSONArray("message");

                        for (int i = 0; arr.length() > i; i++) {
                            JSONObject obj1 = arr.getJSONObject(i);

                            String id = obj1.getString(TAG_AGENCY_ID);
                            String name = obj1.getString(TAG_AGENCY_NAME);
                            String loc_name = obj1.getString(TAG_LOCATION_NAME);
                            String outlet = obj1.getString(TAG_AGENCY_OUTLET);

                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();

                            // adding each child node to HashMap key => value
                            map.put(TAG_AGENCY_ID, id);
                            map.put(TAG_AGENCY_NAME, name);
                            map.put(TAG_LOCATION_NAME, loc_name);
                            map.put(TAG_AGENCY_OUTLET, outlet);

                            complaint_list.add(map);

                            System.out.println("HASHMAP ARRAY" + complaint_list);


                            adapter = new Agency_Adapter(getActivity(),
                                    complaint_list);
                            listView.setAdapter(adapter);

                        }

                    } else if (success == 0) {

                        TastyToast.makeText(getActivity(), "No Agencies Found", TastyToast.LENGTH_SHORT, TastyToast.INFO);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // adapter.notifyDataSetChanged();
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
                //  pDialog.hide();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                //Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();

                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", str_id);

                System.out.println("USER ID ::: " + str_id);

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }
}
