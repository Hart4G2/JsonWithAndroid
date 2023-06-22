package com.example.lr5;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    private static final int ADD_RECORD_REQUEST_CODE = 1;
    private static final int EDIT_RECORD_REQUEST_CODE = 2;
    private static final String TAG = "MainActivity";

    private ListView listViewRecords;
    private SearchView searchView_records;
    private List<Record> recordList;
    private RecordAdapter recordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewRecords = findViewById(R.id.listview_records);
        searchView_records = findViewById(R.id.searchView_records);

        recordList = new ArrayList<>();
        recordAdapter = new RecordAdapter(this, recordList);
        setRecords();

        listViewRecords.setAdapter(recordAdapter);
        registerForContextMenu(listViewRecords);

        listViewRecords.setOnItemClickListener((parent, view, position, id) -> {
            Record selectedRecord = recordList.get(position);

            Intent intent = new Intent(MainActivity.this, ViewDescription.class);
            intent.putExtra("record", selectedRecord);
            startActivity(intent);
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        searchView_records.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
    }

    private void filter(String newText) {
        if(newText != null && !newText.isEmpty()){
            List<Record> filteredList = new ArrayList<>();
            for (Record record : recordList) {
                if (record.getTitle().toLowerCase().contains(newText.toLowerCase())) {
                    filteredList.add(record);
                }
            }
            recordList.clear();
            recordList.addAll(filteredList);
            recordAdapter.notifyDataSetChanged();
        } else {
            setRecords();
        }
    }

    private boolean sorted = false;

    private void sortRecords() {
        if (sorted) {
            setRecords();
            sorted = false;
        } else {
            List<Record> sortedRecords = new ArrayList<>(recordList);
            Collections.sort(sortedRecords);
            recordList.clear();
            recordList.addAll(sortedRecords);
            recordAdapter.notifyDataSetChanged();
            sorted = true;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        Record selectedRecord = recordList.get(position);

        switch (item.getItemId()) {
            case R.id.menu_edit:
                Intent intent = new Intent(this, AddRecordActivity.class);
                intent.putExtra("record", selectedRecord);
                startActivityForResult(intent, EDIT_RECORD_REQUEST_CODE);
                return true;
            case R.id.menu_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Удаление записи");
                builder.setMessage("Вы уверены, что хотите удалить эту запись?");
                builder.setPositiveButton("Удалить", (dialog, which) -> {
                    try {
                        deleteRecordFromJson(selectedRecord);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    setRecords();
                });
                builder.setNegativeButton("Отмена", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_add) {
            addRecord();
            return true;
        } else if (item.getItemId() == R.id.menu_sort) {
            sortRecords();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_RECORD_REQUEST_CODE && resultCode == RESULT_OK) {
            setRecords();
            ((RecordAdapter) listViewRecords.getAdapter()).notifyDataSetChanged();
        } else if(requestCode == EDIT_RECORD_REQUEST_CODE && resultCode == RESULT_OK){
            setRecords();
            ((RecordAdapter) listViewRecords.getAdapter()).notifyDataSetChanged();
        }
    }

    private void addRecord() {
        Intent intent = new Intent(this, AddRecordActivity.class);
        startActivityForResult(intent, ADD_RECORD_REQUEST_CODE);
    }

    private void deleteRecordFromJson(Record record) throws JSONException {

        FileManager.createFileIfNotExists(this);

        JSONObject newRecord = new JSONObject();
        newRecord.put("title", record.getTitle());

        FileManager.deleteRecordFromJson(this, newRecord);
    }

    private void setRecords(){
        if(FileManager.isFileExists(this)){
            recordList.clear();
            String jsonData = FileManager.readDataFromJsonFile(this);
            try {
                JSONArray jsonArray = new JSONArray(jsonData);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String title = jsonObject.getString("title");
                    String text = jsonObject.getString("text");
                    String timeString = jsonObject.getString("time");
                    LocalTime time = LocalTime.parse(timeString);
                    String image = jsonObject.getString("image");

                    recordList.add(new Record(title, text, time, image));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        recordAdapter.notifyDataSetChanged();
    }
}