package dev.smrth.madlibz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity {

    private final String MADLIB_API_URL = "https://madlibz.herokuapp.com/api/random";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //this.getMadlib();
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
     * Renders a Madlib onto the main activity,
     * called by MainActivity.getMadlib()
     * @param madlib JSONObject representing a Madlib
     */
    public void renderMadlib(JSONObject madlib) {
        final TextView madlibContainer = findViewById(R.id.text);

        try {
            String title = madlib.getString("title");
            madlibContainer.setText(title);
        }
        catch (JSONException e) {
            Log.w("CHITGOPEKAR", "JSON key not found!");
        }
    }
}