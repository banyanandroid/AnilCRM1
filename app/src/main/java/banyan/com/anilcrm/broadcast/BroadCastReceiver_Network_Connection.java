package banyan.com.anilcrm.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import banyan.com.anilcrm.service.Service_Add_Enquiry;
import banyan.com.anilcrm.service.Service_Order_Form;

/**
 *
 * Vivekanandhan
 *
 * */

public class BroadCastReceiver_Network_Connection extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("### MyReceiver : onReceive");


        // run service to upload form details data
        Intent order_intent = new Intent(context, Service_Order_Form.class);
        context.startService(order_intent);

        System.out.println("### call Service_Order_Details");
        // run service to upload enquiry data
        Intent enquiry_intent = new Intent(context, Service_Add_Enquiry.class);
        context.startService(enquiry_intent);

    }

}