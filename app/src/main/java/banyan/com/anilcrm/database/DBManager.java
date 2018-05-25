package banyan.com.anilcrm.database;

/**
 * Vivekanandhan
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // order form
    public long insert_Order_Form(String shop_id, String final_order, String user_id) {
        System.out.println("### insert_Order_Form ");

        ContentValues contentValue = new ContentValues();

        contentValue.put(DatabaseHelper.ORDER_FORM_SHOP_ID, shop_id);
        contentValue.put(DatabaseHelper.ORDER_FORM_FINAL_ORDER, final_order);
        contentValue.put(DatabaseHelper.ORDER_FORM_USER_ID, user_id);

        long response = database.insert(DatabaseHelper.TABLE_ORDER_FORM, null, contentValue);
        System.out.println("### insert_Order_Form : response " + response);
        return response;
    }

    public Cursor Fetch_Order_Form() {
        System.out.println("### Fetch_Order_Form");
        String[] columns = new String[]{DatabaseHelper.ORDER_FORM_ID, DatabaseHelper.ORDER_FORM_SHOP_ID, DatabaseHelper.ORDER_FORM_FINAL_ORDER, DatabaseHelper.ORDER_FORM_USER_ID};

        Cursor cursor = database.query(DatabaseHelper.TABLE_ORDER_FORM, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }


    public int delete_Order_Form(long _id) {
        return database.delete(DatabaseHelper.TABLE_ORDER_FORM, DatabaseHelper.ORDER_FORM_ID + "=" + _id, null);
    }


    // enquiry form
    public long insert_Add_Enquiry(String user_id, String shop_name,String name, String mobileno,String landline, String lat,
                                   String lon, String area,String shop_previous, String agency_id,String type, String image,
                                   String loc, String remarks,String branch_id
                                   ) {
        System.out.println("### insert_Order_Form ");

        ContentValues contentValue = new ContentValues();

        contentValue.put(DatabaseHelper.ENQUIRY_USER_ID, user_id);
        contentValue.put(DatabaseHelper.ENQUIRY_SHOP_NAME, shop_name);

        contentValue.put(DatabaseHelper.ENQUIRY_NAME, name);
        contentValue.put(DatabaseHelper.ENQUIRY_MOBILE_NO, mobileno);

        contentValue.put(DatabaseHelper.ENQUIRY_LANDLINE, landline);
        contentValue.put(DatabaseHelper.ENQUIRY_LAT, lat);

        contentValue.put(DatabaseHelper.ENQUIRY_LON, lon);
        contentValue.put(DatabaseHelper.ENQUIRY_AREA, area);

        contentValue.put(DatabaseHelper.ENQUIRY_SHOP_PREVIOUS, shop_previous);
        contentValue.put(DatabaseHelper.ENQUIRY_AGENCY_ID, agency_id);

        contentValue.put(DatabaseHelper.ENQUIRY_TYPE, type);
        contentValue.put(DatabaseHelper.ENQUIRY_IMAGE, image);

        contentValue.put(DatabaseHelper.ENQUIRY_LOC, loc);
        contentValue.put(DatabaseHelper.ENQUIRY_REMARKS, remarks);

        contentValue.put(DatabaseHelper.ENQUIRY_BRANCH_ID, branch_id);

        long response = database.insert(DatabaseHelper.TABLE_ENQUIRY, null, contentValue);
        System.out.println("### insert_Add_Enquiry : response " + response);
        return response;
    }


    public Cursor Fetch_Enquiry() {
        System.out.println("### Fetch_Enquiry");
        String[] columns = new String[]{ DatabaseHelper.ENQUIRY_ID, DatabaseHelper.ENQUIRY_USER_ID, DatabaseHelper.ENQUIRY_SHOP_NAME,
                DatabaseHelper.ENQUIRY_NAME, DatabaseHelper.ENQUIRY_MOBILE_NO,
                DatabaseHelper.ENQUIRY_LANDLINE, DatabaseHelper.ENQUIRY_LAT, DatabaseHelper.ENQUIRY_LON,
                DatabaseHelper.ENQUIRY_AREA, DatabaseHelper.ENQUIRY_SHOP_PREVIOUS, DatabaseHelper.ENQUIRY_AGENCY_ID,
                DatabaseHelper.ENQUIRY_TYPE, DatabaseHelper.ENQUIRY_IMAGE, DatabaseHelper.ENQUIRY_LOC,
                DatabaseHelper.ENQUIRY_REMARKS, DatabaseHelper.ENQUIRY_BRANCH_ID};

        Cursor cursor = database.query(DatabaseHelper.TABLE_ENQUIRY, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }


    public int delete_Enquiry(long _id) {
        return database.delete(DatabaseHelper.TABLE_ENQUIRY, DatabaseHelper.ENQUIRY_ID + "=" + _id, null);
    }

}
