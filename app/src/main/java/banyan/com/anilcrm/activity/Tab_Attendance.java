package banyan.com.anilcrm.activity;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import banyan.com.anilcrm.R;
import banyan.com.anilcrm.global.GPSTracker;
import banyan.com.anilcrm.global.SessionManager;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import pugman.com.simplelocationgetter.SimpleLocationGetter;


/**
 * Created by Jo on 05/03/2018.
 */
public class Tab_Attendance extends Fragment implements SimpleLocationGetter.OnLocationGetListener{

    ImageView img_finger;

    String TAG = "Attendance";

    ProgressDialog pDialog;
    public static RequestQueue queue;

    GPSTracker gps;
    Double latitude, longitude;

    // Session Manager Class
    SessionManager session;

    String str_user_name, str_user_id;


    public Tab_Attendance() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview  = inflater.inflate(R.layout.fragment_attendance, null);

        // Session Manager
        session = new SessionManager(getActivity());

        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
        str_user_name = user.get(SessionManager.KEY_USER);
        str_user_id = user.get(SessionManager.KEY_USER_ID);

        img_finger = (ImageView) rootview.findViewById(R.id.attendance_img_fingerprint);

        img_finger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* gps = new GPSTracker(getActivity());
                if (gps.canGetLocation()) {

                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();

                } else {
                    gps.showSettingsAlert();
                }

                if (latitude == 0.0) {

                    Strat_track();


                    Toast.makeText(getActivity(), "Internal Error Lat", Toast.LENGTH_LONG).show();

                } else if (longitude == 0.0) {

                    gps = new GPSTracker(getActivity());
                    if (gps.canGetLocation()) {

                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();

                    } else {
                        gps.showSettingsAlert();
                    }


                    Toast.makeText(getActivity(), "Internal Error lon", Toast.LENGTH_LONG).show();

                } else {
                    System.out.println("LAT : " + latitude);
                    System.out.println("LON : " + longitude);

                    pDialog = new ProgressDialog(getActivity());
                    pDialog.setMessage("Please wait...");
                    pDialog.show();
                    pDialog.setCancelable(false);
                    queue = Volley.newRequestQueue(getActivity());
                    Function_Attendance();
                }*/

               NewLocation();

            }
        });

        return rootview;
    }

    public void NewLocation(){

        SimpleLocationGetter getter = new SimpleLocationGetter(getActivity(), this);
        getter.getLastLocation();
    }

    @Override
    public void onLocationReady(Location location){
        Log.d("LOCATION", "onLocationReady: lat="+location.getLatitude() + " lon="+location.getLongitude());

        System.out.println("LOCATION 1 :: " + location.getLatitude());
        System.out.println("LOCATION 2 :: " + location.getLongitude());

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please wait...");
        pDialog.show();
        pDialog.setCancelable(false);
        queue = Volley.newRequestQueue(getActivity());
        Function_Attendance();

    }

    @Override
    public void onError(String error){
        Log.e("LOCATION", "Error: "+error);
    }

    /********************************
     * User Attendance Register
     *********************************/

    private void Function_Attendance() {

        final String str_lat = String.valueOf(latitude);
        final String str_long = String.valueOf(longitude);

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_attendance, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                Log.d("USER_REGISTER", response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("success");

                    System.out.println("REG" + success);

                    if (success == 1) {

                        Crouton.makeText(getActivity(),
                                "Your Attendance Registered Successfully",
                                Style.CONFIRM)
                                .show();

                    } else if (success == 2) {
                        Crouton.makeText(getActivity(),
                                "Your Attendance Already Registered",
                                Style.INFO)
                                .show();
                    } else {
                        Crouton.makeText(getActivity(),
                                "Something Went Wrong !",
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

                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                pDialog.hide();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", str_user_id);
                params.put("lat", str_lat);
                params.put("long", str_long);

                System.out.println("user_id" + str_user_id);
                System.out.println("str_lat" + str_lat);
                System.out.println("str_long" + str_long);

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }


    /************************************
     * Start Tracking
     * ************************************/

    private void Strat_track(){

        gps = new GPSTracker(getActivity());
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            System.out.println("ATTENDANCE LAT : " + latitude);
            System.out.println("ATTENDANCE LON : " + longitude);

        } else {
            gps.showSettingsAlert();
        }

    }

}
