package banyan.com.anilcrm.activity;

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
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import banyan.com.anilcrm.Activity_Register;
import banyan.com.anilcrm.R;
import banyan.com.anilcrm.global.SessionManager;
import dmax.dialog.SpotsDialog;


/**
 * Created by Banyan on 05-Mar-18.
 */

public class Activity_Login extends AppCompatActivity {

    // Session Manager Class
    SessionManager session;

    private static final String TAG_STATION_CODE = "stationCode";
    private static final String TAG_STATION_NAME = "stationName";

    SpotsDialog dialog;
    public static RequestQueue queue;
    String TAG = "reg";

    private static long back_pressed;

    private Toolbar mToolbar;

    EditText edt_user_email, edt_password;

    Button btn_login, btn_register;

    String str_user_email, str_password = "";

    String str_user_id, str_user_role, str_user_emp_id, str_user_photo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("LOGIN");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Session Manager
        session = new SessionManager(getApplicationContext());

        edt_user_email = (EditText) findViewById(R.id.edt_username);
        edt_password = (EditText) findViewById(R.id.edt_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (Button) findViewById(R.id.btn_register);


        btn_login.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {

                str_user_email = edt_user_email.getText().toString();
                str_password = edt_password.getText().toString();

                System.out.println("USER_NAME from edit text ::::" + str_user_email);
                System.out.println("PASSWORD  from edit text ::::" + str_password);

                if (str_user_email.equals("")) {
                    TastyToast.makeText(getApplicationContext(), "Enter Username", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
                    edt_user_email.setError("Email cannot be empty");
                } else if (str_password.equals("")) {
                    TastyToast.makeText(getApplicationContext(), "Enter Password", TastyToast.LENGTH_SHORT, TastyToast.WARNING);
                    edt_password.setError("Password cannot be empty");
                } else {

                    try {
                        dialog = new SpotsDialog(Activity_Login.this);
                        dialog.show();
                        queue = Volley.newRequestQueue(Activity_Login.this);
                        Function_Login();
                    } catch (Exception e) {

                    }

                }

            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), Activity_Register.class);
                startActivity(i);
                finish();
            }
        });

    }

    /********************************
     * LOGIN FUNCTION
     *********************************/
    private void Function_Login() {

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_login, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");
                    String message = obj.getString("message");

                    if (success == 1) {
                        dialog.dismiss();

                        JSONObject obj_data = obj.getJSONObject("data");

                        str_user_id = obj_data.getString("user_id");
                        str_user_email = obj_data.getString("username");
                        str_user_role = obj_data.getString("role");
                        str_user_emp_id = obj_data.getString("emp_id");
                        str_user_photo = obj_data.getString("photo");

                        TastyToast.makeText(getApplicationContext(), "Login Success", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);

                        session.createLoginSession(str_user_email, str_user_emp_id);
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("str_user_email", str_user_email);
                        editor.putString("str_user_id", str_user_id);
                        editor.putString("str_user_role", str_user_role);
                        editor.putString("str_user_emp_id", str_user_emp_id);
                        editor.putString("str_user_photo", str_user_photo);

                        editor.commit();

                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        finish();

                    } else {

                        dialog.dismiss();
                        TastyToast.makeText(getApplicationContext(), "Login Failed : Invalid Username or Password", TastyToast.LENGTH_LONG, TastyToast.ERROR);

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

                params.put("user_email", str_user_email);
                params.put("password", str_password);

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    @Override
    public void onBackPressed() {

        if (back_pressed + 2000 > System.currentTimeMillis()) {

            this.moveTaskToBack(true);
        } else {
            Toast.makeText(getBaseContext(), "Press once again to exit!",
                    Toast.LENGTH_SHORT).show();

        }
        back_pressed = System.currentTimeMillis();
    }


}