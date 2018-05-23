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
import android.widget.Button;
import android.widget.EditText;
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
import banyan.com.anilcrm.adapter.Enquiry_Adapter;
import banyan.com.anilcrm.adapter.Location_Adapter;
import banyan.com.anilcrm.global.SessionManager;
import dmax.dialog.SpotsDialog;


/**
 * Created by Jo on 05/03/2018.
 */
public class Tab_Edit_Shop_Fragment extends Fragment {

    private ListView listView;

    private EditText edt_keyword;

    private Button btn_search;

    String TAG = "reg";

    public static final String TAG_SHOP_ID = "shop_id";
    public static final String TAG_AGENCY_NAME = "agencies_name";
    public static final String TAG_SHOP_NAME = "shop_name";
    public static final String TAG_SHOP_OWNER_NAME = "shop_owner_name";
    public static final String TAG_SHOP_CONTACT = "shop_contact";
    public static final String TAG_SHOP_LANDLINE = "shop_landline";
    public static final String TAG_SHOP_LOCATION = "shop_location";
    public static final String TAG_SHOP_PHOTOS = "shop_photos";
    public static final String TAG_SHOP_PRECIOUS = "shop_previous";
    public static final String TAG_SHOP_TYPE = "shop_type";
    public static final String TAG_REMARK = "remarks";

    SpotsDialog dialog;
    public static RequestQueue queue;

    static ArrayList<HashMap<String, String>> shop_list;
    HashMap<String, String> params = new HashMap<String, String>();

    public Enquiry_Adapter adapter;

    // Session Manager Class
    SessionManager session;
    String str_name, str_id;

    String str_keyword = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.tab_edit_shop_layout, null);

        session = new SessionManager(getActivity());

        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        str_name = user.get(SessionManager.KEY_USER);
        str_id = user.get(SessionManager.KEY_USER_ID);

        shop_list = new ArrayList<HashMap<String, String>>();

        edt_keyword = (EditText) rootview.findViewById(R.id.edt_shop_edt_keyword);
        btn_search = (Button) rootview.findViewById(R.id.edt_shop_btn_search);
        listView = (ListView) rootview.findViewById(R.id.listView);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                str_keyword = edt_keyword.getText().toString().trim();

                if (str_keyword.equals("")) {
                    TastyToast.makeText(getActivity(), "Enter Shop Name / Number", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
                    edt_keyword.setError("Enter Shop Name / Number");
                } else {
                    shop_list.clear();
                    dialog = new SpotsDialog(getActivity());
                    dialog.show();
                    queue = Volley.newRequestQueue(getActivity());
                    GetShop();
                }

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String shop_id = shop_list.get(position).get(TAG_SHOP_ID);
                String agencies_name = shop_list.get(position).get(TAG_AGENCY_NAME);
                String shop_name = shop_list.get(position).get(TAG_SHOP_NAME);
                String shop_owner_name = shop_list.get(position).get(TAG_SHOP_OWNER_NAME);
                String shop_contact = shop_list.get(position).get(TAG_SHOP_CONTACT);
                String shop_landline = shop_list.get(position).get(TAG_SHOP_LANDLINE);
                String shop_location = shop_list.get(position).get(TAG_SHOP_LOCATION);
                String shop_photos = shop_list.get(position).get(TAG_SHOP_PHOTOS);
                String shop_previous = shop_list.get(position).get(TAG_SHOP_PRECIOUS);
                String shop_type = shop_list.get(position).get(TAG_SHOP_TYPE);
                String remark = shop_list.get(position).get(TAG_REMARK);

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

                System.out.println("shop_type : " + shop_type);

                Intent i = new Intent(getActivity(), Activity_Shop_Edit.class);
                startActivity(i);
            }

        });

        return rootview;
    }

    /*****************************
     * GET SHOP
     ***************/

    public void GetShop() {

        String tag_json_obj = "json_obj_req";

        System.out.println("CAME DA Enquiry" + AppConfig.url_search_shop);

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_search_shop, new Response.Listener<String>() {

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

                            shop_list.add(map);

                            adapter = new Enquiry_Adapter(getActivity(),
                                    shop_list);
                            listView.setAdapter(adapter);

                        }

                    } else if (success == 0) {

                        dialog.dismiss();
                        TastyToast.makeText(getActivity(), "No Shop Found For this Name / Number", TastyToast.LENGTH_SHORT, TastyToast.CONFUSING);

                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // adapter.notifyDataSetChanged();
                // stopping swipe refresh
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                // stopping swipe refresh
                dialog.dismiss();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", str_id);
                params.put("search_term", str_keyword);

                System.out.println("USER ID : " + str_id);
                System.out.println("SEARCH TERM : " + str_keyword);

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

}
