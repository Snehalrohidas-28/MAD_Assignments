package com.example.ratingprogress;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    RatingBar ratingBar;
    ProgressBar progressBar, circularProgress;
    Button btnSubmit, btnProgress;
    TextView txtRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ratingBar = findViewById(R.id.ratingBar);
        progressBar = findViewById(R.id.progressBar);
        circularProgress = findViewById(R.id.circularProgress);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnProgress = findViewById(R.id.btnProgress);
        txtRating = findViewById(R.id.txtRating);

        btnSubmit.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            txtRating.setText("Rating: " + rating);
        });

        btnProgress.setOnClickListener(v -> {

            float rating = ratingBar.getRating();

            progressBar.setProgress((int) rating);


            circularProgress.setVisibility(View.VISIBLE);

            txtRating.setText("Progress Updated: " + rating);

            circularProgress.postDelayed(() ->
                    circularProgress.setVisibility(View.GONE), 2000);
        });

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) ->
                txtRating.setText("Live Rating: " + rating));
    }
}
