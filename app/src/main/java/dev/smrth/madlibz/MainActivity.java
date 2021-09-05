package dev.smrth.madlibz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    // Constants
    private final String MADLIB_API_URL = "https://madlibz.herokuapp.com/api/random";
    public static final String SOLUTION = "MADLIB_SOLUTION";
    private final int TITLE = 42069;

    // Madlib attrs
    private JSONArray blanks;
    private JSONArray answers;
    private JSONArray template;

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
        madlibTitleTV.setTextSize(40);
        madlibContianerLL.addView(madlibTitleTV);

        try {
            // Set title
            madlibTitleTV.setText(madlib.getString("title"));

            // Get Blanks and Values
            this.blanks =  madlib.getJSONArray("blanks");
            this.template =  madlib.getJSONArray("value"); // set in SP for later; 2 longer than blanks


            /*
                Create blanks programmatically & dynamically
                Placeholder -> "<blanks.getString(<idx of blank>)"
             */

            for (int i = 0; i < this.blanks.length(); i++) {
                EditText current = new EditText(this);
                current.setHint(this.blanks.getString(i));
                //  TODO REMOVE
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

    /**
     * Goes through all the Madlib blanks and makes sure
     * that there is some non-space character in each of them.
     * Returns false if one missed input is found.
     * Pops Toast with missing input field's name.
     * Also creates MainActivity.answers JSONArray while validating.
     * @return
     */
    public boolean validateMadlib() {
        final LinearLayout madlibContianerLL = findViewById(R.id.madlib_container);
        this.answers = new JSONArray();

        for (int i = 0; i < madlibContianerLL.getChildCount(); i++) {

            View v = madlibContianerLL.getChildAt(i);
            if (!(v instanceof EditText))
                continue;

            TextView tv = (TextView) v;
            String text = tv.getText().toString();

            if (text.replace(" ", "").equals("")) {
                Toast.makeText(this, "Enter a value for '" + tv.getHint().toString() + "'!", Toast.LENGTH_SHORT).show();
                return false;
            }
            else {
                this.answers.put(text);
            }
        }

        return true;
    }

    public String genSolutionHTML() throws JSONException {
        final TextView madlibTitleTv = findViewById(this.TITLE);

        String html = "<h1>" + madlibTitleTv.getText() + "</h1><br><p>";

        for (int i = 0; i < this.template.length()-1; i++) {
            if (i == this.template.length()-2) {
                html += this.template.get(i); // idx unique to template arr
            }
            else {
                html += this.template.get(i);
                html += "<b>" + this.answers.get(i) + "</b>";
            }
        }
        html += "</p>";
        return html;
    }

    public void solveMadlib(View v) {
        // If the user hasn't solved their Madlib yet -> shake btn & return
        if (!this.validateMadlib()) {
            v.startAnimation(AnimationUtils.loadAnimation(this,R.anim.shake));
            return;
        }

        String solution;

        try {
            solution = genSolutionHTML();
        }
        catch (JSONException e) {
            Log.w("CHITGOPEKAR", e.toString());
            solution = "<h1>Unknown Error!</h1><p>Reference: " + e.toString() + "</p>";
        }

        Intent intent = new Intent(getBaseContext(), SolutionActivity.class);
        intent.putExtra(this.SOLUTION, solution);
        startActivity(intent);
    }
}