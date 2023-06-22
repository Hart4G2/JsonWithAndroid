package com.example.lr5;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class RecordAdapter extends ArrayAdapter<Record> {

    private Context context;
    private List<Record> recordList;

    public RecordAdapter(Context context, List<Record> recordList) {
        super(context, 0, recordList);
        this.context = context;
        this.recordList = recordList;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_record, parent, false);
        }

        Record record = getItem(position);

        TextView titleTextView = convertView.findViewById(R.id.textview_title);
        TextView textTextView = convertView.findViewById(R.id.textview_text);
        TextView timeTextView = convertView.findViewById(R.id.textview_time);
        ImageView imageView = convertView.findViewById(R.id.imageview_record);

        titleTextView.setText(record.getTitle());
        textTextView.setText(record.getText());
        timeTextView.setText(record.getTime().toString());

        String imageName = record.getImage();
        Bitmap bitmap = FileManager.getImage(this.getContext(), imageName);
        imageView.setImageBitmap(bitmap);

        return convertView;
    }
}
