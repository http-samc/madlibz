package dev.smrth.madlibz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

public class HistoryActivity extends AppCompatActivity {

    // Constants
    private JSONArray history;

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
            Log.w("CHITGOPEKAR", this.history.get(i).toString());
        }

    }
}