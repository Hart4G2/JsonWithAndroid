package com.example.lr5;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import com.example.lr5.databinding.ActivityViewDescriptionBinding;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewDescription extends AppCompatActivity {

    private ActivityViewDescriptionBinding binding;
    private Record record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityViewDescriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("record")) {
            record = (Record) intent.getSerializableExtra("record");
        }

        CollapsingToolbarLayout toolbar = binding.toolbarLayout;
        TextView descriptionTextView = findViewById(R.id.textview_description);

        Bitmap bitmap = FileManager.getImage(this, record.getImage());

        toolbar.setTitle(record.getTitle());
        toolbar.setBackground(new BitmapDrawable(getResources(), bitmap));
        descriptionTextView.setText(record.getText());

        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isImageShown = false;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    if (!isImageShown) {
                        isImageShown = true;
                        toolbar.setVisibility(View.VISIBLE);
                    }
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    if (isImageShown) {
                        isImageShown = false;
                        toolbar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }
}