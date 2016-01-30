package melembraai.matheusfroes.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import melembraai.matheusfroes.activities.R;
import melembraai.matheusfroes.adapters.ReminderAdapter;
import melembraai.matheusfroes.db.ReminderDAO;
import melembraai.matheusfroes.domain.Reminder;

public class ActiveRemindersFragment extends Fragment {
    private ReminderDAO reminderDAO;
    private ReminderAdapter activeRemindersAdapter;
    private ListView remindersList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.active_reminders, null);

        reminderDAO = new ReminderDAO(getActivity());

        activeRemindersAdapter = new ReminderAdapter(getActivity(), reminderDAO.getReminderCursor(Reminder.STATUS_ACTIVE), true);
        remindersList = (ListView) view.findViewById(R.id.reminders_list);
        remindersList.setAdapter(activeRemindersAdapter);
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
        activeRemindersAdapter.changeCursor(reminderDAO.getReminderCursor(Reminder.STATUS_ACTIVE));
        activeRemindersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRemindersList();
    }
}
