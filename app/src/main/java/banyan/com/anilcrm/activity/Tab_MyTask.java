package banyan.com.anilcrm.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import banyan.com.anilcrm.R;
import banyan.com.anilcrm.adapter.Chat_Adapter;
import banyan.com.anilcrm.global.SessionManager;


/**
 * Created by Jo on 05/03/2018.
 */
public class Tab_MyTask extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;

    String TAG = "reg";
    public static final String TAG_SCHEDULE_ID = "schedule_id";
    public static final String TAG_SCHEDULE_TITLE = "schedule_title";
    public static final String TAG_SCHEDULE_DES = "schedule_description";
    public static final String TAG_SCHEDULE_NOTE = "schedule_note";
    public static final String TAG_SCHEDULE_DATE = "schedule_date";

    String str_user_name, str_user_id;

    ProgressDialog pDialog;
    public static RequestQueue queue;

    static ArrayList<HashMap<String, String>> complaint_list;

    HashMap<String, String> params = new HashMap<String, String>();

    public Chat_Adapter adapter;

    // Session Manager Class
    SessionManager session;

    String str_name, str_id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_task, null);

        session = new SessionManager(getActivity());

        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
        str_name = user.get(SessionManager.KEY_USER);
        str_id = user.get(SessionManager.KEY_USER_ID);

        listView = (ListView) rootView.findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);

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
                                            makeJsonGETObjectRequest();

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

                String str_select_schedule_id = complaint_list.get(position).get(TAG_SCHEDULE_ID);
                String str_select_schedule_name = complaint_list.get(position).get(TAG_SCHEDULE_TITLE);
                String str_select_schedule_description = complaint_list.get(position).get(TAG_SCHEDULE_DES);
                String str_select_schedule_note = complaint_list.get(position).get(TAG_SCHEDULE_NOTE);
                String str_select_schedule_update = complaint_list.get(position).get(TAG_SCHEDULE_DATE);

                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("schedule_id", str_select_schedule_id);
                editor.putString("schedule_name", str_select_schedule_name);
                editor.putString("schedule_description", str_select_schedule_description);
                editor.putString("schedule_note", str_select_schedule_note);
                editor.putString("schedule_update", str_select_schedule_update);
                editor.commit();

                Intent i = new Intent(getActivity(), Activity_Task_Description.class);
                startActivity(i);
            }

        });

        return rootView;
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        try {
            complaint_list.clear();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(true);
            queue = Volley.newRequestQueue(getActivity());
            makeJsonGETObjectRequest();

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /*****************************
     * GET REQ
     ***************/

    public void makeJsonGETObjectRequest() {

        String tag_json_obj = "json_obj_req";

        System.out.println("CAME DA 1" + AppConfig.url_tasklist);

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_tasklist, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("success");

                    if (success == 1) {

                        JSONArray arr;

                        arr = obj.getJSONArray("schedule");

                        for (int i = 0; arr.length() > i; i++) {
                            JSONObject obj1 = arr.getJSONObject(i);

                            String id = obj1.getString(TAG_SCHEDULE_ID);
                            String title = obj1.getString(TAG_SCHEDULE_TITLE);
                            String description = obj1.getString(TAG_SCHEDULE_DES);
                            String note = obj1.getString(TAG_SCHEDULE_NOTE);
                            String date = obj1.getString(TAG_SCHEDULE_DATE);

                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();

                            // adding each child node to HashMap key => value
                            map.put(TAG_SCHEDULE_ID, id);
                            map.put(TAG_SCHEDULE_TITLE, title);
                            map.put(TAG_SCHEDULE_DES, description);
                            map.put(TAG_SCHEDULE_NOTE, note);
                            map.put(TAG_SCHEDULE_DATE, date);

                            complaint_list.add(map);

                            System.out.println("HASHMAP ARRAY" + complaint_list);


                            adapter = new Chat_Adapter(getActivity(),
                                    complaint_list);
                            listView.setAdapter(adapter);

                        }

                    }else if (success == 0){

                        TastyToast.makeText(getActivity(), "No Data Found", TastyToast.LENGTH_SHORT, TastyToast.INFO);

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

               // Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();

                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", str_id);

                return checkParams(params);
            }

            private Map<String, String> checkParams(Map<String, String> map) {
                Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
                    if (pairs.getValue() == null) {
                        map.put(pairs.getKey(), "");
                    }
                }
                return map;
            }
        };

        int socketTimeout = 60000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        // Adding request to request queue
        queue.add(request);
    }



}
