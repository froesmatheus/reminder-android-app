package melembraai.matheusfroes.activities;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import melembraai.matheusfroes.adapters.ReminderAdapter;
import melembraai.matheusfroes.db.ReminderDAO;
import melembraai.matheusfroes.domain.Reminder;
import melembraai.matheusfroes.receivers.NotificationPublisher;

public class MainActivity extends AppCompatActivity {
    private Calendar reminderDate;
    private EditText datePickerEdt, timePickerEdt, reminderContentEdt;
    private Button cancelButton, confirmButton;
    private AlarmManager alarmManager;
    private NumberFormat format;
    private Calendar currentDate;
    private ReminderDAO reminderDAO;
    private ReminderAdapter adapter;
    private ListView remindersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.new_reminder_btn);
        reminderDate = Calendar.getInstance();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        currentDate = Calendar.getInstance();
        format = NumberFormat.getIntegerInstance();
        format.setMinimumIntegerDigits(2);

        reminderDAO = new ReminderDAO(this);

        adapter = new ReminderAdapter(this, reminderDAO.getReminderCursor(), true);
        remindersList = (ListView) findViewById(R.id.reminders_list);
        remindersList.setAdapter(adapter);
        TextView emptyView = (TextView) findViewById(R.id.empty_view);
        remindersList.setEmptyView(emptyView);

        registerForContextMenu(remindersList);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewReminderDialog();
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.add("Excluir");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (item.getTitle().equals("Excluir")) {
            reminderDAO.delete(info.id);
            adapter.changeCursor(reminderDAO.getReminderCursor());
            adapter.notifyDataSetChanged();
        }

        return super.onContextItemSelected(item);
    }

    private void showNewReminderDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.new_reminder_layout, null);

        datePickerEdt = (EditText) view.findViewById(R.id.date_picker_edt);
        timePickerEdt = (EditText) view.findViewById(R.id.time_picker_edt);
        reminderContentEdt = (EditText) view.findViewById(R.id.reminder_message);
        cancelButton = (Button) view.findViewById(R.id.cancel_btn);
        confirmButton = (Button) view.findViewById(R.id.confirm_btn);
        final Spinner reminderSpinner = (Spinner) view.findViewById(R.id.spinner_reminder);
        CheckBox remindEarlyChk = (CheckBox) view.findViewById(R.id.remind_me_early_chk);
        final TextView remindEarly = (TextView) view.findViewById(R.id.remind_early_txt);

        remindEarlyChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    reminderSpinner.setVisibility(View.VISIBLE);
                    remindEarly.setVisibility(View.VISIBLE);
                } else {
                    reminderSpinner.setVisibility(View.GONE);
                    remindEarly.setVisibility(View.GONE);
                }
            }
        });

        List<String> remindersTime = new ArrayList<>();

        remindersTime.add("30min antes");
        remindersTime.add("1h antes");
        remindersTime.add("3hs antes");
        remindersTime.add("10hs antes");
        remindersTime.add("1 dia antes");
        SpinnerAdapter adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, remindersTime );

        reminderSpinner.setAdapter(adapter);

        currentDate = Calendar.getInstance();

        datePickerEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePicker();
                }
            }
        });

        datePickerEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        timePickerEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        timePickerEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showTimePicker();
                }
            }
        });

        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyFields()) {
                    addNewReminder();
                    dialog.dismiss();
                } else {
                    Toast.makeText(MainActivity.this, "Todos os campos precisam ser preenchidos.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addNewReminder() {
        Reminder reminder = new Reminder(reminderContentEdt.getText().toString(), reminderDate);

        reminderDAO.insert(reminder);
        adapter.changeCursor(reminderDAO.getReminderCursor());
        adapter.notifyDataSetChanged();

        Toast.makeText(this, "Lembrete adicionado com sucesso.", Toast.LENGTH_SHORT).show();
        scheduleNewReminder(reminder);
    }

    private void scheduleNewReminder(Reminder reminder) {
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);

        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, getNotification("Novo lembrete!", reminder.getReminderContent()));


        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, reminder.getReminderDate().getTimeInMillis(), pendingIntent);
    }

    private void showTimePicker() {
        final TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                reminderDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                reminderDate.set(Calendar.MINUTE, minute);
                reminderDate.set(Calendar.SECOND, 0);

                timePickerEdt.setText(format.format(hourOfDay) + ":" + format.format(minute));
            }
        }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true);

        timePickerDialog.show();
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                reminderDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                reminderDate.set(Calendar.MONTH, monthOfYear);
                reminderDate.set(Calendar.YEAR, year);

                datePickerEdt.setText(format.format(dayOfMonth) + "/" + format.format(++monthOfYear) + "/" + year);
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(currentDate.getTimeInMillis());
        datePickerDialog.show();
    }


    private boolean verifyFields() {
        boolean verified = true;

        if (reminderContentEdt.getText().toString().trim().isEmpty()) {
            verified = false;
        }

        if (datePickerEdt.getText().toString().trim().isEmpty()) {
            verified = false;
        }

        if (timePickerEdt.getText().toString().trim().isEmpty()) {
            verified = false;
        }

        return verified;
    }

    public Notification getNotification(String title, String message) {
        Notification.Builder builder = new Notification.Builder(this)

        .setContentTitle(title)
        .setContentText(message)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setDefaults(Notification.DEFAULT_ALL);

        return builder.build();

    }
}
