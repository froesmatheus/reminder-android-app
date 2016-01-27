package melembraai.matheusfroes.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Matheus on 06/01/2016.
 */
public class DB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MeLembraAi";
    private static final int DATABASE_VERSION = 1;

    public static final String REMINDER_TABLE_NAME = "Reminders";
    public static final String REMINDER_COLUMN_ID = "_id";
    public static final String REMINDER_COLUMN_CONTENT = "content";
    public static final String REMINDER_COLUMN_DATE = "date";
    public static final String REMINDER_COLUMN_STATUS = "status";

    private static final String CREATE_REMINDER_TABLE_DDL = "CREATE TABLE IF NOT EXISTS " + DB.REMINDER_TABLE_NAME + " (" +
            REMINDER_COLUMN_ID +  " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            REMINDER_COLUMN_CONTENT + " TEXT NOT NULL, " +
            REMINDER_COLUMN_DATE + " INTEGER NOT NULL, " +
            REMINDER_COLUMN_STATUS + " TEXT NOT NULL);";


    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_REMINDER_TABLE_DDL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + REMINDER_TABLE_NAME + ";");
        onCreate(db);
    }
}
