package melembraai.matheusfroes.domain;

import java.util.Calendar;

/**
 * Created by Bruno on 06/01/2016.
 */
public class Reminder {
    private int id;
    private String reminderContent;
    private Calendar reminderDate;

    public Reminder(String reminderContent, Calendar reminderDate) {
        this.reminderContent = reminderContent;
        this.reminderDate = reminderDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReminderContent() {
        return reminderContent;
    }

    public void setReminderContent(String reminderContent) {
        this.reminderContent = reminderContent;
    }

    public Calendar getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(Calendar reminderDate) {
        this.reminderDate = reminderDate;
    }
}
