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
import android.widget.Toast;

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
import banyan.com.anilcrm.global.SessionManager;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by User on 9/26/2016.
 */
public class Activity_MySche_Description extends AppCompatActivity {

    private Toolbar mToolbar;
    EditText edt_name, edt_description, edt_note, edt_date;
    Button btn_update, btn_cancel;
    String str_task_id, str_task_name, str_task_description, str_task_note, str_task_updateon;

    ProgressDialog pDialog;
    public static RequestQueue queue;

    public static final String TAG_TASK_ID = "task_id";
    public static final String TAG_TASK_TITLE = "task_name";
    public static final String TAG_TASK_DES = "task_description";
    public static final String TAG_TASK_NOTE = "task_note";
    public static final String TAG_TASK_DATE = "task_date";

    String TAG = "add task";

    // Session Manager Class
    SessionManager session;

    String str_name, str_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mytask_detail);

        session = new SessionManager(Activity_MySche_Description.this);

        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
        str_name = user.get(SessionManager.KEY_USER);
        str_id = user.get(SessionManager.KEY_USER_ID);


        edt_name = (EditText) findViewById(R.id.mytask_edt_taskname);
        edt_description = (EditText) findViewById(R.id.mytask_edt_taskdes);
        edt_note = (EditText) findViewById(R.id.mytask_edt_tasknote);
        edt_date = (EditText) findViewById(R.id.mytask_edt_taskupdateon);

        btn_update = (Button) findViewById(R.id.mytask_btn_update);

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
                .getDefaultSharedPreferences(Activity_MySche_Description.this);

        str_task_id = sharedPreferences.getString("task_id", "task_id");
        str_task_name = sharedPreferences.getString("task_name", "task_name");
        str_task_description = sharedPreferences.getString("task_description", "task_description");
        str_task_note = sharedPreferences.getString("task_note", "task_note");
        str_task_updateon = sharedPreferences.getString("task_update", "task_update");

        try {
            edt_name.setText("" + str_task_name);
            edt_description.setText("" + str_task_description);
            edt_note.setText("" + str_task_note);
            edt_date.setText("" + str_task_updateon);
        } catch (Exception e) {

        }

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                str_task_name = edt_name.getText().toString();
                str_task_description = edt_description.getText().toString();
                str_task_note = edt_note.getText().toString();

                if (str_task_name.equals("")) {
                    edt_name.setError("Please Enter Task Name");
                } else if (str_task_description.equals("")) {
                    edt_description.setError("Please Enter Task Description");
                } else if (str_task_note.equals("")) {
                    edt_note.setError("Please Enter Some Note");
                } else {

                    pDialog = new ProgressDialog(Activity_MySche_Description.this);
                    pDialog.setMessage("Please wait...");
                    pDialog.show();
                    pDialog.setCancelable(false);
                    queue = Volley.newRequestQueue(Activity_MySche_Description.this);
                    Function_UpdateTask();
                }
            }
        });

    }

    /********************************
     * Function_UpdateTask
     *********************************/

    private void Function_UpdateTask() {

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_update_schedule, new Response.Listener<String>() {

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

                        Crouton.makeText(Activity_MySche_Description.this,
                                "Task Updated Successfully",
                                Style.CONFIRM)
                                .show();

                        try {
                            queue = Volley.newRequestQueue(Activity_MySche_Description.this);
                            GetMyTask();

                        } catch (Exception e) {
                            // TODO: handle exception
                        }

                    } else {
                        pDialog.hide();

                        Crouton.makeText(Activity_MySche_Description.this,
                                "Task Update Failed Please Try Again",
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

                params.put("task_id", str_task_id);
                params.put("task_name", str_task_name);
                params.put("task_des", str_task_description);
                params.put("task_note", str_task_note);

                System.out.println("task_id" + str_task_id);
                System.out.println("task_name" + str_task_name);
                System.out.println("task_des" + str_task_description);
                System.out.println("task_note" + str_task_note);

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    /*****************************
     * GET REQ
     ***************************/

    public void GetMyTask() {

        String tag_json_obj = "json_obj_req";

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_get_single_schedule, new Response.Listener<String>() {

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

                            String id = obj1.getString(TAG_TASK_ID);
                            String title = obj1.getString(TAG_TASK_TITLE);
                            String description = obj1.getString(TAG_TASK_DES);
                            String note = obj1.getString(TAG_TASK_NOTE);
                            String date = obj1.getString(TAG_TASK_DATE);


                            edt_name.setText("" + title);
                            edt_description.setText("" + description);
                            edt_note.setText("" + note);
                            edt_date.setText("" + date);
                        }

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

                Toast.makeText(Activity_MySche_Description.this, error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("schedule_id", str_task_id); // replace as str_id

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }


}
