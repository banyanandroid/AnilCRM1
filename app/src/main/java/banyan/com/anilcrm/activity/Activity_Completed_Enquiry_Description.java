package banyan.com.anilcrm.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.nguyenhoanglam.imagepicker.model.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import banyan.com.anilcrm.R;
import banyan.com.anilcrm.global.GPSTracker;
import banyan.com.anilcrm.global.SessionManager;
import dmax.dialog.SpotsDialog;
import pugman.com.simplelocationgetter.SimpleLocationGetter;

/**
 * Created by Jarvis on 10-03-2018.
 */

public class Activity_Completed_Enquiry_Description extends AppCompatActivity implements SimpleLocationGetter.OnLocationGetListener {

    private Toolbar mToolbar;
    SpotsDialog dialog;
    public static RequestQueue queue;
    String TAG = "Auto_Res";

    String str_enq_no = "";

    public static final String TAG_ENQ_ID = "enquiry_id";
    public static final String TAG_ENQ_NO = "enquiry_no";
    public static final String TAG_ENQ_CUS_NAME = "enquiry_cus_name";
    public static final String TAG_CUS_TYPE = "customer_type";
    public static final String TAG_CUS_MOB_NO = "enquiry_cus_mobileno";
    public static final String TAG_CUS_EMAIL = "enquiry_cus_email";
    public static final String TAG_CUS_AREA = "enquiry_cus_area";
    public static final String TAG_CUS_IMAGE = "enquiry_cus_image";
    public static final String TAG_ENQ_DES = "enquiry_desc";
    public static final String TAG_ENQ_PROJECT = "enquiry_project";
    public static final String TAG_ENQ_COM_PROJECT = "enquiry_com_project";
    public static final String TAG_ENQ_EMP_ID = "enquiry_empid";
    public static final String TAG_ENQ_STATUS = "enquiry_status";
    public static final String TAG_ENQ_QTY = "enquiry_qty";
    public static final String TAG_ENQ_SQ_FT = "enquiry_sq_ft";
    public static final String TAG_LAT = "location_lat";
    public static final String TAG_LONG = "location_lon";
    public static final String TAG_CREATE_ON = "enquiry_createdon";
    public static final String TAG_UPDATE_ON = "enquiry_updatedon";
    public static final String TAG_ENQ_ACT = "enquiry_act";
    public static final String TAG_REF_BY = "referred_by";
    public static final String TAG__DES_SHEET = "design_sheet";


    // Session Manager Class
    SessionManager session;

    //Date Picker
    DatePickerDialog dpd;
    int int_year, int_month, int_date;

    String str_proj_completion_date = "DATE";


    TextView txt_img_count, txt_date;

    Spinner spn_client_type, spn_ref_by, spn_design_sheet;

    EditText edt_name, edt_mobile, edt_location, edt_email,
            edt_approx_no_of_windows, edt_approx_sq_feet, edt_proj_description, edt_current_project_status, edt_ref_by;

    ImageView img_view;

    TextView txt_ref_by, txt_client_type, txt_design_sheet;

    String str_name, str_mobile, str_location, str_email,
            str_approx_no_of_windows, str_approx_sq_feet, str_proj_description, str_current_project_status,
            str_client_type, str_ref_by, str_design_sheet = "";

    String str_latitude, str_longitude = "";

    //SESSION
    String str_user_email, str_user_id = "";

    // PIC Upload
    String listString = "";
    String encodedstring = "";
    String image_type = "";
    ArrayList<String> Arraylist_image_encode = null;
    ArrayList<String> Arraylist_dummy = null;
    private int REQUEST_CODE_PICKER = 2000;

    private Context mContext;

    private ArrayList<Image> images = new ArrayList<>();

    GPSTracker gps;
    Double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_enquiry);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Edit Enquiry");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        str_user_email = user.get(SessionManager.KEY_USER);
        str_user_id = user.get(SessionManager.KEY_USER_ID);

        System.out.println("SESSION USER EMAIL   " + str_user_email);
        System.out.println("SESSION USER ID      " + str_user_id);

        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_mobile = (EditText) findViewById(R.id.edt_mobile_number);
        edt_location = (EditText) findViewById(R.id.edt_location);
        edt_email = (EditText) findViewById(R.id.edt_email);

        edt_approx_no_of_windows = (EditText) findViewById(R.id.edt_no_of_window);
        edt_approx_sq_feet = (EditText) findViewById(R.id.edt_approx_sqft);
        edt_proj_description = (EditText) findViewById(R.id.edt_project_description);
        edt_current_project_status = (EditText) findViewById(R.id.edt_project_status);

        edt_ref_by = (EditText) findViewById(R.id.edt_ref_by);

        spn_client_type = (Spinner) findViewById(R.id.spn_client_type);
        spn_ref_by = (Spinner) findViewById(R.id.spn_ref_by);
        spn_design_sheet = (Spinner) findViewById(R.id.spn_design_sheet);

        spn_client_type.setFocusable(false);
        spn_ref_by.setFocusable(false);
        spn_design_sheet.setFocusable(false);

        txt_date = (TextView) findViewById(R.id.txt_date);
        txt_img_count = (TextView) findViewById(R.id.txt_img_count);

        txt_ref_by = (TextView) findViewById(R.id.txt_ref_by);
        txt_client_type = (TextView) findViewById(R.id.txt_client_type);
        txt_design_sheet = (TextView) findViewById(R.id.txt_design_sheet);

        img_view = (ImageView) findViewById(R.id.complaintupdate_img_post);

        Arraylist_image_encode = new ArrayList<String>();
        Arraylist_dummy = new ArrayList<String>();

        try {
            //GetLocation();
            NewLocation();
        } catch (Exception e) {

        }

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(Activity_Completed_Enquiry_Description.this);

        str_enq_no = sharedPreferences.getString("str_enq_no", "str_enq_no");

        try {
            dialog = new SpotsDialog(Activity_Completed_Enquiry_Description.this);
            dialog.show();
            queue = Volley.newRequestQueue(Activity_Completed_Enquiry_Description.this);
            GetViewEnquiries();
        } catch (Exception e1) {

        }

    }


    public void NewLocation() {

        SimpleLocationGetter getter = new SimpleLocationGetter(this, this);
        getter.getLastLocation();
    }

    @Override
    public void onLocationReady(Location location) {
        Log.d("LOCATION", "onLocationReady: lat=" + location.getLatitude() + " lon=" + location.getLongitude());

        System.out.println("LOCATION 1 :: " + location.getLatitude());
        System.out.println("LOCATION 2 :: " + location.getLongitude());

        latitude = location.getLatitude();
        longitude = location.getLongitude();

    }

    @Override
    public void onError(String error) {
        Log.e("LOCATION", "Error: " + error);
    }




    /********************************
     *  Get Location
     * ********************************/

   /* public void GetLocation(){
        gps = new GPSTracker(Activity_Add_Enquiry.this);
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

        } else {
            gps.showSettingsAlert();
        }

        if (latitude == 0.0) {

            Strat_track();


            Toast.makeText(Activity_Add_Enquiry.this, "Internal Error Lat", Toast.LENGTH_LONG).show();

        } else if (longitude == 0.0) {

            gps = new GPSTracker(Activity_Add_Enquiry.this);
            if (gps.canGetLocation()) {

                latitude = gps.getLatitude();
                longitude = gps.getLongitude();

            } else {
                gps.showSettingsAlert();
            }


            Toast.makeText(Activity_Add_Enquiry.this, "Internal Error lon", Toast.LENGTH_LONG).show();

        } else {
            System.out.println("LAT : " + latitude);
            System.out.println("LON : " + longitude);

          //  edt_location.setText("Lat : " + latitude + " Long : " + longitude);
        }
    }*/

    /************************************
     * Start Tracking
     * ************************************/

   /* private void Strat_track(){

        gps = new GPSTracker(Activity_Add_Enquiry.this);
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

        } else {
            gps.showSettingsAlert();
        }

    }*/

    /********************************
     *FUNCTION SUBMIT ENQUIRY
     *********************************/
    public void GetViewEnquiries() {

        String tag_json_obj = "json_obj_req";

        System.out.println("CAME DA Enquiry" + AppConfig.url_view_enquiry);

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_view_enquiry, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {

                        JSONArray arr;

                        arr = obj.getJSONArray("data");

                        for (int i = 0; arr.length() > i; i++) {
                            JSONObject obj1 = arr.getJSONObject(i);

                            String id = obj1.getString(TAG_ENQ_ID);
                            String enq_no = obj1.getString(TAG_ENQ_NO);
                            String enq_cus_name = obj1.getString(TAG_ENQ_CUS_NAME);
                            String enq_enq_status = obj1.getString(TAG_ENQ_STATUS);
                            String customer_type = obj1.getString(TAG_CUS_TYPE);
                            String enquiry_cus_mobileno = obj1.getString(TAG_CUS_MOB_NO);
                            String enquiry_cus_email = obj1.getString(TAG_CUS_EMAIL);
                            String enquiry_cus_area = obj1.getString(TAG_CUS_AREA);
                            String enquiry_cus_image = obj1.getString(TAG_CUS_IMAGE);
                            String enquiry_desc = obj1.getString(TAG_ENQ_DES);
                            String enquiry_project = obj1.getString(TAG_ENQ_PROJECT);
                            String enquiry_com_project = obj1.getString(TAG_ENQ_COM_PROJECT);
                            String enquiry_empid = obj1.getString(TAG_ENQ_EMP_ID);
                            String enquiry_status = obj1.getString(TAG_ENQ_STATUS);
                            String enquiry_qty = obj1.getString(TAG_ENQ_QTY);
                            String enquiry_sq_ft = obj1.getString(TAG_ENQ_SQ_FT);
                            String location_lat = obj1.getString(TAG_LAT);
                            String location_lon = obj1.getString(TAG_LONG);
                            String enquiry_createdon = obj1.getString(TAG_CREATE_ON);
                            String enquiry_updatedon = obj1.getString(TAG_UPDATE_ON);
                            String enquiry_act = obj1.getString(TAG_ENQ_ACT);
                            String ref_by = obj1.getString(TAG_REF_BY);
                            String des_sheet = obj1.getString(TAG__DES_SHEET);

                            edt_name.setText("" + enq_cus_name);
                            edt_mobile.setText("" + enquiry_cus_mobileno);
                            edt_location.setText("" + enquiry_cus_area);
                            edt_email.setText("" + enquiry_cus_email);
                            edt_approx_no_of_windows.setText("" + enquiry_qty);
                            edt_approx_sq_feet.setText("" + enquiry_sq_ft);
                            edt_proj_description.setText("" + enquiry_desc);
                            edt_current_project_status.setText("" + enquiry_project);

                            txt_client_type.setText("" + customer_type);
                            txt_date.setText("" + enquiry_com_project);
                            txt_design_sheet.setText("" + des_sheet);

                            if (ref_by.equals("Simta Directors") || ref_by.equals("Office Refference") || ref_by.equals("Own Enquiry")) {
                                edt_ref_by.setVisibility(View.GONE);
                                txt_ref_by.setText("" + ref_by);
                            } else {
                                edt_ref_by.setVisibility(View.VISIBLE);
                                edt_ref_by.setText("" + ref_by);
                            }

                            if (enquiry_cus_image.equals("")) {

                            } else {

                                String imgUrl = enquiry_cus_image;

                                Glide.with(getApplicationContext())
                                        .load(imgUrl)
                                        .thumbnail(0.5f)
                                        .into(img_view);


                            }

                        }

                    } else if (success == 0) {

                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // adapter.notifyDataSetChanged();
                // stopping swipe refresh
                dialog.dismiss();
                //  pDialog.hide();
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

                params.put("enquiry_no", str_enq_no);

                System.out.println("ENQUIRY NO : " + str_enq_no);

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }


}
