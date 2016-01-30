package melembraai.matheusfroes.db;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

import melembraai.matheusfroes.domain.Reminder;

/**
 * Created by Bruno on 06/01/2016.
 */
public class ReminderDAO {
    private SQLiteDatabase db;

    public ReminderDAO(Context context) {
        DB dbCore = new DB(context);
        db = dbCore.getWritableDatabase();
    }

    public long insert(Reminder reminder) {
        ContentValues ct = new ContentValues();

        ct.put(DB.REMINDER_COLUMN_CONTENT, reminder.getReminderContent());
        ct.put(DB.REMINDER_COLUMN_DATE, reminder.getReminderDate().getTimeInMillis());
        ct.put(DB.REMINDER_COLUMN_STATUS, reminder.getStatus());

        long status = db.insert(DB.REMINDER_TABLE_NAME, null, ct);

        return status;
    }

    public boolean delete(long _id) {
        long status = db.delete(DB.REMINDER_TABLE_NAME, "_id = ?", new String[]{String.valueOf(_id)});

        return status != 0;
    }

    public Cursor getReminderCursor(String reminderStatus) {
        return db.rawQuery("SELECT * FROM " + DB.REMINDER_TABLE_NAME + " WHERE " + DB.REMINDER_COLUMN_STATUS + " = " + "\"" + reminderStatus + "\""
                + " ORDER BY " + DB.REMINDER_COLUMN_DATE + ";", null);
    }

    public void updateReminderStatus(Reminder reminder, String reminderStatus) {
        ContentValues ct = new ContentValues();

        ct.put(DB.REMINDER_COLUMN_DATE, reminder.getReminderDate().getTimeInMillis());
        ct.put(DB.REMINDER_COLUMN_CONTENT, reminder.getReminderContent());
        ct.put(DB.REMINDER_COLUMN_STATUS, reminderStatus);

        db.update(DB.REMINDER_TABLE_NAME, ct, "_id = ?", new String[]{String.valueOf(reminder.getId())});
    }
}
