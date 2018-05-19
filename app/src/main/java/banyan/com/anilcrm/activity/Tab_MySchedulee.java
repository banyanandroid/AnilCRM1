package banyan.com.anilcrm.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import banyan.com.anilcrm.R;
import banyan.com.anilcrm.adapter.MyTask_Adapter;
import banyan.com.anilcrm.global.SessionManager;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


/**
 * Created by Jo on 05/03/2018.
 */
public class Tab_MySchedulee extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public Tab_MySchedulee() {
        // Required empty public constructor
    }

    FloatingActionButton fab_addtask;

    String str_user_name, str_user_id;
    String str_task_name, str_task_des;

    ProgressDialog pDialog;
    public static RequestQueue queue;

    // Session Manager Class
    SessionManager session;

    String TAG = "add task";

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView mytask_listView;

    public static final String TAG_TASK_ID = "task_id";
    public static final String TAG_TASK_TITLE = "task_name";
    public static final String TAG_TASK_DES = "task_description";
    public static final String TAG_TASK_NOTE = "task_note";
    public static final String TAG_TASK_DATE = "task_date";

    static ArrayList<HashMap<String, String>> complaint_list;

    HashMap<String, String> params = new HashMap<String, String>();

    public MyTask_Adapter adapter;

    String str_select_task_id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_myschedule, null);

        // Session Manager
        session = new SessionManager(getActivity());

        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
        str_user_name = user.get(SessionManager.KEY_USER);
        str_user_id = user.get(SessionManager.KEY_USER_ID);


        fab_addtask = (FloatingActionButton) rootView.findViewById(R.id.fab_add_task);

        mytask_listView = (ListView) rootView.findViewById(R.id.listView_mytask);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout_MYTASK);

        // Hashmap for ListView
        complaint_list = new ArrayList<HashMap<String, String>>();

        swipeRefreshLayout.setOnRefreshListener(this);

        fab_addtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AddTask();
            }
        });


        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        try {
                                            pDialog = new ProgressDialog(getActivity());
                                            pDialog.setMessage("Please wait...");
                                            pDialog.setCancelable(true);
                                            queue = Volley.newRequestQueue(getActivity());
                                            GetMyTask();

                                        } catch (Exception e) {
                                            // TODO: handle exception
                                        }
                                    }
                                }
        );


        mytask_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String str_select_task_id = complaint_list.get(position).get(TAG_TASK_ID);
                String str_select_task_name = complaint_list.get(position).get(TAG_TASK_TITLE);
                String str_select_task_description = complaint_list.get(position).get(TAG_TASK_DES);
                String str_select_task_note = complaint_list.get(position).get(TAG_TASK_NOTE);
                String str_select_task_update = complaint_list.get(position).get(TAG_TASK_DATE);

                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("task_id", str_select_task_id);
                editor.putString("task_name", str_select_task_name);
                editor.putString("task_description", str_select_task_description);
                editor.putString("task_note", str_select_task_note);
                editor.putString("task_update", str_select_task_update);
                editor.commit();

                Intent i = new Intent(getActivity(), Activity_MySche_Description.class);
                startActivity(i);
            }

        });


        mytask_listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub

                str_select_task_id = complaint_list.get(pos).get(TAG_TASK_ID);

                Delete_Task_alert();

                return true;
            }
        });

        return rootView;
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        try {
            complaint_list.clear();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(true);
            queue = Volley.newRequestQueue(getActivity());
            GetMyTask();

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /*****************************
     * Add My Task Alert
     ***************************/

    public void AddTask() {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li
                .inflate(R.layout.activity_alert_addtask, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        alertDialogBuilder.setTitle("Add New Schedule");

        alertDialogBuilder.setIcon(R.mipmap.ic_launcher);

        alertDialogBuilder.setView(promptsView);

        final EditText edt_task_name = (EditText) promptsView
                .findViewById(R.id.alert_edt_taskname);

        final EditText edt_task_description = (EditText) promptsView
                .findViewById(R.id.alert_edt_taskdes);


        alertDialogBuilder.setCancelable(false)

                .setPositiveButton("Done",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                str_task_name = edt_task_name.getText().toString();
                                str_task_des = edt_task_description.getText().toString();

                                if (str_task_name.equals("")) {
                                    edt_task_name.setError("Please Enter Task Name");
                                    System.out.println("1111");
                                } else if (str_task_des.equals("")) {
                                    edt_task_name.setError("Please Enter Task Description");
                                    System.out.println("2222");
                                } else {

                                    pDialog = new ProgressDialog(getActivity());
                                    pDialog.setMessage("Please wait...");
                                    pDialog.show();
                                    pDialog.setCancelable(false);
                                    queue = Volley.newRequestQueue(getActivity());
                                    Function_addTask();
                                    // dialog.cancel();
                                }

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    /*****************************
     * GET My Task
     ***************************/

    public void GetMyTask() {

        String tag_json_obj = "json_obj_req";
        System.out.println("CAME 1" + AppConfig.url_schedulelist);
        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_schedulelist, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("success");

                    if (success == 1) {

                        JSONArray arr;

                        arr = obj.getJSONArray("schedule");

                        for (int i = 0; arr.length() > i; i++) {
                            JSONObject obj1 = arr.getJSONObject(i);

                            String id = obj1.getString(TAG_TASK_ID);
                            String title = obj1.getString(TAG_TASK_TITLE);
                            String description = obj1.getString(TAG_TASK_DES);
                            String note = obj1.getString(TAG_TASK_NOTE);
                            String date = obj1.getString(TAG_TASK_DATE);

                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();

                            // adding each child node to HashMap key => value
                            map.put(TAG_TASK_ID, id);
                            map.put(TAG_TASK_TITLE, title);
                            map.put(TAG_TASK_DES, description);
                            map.put(TAG_TASK_NOTE, note);
                            map.put(TAG_TASK_DATE, date);

                            complaint_list.add(map);

                            System.out.println("HASHMAP ARRAY" + complaint_list);


                            adapter = new MyTask_Adapter(getActivity(),
                                    complaint_list);
                            mytask_listView.setAdapter(adapter);

                        }

                    } else if (success == 0) {

                        TastyToast.makeText(getActivity(), "No Data Found", TastyToast.LENGTH_SHORT, TastyToast.INFO);
                        adapter = new MyTask_Adapter(getActivity(),
                                complaint_list);
                        mytask_listView.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
                //  pDialog.hide();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();

                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", str_user_id); // replace as str_id

                System.out.println("user_id" + str_user_id);

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

    /********************************
     * Function_addTask
     ********************************/

    private void Function_addTask() {

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_add_schedule, new Response.Listener<String>() {

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
                                "Task Schedule Successfully",
                                Style.CONFIRM)
                                .show();
                        pDialog.hide();
                        try {
                            complaint_list.clear();
                            queue = Volley.newRequestQueue(getActivity());
                            GetMyTask();

                        } catch (Exception e) {
                            // TODO: handle exception
                        }

                    } else {

                        pDialog.hide();
                        Crouton.makeText(getActivity(),
                                "Schedule Added Failed Please Try Again",
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

                params.put("task_name", str_task_name);
                params.put("task_des", str_task_des);
                params.put("user_id", str_user_id);

                System.out.println("task_name" + str_task_name);
                System.out.println("task_des" + str_task_des);
                System.out.println("user_id" + str_user_id);

                return params;
            }

        };

        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        queue.add(request);
    }


    /************************************
     * Delete My Task Alert Dialog
     ***********************************/

    private void Delete_Task_alert() {

        new AlertDialog.Builder(getActivity())
                .setTitle("SIMTA")
                .setMessage("Want to Delete This Task?")
                .setIcon(R.mipmap.ic_launcher)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                    }
                })
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub

                                System.out.println("Task ID " + str_select_task_id);

                                pDialog = new ProgressDialog(getActivity());
                                pDialog.setMessage("Please wait...");
                                pDialog.show();
                                pDialog.setCancelable(false);
                                queue = Volley.newRequestQueue(getActivity());
                                Function_DeleteTask();

                            }
                        }).show();
    }


    /************************************
     * Delete My Task Function
     ***********************************/

    private void Function_DeleteTask() {

        StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.url_delete_schedule, new Response.Listener<String>() {

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
                                "Task Deleted Successfully",
                                Style.CONFIRM)
                                .show();
                        pDialog.hide();

                        try {
                            complaint_list.clear();
                            queue = Volley.newRequestQueue(getActivity());
                            GetMyTask();

                        } catch (Exception e) {
                            // TODO: handle exception
                        }

                    } else {

                        pDialog.hide();
                        Crouton.makeText(getActivity(),
                                "Task Deleted Failed Please Try Again",
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

                params.put("task_id", str_select_task_id);

                System.out.println("task_id" + str_select_task_id);

                return params;
            }

        };

        // Adding request to request queue
        queue.add(request);
    }
}
