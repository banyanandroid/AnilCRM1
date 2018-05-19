package banyan.com.anilcrm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import banyan.com.anilcrm.activity.Activity_Login;
import banyan.com.anilcrm.activity.AppConfig;
import banyan.com.anilcrm.global.GPSTracker;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;



/**
 * Created by Jo on 9/21/2016.
 */
public class Activity_Register extends Activity {

    Button btn_register, btn_login_red;
    EditText edt_user_name,edt_email, edt_password;

    GPSTracker gps;
    private static long back_pressed;

    String str_password, str_name, str_email;
    Double latitude, longitude;
    TelephonyManager telephonyManager;

    String TAG = "reg";
    private static final String TAG_NAME = "user_name";
    private static final String TAG_ID = "user_id";

    String str_user_name, str_user_id;

    ProgressDialog pDialog;
    public static RequestQueue queue;

    String regid = null;
    String GcmId = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn_register = (Button) findViewById(R.id.reg_btn_submit);
        btn_login_red = (Button) findViewById(R.id.reg_btn_login);
        edt_user_name = (EditText) findViewById(R.id.login_edt_username);
        edt_email = (EditText) findViewById(R.id.login_edt_email);
        edt_password = (EditText) findViewById(R.id.login_edt_pwd);



        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                str_name = edt_user_name.getText().toString();
                str_email = edt_email.getText().toString();
                str_password = edt_password.getText().toString();


                if (str_name.equals("")) {
                    edt_user_name.setError("Please Enter Username");
                }else if (str_email.equals("")) {
                    edt_email.setError("Please Enter Email");
                } else if (str_password.equals("")) {
                    edt_password.setError("please Enter your Password");
                } else {
                    pDialog = new ProgressDialog(Activity_Register.this);
                    pDialog.setMessage("Please wait...");
                    pDialog.show();
                    pDialog.setCancelable(false);
                    queue = Volley.newRequestQueue(Activity_Register.this);
                    Function_Register();
                }

            }
        });

        btn_login_red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), Activity_Login.class);
                startActivity(i);
                finish();
            }
        });

    }



    /********************************
     * User Registration
     *********************************/

    private void Function_Register() {

        final String str_lat = "0.0";
        final String str_long = "0.0";

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_registration, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                Log.d("USER_REGISTER", response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("status");

                    System.out.println("REG" + success);

                    if (success == 1) {

                        edt_user_name.setText("");
                        edt_email.setText("");
                        edt_password.setText("");

                        Crouton.makeText(Activity_Register.this,
                                "Registeration Completed You Can Login Now",
                                Style.CONFIRM)
                                .show();

                    } else {
                        Crouton.makeText(Activity_Register.this,
                                "Register Failed Please Try Again",
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

                params.put("name", str_name);
                params.put("user_name", str_email);
                params.put("password", str_password);

                System.out.println("name" + str_name);
                System.out.println("user_name" + str_email);
                System.out.println("password" + str_password);

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
