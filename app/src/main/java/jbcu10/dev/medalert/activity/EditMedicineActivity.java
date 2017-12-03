package jbcu10.dev.medalert.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jbcu10.dev.medalert.R;
import jbcu10.dev.medalert.config.AppController;
import jbcu10.dev.medalert.db.MedicineRepository;
import jbcu10.dev.medalert.model.Medicine;

public class EditMedicineActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {

    public static final String DATEPICKER_TAG = "Date Picker";
    private static final String TAG = EditMedicineActivity.class.getSimpleName();
    public MedicineRepository medicineRepository;
    @BindView(R.id.edit_expiration)
    EditText edit_expiration;
    @BindView(R.id.edit_type)
    EditText edit_type;
    @BindView(R.id.edit_name)
    EditText edit_name;
    @BindView(R.id.edit_generic_name)
    EditText edit_generic_name;
    @BindView(R.id.edit_description)
    EditText edit_description;
    @BindView(R.id.edit_diagnosis)
    EditText edit_diagnosis;
    @BindView(R.id.edit_total)
    EditText edit_total;
    @BindView(R.id.button_submit)
    Button button_submit;
    @BindView(R.id.edit_schedule)
    EditText edit_schedule;
    Calendar calendar;
    DatePickerDialog datePickerDialog;
    Medicine medicine = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_medicine);
        ButterKnife.bind(this);
        initializedViews();
        medicineRepository = new MedicineRepository(EditMedicineActivity.this);
        AppController appController = AppController.getInstance();
        HomeActivity.selectedItem =1;
        medicine = appController.getMedicine();
        setMedicineData(medicine);
        calendar = Calendar.getInstance();
        datePickerDialog = DatePickerDialog.newInstance(EditMedicineActivity.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
    }

    @OnClick(R.id.edit_expiration)
    public void onClickEditExpiration(View view) {
        datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
    }


    private void setMedicineData(Medicine medicine) {
        edit_name.setText(medicine.getName());
        edit_generic_name.setText(medicine.getGenericName());
        edit_description.setText(medicine.getDescription());
        edit_diagnosis.setText(medicine.getDiagnosis());

        Date date = new Date(medicine.getExpiration());
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        edit_expiration.setText(df.format(date));
        edit_type.setText(medicine.getType());
        edit_total.setText(String.valueOf(medicine.getTotal()));
        StringBuffer stringBuffer = new StringBuffer();
        if( medicine.getSchedules()!=null) {
            List<String> strings = new LinkedList<>();
            strings.addAll(medicine.getSchedules());
            for (int a = 0 ; a<strings.size();a++) {
                stringBuffer.append(strings.get(a));
                if(a!=strings.size()-1){
                    stringBuffer.append(", ");
                }
            }
            edit_schedule.setText(stringBuffer);
        }

    }

    @OnClick(R.id.edit_type)
    public void onClickEditType(View view) {

        new MaterialDialog.Builder(this)
                .title("Select Type")
                .items(R.array.type)
                .itemsCallbackSingleChoice(-1, (dialog, view1, which, text) -> {

                    edit_type.setText(text);
                    return true;
                })
                .positiveText("Submit")
                .show();

    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        month = month + 1;
        String smonth = month + "";
        String sday = day + "";
        if (month < 10) {
            smonth = "0" + month;
        }
        if (day < 10) {
            sday = "0" + day;
        }
        edit_expiration.setText(sday + "-" + smonth + "-" + year);
    }


    public void initializedViews() {
        edit_expiration = findViewById(R.id.edit_expiration);
        edit_type = findViewById(R.id.edit_type);
        edit_name = findViewById(R.id.edit_name);
        edit_generic_name = findViewById(R.id.edit_generic_name);
        edit_description = findViewById(R.id.edit_description);
        edit_diagnosis = findViewById(R.id.edit_diagnosis);
        edit_total = findViewById(R.id.edit_total);
        edit_schedule = findViewById(R.id.edit_schedule);

    }

    @OnClick(R.id.button_submit)
    public void onClickButtonSubmit(View view) {

        new MaterialDialog.Builder(EditMedicineActivity.this)
                .title("Save Medicine?")
                .content("Are you sure you want save this items?")
                .positiveText("Save")
                .negativeText("Cancel")
                .onPositive((dialog, which) -> {

                    String expirationDateString = edit_expiration.getText().toString();
                    DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    Date expirationDate;
                    long milliseconds = 0;
                    try {
                        expirationDate = df.parse(expirationDateString);
                        milliseconds = expirationDate.getTime();

                    } catch (ParseException e) {
                        Log.d("Error", e.getMessage());
                    }


                    try {
                        boolean isCreated = medicineRepository.update(new Medicine(medicine.getId(),medicine.getUuid(), edit_name.getText().toString(), edit_generic_name.getText().toString(), edit_diagnosis.getText().toString(), edit_description.getText().toString(), milliseconds, Integer.parseInt(edit_total.getText().toString()), null, edit_type.getText().toString(),getSchedule()));

                        if (isCreated) {
                            Intent intent = new Intent(EditMedicineActivity.this, HomeActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        }
                        if (!isCreated) {
                            Snackbar.make(findViewById(android.R.id.content), "Failed to Save Medicine!", Snackbar.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.d("Error", e.getMessage());
                    }


                })
                .onNegative((dialog, which) -> {
                }).show();

    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    @OnClick(R.id.edit_schedule)
    public void onClickEditSchedule(View view) {

        new MaterialDialog.Builder(this)
                .title("Select Schedule")
                .items(R.array.schedule)
                .itemsCallbackMultiChoice(null, (dialog, which, text) -> {

                    StringBuffer stringBuffer = new StringBuffer();
                    for (int i = 0; i < text.length; i++) {
                        stringBuffer.append(text[i]);
                        if(i!=text.length-1){
                            stringBuffer.append(", ");
                        }
                    }
                    edit_schedule.setText(stringBuffer);
                    return true;
                })
                .positiveText("Choose")
                .show();

    }
    public List<String> getSchedule(){
        String[] schedules = edit_schedule.getText().toString().split(", ");
        List<String> scheduleString = new LinkedList<>();
        scheduleString.addAll(Arrays.asList(schedules));
        return scheduleString;

    }
}
