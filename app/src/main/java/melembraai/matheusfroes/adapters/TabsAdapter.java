package melembraai.matheusfroes.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Toast;

import melembraai.matheusfroes.fragments.ActiveRemindersFragment;
import melembraai.matheusfroes.fragments.InactiveRemindersFragment;

/**
 * Created by mathe on 27/01/2016.
 */
public class TabsAdapter extends FragmentPagerAdapter {
    private Context context;
    private String[] titles = {"ATUAIS", "ANTERIORES"};
    private Fragment activeReminders, inactiveReminders;

    public TabsAdapter(FragmentManager fm, Context ctx) {
        super(fm);
        this.context = ctx;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:
                if (activeReminders != null) {
                    return activeReminders;
                } else {
                    return activeReminders = new ActiveRemindersFragment();
                }
            case 1:
                if (inactiveReminders != null) {
                    return inactiveReminders;
                } else {
                    return inactiveReminders = new InactiveRemindersFragment();
                }
        }

        return null;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
