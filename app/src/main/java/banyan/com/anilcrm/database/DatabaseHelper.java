package banyan.com.anilcrm.database;

/**
 * Vivekanandhan
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Information
    static final String DB_NAME = "JOURNALDEV_COUNTRIES.DB";

    // database version
    static final int DB_VERSION = 1;


    // Table Name Order Form
    public static final String TABLE_ORDER_FORM = "order_form";
    // Table columns
    public static final String ORDER_FORM_ID = "order_form_id";
    public static final String ORDER_FORM_SHOP_ID = "shop_id";
    public static final String ORDER_FORM_FINAL_ORDER = "final_order";
    public static final String ORDER_FORM_USER_ID = "user_id";
    // Creating table query
    private static final String CREATE_TABLE_ORDER_FORM = "create table " + TABLE_ORDER_FORM + "("
            + ORDER_FORM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ORDER_FORM_SHOP_ID + " TEXT NOT NULL, " +
            ORDER_FORM_FINAL_ORDER + " TEXT NOT NULL, " +
            ORDER_FORM_USER_ID + " TEXT NOT NULL);";

    // Table Name : enquiry
    public static final String TABLE_ENQUIRY = "enquiry";
    // Table columns
    public static final String ENQUIRY_ID = "enquiry_id";
    public static final String ENQUIRY_USER_ID = "user_id";
    public static final String ENQUIRY_SHOP_NAME = "shop_name";
    public static final String ENQUIRY_NAME = "name";
    public static final String ENQUIRY_MOBILE_NO = "mobileno";
    public static final String ENQUIRY_LANDLINE = "landline";
    public static final String ENQUIRY_LAT = "lat";
    public static final String ENQUIRY_LON = "lon";
    public static final String ENQUIRY_AREA = "area";
    public static final String ENQUIRY_SHOP_PREVIOUS = "shop_previous";
    public static final String ENQUIRY_AGENCY_ID = "agency_id";
    public static final String ENQUIRY_TYPE = "type";
    public static final String ENQUIRY_IMAGE = "image";
    public static final String ENQUIRY_LOC = "loc";
    public static final String ENQUIRY_REMARKS = "remarks";
    public static final String ENQUIRY_BRANCH_ID = "branch_id";

    // Creating table query
    private static final String CREATE_TABLE_ENQUIRY = "create table " + TABLE_ENQUIRY + "("
            + ENQUIRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ENQUIRY_USER_ID + " TEXT NOT NULL, " +
            ENQUIRY_SHOP_NAME + " TEXT NOT NULL, " +
            ENQUIRY_NAME + " TEXT NOT NULL, " +
            ENQUIRY_MOBILE_NO + " TEXT NOT NULL, " +
            ENQUIRY_LANDLINE + " TEXT NOT NULL, " +
            ENQUIRY_LAT + " TEXT NOT NULL, " +
            ENQUIRY_LON + " TEXT NOT NULL, " +
            ENQUIRY_AREA + " TEXT NOT NULL, " +
            ENQUIRY_SHOP_PREVIOUS + " TEXT NOT NULL, " +
            ENQUIRY_AGENCY_ID + " TEXT NOT NULL, " +
            ENQUIRY_TYPE + " TEXT NOT NULL, " +
            ENQUIRY_IMAGE + " TEXT NOT NULL, " +
            ENQUIRY_LOC + " TEXT NOT NULL, " +
            ENQUIRY_REMARKS + " TEXT NOT NULL, " +
            ENQUIRY_BRANCH_ID + " TEXT NOT NULL);";


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("### DatabaseHelper : onCreate");
        System.out.println("### CREATE_TABLE_ORDER_FORM : " + CREATE_TABLE_ORDER_FORM);
        db.execSQL(CREATE_TABLE_ORDER_FORM);

        System.out.println("### CREATE_TABLE_ENQUIRY : " + CREATE_TABLE_ENQUIRY);
        db.execSQL(CREATE_TABLE_ENQUIRY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("### DatabaseHelper : onUpgrade");

        System.out.println("### DROP TABLE IF EXISTS " + TABLE_ORDER_FORM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_FORM);

        System.out.println("### DROP TABLE IF EXISTS " + TABLE_ENQUIRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENQUIRY);
        onCreate(db);
    }
}
