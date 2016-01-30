package melembraai.matheusfroes.activities;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import melembraai.matheusfroes.adapters.TabsAdapter;
import melembraai.matheusfroes.db.ReminderDAO;
import melembraai.matheusfroes.domain.Reminder;
import melembraai.matheusfroes.extras.NotificationScheduler;
import melembraai.matheusfroes.extras.SlidingTabLayout;
import melembraai.matheusfroes.fragments.ActiveRemindersFragment;

/**
 * Created by mathe on 27/01/2016.
 */
public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private SlidingTabLayout slidingTabLayout;
    private TabsAdapter tabsAdapter;
    private Calendar reminderDate;
    private EditText datePickerEdt, timePickerEdt, reminderContentEdt;
    private Button cancelButton, confirmButton;
    private AlarmManager alarmManager;
    private NumberFormat format;
    private Calendar currentDate;
    private ReminderDAO reminderDAO;
    private NotificationScheduler notificationScheduler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Lembretes");
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        notificationScheduler = new NotificationScheduler(this);
        reminderDate = Calendar.getInstance();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        currentDate = Calendar.getInstance();
        format = NumberFormat.getIntegerInstance();
        format.setMinimumIntegerDigits(2);
        reminderDAO = new ReminderDAO(this);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabsAdapter = new TabsAdapter(getSupportFragmentManager(), this);

        viewPager.setAdapter(tabsAdapter);
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        slidingTabLayout.setSelectedIndicatorColors(Color.WHITE);

        slidingTabLayout.setCustomTabView(R.layout.layout_tab_view, R.id.txt_tab);
        slidingTabLayout.setViewPager(viewPager);

        FloatingActionButton newReminder = (FloatingActionButton) findViewById(R.id.new_reminder_btn);
        newReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewReminderDialog();
            }
        });

    }


    private void showNewReminderDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = LayoutInflater.from(this).inflate(R.layout.new_reminder_layout, null);

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
        SpinnerAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, remindersTime);

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
        Reminder reminder = new Reminder(reminderContentEdt.getText().toString(), reminderDate, Reminder.STATUS_ACTIVE);
        long reminderId = reminderDAO.insert(reminder);
        reminder.setId(reminderId);

        Toast.makeText(this, "Lembrete adicionado com sucesso.", Toast.LENGTH_SHORT).show();
        notificationScheduler.schedule(reminder);
        ActiveRemindersFragment activeRemindersFragment = (ActiveRemindersFragment) tabsAdapter.getItem(0);
        activeRemindersFragment.updateRemindersList();
    }


    private void showTimePicker() {
        final TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.change_snooze_time:
                showChangeSnoozeTimeDialog();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void showChangeSnoozeTimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.snooze_time_layout, null);

        final EditText snoozeTimeEdt = (EditText) view.findViewById(R.id.snooze_time_edt);
        int snoozeTime = getSharedPreferences("USER_PREFERENCES", MODE_PRIVATE).getInt("snoozeTime", 10);
        snoozeTimeEdt.setText(String.valueOf(snoozeTime));
        Button saveBtn = (Button) view.findViewById(R.id.save_btn);

        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();

        saveBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkFields()) {
                    SharedPreferences.Editor editor = getSharedPreferences("USER_PREFERENCES", MODE_PRIVATE).edit();
                    editor.putInt("snoozeTime", Integer.valueOf(snoozeTimeEdt.getText().toString()));
                    editor.apply();

                    dialog.dismiss();
                }
            }

            private boolean checkFields() {
                boolean checked = true;

                if (snoozeTimeEdt.getText().toString().trim().isEmpty()) {
                    checked = false;
                    Toast.makeText(MainActivity.this, "O campo não pode estar vazio", Toast.LENGTH_SHORT).show();
                }


                if (Integer.valueOf(snoozeTimeEdt.getText().toString()) <= 0) {
                    checked = false;
                    Toast.makeText(MainActivity.this, "O tempo não pode ser menor ou igual a 0", Toast.LENGTH_SHORT).show();
                }

                return checked;
            }
        });

    }
}
