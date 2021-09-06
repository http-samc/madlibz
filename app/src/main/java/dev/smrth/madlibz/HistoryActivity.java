package dev.smrth.madlibz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
            Log.w("CHITGOPEKAR", e.toString());
        }
    }

    public void renderHistory() throws JSONException {
        this.history = new JSONArray(
                this.sharedPreferences.getString(MainActivity.MADLIB_HISTORY, "[]")
        );

        for (int i = 0; i < this.history.length(); i++) {
            JSONObject madlib = (JSONObject) this.history.get(i);
            LinearLayout madlibHistoryContainer = new LinearLayout(
                    this,
                    null,
                    0,
                    R.style.madlibHistoryContainer
            );
            TextView madlibHistoryTitle = new TextView(
                    this,
                    null,
                    0,
                    R.style.madlibHistoryTitle
            );
            madlibHistoryTitle.setText("A trip to the park"
                    //madlib.getString("title")
            );
            Button openBtn = new Button(
                    this,
                    null,
                    0,
                    R.style.madlibHistoryOpenBtn
            );
            Button delBtn = new Button(
                    this,
                    null,
                    0,
                    R.style.madlibHistoryDelBtn
            );

            madlibHistoryContainer.addView(madlibHistoryTitle);
            madlibHistoryContainer.addView(openBtn);
            madlibHistoryContainer.addView(delBtn);

            this.historyContainerLL.addView(
                    madlibHistoryContainer
            );
            //Log.w("CHITGOPEKAR", this.history.get(i).toString());
        }

    }
}