package dev.smrth.madlibz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HistoryActivity extends AppCompatActivity {

    // Instance vars
    private JSONArray history;
    private LinearLayout historyContainerLL;

    // Shared Preferences Constants
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Instantiating select constants
        this.sharedPreferences = getSharedPreferences(
                MainActivity.PREFERENCE_FILE_KEY,
                Context.MODE_PRIVATE
        );
        this.historyContainerLL = findViewById(R.id.history_container);

        try {
            this.renderHistory();
        }
        catch (JSONException e) {
            //Log.w("CHITGOPEKAR", e.toString());
        }
    }

    public String genSolutionHTML(JSONArray template, JSONArray answers, String title) throws JSONException {

        String html = "<h1>" + title + "</h1><p>";

        for (int i = 0; i < template.length()-1; i++) {
            if (i == template.length()-2) {
                html += template.get(i); // idx unique to template arr
            }
            else {
                html += template.get(i);
                html += "<b>" + answers.get(i) + "</b>";
            }
        }
        html += "</p>";
        return html;
    }

    public void renderHistory() throws JSONException {
        this.history = new JSONArray(
                this.sharedPreferences.getString(MainActivity.MADLIB_HISTORY, "[]")
        );

        for (int i = 0; i < this.history.length(); i++) {
            JSONObject madlib = (JSONObject) this.history.get(i);
            String html = this.genSolutionHTML(
                    new JSONArray(madlib.getString("template")),
                    new JSONArray(madlib.getString("answers")),
                    madlib.getString("title")
            );
            TextView container = new TextView(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                container.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT));
            } else {
                container.setText(Html.fromHtml(html));
            }
            this.historyContainerLL.addView(container);
        }
    }

    public void clearHistory(View v) {
        // Remove everything (incl. headers) and just return to MainActivity
        this.historyContainerLL.removeAllViews();

        // Clear SharedPreferences
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(MainActivity.MADLIB_HISTORY, "[]");
        editor.apply();

        // Send back to MainActivity
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
    }
}