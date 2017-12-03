package com.psyclone.fan.activities;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.psyclone.fan.R;
import com.psyclone.fan.helpers.DateTimeHelper;
import com.psyclone.fan.modules.GlideApp;

import org.json.JSONException;
import org.json.JSONObject;

import static android.view.View.GONE;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ShowDetailsActivity extends AppCompatActivity {
    private ImageView poster;
    private TextView title, channel, time, castLabel, cast, description, releaseLabel, release, writerLabel, writer, directorLabel, director, producerLabel, producer, musicDirectorLabel, musicDirector;
    private RatingBar showRating;
    private LinearLayout showGenre;
    private JSONObject json;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_show_details, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getIntent().getStringExtra("name"));
        }

        initializeViews();

        GlideApp.with(this)
                .load(getIntent().getStringExtra("poster"))
                .placeholder(R.drawable.placeholder_poster)
                .transition(withCrossFade())
                .into(poster);

        title.setText("\n" + getIntent().getStringExtra("name"));
        channel.setText(getIntent().getStringExtra("channel_name"));
        time.setText(DateTimeHelper.covertTo12Hour(getIntent().getStringExtra("time")));

        try {
            json = new JSONObject(getIntent().getStringExtra("details"));
        } catch(JSONException e) {
            e.printStackTrace();
        }

        setShowRating();
        setShowGenre();
        setEmphasizedText();
        setDetails();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeViews() {
        poster = findViewById(R.id.iv_show_details_poster);
        title = findViewById(R.id.tv_show_title);
        channel = findViewById(R.id.tv_channel);
        time = findViewById(R.id.tv_time);
        showRating = findViewById(R.id.rb_show_rating);
        showGenre = findViewById(R.id.lv_show_genre);
        castLabel = findViewById(R.id.label_show_details_cast);
        cast = findViewById(R.id.tv_show_details_cast);
        description = findViewById(R.id.tv_show_details_description);
        releaseLabel = findViewById(R.id.label_show_details_release);
        release = findViewById(R.id.tv_show_details_release);
        writerLabel = findViewById(R.id.label_show_details_writer);
        writer = findViewById(R.id.tv_show_details_writer);
        directorLabel = findViewById(R.id.label_show_details_director);
        director = findViewById(R.id.tv_show_details_director);
        producerLabel = findViewById(R.id.label_show_details_producer);
        producer = findViewById(R.id.tv_show_details_producer);
        musicDirectorLabel = findViewById(R.id.label_show_details_music_director);
        musicDirector = findViewById(R.id.tv_show_details_music_director);
    }

    private void setShowRating() {
        try {
            showRating.setRating(Float.parseFloat(json.getString("IMDB Rating").split("/")[0]) / 2);
        } catch(JSONException e) {
            showRating.setVisibility(GONE);
        }
    }

    private void setShowGenre() {
        float scale = getResources().getDisplayMetrics().density;
        int rightPadding = (int) (8*scale + 0.5f);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llp.setMarginEnd(rightPadding);
        try {
            String[] tempGenres = json.getString("Genre:").split(",");

            for(String tempGenre : tempGenres) {
                String[] genres = tempGenre.split("/");
                for(String genre : genres) {
                    TextView tvGenre = new TextView(this);
                    tvGenre.setText(genre);
                    tvGenre.setTextColor(ContextCompat.getColor(this, R.color.textPrimary));
                    tvGenre.setLayoutParams(llp);
                    tvGenre.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_corner));
                    showGenre.addView(tvGenre);
                }
            }
        } catch(JSONException e) {
            showGenre.setVisibility(GONE);
        }
    }

    private void setEmphasizedText() {
        if(json.toString().contains("\"Actor\":"))
            setValueForFields("Actor", null, cast, false);
        else if(json.toString().contains("\"Voices\":")) {
            castLabel.setText(getString(R.string.show_details_voices_of));
            setValueForFields("Voices", null, cast, false);
        } else if(json.toString().contains("\"Voices of\":")) {
            castLabel.setText(getString(R.string.show_details_voices_of));
            setValueForFields("Voices of", null, cast, false);
        } else if(json.toString().contains("\"Judges\":")) {
            castLabel.setText(getString(R.string.show_details_judges));
            setValueForFields("Judges", null, cast, false);
        } else if(json.toString().contains("\"Hosted By\":")) {
            castLabel.setText(getString(R.string.show_details_hosted_by));
            setValueForFields("Hosted By", null, cast, false);
        }
    }

    private void setDetails() {
        setValueForFields("Show Description", null, description, false);

        setValueForFields("Release Date", releaseLabel, release, true);
        setValueForFields("Writer", writerLabel, writer, true);
        setValueForFields("Director", directorLabel, director, true);
        setValueForFields("Producer", producerLabel, producer, true);
        setValueForFields("Music Director", musicDirectorLabel, musicDirector, true);
    }

    private void setValueForFields(String key, TextView label, TextView value, boolean canSkip) {
        try {
            value.setText(json.getString(key));
        } catch (JSONException e) {
            if(canSkip) {
                label.setVisibility(GONE);
                value.setVisibility(GONE);
            }
        }
    }

    public void showTrivia(MenuItem mi) {
        try {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.alert_dialog_trivia))
                    .setMessage(json.getString("Trivia"))
                    .setNegativeButton(getString(R.string.alert_dialog_close), null)
                    .show();
        } catch(JSONException e) {
            Toast.makeText(this, getString(R.string.toast_no_trivia), Toast.LENGTH_SHORT).show();
        }
    }
}

