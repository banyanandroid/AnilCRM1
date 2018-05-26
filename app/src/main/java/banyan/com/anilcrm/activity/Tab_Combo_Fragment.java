package banyan.com.anilcrm.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.github.fabtransitionactivity.SheetLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import banyan.com.anilcrm.R;
import banyan.com.anilcrm.adapter.Combo_Adapter;
import banyan.com.anilcrm.adapter.SalesReturn_Adapter;
import banyan.com.anilcrm.global.SessionManager;


/**
 * Created by Jo on 05/03/2018.
 */
public class Tab_Combo_Fragment extends Fragment implements SheetLayout.OnFabAnimationEndListener, SwipeRefreshLayout.OnRefreshListener {

    SheetLayout mSheetLayout;
    FloatingActionButton mFab;

    ProgressDialog pDialog;
    public static RequestQueue queue;

    // Session Manager Class
    SessionManager session;

    String TAG = "add task";

    private ListView List;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static final String TAG_OFFER_NAME = "offer_name";
    public static final String TAG_SCHEME_CODE = "scheme_code";
    public static final String TAG_FROM_DATE = "from_date";
    public static final String TAG_TO_DATE = "to_date";
    public static final String TAG_CATEGORY_NAME = "category_name";
    public static final String TAG_PRODUCT_NAME = "product_name";

    static ArrayList<HashMap<String, String>> complaint_list;

    HashMap<String, String> params = new HashMap<String, String>();

    public Combo_Adapter adapter;

    String str_select_task_id;

    private static final int REQUEST_CODE = 1;
    String str_user_name, str_user_id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.tab_combo_offer_layout, null);

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

                String offer_name = complaint_list.get(position).get(TAG_OFFER_NAME);
                String scheme_code = complaint_list.get(position).get(TAG_SCHEME_CODE);
                String from_date = complaint_list.get(position).get(TAG_FROM_DATE);
                String to_date = complaint_list.get(position).get(TAG_TO_DATE);
                String category_name = complaint_list.get(position).get(TAG_CATEGORY_NAME);
                String product_name = complaint_list.get(position).get(TAG_PRODUCT_NAME);


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
        Intent intent = new Intent(getActivity(), Activity_Sales_Return_Form.class);
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
     * GET REQ
     ***************/

    public void GetMyNewEnquiries() {

        String tag_json_obj = "json_obj_req";

        System.out.println("CAME DA Enquiry" + AppConfig.url_combo_list);

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_combo_list, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("success");

                    if (success == 1) {

                        JSONArray arr;

                        arr = obj.getJSONArray("combooffer");

                        for (int i = 0; arr.length() > i; i++) {
                            JSONObject obj1 = arr.getJSONObject(i);

                            String offer_name = obj1.getString(TAG_OFFER_NAME);
                            String scheme_code = obj1.getString(TAG_SCHEME_CODE);
                            String from_date = obj1.getString(TAG_FROM_DATE);
                            String to_date = obj1.getString(TAG_TO_DATE);
                            String category_name = obj1.getString(TAG_CATEGORY_NAME);
                            String product_name = obj1.getString(TAG_PRODUCT_NAME);

                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();

                            // adding each child node to HashMap key => value
                            map.put(TAG_OFFER_NAME, offer_name);
                            map.put(TAG_SCHEME_CODE, scheme_code);
                            map.put(TAG_FROM_DATE, from_date);
                            map.put(TAG_TO_DATE, to_date);
                            map.put(TAG_CATEGORY_NAME, category_name);
                            map.put(TAG_PRODUCT_NAME, product_name);

                            complaint_list.add(map);

                            adapter = new Combo_Adapter(getActivity(),
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
