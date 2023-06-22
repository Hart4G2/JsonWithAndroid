package com.example.lr5;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileManager {

    private static final String TAG = "FileManager";
    private static final String FILE_NAME = "records.json";

    public static String readDataFromJsonFile(Context context) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            InputStream inputStream = context.openFileInput(FILE_NAME);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            bufferedReader.close();
        } catch (IOException e) {
            Log.e(TAG, "Error reading JSON file: " + e.getMessage());
        }

        return stringBuilder.toString();
    }

    public static void writeDataToJsonFile(Context context, JSONArray jsonArray) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fileOutputStream.write(jsonArray.toString().getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Error writing to JSON file: " + e.getMessage());
        }
    }

    public static JSONArray getJsonArray(Context context) {
        String json = readDataFromJsonFile(context);

        JSONArray jsonArray;
        try {
            if (json.isEmpty()) {
                jsonArray = new JSONArray();
            } else {
                jsonArray = new JSONArray(json);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON array: " + e.getMessage());
            jsonArray = new JSONArray();
        }

        return jsonArray;
    }

    public static void addRecordToJson(Context context, JSONObject newRecord) {
        JSONArray jsonArray = getJsonArray(context);
        jsonArray.put(newRecord);
        writeDataToJsonFile(context, jsonArray);
    }

    public static boolean isFileExists(Context context) {
        String[] fileList = context.fileList();
        for (String fileName : fileList) {
            if (fileName.equals(FILE_NAME)) {
                return true;
            }
        }
        return false;
    }

    public static void createFileIfNotExists(Context context) {
        if (!isFileExists(context)) {
            JSONArray jsonArray = new JSONArray();
            writeDataToJsonFile(context, jsonArray);
        }
    }

    public static void updateRecordInJson(Context context, JSONObject updatedRecord) {
        JSONArray jsonArray = getJsonArray(context);

        String recordId = updatedRecord.optString("title");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject record = jsonArray.optJSONObject(i);
            if (record != null && record.optString("title").equals(recordId)) {
                try {
                    jsonArray.put(i, updatedRecord);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
        }

        writeDataToJsonFile(context, jsonArray);
    }

    public static void deleteRecordFromJson(Context context, JSONObject recordToDelete) {
        JSONArray jsonArray = getJsonArray(context);

        String recordId = recordToDelete.optString("title");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject record = jsonArray.optJSONObject(i);
            if (record != null && record.optString("title").equals(recordId)) {
                jsonArray.remove(i);
                break;
            }
        }

        writeDataToJsonFile(context, jsonArray);
    }

    public static String saveImage(Context context, Bitmap bitmap) {
        String fileName = generateUniqueFileName();
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.close();
            return fileName;
        } catch (IOException e) {
            Log.e(TAG, "Error saving an image file: " + e.getMessage());
            return null;
        }
    }

    public static String generateUniqueFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return "image_" + timeStamp + ".jpg";
    }

    public static Bitmap getImage(Context context, String fileName) {
        try {
            FileInputStream fileInputStream = context.openFileInput(fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
            fileInputStream.close();
            return bitmap;
        } catch (IOException e) {
            Log.e(TAG, "Error loading the image file: " + e.getMessage());
            return null;
        }
    }
}
