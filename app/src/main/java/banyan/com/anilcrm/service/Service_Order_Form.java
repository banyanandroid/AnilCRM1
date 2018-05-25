package banyan.com.anilcrm.service;

import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import banyan.com.anilcrm.R;
import banyan.com.anilcrm.activity.Activity_Order_Form;
import banyan.com.anilcrm.activity.AppConfig;
import banyan.com.anilcrm.activity.MainActivity;
import banyan.com.anilcrm.database.DBManager;
import banyan.com.anilcrm.database.DatabaseHelper;


/***
 * Vivekanandhan
 *
 * */

public class Service_Order_Form extends Service {

    private static final String TAG = "HelloService";

    private boolean isRunning = false;

    private RequestQueue queue;

    private DBManager dbManager;

    @Override
    public void onCreate() {
        Log.i(TAG, "### Service onCreate");

        isRunning = true;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        Log.i(TAG, "### Service onStartCommand");

        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR
        new Thread(new Runnable() {
            @Override
            public void run() {

                // get local db data
                dbManager = new DBManager(getApplicationContext());
                dbManager.open();

                Cursor cursor = dbManager.Fetch_Order_Form();
                // get form order details from local database
                if (cursor.moveToFirst()){
                    do{

                        String order_form_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ORDER_FORM_ID));
                        String shop_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ORDER_FORM_SHOP_ID));
                        String final_order = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ORDER_FORM_FINAL_ORDER));
                        String user_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ORDER_FORM_USER_ID));

                        Log.i(TAG, "### order_form_id : " + order_form_id);

                        queue = Volley.newRequestQueue(getApplicationContext());
                        // upload order data to server
                        Save_Order_Form(order_form_id, shop_id, final_order, user_id);
                    }while(cursor.moveToNext());
                }
                //Stop service once it finishes its task
                cursor.close();

                stopSelf();

            }
        }).start();

        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "### Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {

        isRunning = false;

        Log.i(TAG, "### Service onDestroy");
    }


    /* */

    /************************************

     //////// * SAVE ORDER FORM  * ////////

     ************************************/

    private void Save_Order_Form(final String order_form_id, final String shop_id, final String final_order, final  String user_id) {

        System.out.println("### Save_Order_Form");
        System.out.println("###  App_Config.url_user_details "+  AppConfig.url_add_order);


        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_add_order, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                Log.d("USER_LOGIN", response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    System.out.println("### REG 00" + obj);

                    int success = obj.getInt("status");

                    System.out.println("### REG" + success);

                    if (success == 1) {

                        System.out.println("### Local Form Order Uploaded to Server Succesfully.");

                        // after data upload to server
                        //remove data in local db
                        int result = dbManager.delete_Order_Form( Long.parseLong(order_form_id) );
                        if (result != 0)
                            System.out.println("### Local Form Order Details Deleted Succesfully.");
                        else
                            System.out.println("### Local Form Order Details Not Deleted.");

                    } else {

                        System.out.println("###  Local  Form Order data upload to server failed. ");
                    }

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

                params.put("shop_id", shop_id);
                params.put("product_details", final_order);
                params.put("user_id", user_id);

                System.out.println("### SHOP ID   :::::" + shop_id);
                System.out.println("### USER ID        :::::" + final_order);
                System.out.println("### PRODUCT_DETAILS   :::::" + user_id);

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
}