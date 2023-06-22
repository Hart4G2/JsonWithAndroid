package com.example.lr5;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalTime;

public class AddRecordActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST_CODE = 12;
    private static final String TAG = "AddRecordActivity";
    private EditText editTextTitle;
    private EditText editTextTime;
    private EditText editTextDescription;
    private ImageView imageView;
    private Button buttonAdd;
    private boolean isUpdating = false;
    private Record record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        editTextTitle = findViewById(R.id.editText_title);
        editTextTime = findViewById(R.id.editText_time);
        editTextDescription = findViewById(R.id.editText_description);
        imageView = findViewById(R.id.imageView);
        buttonAdd = findViewById(R.id.button_add);

        buttonAdd.setOnClickListener(v -> {
            if(isUpdating){
                editRecord();
            } else {
                addRecord();
            }
        });

        imageView.setOnClickListener(v -> openImagePicker());

        editTextTitle.addTextChangedListener(textWatcher);
        editTextTime.addTextChangedListener(textWatcher);
        editTextDescription.addTextChangedListener(textWatcher);

        buttonAdd.setEnabled(false);
        editTextTitle.setEnabled(true);

        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("record")) {
            record = (Record) intent.getSerializableExtra("record");
            isUpdating = true;

            editTextTitle.setText(record.getTitle());
            editTextTitle.setEnabled(false);
            editTextTime.setText(record.getTime().toString());
            editTextDescription.setText(record.getText());

            Bitmap bitmap = FileManager.getImage(this, record.getImage());
            imageView.setImageBitmap(bitmap);

            buttonAdd.setText("Изменить");
        }

    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            boolean isFieldsFilled = !editTextTitle.getText().toString().isEmpty()
                    && !editTextTime.getText().toString().isEmpty()
                    && !editTextDescription.getText().toString().isEmpty();

            boolean isFieldsValid = Utils.isTimeValid(editTextTime.getText().toString());

            buttonAdd.setEnabled(isFieldsFilled && isFieldsValid);
        }
    };

    private void addRecord() {
        try {
            addRecordToJson();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        finishWithResultOK();
    }

    private void addRecordToJson() throws JSONException {

        FileManager.createFileIfNotExists(this);

        JSONObject newRecord = new JSONObject();
        newRecord.put("title", editTextTitle.getText().toString());
        newRecord.put("text", editTextDescription.getText().toString());
        newRecord.put("time", editTextTime.getText().toString());

        Bitmap bitmap = imageView.getDrawingCache();
        String imageName = FileManager.saveImage(this, bitmap);

        newRecord.put("image", imageName);

        FileManager.addRecordToJson(this, newRecord);

        Record record = new Record(editTextTitle.getText().toString(),
                editTextDescription.getText().toString(),
                LocalTime.parse(editTextTime.getText().toString()),
                imageName);
    }

    private void editRecord() {
        try {
            editRecordInJson();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        finishWithResultOK();
    }

    private void editRecordInJson() throws JSONException {

        FileManager.createFileIfNotExists(this);

        JSONObject newRecord = new JSONObject();
        newRecord.put("title", editTextTitle.getText().toString());
        newRecord.put("text", editTextDescription.getText().toString());
        newRecord.put("time", editTextTime.getText().toString());

        Bitmap bitmap = imageView.getDrawingCache();
        String imageName = FileManager.saveImage(this, bitmap);

        newRecord.put("image", imageName);

        FileManager.updateRecordInJson(this, newRecord);

        Record record = new Record(editTextTitle.getText().toString(),
                editTextDescription.getText().toString(),
                LocalTime.parse(editTextTime.getText().toString()),
                imageName);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageView.setImageURI(data.getData());
        }
    }

    private void finishWithResultOK(){
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}