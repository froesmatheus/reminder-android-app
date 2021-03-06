package melembraai.matheusfroes.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import melembraai.matheusfroes.activities.R;
import melembraai.matheusfroes.adapters.ReminderAdapter;
import melembraai.matheusfroes.db.ReminderDAO;
import melembraai.matheusfroes.domain.Reminder;
import melembraai.matheusfroes.extras.NotificationScheduler;

/**
 * Created by mathe on 27/01/2016.
 */
public class InactiveRemindersFragment extends Fragment {
    private ReminderDAO reminderDAO;
    private ReminderAdapter adapter;
    private ListView remindersList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.active_reminders, null);

        reminderDAO = new ReminderDAO(getActivity());

        adapter = new ReminderAdapter(getActivity(), reminderDAO.getReminderCursor(Reminder.STATUS_INACTIVE), true);
        remindersList = (ListView) view.findViewById(R.id.reminders_list);
        remindersList.setAdapter(adapter);
        TextView emptyView = (TextView) view.findViewById(R.id.empty_view);
        remindersList.setEmptyView(emptyView);

        registerForContextMenu(remindersList);

        return view;
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
            updateRemindersList();
        }

        return super.onContextItemSelected(item);
    }

    public void updateRemindersList() {
        adapter.changeCursor(reminderDAO.getReminderCursor(Reminder.STATUS_INACTIVE));
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onResume() {
        super.onResume();
        updateRemindersList();
    }
}
