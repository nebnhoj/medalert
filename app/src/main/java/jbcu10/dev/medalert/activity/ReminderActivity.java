package jbcu10.dev.medalert.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import jbcu10.dev.medalert.R;
import jbcu10.dev.medalert.config.AppController;
import jbcu10.dev.medalert.db.DatabaseCRUDHandler;
import jbcu10.dev.medalert.model.Medicine;
import jbcu10.dev.medalert.model.Reminder;

public class ReminderActivity extends AppCompatActivity {
    public DatabaseCRUDHandler db;
    Reminder reminder = null;
    LinearLayout ll_alarm_handler, ll_medicine_handler;
    EditText edit_description;
    @BindView(R.id.button_submit)
    Button button_submit;
    ArrayList<String> strings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        db = new DatabaseCRUDHandler(this);
        initializedViews();



        AppController appController = AppController.getInstance();
        reminder = db.getReminder(appController.getReminderId());
        edit_description.setText(reminder.getDescription());
        reminder.setMedicineList(db.getAllReminderMedicine(reminder.getUuid()));
        reminder.setTime(db.getAllReminderTime(reminder.getUuid()));

        List<Medicine> medicines =db.getAllMedicine();
        int sameUuid = 0;
        for (Medicine medicine:medicines){
            if(reminder.getMedicineList()!=null) {
                for (Medicine medicine1 : reminder.getMedicineList()) {
                    if (medicine1.getUuid().equals(medicine.getUuid())) {
                        sameUuid++;
                    }
                }
            }
            final CheckBox checkBoxMedicine = new CheckBox(this);
            checkBoxMedicine.setText(medicine.getName());
            checkBoxMedicine.setId(medicine.getId());
            checkBoxMedicine.setHint(medicine.getUuid());
            if(sameUuid>0){
                checkBoxMedicine.setChecked(true);
            }
            checkBoxMedicine.setLayoutParams(
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
            checkBoxMedicine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        strings.add(checkBoxMedicine.getHint().toString());
                    }else{
                        strings.remove(checkBoxMedicine.getHint().toString());
                    }

                }
            });
            ll_medicine_handler.addView(checkBoxMedicine);
            sameUuid=0;
        }
        int a= 0;
        for (String time : reminder.getTime()) {
            TextView txtAlarm = new TextView(this);
            txtAlarm.setText(time);
            txtAlarm.setId(a++);
            txtAlarm.setTextColor(Color.BLACK);
            txtAlarm.setLayoutParams(
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
            ll_alarm_handler.addView(txtAlarm);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.reminder, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.add_alarm:
            //    timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void initializedViews(){
        ll_alarm_handler = findViewById(R.id.ll_alarm_handler);
        ll_medicine_handler = findViewById(R.id.ll_medicine_handler);
        edit_description = findViewById(R.id.edit_description);
    }
}