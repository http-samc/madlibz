package dev.smrth.madlibz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String MADLIB_API_URL = "https://madlibz.herokuapp.com/api/random";
    private final int BLANKS_START = 69420;
    private final int TITLE = 42069;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getMadlib();
    }

    /**
     * Makes a GET request to MainActivity.MADLIB_API_URL to get
     * a JSONObject representation of a Madlib.
     * Calls MainActivity.renderMadlib() to display it.
     */
    public void getMadlib() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        MainActivity mainActivity = this;
        JsonObjectRequest req = new JsonObjectRequest
                (Request.Method.GET, this.MADLIB_API_URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject madlib) {
                        mainActivity.renderMadlib(madlib);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("CHITGOPEKAR", error.toString());
                    }
                });
        requestQueue.add(req);
    }

    /**
     * First removes the current Madlib by calling
     * MainActivity.destroyMadlib() and then gets a new
     * one with MainActivity.getMadlib()
     * @param v View representing the button that was clicked
     */
    public void refreshMadlib(View v) {
        this.destroyMadlib();
        this.getMadlib();
    }

    /**
     * Renders a Madlib onto the main activity,
     * called by MainActivity.getMadlib()
     * @param madlib JSONObject representing a Madlib
     */
    public void renderMadlib(JSONObject madlib) {

        // Getting refernce to Madlib Container
        final LinearLayout madlibContianerLL = findViewById(R.id.madlib_container);

        // Creating title TV and adding to Madlib Container
        final TextView madlibTitleTV = new TextView(this);
        madlibTitleTV.setId(this.TITLE);
        madlibContianerLL.addView(madlibTitleTV);

        try {
            // Set title
            madlibTitleTV.setText(madlib.getString("title"));

            // Get Blanks and Values
            JSONArray blanks =  madlib.getJSONArray("blanks");
            JSONArray values =  madlib.getJSONArray("value"); // set in SP for later; 2 longer than blanks


            /*
                Create blanks programmatically & dynamically
                ID -> "<this.BLANKS_START + idx of blank>"
                Placeholder -> "<blanks.getString(<idx of blank>)"
             */

            for (int i = 0; i < blanks.length(); i++) {
                EditText current = new EditText(this);
                current.setHint(blanks.getString(i));
                current.setId(this.BLANKS_START+i);
                madlibContianerLL.addView(current);
            }
        }
        catch (JSONException e) {
            Log.w("CHITGOPEKAR", e.toString());
        }
    }

    /**
     * Destroys a rendered Madlib by finding the
     * Madlib Container layout and removing all views
     */
    public void destroyMadlib() {
        final LinearLayout madlibContianerLL = findViewById(R.id.madlib_container);
        madlibContianerLL.removeAllViews();
    }
}