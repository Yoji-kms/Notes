package com.yoji.notes.notes;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.yoji.notes.App;
import com.yoji.notes.R;
import com.yoji.notes.authentication.SettingsActivity;
import com.yoji.notes.database.NoteData;

import java.util.Calendar;
import java.util.Objects;

public class CreateOrEditNoteActivity extends AppCompatActivity {

    private EditText titleEdtTxt;
    private EditText textEdtTxt;
    private EditText deadlineEdtTxt;
    private Button calendarBtn;
    private Button saveNoteBtn;
    private CheckBox hasDeadlineCheckbox;

    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private long id;
    private NoteData noteData;

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String text = textEdtTxt.getText().toString();
            saveNoteBtn.setEnabled(!text.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private CompoundButton.OnCheckedChangeListener checkboxListener = (buttonView, isChecked) -> {
        calendarBtn.setEnabled(isChecked);
        deadlineEdtTxt.setEnabled(isChecked);
        if (!isChecked) {
            deadlineEdtTxt.setText("");
        }
    };

    private DatePickerDialog.OnDateSetListener onDateSetListener = (view, year, month, dayOfMonth) -> {
        String cleanDate = numToString(dayOfMonth) + numToString(month + 1) + numToString(year);
        String formattedDate = maskCleanDate(cleanDate);
        deadlineEdtTxt.setText(formattedDate);

        datePickerDialog.dismiss();
    };

    private View.OnClickListener calendarBtnOnClickListener = v -> {
        datePickerDialog = new DatePickerDialog(
                this,
                onDateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    };

    private TextWatcher deadlineTextWatcher = new TextWatcher() {
        private String current = "";

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().equals(current) && !s.toString().isEmpty()) {
                String inputClean = removeDividers(s.toString());
                String currentClean = removeDividers(current);

                int selection = inputClean.length();
                for (int i = 2; i <= inputClean.length() && i < 6; i += 2) {
                    selection++;
                }
                if (inputClean.equals(currentClean)) selection--;

                inputClean = maskCleanDate(inputClean);

                current = inputClean;
                deadlineEdtTxt.setText(current);
                deadlineEdtTxt.setSelection(getNumberBetween(selection, 0, current.length()));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private View.OnClickListener saveNoteBtnOnClickListener = v -> {
        saveNoteToDb();
        finish();
    };

    private View.OnClickListener cancelBtnOnClickListener = v -> finish();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        id = getIntent().getLongExtra("id", 0);
        init();
    }

    private void init() {
        titleEdtTxt = findViewById(R.id.titleEdtTxtId);
        textEdtTxt = findViewById(R.id.textEdtTxtId);
        deadlineEdtTxt = findViewById(R.id.deadlineEdtTxtId);
        hasDeadlineCheckbox = findViewById(R.id.hasDeadlineCheckboxId);
        calendarBtn = findViewById(R.id.calendarBtnId);
        saveNoteBtn = findViewById(R.id.saveNoteBtnId);
        Button cancelBtn = findViewById(R.id.cancelBtnId);

        Toolbar toolbar = findViewById(R.id.toolbarId);
        toolbar.setTitle(R.string.note);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        textEdtTxt.addTextChangedListener(textWatcher);
        deadlineEdtTxt.addTextChangedListener(deadlineTextWatcher);
        hasDeadlineCheckbox.setOnCheckedChangeListener(checkboxListener);
        saveNoteBtn.setOnClickListener(saveNoteBtnOnClickListener);
        cancelBtn.setOnClickListener(cancelBtnOnClickListener);
        calendarBtn.setOnClickListener(calendarBtnOnClickListener);

        calendar = Calendar.getInstance();
        if (id != 0) {
            noteData = App.getNoteRepository().getNoteById(id);
            restoreNoteData(noteData);
        }
    }

    private String numToString(int number) {
        if (number < 10) {
            return "0" + number;
        }
        return "" + number;
    }

    private int getNumberBetween(int number, int min, int max) {
        return Math.max(min, Math.min(max, number));
    }

    private String maskCleanDate(String cleanDate) {
        if (cleanDate.length() == 0) {
            return "";
        }
        if (cleanDate.length() < 8) {
            switch (cleanDate.length()) {
                case 2:
                    int day = Integer.parseInt(cleanDate.substring(0, 2));
                    day = getNumberBetween(day, 1, 31);
                    cleanDate = numToString(day);
                    break;
                case 4:
                    int month = Integer.parseInt(cleanDate.substring(2, 4));
                    month = getNumberBetween(month, 1, 12);
                    cleanDate = cleanDate.substring(0, 2) + numToString(month);
                    break;
            }
            String ddmmyyyy = "ddmmyyyy";
            cleanDate = cleanDate + ddmmyyyy.substring(cleanDate.length());
        } else {
            int day = Integer.parseInt(cleanDate.substring(0, 2));
            int month = Integer.parseInt(cleanDate.substring(2, 4));
            int year = Integer.parseInt(cleanDate.substring(4, 8));

            calendar.set(Calendar.MONTH, month);
            year = getNumberBetween(year, 2020, 2100);
            calendar.set(Calendar.YEAR, year);

            day = Math.min(day, calendar.getActualMaximum(Calendar.DATE));
            cleanDate = numToString(day) + numToString(month) + numToString(year);
        }

        String dayStr = cleanDate.substring(0, 2);
        String monthStr = cleanDate.substring(2, 4);
        String yearStr = cleanDate.substring(4, 8);

        return getString(R.string.date_format, dayStr, monthStr, yearStr);
    }

    private String removeDividers(String string) {
        return string.replaceAll("[^\\d.]|\\.", "");
    }

    private void saveNoteToDb() {
        String title = titleEdtTxt.getText().toString().trim();
        String text = textEdtTxt.getText().toString().trim();
        String deadline = deadlineEdtTxt.getText().toString().trim();

        App.getNoteRepository().saveNote(noteData, title, text, deadline);
    }

    private void restoreNoteData(NoteData noteData) {
        titleEdtTxt.setText(noteData.getTitle());
        textEdtTxt.setText(noteData.getText());
        hasDeadlineCheckbox.setChecked(noteData.getHasDeadline());
        if (noteData.getDeadline() != null) {
            deadlineEdtTxt.setText(noteData.getDeadline().toString());
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                finish();
                break;
            case R.id.settings_menu:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}