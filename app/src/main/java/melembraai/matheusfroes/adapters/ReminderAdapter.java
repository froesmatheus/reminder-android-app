package melembraai.matheusfroes.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Calendar;

import melembraai.matheusfroes.activities.R;
import melembraai.matheusfroes.db.DB;

/**
 * Created by Bruno on 06/01/2016.
 */
public class ReminderAdapter extends CursorAdapter {
    private NumberFormat format;


    public ReminderAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);

        format = NumberFormat.getIntegerInstance();
        format.setMinimumIntegerDigits(2);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.reminder_view, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView reminderContentTxt = (TextView) view.findViewById(R.id.reminder_content_tv);
        TextView reminderDateTxt = (TextView) view.findViewById(R.id.reminder_date_tv);

        String reminderContentStr = cursor.getString(cursor.getColumnIndex(DB.REMINDER_COLUMN_CONTENT));
        long reminderDate = cursor.getLong(cursor.getColumnIndex(DB.REMINDER_COLUMN_DATE));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(reminderDate);

        String dateStr = format.format(calendar.get(Calendar.DAY_OF_MONTH)) + "/" +
                format.format(calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);

        String hourMinuteStr = format.format(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + format.format(calendar.get(Calendar.MINUTE));

        reminderContentTxt.setText(reminderContentStr);
        reminderDateTxt.setText(dateStr + " - " + hourMinuteStr);
    }
}
