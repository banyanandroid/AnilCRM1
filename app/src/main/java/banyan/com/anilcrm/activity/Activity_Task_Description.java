package banyan.com.anilcrm.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import banyan.com.anilcrm.R;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by User on 9/27/2016.
 */
public class Activity_Task_Description extends AppCompatActivity {

    private Toolbar mToolbar;
    EditText edt_name, edt_description, edt_note, edt_date;
    String str_schedule_id, str_schedule_name, str_schedule_description, str_schedule_note, str_schedule_updateon;
    Button btn_update;

    ProgressDialog pDialog;
    public static RequestQueue queue;

    String TAG = "Schedule";
    String str_new_note;

    public static final String TAG_SCHEDULE_ID = "schedule_id";
    public static final String TAG_SCHEDULE_TITLE = "schedule_title";
    public static final String TAG_SCHEDULE_DES = "schedule_description";
    public static final String TAG_SCHEDULE_NOTE = "schedule_note";
    public static final String TAG_SCHEDULE_DATE = "schedule_date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_detail);

        edt_name = (EditText) findViewById(R.id.schedule_edt_taskname);
        edt_description = (EditText) findViewById(R.id.schedule_edt_taskdes);
        edt_note = (EditText) findViewById(R.id.schedule_edt_tasknote);
        edt_date = (EditText) findViewById(R.id.schedule_edt_taskupdateon);
        btn_update = (Button) findViewById(R.id.myschedule_btn_update);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(Activity_Task_Description.this);

        str_schedule_id = sharedPreferences.getString("schedule_id", "schedule_id");
        str_schedule_name = sharedPreferences.getString("schedule_name", "schedule_name");
        str_schedule_description = sharedPreferences.getString("schedule_description", "schedule_description");
        str_schedule_note = sharedPreferences.getString("schedule_note", "schedule_note");
        str_schedule_updateon = sharedPreferences.getString("schedule_update", "schedule_update");

        try {
            edt_name.setText("" + str_schedule_name);
            edt_description.setText("" + str_schedule_description);
            edt_note.setText("" + str_schedule_note);
            edt_date.setText("" + str_schedule_updateon);
        } catch (Exception e) {

        }

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                str_new_note = edt_note.getText().toString();

                if (str_new_note.equals("")) {
                    edt_note.setError("Please Enter Note");
                } else {

                    pDialog = new ProgressDialog(Activity_Task_Description.this);
                    pDialog.setMessage("Please wait...");
                    pDialog.show();
                    pDialog.setCancelable(false);
                    queue = Volley.newRequestQueue(Activity_Task_Description.this);
                    Function_UpdateSchedule();
                }

            }
        });

    }


    /********************************
     * Function_Update Schedule
     *********************************/

    private void Function_UpdateSchedule() {

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_update_my_task, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                Log.d("USER_REGISTER", response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("success");

                    System.out.println("REG" + success);

                    if (success == 1) {

                        pDialog.hide();

                        Crouton.makeText(Activity_Task_Description.this,
                                "Schedule Updated Successfully",
                                Style.CONFIRM)
                                .show();

                        try {
                            queue = Volley.newRequestQueue(Activity_Task_Description.this);
                            GetMySchedule();

                        } catch (Exception e) {
                            // TODO: handle exception
                        }

                    } else {
                        pDialog.hide();

                        Crouton.makeText(Activity_Task_Description.this,
                                "Schedule Update Failed Please Try Again",
                                Style.ALERT)
                                .show();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                pDialog.hide();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.hide();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("schedule_id", str_schedule_id);
                params.put("note", str_new_note);

                System.out.println("schedule_id" + str_schedule_id);
                System.out.println("note" + str_new_note);

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }


    /*****************************
     * GET Schedule
     ***************************/

    public void GetMySchedule() {

        String tag_json_obj = "json_obj_req";

        System.out.println("CAME DA 1");

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_get_single_task, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("success");

                    if (success == 1) {

                        JSONArray arr;

                        arr = obj.getJSONArray("Schedule");

                        for (int i = 0; arr.length() > i; i++) {
                            JSONObject obj1 = arr.getJSONObject(i);

                            String id = obj1.getString(TAG_SCHEDULE_ID);
                            String title = obj1.getString(TAG_SCHEDULE_TITLE);
                            String description = obj1.getString(TAG_SCHEDULE_DES);
                            String note = obj1.getString(TAG_SCHEDULE_NOTE);
                            String date = obj1.getString(TAG_SCHEDULE_DATE);

                            edt_name.setText("" + title);
                            edt_description.setText("" + description);
                            edt_note.setText("" + note);
                            edt_date.setText("" + date);

                        }

                    } else if (success == 0) {

                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

               //   Toast.makeText(Activity_Task_Description.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("task_id", str_schedule_id);

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }


}
