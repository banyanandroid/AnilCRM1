package banyan.com.anilcrm.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import banyan.com.anilcrm.R;
import banyan.com.anilcrm.adapter.Enquiry_Adapter;
import banyan.com.anilcrm.global.SessionManager;
import com.github.fabtransitionactivity.SheetLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Jo on 05/03/2018.
 */
public class Tab_Enquiry_Shop_Fragment extends Fragment implements SheetLayout.OnFabAnimationEndListener, SwipeRefreshLayout.OnRefreshListener {

    SheetLayout mSheetLayout;
    FloatingActionButton mFab;

    ProgressDialog pDialog;
    public static RequestQueue queue;

    // Session Manager Class
    SessionManager session;

    String TAG = "add task";

    private ListView List;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static final String TAG_SHOP_ID = "shop_id";
    public static final String TAG_AGENCY_NAME = "agencies_name";
    public static final String TAG_SHOP_NAME = "shop_name";
    public static final String TAG_SHOP_OWNER_NAME = "shop_owner_name";
    public static final String TAG_SHOP_CONTACT = "shop_contact";
    public static final String TAG_SHOP_LANDLINE = "shop_landline";
    public static final String TAG_SHOP_LOCATION = "shop_location";
    public static final String TAG_SHOP_PHOTOS = "shop_photos";
    public static final String TAG_SHOP_PRECIOUS= "shop_previous";
    public static final String TAG_SHOP_TYPE = "shop_type";
    public static final String TAG_REMARK = "remarks";

    static ArrayList<HashMap<String, String>> complaint_list;

    HashMap<String, String> params = new HashMap<String, String>();

    public Enquiry_Adapter adapter;

    String str_select_task_id;

    private static final int REQUEST_CODE = 1;
    String str_user_name, str_user_id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.tab_new_enquiry_layout, null);

        // ButterKnife.bind(getActivity());

        // Session Manager
        session = new SessionManager(getActivity());

        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
        str_user_name = user.get(SessionManager.KEY_USER);
        str_user_id = user.get(SessionManager.KEY_USER_ID);

        // Hashmap for ListView
        complaint_list = new ArrayList<HashMap<String, String>>();

        mFab = (FloatingActionButton) rootview.findViewById(R.id.fab_add_task);
        mSheetLayout = (SheetLayout) rootview.findViewById(R.id.bottom_sheet);

        mSheetLayout.setFab(mFab);
        mSheetLayout.setFabAnimationEndListener(this);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSheetLayout.expandFab();
            }
        });

        List = (ListView) rootview.findViewById(R.id.alloted_comp_listView);
        swipeRefreshLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.alloted_comp_swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        try {
                                            queue = Volley.newRequestQueue(getActivity());
                                            GetMyNewEnquiries();

                                        } catch (Exception e) {
                                            // TODO: handle exceptions
                                        }
                                    }
                                }
        );

        List.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String shop_id = complaint_list.get(position).get(TAG_SHOP_ID);
                String agencies_name = complaint_list.get(position).get(TAG_AGENCY_NAME);
                String shop_name = complaint_list.get(position).get(TAG_SHOP_NAME);
                String shop_owner_name = complaint_list.get(position).get(TAG_SHOP_OWNER_NAME);
                String shop_contact = complaint_list.get(position).get(TAG_SHOP_CONTACT);
                String shop_landline = complaint_list.get(position).get(TAG_SHOP_LANDLINE);
                String shop_location = complaint_list.get(position).get(TAG_SHOP_LOCATION);
                String shop_photos = complaint_list.get(position).get(TAG_SHOP_PHOTOS);
                String shop_previous = complaint_list.get(position).get(TAG_SHOP_PRECIOUS);
                String shop_type = complaint_list.get(position).get(TAG_SHOP_TYPE);
                String remark = complaint_list.get(position).get(TAG_REMARK);

                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("shop_id", shop_id);
                editor.putString("agencies_name", agencies_name);
                editor.putString("shop_name", shop_name);
                editor.putString("shop_owner_name", shop_owner_name);
                editor.putString("shop_contact", shop_contact);
                editor.putString("shop_landline", shop_landline);
                editor.putString("shop_location", shop_location);
                editor.putString("shop_photos", shop_photos);
                editor.putString("shop_previous", shop_previous);
                editor.putString("shop_type", shop_type);
                editor.putString("remark", remark);

                editor.commit();

                Intent i = new Intent(getActivity(), Activity_Shop_Description.class);
                startActivity(i);
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
            GetMyNewEnquiries();

        } catch (Exception e) {
            // TODO: handle exception
        }
    }


    @Override
    public void onFabAnimationEnd() {
        Intent intent = new Intent(getActivity(), Activity_Add_Enquiry.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            mSheetLayout.contractFab();
        }
    }


    /*****************************
     * GET SHOP
     ***************/

    public void GetMyNewEnquiries() {

        String tag_json_obj = "json_obj_req";

        System.out.println("CAME DA Enquiry" + AppConfig.url_list_shop);

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_list_shop, new Response.Listener<String>() {

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

                            String shop_id = obj1.getString(TAG_SHOP_ID);
                            String agencies_name = obj1.getString(TAG_AGENCY_NAME);
                            String shop_name = obj1.getString(TAG_SHOP_NAME);
                            String shop_owner_name = obj1.getString(TAG_SHOP_OWNER_NAME);
                            String shop_contact = obj1.getString(TAG_SHOP_CONTACT);
                            String shop_landline = obj1.getString(TAG_SHOP_LANDLINE);
                            String shop_location = obj1.getString(TAG_SHOP_LOCATION);
                            String shop_photos = obj1.getString(TAG_SHOP_PHOTOS);
                            String shop_previous = obj1.getString(TAG_SHOP_PRECIOUS);
                            String shop_type = obj1.getString(TAG_SHOP_TYPE);
                            String remark = obj1.getString(TAG_REMARK);

                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();

                            // adding each child node to HashMap key => value
                            map.put(TAG_SHOP_ID, shop_id);
                            map.put(TAG_AGENCY_NAME, agencies_name);
                            map.put(TAG_SHOP_NAME, shop_name);
                            map.put(TAG_SHOP_OWNER_NAME, shop_owner_name);
                            map.put(TAG_SHOP_CONTACT, shop_contact);
                            map.put(TAG_SHOP_LANDLINE, shop_landline);
                            map.put(TAG_SHOP_LOCATION, shop_location);
                            map.put(TAG_SHOP_PHOTOS, shop_photos);
                            map.put(TAG_SHOP_PRECIOUS, shop_previous);
                            map.put(TAG_SHOP_TYPE, shop_type);
                            map.put(TAG_REMARK, remark);

                            complaint_list.add(map);

                            adapter = new Enquiry_Adapter(getActivity(),
                                    complaint_list);
                            List.setAdapter(adapter);

                        }

                    } else if (success == 0) {

                        swipeRefreshLayout.setRefreshing(false);

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

                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", str_user_id);

                System.out.println("ENQUIRY ID : " + str_user_id);

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }


}
