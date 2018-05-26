package banyan.com.anilcrm.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sdsmdg.tastytoast.TastyToast;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import banyan.com.anilcrm.R;
import banyan.com.anilcrm.adapter.MyListAdapter;
import banyan.com.anilcrm.database.DBManager;
import banyan.com.anilcrm.global.SessionManager;
import banyan.com.anilcrm.model.Hero;
import dmax.dialog.SpotsDialog;

/**
 * Created by Jothiprabhakar on 29-Mar-18.
 */

public class Activity_Add_Combo extends AppCompatActivity {

    private Toolbar mToolbar;
    SpotsDialog dialog;
    public static RequestQueue queue;
    String TAG = "Auto_Res";

    // Session Manager Class
    SessionManager session;

    Button btn_add_to_cart, btn_submit;

    SearchableSpinner spinner_shop, spinner_category, spinner_product;


    EditText edt_amount, edt_name;

    ListView list_cart;

    public static TextView txt_total;

    // New Listview
    public static List<Hero> heroList;

    // creating new HashMap
    HashMap<String, String> map = new HashMap<String, String>();

    static ArrayList<HashMap<String, String>> complaint_list;
    HashMap<String, String> params = new HashMap<String, String>();

    TextView t1;

    ArrayList<String> Arraylist_selected_product = null;

    ArrayList<String> Arraylist_shop_id = null;
    ArrayList<String> Arraylist_shop_name = null;

    ArrayList<String> Arraylist_group_id = null;
    ArrayList<String> Arraylist_group_name = null;

    ArrayList<String> Arraylist_product_id = null;
    ArrayList<String> Arraylist_product_name = null;
    ArrayList<String> Arraylist_product_price = null;


    ArrayList<String> Arraylist_id = null;
    ArrayList<String> Arraylist_groupid = null;
    ArrayList<String> Arraylist_qty = null;
    ArrayList<String> Arraylist_price = null;
    ArrayList<String> Arraylist_final = null;

    //SESSION
    String str_user_email, str_user_id = "";
    String str_final_order = "";
    String str_offer_name, str_Shop_id, str_group, str_group_id, str_product, str_product_id, str_price, str_qty, str_shop_type = "";
    String str_final_offer_name, str_final_offer_amount = "";
    public static float float_total_amount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combo_form);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Add Combo Offer");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("combo", "combo");
                startActivity(i);
                finish();
            }
        });

        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        str_user_email = user.get(SessionManager.KEY_USER);
        str_user_id = user.get(SessionManager.KEY_USER_ID);

        spinner_category = (SearchableSpinner) findViewById(R.id.order_spinner_category);
        spinner_product = (SearchableSpinner) findViewById(R.id.order_spinner_product);

        edt_amount = (EditText) findViewById(R.id.order_edt_qty);
        edt_name = (EditText) findViewById(R.id.order_edt_name);

        txt_total = (TextView) findViewById(R.id.order_txt_total_amt);

        list_cart = (ListView) findViewById(R.id.order_list_product_display);

        btn_add_to_cart = (Button) findViewById(R.id.order_btn_add_to_Cart);
        btn_submit = (Button) findViewById(R.id.order_btn_submit_order);

        System.out.println("SESSION USER EMAIL   " + str_user_email);
        System.out.println("SESSION USER ID      " + str_user_id);

        Arraylist_group_id = new ArrayList<String>();
        Arraylist_group_name = new ArrayList<String>();

        Arraylist_product_id = new ArrayList<String>();
        Arraylist_product_name = new ArrayList<String>();
        Arraylist_product_price = new ArrayList<String>();

        Arraylist_id = new ArrayList<String>();
        Arraylist_groupid = new ArrayList<String>();
        Arraylist_qty = new ArrayList<String>();
        Arraylist_price = new ArrayList<String>();
        Arraylist_final = new ArrayList<String>();

        // Initialize Arraytlist
        heroList = new ArrayList<>();

        // Hashmap for ListView
        complaint_list = new ArrayList<HashMap<String, String>>();
        Arraylist_selected_product = new ArrayList<String>();

        spinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                System.out.println("Group");

                t1 = (TextView) view;
                str_group = t1.getText().toString();
                str_group_id = Arraylist_group_id.get(position);

                System.out.println("Group ID : " + str_group_id);

                Arraylist_product_id.clear();
                Arraylist_product_name.clear();
                Arraylist_product_price.clear();

                if (str_Shop_id == null) {
                    TastyToast.makeText(getApplicationContext(), "Please Select a Shop", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
                } else {
                    dialog = new SpotsDialog(Activity_Add_Combo.this);
                    dialog.show();
                    queue = Volley.newRequestQueue(Activity_Add_Combo.this);
                    Function_Get_Product();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_product.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                System.out.println("Product");

                t1 = (TextView) view;

                str_product = t1.getText().toString();
                str_product_id = Arraylist_product_id.get(position);
                str_price = Arraylist_product_price.get(position);

                System.out.println("Product ID : " + str_product_id);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btn_add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (str_group_id == null) {
                    TastyToast.makeText(getApplicationContext(), "Please Select Category", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
                } else if (str_product_id == null) {
                    TastyToast.makeText(getApplicationContext(), "Please Select Product", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
                } else if (str_price == null) {
                    TastyToast.makeText(getApplicationContext(), "Internal Price Error", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
                } else {


                    String str_amt = "";

                    System.out.println("QTY " + str_qty);
                    System.out.println("PRICE " + str_price);
                    System.out.println("TATAL " + str_amt);

                    heroList.add(new Hero(str_product_id, str_group_id, str_amt, str_product, str_qty));

                    MyListAdapter adapter = new MyListAdapter(Activity_Add_Combo.this, R.layout.my_custom_list, heroList);
                    list_cart.setAdapter(adapter);
                }

            }
        });


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                str_final_offer_name = edt_name.getText().toString().trim();
                str_final_offer_amount = edt_amount.getText().toString().trim();

                Get_VALUE();
            }
        });

        dialog = new SpotsDialog(Activity_Add_Combo.this);
        dialog.show();
        Arraylist_group_id.clear();
        Arraylist_group_name.clear();

        queue = Volley.newRequestQueue(Activity_Add_Combo.this);
        Function_Get_ProdcutGroup();

    }

    /***************************
     * GET Product Group
     ***************************/

    public void Function_Get_ProdcutGroup() {

        String tag_json_obj = "json_obj_req";
        System.out.println("CAME 1");
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_user_group_list, new Response.Listener<String>() {

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

                            String id = obj1.getString("productgroup_id");
                            String name = obj1.getString("productgroup_name");

                            Arraylist_group_name.add(name);
                            Arraylist_group_id.add(id);

                            try {
                                spinner_category
                                        .setAdapter(new ArrayAdapter<String>(Activity_Add_Combo.this,
                                                android.R.layout.simple_spinner_dropdown_item,
                                                Arraylist_group_name));


                            } catch (Exception e) {

                            }
                        }

                        dialog.dismiss();
                    } else if (success == 0) {

                        dialog.dismiss();
                        TastyToast.makeText(getApplicationContext(), "No Category Found", TastyToast.LENGTH_SHORT, TastyToast.WARNING);

                    }

                    dialog.dismiss();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();

                TastyToast.makeText(getApplicationContext(), "Internal Error !", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    /***************************
     * GET Product By Group
     ***************************/

    public void Function_Get_Product() {

        String tag_json_obj = "json_obj_req";
        System.out.println("CAME 1");
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_user_product, new Response.Listener<String>() {

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

                            String id = obj1.getString("product_id");
                            String name = obj1.getString("product_name");
                            String price = obj1.getString("product_price");

                            Arraylist_product_id.add(id);
                            Arraylist_product_name.add(name);
                            Arraylist_product_price.add(price);

                            try {
                                spinner_product
                                        .setAdapter(new ArrayAdapter<String>(Activity_Add_Combo.this,
                                                android.R.layout.simple_spinner_dropdown_item,
                                                Arraylist_product_name));


                            } catch (Exception e) {

                            }
                        }

                        dialog.dismiss();
                    } else if (success == 0) {

                        dialog.dismiss();
                        TastyToast.makeText(getApplicationContext(), "No Product Found", TastyToast.LENGTH_SHORT, TastyToast.WARNING);

                    }

                    dialog.dismiss();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();

                TastyToast.makeText(getApplicationContext(), "Internal Error !", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("group_id", str_group_id);
                params.put("shop_id", str_Shop_id);

                System.out.println("GROUP IDDD :: " + str_group_id);
                System.out.println("SHOPPP IDDD :: " + str_Shop_id);

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    /********************************
     * Vivekanandhan
     *
     * Function Get Product
     * ******************************/
    public void Get_VALUE() {

        for (int i = 0; i < heroList.size(); i++) {

            Hero hero = heroList.get(i);
            String str_id = hero.getId();
            String str_groupid = hero.getgroupId();
            String str_qty = hero.getQty();
            String str_price = hero.getPrice();

            Arraylist_id.add(str_id);
            Arraylist_groupid.add(str_groupid);
            Arraylist_qty.add(str_qty);
            Arraylist_price.add(str_price);
        }

        Arraylist_final.clear();

        for (int j = 0; j < Arraylist_id.size(); j++) {

            String str_final_id = Arraylist_id.get(j);
            String str_final_groupid = Arraylist_groupid.get(j);
            String str_final_qty = Arraylist_qty.get(j);
            String str_final_price = Arraylist_price.get(j);

            String final_order = str_final_id + "-" + str_final_groupid;

            Arraylist_final.add(final_order);
        }

        //Ordered Items
        str_final_order = TextUtils.join(", ", Arraylist_final);

        System.out.println("SESSION_ID    :::::" + str_user_id);
        System.out.println("PRODUCT_DETAILS   :::::" + str_final_order);
        System.out.println("SHOP ID   :::::" + str_Shop_id);

        if (str_final_order.equals("")) {

            TastyToast.makeText(getApplicationContext(), "Select Your Product", TastyToast.LENGTH_SHORT, TastyToast.WARNING);

        } else if (str_final_offer_name.equals("")) {

            TastyToast.makeText(getApplicationContext(), "Select Enter Offer Name", TastyToast.LENGTH_SHORT, TastyToast.WARNING);

        } else if (str_final_offer_amount.equals("")) {

            TastyToast.makeText(getApplicationContext(), "Select Enter Offer Amount", TastyToast.LENGTH_SHORT, TastyToast.WARNING);

        } else {

            System.out.println("SESSION_ID    :::::" + str_user_id);
            System.out.println("PRODUCT_DETAILS   :::::" + str_final_order);
            System.out.println("OFFER AMOUNT  :::::" + str_final_offer_amount);
            System.out.println("OFFER NAME  :::::" + str_final_offer_name);

            try {
                // Vivekanandhan
                // check network is connected
                if (isNetworkConnected()) {

                    dialog = new SpotsDialog(Activity_Add_Combo.this);
                    dialog.show();
                    queue = Volley.newRequestQueue(Activity_Add_Combo.this);
                    Function_Proceed();

                } else {

                    //store data in local db
                    DBManager dbManager = new DBManager(this);
                    dbManager.open();
                    long response = dbManager.insert_Order_Form(str_Shop_id, str_final_order, str_user_id);
                    System.out.println("### response : " + response);
                    if (response == -1) {

                        TastyToast.makeText(getApplicationContext(), "Oops...! Try again Later..!", TastyToast.LENGTH_LONG, TastyToast.WARNING);

                    } else {

                        TastyToast.makeText(getApplicationContext(), "Order Placed Successfully!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);

                        new AlertDialog.Builder(Activity_Add_Combo.this)
                                .setTitle("Anil Foods")
                                .setMessage("Order Placed Successfully !")
                                .setIcon(R.mipmap.ic_launcher)
                                .setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,

                                                                int which) {
                                                // TODO Auto-generated method stub

                                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                                i.putExtra("from_value", "order");
                                                startActivity(i);

                                            }

                                        }).show();


                    }

                }

            } catch (Exception e) {
                System.out.println("### Exception");
            }
        }

    }

    /******************************************
     *    Proceed Function
     * ****************************************/
    private void Function_Proceed() {

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_add_order, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                Log.d("USER_LOGIN", response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    System.out.println("REG 00" + obj);

                    int success = obj.getInt("status");

                    System.out.println("REG" + success);

                    if (success == 1) {

                        dialog.dismiss();

                        TastyToast.makeText(getApplicationContext(), "Order Placed Successfully!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);

                        new AlertDialog.Builder(Activity_Add_Combo.this)
                                .setTitle("Anil Foods")
                                .setMessage("Order Placed Successfully !")
                                .setIcon(R.mipmap.ic_launcher)
                                .setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,

                                                                int which) {
                                                // TODO Auto-generated method stub

                                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                                i.putExtra("from_value", "order");
                                                startActivity(i);

                                            }

                                        }).show();
                    } else {

                        TastyToast.makeText(getApplicationContext(), "Oops...! Try again Later..!", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                    }

                    dialog.dismiss();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("offer_name", str_final_offer_name);
                params.put("product_details", str_final_order);
                params.put("amount", str_final_offer_amount);
                params.put("user_id", str_user_id);

                System.out.println("OFFER NAME  :::::" + str_final_offer_name);
                System.out.println("OFFER AMOUNT  :::::" + str_final_offer_amount);
                System.out.println("USER ID        :::::" + str_user_id);
                System.out.println("PRODUCT_DETAILS   :::::" + str_final_order);

                return checkParams(params);
                //return params;
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

    private boolean isNetworkConnected() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

}