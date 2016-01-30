package melembraai.matheusfroes.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import melembraai.matheusfroes.domain.Reminder;
import melembraai.matheusfroes.extras.NotificationScheduler;

/**
 * Created by mathe on 30/01/2016.
 */
public class NotificationActionsReceiver extends BroadcastReceiver {
    public static String ACTION_OK = "melembraai.matheusfroes.receivers.NotificationActionsReceiver.OK";
    public static String ACTION_SNOOZE = "melembraai.matheusfroes.receivers.NotificationActionsReceiver.SNOOZE";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Reminder reminder = (Reminder) intent.getSerializableExtra("reminder");
        int id = Integer.valueOf(reminder.getId() + "");

        if (intent.getAction().equals(ACTION_OK)) {
            manager.cancelAll();
        } else if (intent.getAction().equals(ACTION_SNOOZE)) {
            NotificationScheduler scheduler = new NotificationScheduler(context);
            int snoozeTime = context.getSharedPreferences("USER_PREFERENCES", Context.MODE_PRIVATE).getInt("snoozeTime", 10);
            scheduler.schedule(reminder, (snoozeTime * 60000));
            Toast.makeText(context, "O próximo aviso será em " + snoozeTime + " minutos.", Toast.LENGTH_SHORT).show();
            manager.cancelAll();
        }

    }
}
