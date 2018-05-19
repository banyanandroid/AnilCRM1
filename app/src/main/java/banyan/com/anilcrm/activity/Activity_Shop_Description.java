package banyan.com.anilcrm.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import banyan.com.anilcrm.R;
import dmax.dialog.SpotsDialog;

/**
 * Created by Jarvis on 29-03-2018.
 */

public class Activity_Shop_Description  extends AppCompatActivity {

    private Toolbar mToolbar;
    SpotsDialog dialog;

    EditText edt_agency ,edt_shop_name, edt_owner_name, edt_contact_number, edt_landline,
            edt_location, edt_previous_supply, edt_shop_type,edt_remark;

    ImageView img_view;

    String str_agency,str_shop_name, str_owner_name, str_contact_number, str_landline,
            str_location, str_previous_supply,str_shop_type, str_img, str_remark = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquiry_des);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Shop Info");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("from_value","enquiry");
                startActivity(i);
                finish();
            }
        });

        edt_agency = (EditText) findViewById(R.id.edt_agency);
        edt_shop_name = (EditText) findViewById(R.id.edt_shop_name);
        edt_owner_name = (EditText) findViewById(R.id.edt_owner_name);
        edt_contact_number = (EditText) findViewById(R.id.edt_contact_number);
        edt_landline = (EditText) findViewById(R.id.edt_landline_number);
        edt_location = (EditText) findViewById(R.id.edt_location);
        edt_previous_supply = (EditText) findViewById(R.id.edt_previous_supply);
        edt_shop_type = (EditText) findViewById(R.id.edt_shop_type);
        edt_remark = (EditText) findViewById(R.id.edt_remark);

        img_view = (ImageView) findViewById(R.id.shop_img);

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(Activity_Shop_Description.this);

        str_agency = sharedPreferences.getString("agencies_name", "agencies_name");
        str_shop_name = sharedPreferences.getString("shop_name", "shop_name");
        str_owner_name = sharedPreferences.getString("shop_owner_name", "shop_owner_name");
        str_contact_number = sharedPreferences.getString("shop_contact", "shop_contact");
        str_landline = sharedPreferences.getString("shop_landline", "shop_landline");
        str_location = sharedPreferences.getString("shop_location", "shop_location");
        str_previous_supply = sharedPreferences.getString("shop_previous", "shop_previous");
        str_shop_type = sharedPreferences.getString("shop_type", "shop_type");
        str_img = sharedPreferences.getString("shop_photos", "shop_photos");
        str_img = sharedPreferences.getString("shop_photos", "shop_photos");
        str_remark = sharedPreferences.getString("remark", "remark");

        try {

            edt_agency.setText(""+str_agency);
            edt_shop_name.setText(""+str_shop_name);
            edt_owner_name.setText(""+str_owner_name);
            edt_contact_number.setText(""+str_contact_number);
            edt_landline.setText(""+str_landline);
            edt_location.setText(""+str_location);
            edt_previous_supply.setText(""+str_previous_supply);
            edt_shop_type.setText(""+str_shop_type);
            edt_remark.setText(""+str_remark);

            Glide.with(getApplicationContext())
                    .load(str_img)
                    .thumbnail(0.5f)
                    .into(img_view);

        }catch (Exception e){

        }


    }

    }
