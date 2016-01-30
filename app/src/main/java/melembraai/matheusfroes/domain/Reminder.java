package melembraai.matheusfroes.domain;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Bruno on 06/01/2016.
 */
public class Reminder implements Serializable {
    public static String STATUS_ACTIVE = "active";
    public static String STATUS_INACTIVE = "inactive";


    private long id;
    private String reminderContent;
    private Calendar reminderDate;
    private String status;


    public Reminder(String reminderContent, Calendar reminderDate, String status) {
        this.reminderContent = reminderContent;
        this.reminderDate = reminderDate;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
