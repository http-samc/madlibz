package dev.smrth.madlibz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
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
}