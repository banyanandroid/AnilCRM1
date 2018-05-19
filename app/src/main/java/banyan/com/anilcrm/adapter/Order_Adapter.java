package banyan.com.anilcrm.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import banyan.com.anilcrm.R;
import banyan.com.anilcrm.activity.Tab_Order_Fragment;


public class Order_Adapter extends BaseAdapter {
	private Activity activity;
    private Context context;
    private LinearLayout singleMessageContainer;

    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;

    private String[] bgColors;

    public Order_Adapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        bgColors = activity.getApplicationContext().getResources().getStringArray(R.array.movie_serial_bg);
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
          
        public View getView(int position, View convertView, ViewGroup parent) {
            View v=convertView;
            if(convertView==null)
                v = inflater.inflate(R.layout.list_row_enquiry, null);

            TextView title = (TextView)v.findViewById(R.id.title);
            TextView date = (TextView)v.findViewById(R.id.serial);
            TextView status = (TextView)v.findViewById(R.id.status);

            HashMap<String, String> result = new HashMap<String, String>();
            result = data.get(position);

            title.setText(result.get(Tab_Order_Fragment.TAG_SHOP_NAME));
            date.setText(result.get(Tab_Order_Fragment.TAG_QTY));
            status.setText(result.get(Tab_Order_Fragment.TAG_PRICE));

            String color = bgColors[position % bgColors.length];
            title.setBackgroundColor(Color.parseColor(color));

            return v;
        
    }
    
}