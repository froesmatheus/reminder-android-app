package melembraai.matheusfroes.extras;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import melembraai.matheusfroes.activities.R;
import melembraai.matheusfroes.db.ReminderDAO;
import melembraai.matheusfroes.domain.Reminder;
import melembraai.matheusfroes.receivers.NotificationPublisher;
import melembraai.matheusfroes.receivers.NotificationActionsReceiver;

/**
 * Created by mathe on 27/01/2016.
 */
public class NotificationScheduler {
    private AlarmManager alarmManager;
    private ReminderDAO reminderDAO;
    private Context context;

    public NotificationScheduler(Context context) {
        this.context = context;
        reminderDAO = new ReminderDAO(context);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void schedule(Reminder reminder) {
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);

        notificationIntent.putExtra("reminder", reminder);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, getNotification("Novo lembrete!", reminder));


        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, reminder.getReminderDate().getTimeInMillis(), pendingIntent);
    }

    public void schedule(Reminder reminder, long timeInMillis) {
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);

        notificationIntent.putExtra("reminder", reminder);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, getNotification("Novo lembrete!", reminder));


        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, 0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + timeInMillis, pendingIntent);
    }


    public Notification getNotification(String title, Reminder reminder) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)

                .setContentTitle(title)
                .setContentText(reminder.getReminderContent())
                .setSmallIcon(R.drawable.ic_reminder)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true);


        Intent okIntent = new Intent(context, NotificationActionsReceiver.class);
        okIntent.setAction(NotificationActionsReceiver.ACTION_OK);
        okIntent.putExtra("reminder", reminder);
        PendingIntent okPendingIntent = PendingIntent.getBroadcast(context, 0, okIntent, 0);

        Intent snoozeIntent = new Intent(context, NotificationActionsReceiver.class);
        snoozeIntent.setAction(NotificationActionsReceiver.ACTION_SNOOZE);
        snoozeIntent.putExtra("reminder", reminder);
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context, 0, snoozeIntent, 0);


        NotificationCompat.Action actionSnooze = new NotificationCompat.Action(R.mipmap.ic_action_snooze, "ADIAR", snoozePendingIntent);
        NotificationCompat.Action actionDone = new NotificationCompat.Action(R.mipmap.ic_action_done, "OK", okPendingIntent);

        builder.addAction(actionSnooze);
        builder.addAction(actionDone);



        return builder.build();
    }

    public void cancelSchedule(Reminder reminder) {
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, getNotification("Novo lembrete!", reminder));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
    }
}
