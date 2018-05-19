package banyan.com.anilcrm.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import banyan.com.anilcrm.R;
import banyan.com.anilcrm.global.GPSTracker;
import banyan.com.anilcrm.global.SessionManager;
import dmax.dialog.SpotsDialog;
import pugman.com.simplelocationgetter.SimpleLocationGetter;

/**
 * Created by Jarvis on 10-03-2018.
 */

public class Activity_Edit_Enquiry extends AppCompatActivity implements SimpleLocationGetter.OnLocationGetListener {

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

    Button btn_date, btn_add_pic, btn_submit;

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

    private ArrayList<com.nguyenhoanglam.imagepicker.model.Image> images = new ArrayList<>();

    GPSTracker gps;
    Double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_enquiry);

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

        btn_date = (Button) findViewById(R.id.btn_date);
        btn_add_pic = (Button) findViewById(R.id.btn_add_photos);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        txt_date = (TextView) findViewById(R.id.txt_date);
        txt_img_count = (TextView) findViewById(R.id.txt_img_count);

        txt_ref_by = (TextView) findViewById(R.id.txt_ref_by);
        txt_client_type = (TextView) findViewById(R.id.txt_client_type);
        txt_design_sheet = (TextView) findViewById(R.id.txt_design_sheet);

        img_view = (ImageView) findViewById(R.id.complaintupdate_img_post);

        Arraylist_image_encode = new ArrayList<String>();
        Arraylist_dummy = new ArrayList<String>();

        spn_client_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                str_client_type = parent.getItemAtPosition(pos).toString();

                if (str_client_type.equals("Select Client Type")) {
                    txt_client_type.setText("");
                } else {
                    txt_client_type.setText("" + str_client_type);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Another interface callback
            }

        });

        spn_ref_by.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                str_ref_by = parent.getItemAtPosition(pos).toString();

                edt_ref_by.setVisibility(View.GONE);

                if (str_ref_by.equals("Reffered By")) {
                    txt_ref_by.setText("");
                } else {
                    txt_ref_by.setText("" + str_ref_by);
                }

                if (str_ref_by.equals("Any Other")) {
                    edt_ref_by.setVisibility(View.VISIBLE);
                } else {
                    edt_ref_by.setVisibility(View.GONE);
                    str_ref_by = parent.getItemAtPosition(pos).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Another interface callback
            }

        });

        spn_design_sheet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                str_design_sheet = parent.getItemAtPosition(pos).toString();

                if (str_design_sheet.equals("Design Sheet Required?")) {
                    txt_design_sheet.setText("");
                } else {
                    txt_design_sheet.setText("" + str_design_sheet);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Another interface callback
            }

        });

        try {
            //GetLocation();
            NewLocation();
        } catch (Exception e) {

        }

        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                int_year = c.get(Calendar.YEAR);
                int_month = c.get(Calendar.MONTH);
                int_date = c.get(Calendar.DAY_OF_MONTH);

                // Launch Date Picker Dialog
                dpd = new DatePickerDialog(
                        Activity_Edit_Enquiry.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // Display Selected date in textbox
                                int month = monthOfYear + 1;
                                String formattedMonth = "" + month;
                                String formattedDayOfMonth = "" + dayOfMonth;

                                //The date and month have to be double digits. (ie) if month or date is 1 , then it has to be 01
                                if (month < 10) {
                                    formattedMonth = "0" + month;
                                }
                                if (dayOfMonth < 10) {
                                    formattedDayOfMonth = "0" + dayOfMonth;
                                }
                                //For posting journey date in json we get and concatinate date in this string
                                str_proj_completion_date = year + "-" + formattedMonth + "-" + formattedDayOfMonth;
                                txt_date.setText("" + str_proj_completion_date);
                            }
                        }, int_year, int_month, int_date);
                dpd.show();
            }

        });

        btn_add_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                image_type = "Location photos";
                ImagePicker();
            }
        });


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                str_name = edt_name.getText().toString();
                str_mobile = edt_mobile.getText().toString();
                str_location = edt_location.getText().toString();
                str_email = edt_email.getText().toString();
                str_approx_no_of_windows = edt_approx_no_of_windows.getText().toString();
                str_approx_sq_feet = edt_approx_sq_feet.getText().toString();
                str_current_project_status = edt_current_project_status.getText().toString();
                str_proj_description = edt_proj_description.getText().toString();
                str_proj_completion_date = txt_date.getText().toString().trim();

                str_client_type = txt_client_type.getText().toString().trim();
                str_design_sheet = txt_design_sheet.getText().toString().trim();
                str_ref_by = txt_ref_by.getText().toString().trim();

                if (str_name.equals("")) {
                    TastyToast.makeText(getApplicationContext(), "Enter Name", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
                    edt_name.setError("Name cannot be empty");
                } else if (str_mobile.equals("")) {
                    TastyToast.makeText(getApplicationContext(), "Enter Mobile Number", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
                    edt_mobile.setError("Mobile Number cannot be empty");
                } else if (str_location.equals("")) {
                    TastyToast.makeText(getApplicationContext(), "Enter Location", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
                    edt_location.setError("Location cannot be empty");
                } else {

                    try {

                        if (str_client_type.equals("Select Client Type")) {
                            str_client_type = "";
                        } else {
                        }
                        if (str_ref_by.equals("Reffered By")) {
                            str_ref_by = "";

                        } else {
                        }
                        if (str_ref_by.equals("Any Other")) {
                            str_ref_by = edt_ref_by.getText().toString().trim();
                        } else {
                        }
                        if (str_design_sheet.equals("Design Sheet Required?")) {
                            str_design_sheet = "";
                        } else {
                        }

                        dialog = new SpotsDialog(Activity_Edit_Enquiry.this);
                        dialog.show();
                        queue = Volley.newRequestQueue(Activity_Edit_Enquiry.this);
                        Function_Submit_Enquiry();
                    } catch (Exception e) {

                    }

                }
            }
        });


        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(Activity_Edit_Enquiry.this);

        str_enq_no = sharedPreferences.getString("str_enq_no", "str_enq_no");

        try {
            dialog = new SpotsDialog(Activity_Edit_Enquiry.this);
            dialog.show();
            queue = Volley.newRequestQueue(Activity_Edit_Enquiry.this);
            GetViewEnquiries();
        } catch (Exception e) {

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


    /*******************************
     *  PIC UPLOADER
     * ***************************/

    // Recomended builder
    public void ImagePicker() {
        ImagePicker.with(this)
                .setFolderMode(true) // set folder mode (false by default)
                .setFolderTitle("Folder") // folder selection title
                .setImageTitle("Tap to select") // image selection title
                .setMultipleMode(true) // multi mode (default mode)
                .setMaxSize(1)// max images can be selected (999 by default)
                .setKeepScreenOn(true)
                .start();

        Config config = new Config();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {
            images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);

            StringBuilder sb = new StringBuilder();
            for (int i = 0, l = images.size(); i < l; i++) {

                String str_img_path = images.get(i).getPath();

                Bitmap bmBitmap = BitmapFactory.decodeFile(str_img_path);
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                bmBitmap.compress(Bitmap.CompressFormat.JPEG, 25, bao);
                byte[] ba = bao.toByteArray();
                encodedstring = Base64.encodeToString(ba, 0);
                Log.e("base64", "-----" + encodedstring);

                img_view.setImageBitmap(bmBitmap);

                Arraylist_image_encode.add(encodedstring);

                txt_img_count.setText("(" + Arraylist_image_encode.size() + ")" + " Images Added");
                btn_add_pic.setText("Change Pic");
            }
            Encode_Image1();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void Encode_Image1() {

        listString = TextUtils.join("IMAGE:", Arraylist_image_encode);

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


    /********************************
     *FUNCTION SUBMIT ENQUIRY
     *********************************/
    private void Function_Submit_Enquiry() {

        str_latitude = String.valueOf(latitude);
        str_longitude = String.valueOf(longitude);

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_update_enquiry, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    if (success == 1) {
                        dialog.dismiss();

                        TastyToast.makeText(getApplicationContext(), "Enquiry Updated Successfully", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);


                    } else {

                        dialog.dismiss();
                        TastyToast.makeText(getApplicationContext(), " Oops..! Enquiry Submission Failed", TastyToast.LENGTH_LONG, TastyToast.ERROR);

                    }
                    dialog.dismiss();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("name", str_name);
                params.put("type", str_client_type);
                params.put("mobileno", str_mobile);
                params.put("email", str_email);
                params.put("area", str_location);
                params.put("qty", str_approx_no_of_windows);
                params.put("sq_ft", str_approx_sq_feet);
                params.put("desc", str_proj_description);
                params.put("status", str_current_project_status);
                params.put("project_date", str_proj_completion_date);
                params.put("image", listString);
                params.put("lat", str_latitude);
                params.put("lon", str_longitude);
                params.put("user_id", str_user_id);
                params.put("refered_by", str_ref_by);
                params.put("design_sheet", str_design_sheet);
                params.put("enq_no", str_enq_no);


                System.out.println("name  " + str_name);
                System.out.println("type  " + str_client_type);
                System.out.println("mobileno  " + str_mobile);
                System.out.println("email  " + str_email);
                System.out.println("area  " + str_location);
                System.out.println("qty  " + str_approx_no_of_windows);
                System.out.println("sq_ft  " + str_approx_sq_feet);
                System.out.println("desc  " + str_proj_description);
                System.out.println("status  " + str_current_project_status);
                System.out.println("project_date  " + str_proj_completion_date);
                System.out.println("lat  " + str_latitude);
                System.out.println("lon  " + str_longitude);
                System.out.println("user_id  " + str_user_id);
                System.out.println("refered_by  " + str_ref_by);
                System.out.println("design_sheet  " + str_design_sheet);

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
