package melembraai.matheusfroes.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import melembraai.matheusfroes.db.ReminderDAO;
import melembraai.matheusfroes.domain.Reminder;

/**
 * Created by mathe on 27/01/2016.
 */
public class NotificationPublisher extends BroadcastReceiver {
    public static String NOTIFICATION = "notification";
    private ReminderDAO reminderDAO;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        Reminder reminder = (Reminder) intent.getSerializableExtra("reminder");

        reminderDAO = new ReminderDAO(context);

        int id = Integer.valueOf(reminder.getId()+"");
        notificationManager.notify(id, notification);
        reminderDAO.updateReminderStatus(reminder, Reminder.STATUS_INACTIVE);
    }
}
