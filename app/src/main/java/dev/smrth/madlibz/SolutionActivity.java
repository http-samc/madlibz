package dev.smrth.madlibz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;

public class SolutionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution);
        this.renderMadlibSolution();
    }

    public void renderMadlibSolution() {
        final TextView madlibSolution = findViewById(R.id.madlib_solution);
        String solution = getIntent().getStringExtra(MainActivity.SOLUTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            madlibSolution.setText(Html.fromHtml(solution, Html.FROM_HTML_MODE_COMPACT));
        } else {
            madlibSolution.setText(Html.fromHtml(solution));
        }
    }

    public void shareMadlib(View v) {
        final TextView madlibSolution = findViewById(R.id.madlib_solution);
        String shareStr = "Check out what I made on Madlibz: " + madlibSolution.getText().toString();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareStr);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    public void goHome(View v) {
        // Send back to MainActivity
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
    }
}