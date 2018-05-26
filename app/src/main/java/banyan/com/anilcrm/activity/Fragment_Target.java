package banyan.com.anilcrm.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import banyan.com.anilcrm.R;
import banyan.com.anilcrm.global.SessionManager;
import dmax.dialog.SpotsDialog;


/**
 * Created by Jo 05-03-2018.
 */
public class Fragment_Target extends Fragment {


    public Fragment_Target() {
        // Required empty public constructor
    }

    String TAG = "Complaints";
    public static RequestQueue queue;
    SpotsDialog dialog;

    SessionManager session;
    String str_user_name, str_user_id, str_gcm = "";

    public static final String TAG_FROM_DATE = "from_date";
    public static final String TAG_TO_DATE = "to_date";
    public static final String TAG_TARGET_AMOUNT = "target_amount";
    public static final String TAG_REACHED_AMOUNT = "reached_amount";

    int target = 0;
    int achive = 0;

    String str_from_date, str_to_date = "";

    private PieChart pieChart;
    private TextView txt_from , txt_to;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_target, container, false);

        session = new SessionManager(getActivity());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        str_user_name = user.get(SessionManager.KEY_USER);
        str_user_id = user.get(SessionManager.KEY_USER_ID);

        pieChart = (PieChart) rootView.findViewById(R.id.pie_chart);
        txt_from = (TextView) rootView.findViewById(R.id.target_txt_from);
        txt_to = (TextView) rootView.findViewById(R.id.target_txt_to);


        try {

            dialog = new SpotsDialog(getActivity());
            dialog.show();
            queue = Volley.newRequestQueue(getActivity());
            GetMyTask();

        }catch (Exception e) {

        }

        return rootView;
    }

    /*****************************
     * GET My Task
     ***************************/

    public void GetMyTask() {

        System.out.println("CAME 1");
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_target, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("success");

                    if (success == 1) {

                        JSONArray arr;

                        arr = obj.getJSONArray("target");

                        for (int i = 0; arr.length() > i; i++) {
                            JSONObject obj1 = arr.getJSONObject(i);

                            target = obj1.getInt(TAG_TARGET_AMOUNT);
                            achive = obj1.getInt(TAG_REACHED_AMOUNT);
                            str_from_date = obj1.getString(TAG_FROM_DATE);
                            str_to_date = obj1.getString(TAG_TO_DATE);

                            System.out.println("target" + target);
                            System.out.println("achive" + achive);
                            try{
                                txt_from.setText("From : " + str_from_date);
                                txt_to.setText("To : " + str_to_date);
                                dialog.dismiss();
                                function_piechart();
                            }catch (Exception e) {

                            }

                        }

                        dialog.dismiss();
                    } else if (success == 0) {

                        dialog.dismiss();
                        Toast.makeText(getActivity(), "No Target Found For You", Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                // stopping swipe refresh
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();

                dialog.dismiss();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", str_user_id); // replace as str_id

                System.out.println("user_id" + str_user_id);

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }

    private void function_piechart(){

        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(target, 0));
        entries.add(new Entry(achive, 1));

        PieDataSet dataset = new PieDataSet(entries, "Anil CRM");

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("Amount Target");
        labels.add("Amount Achived");

        PieData data = new PieData(labels, dataset);
        dataset.setColors(ColorTemplate.JOYFUL_COLORS); //
        pieChart.setDescription("");
        pieChart.setData(data);


        pieChart.animateY(2000);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setCenterText(generateCenterSpannableText());

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);

        pieChart.setDrawCenterText(true);

        pieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

    }


    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("Amount Target from : " + str_from_date + "\n To Date : " + str_to_date
                + "\n" + achive + " / " + target );

        return s;
    }
}
