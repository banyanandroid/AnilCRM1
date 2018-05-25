package banyan.com.anilcrm.service;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import banyan.com.anilcrm.activity.AppConfig;
import banyan.com.anilcrm.database.DBManager;
import banyan.com.anilcrm.database.DatabaseHelper;


/***
 * Vivekanandhan
 *
 * */

public class Service_Add_Enquiry extends Service {

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

                Cursor cursor = dbManager.Fetch_Enquiry();
                // get form order details from local database
                if (cursor.moveToFirst()){
                    do{
                        System.out.println("### cursor.getColumnIndex(DatabaseHelper.ENQUIRY_ID "+cursor.getColumnIndex(DatabaseHelper.ENQUIRY_ID));

                        String enquiry_id = ""+cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ENQUIRY_ID));
                        String user_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ENQUIRY_USER_ID)); // user id
                        String shop_name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ENQUIRY_SHOP_NAME));
                        String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ENQUIRY_NAME));
                        String mobileno = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ENQUIRY_MOBILE_NO));
                        String landline = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ENQUIRY_LANDLINE));
                        String lat = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ENQUIRY_LAT));
                        String lon = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ENQUIRY_LON));
                        String area = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ENQUIRY_AREA));
                        String shop_previous = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ENQUIRY_SHOP_PREVIOUS));
                        String agency_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ENQUIRY_AGENCY_ID));
                        String type = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ENQUIRY_TYPE));
                        String image = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ENQUIRY_IMAGE));
                        String loc = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ENQUIRY_LOC));
                        String remarks = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ENQUIRY_REMARKS));
                        String branch_id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ENQUIRY_BRANCH_ID));

                        Log.i(TAG, "###  enquiry_id : " + enquiry_id);

                        queue = Volley.newRequestQueue(getApplicationContext());
                        // upload order data to server
                        Save_Add_Enquiry(enquiry_id, user_id,shop_name,name,mobileno,landline,lat,lon,area,shop_previous,agency_id,type,image,loc,
                                remarks,branch_id);
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

    private void Save_Add_Enquiry(final  String enquiry_id, final  String user_id, final  String  shop_name, final  String name, final  String mobileno, final  String landline,
                                  final  String lat, final  String lon, final  String area, final  String shop_previous, final  String agency_id,
                                  final  String type, final  String image, final  String loc, final  String remarks, final  String branch_id) {

        System.out.println("### Save_Add_Enquiry");
        System.out.println("###  App_Config.url_add_shop "+  AppConfig.url_add_shop);


        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_add_shop, new Response.Listener<String>() {
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
                        int result = dbManager.delete_Enquiry( Long.parseLong(enquiry_id) );
                        if (result != 0)
                            System.out.println("### Local Form Order Details Deleted Succesfully.");
                        else
                            System.out.println("### Local Form Order Details Not Deleted.");

                    }else if (success == 2) { // already exist

                        System.out.println("### Local data already exist in server.");

                        // after data upload to server
                        //remove data in local db
                        int result = dbManager.delete_Enquiry( Long.parseLong(enquiry_id) );
                        if (result != 0)
                            System.out.println("### Local Form Order Details Deleted Succesfully.");
                        else
                            System.out.println("### Local Form Order Details Not Deleted.");

                    }  else {

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

                System.out.println("### onErrorResponse");
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", user_id);
                params.put("shop_name", shop_name);
                params.put("name", name);
                params.put("mobileno", mobileno);
                params.put("landline", landline);
                params.put("lat", lat);
                params.put("lon", lon);
                params.put("area", area);
                params.put("shop_previous", shop_previous);
                params.put("agency_id", agency_id);
                params.put("type", type);
                params.put("image", image);
                params.put("loc", loc);
                params.put("remarks", remarks);
                params.put("branch_id", branch_id);

                System.out.println("### user_id  :::::" + user_id);
                System.out.println("### shop_name   :::::" + shop_name);
                System.out.println("###name   :::::" + name);
                System.out.println("### mobileno  :::::" + mobileno);
                System.out.println("### landline   :::::" + landline);
                System.out.println("### lat   :::::" + lat);
                System.out.println("### lon  :::::" + lon);
                System.out.println("### area   :::::" + area);
                System.out.println("### shop_previous   :::::" + shop_previous);
                System.out.println("### agency_id   :::::" + agency_id);
                System.out.println("### type   :::::" + type);
                System.out.println("### image   :::::" + image);
                System.out.println("### loc   :::::" + loc);
                System.out.println("### remarks   :::::" + remarks);
                System.out.println("### branch_id   :::::" + branch_id);

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