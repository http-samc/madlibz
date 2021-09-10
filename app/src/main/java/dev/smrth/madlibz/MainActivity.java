package dev.smrth.madlibz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
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
    private final String MADLIB_API_URL = "https://www.smrth.dev/api/madlibz";
    public static final String SOLUTION = "MADLIB_SOLUTION";
    private final int TITLE = 42069;

    // Shared Preferences Constants
    public static final String PREFERENCE_FILE_KEY = "dev.smrth.madlibz.PREFERENCE_FILE_KEY";
    // Other activities don't need to know last Madlib...
    private final String MADLIB_LAST = "dev.smrth.madlibz.MADLIB_LAST";
    public static final String MADLIB_HISTORY = "dev.smrth.madlibz.MADLIB_HISTORY";

    // Vars to be instantiated on load
    private LinearLayout madlibContianerLL;
    private SharedPreferences sharedPreferences;

    // Madlib attrs
    private JSONArray blanks;
    private JSONArray answers;
    private JSONArray template;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiating select constants
        this.madlibContianerLL = findViewById(R.id.madlib_container);
        this.sharedPreferences = getSharedPreferences(
                this.PREFERENCE_FILE_KEY,
                Context.MODE_PRIVATE
        );

        // Rendering Madlib
        this.renderLastMadlib();
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

        // We don't have answers yet
        this.answers = null;

        // Creating title TV and adding to Madlib Container
        final TextView madlibTitleTV = new TextView(this, null, 0, R.style.title);
        madlibTitleTV.setId(this.TITLE);
        madlibTitleTV.setTextSize(40);
        this.madlibContianerLL.addView(madlibTitleTV);

        try {
            // Set title
            madlibTitleTV.setText(madlib.getString("title"));

            // Get Blanks and Values
            this.blanks =  new JSONArray(madlib.getString("blanks"));
            if (madlib.has("value"))
                this.template =  new JSONArray(madlib.getString("value"));
            else
                this.template =  new JSONArray(madlib.getString("template"));

            /*
                Create blanks programmatically & dynamically
                Placeholder -> "<blanks.getString(<idx of blank>)"
             */

            for (int i = 0; i < this.blanks.length(); i++) {
                EditText current = new EditText(this);
                current.setHint(this.blanks.getString(i));
                this.madlibContianerLL.addView(current);
            }

        }
        catch (JSONException e) {
            Log.w("CHITGOPEKAR", e.toString());
        }
    }

    /**
     * Converts cached Madlib (from SP) into single JSONObject
     * that MainActivity.renderMadlib(JSONObject madlib) can render.
     * Contains logic that determines if a previous Madlib existed
     * or if we should just request a new one. Should be called only
     * on initial load.
     */
    public void renderLastMadlib() {

        // Creating madlib JSONObject
        JSONObject madlib;
        try {
            if (this.sharedPreferences.contains(this.MADLIB_LAST)) {
                madlib = new JSONObject(
                        this.sharedPreferences.getString(this.MADLIB_LAST,
                                null)
                ); // defValue never triggered
            }
            else
                throw new JSONException("");
        }
        catch (JSONException e) {
            // Just go for a brand new Madlib
            this.getMadlib();
            return;
        }

        // Rendering Object
        this.renderMadlib(madlib);

        // Putting in answers
        try {
            // Get loaded answers
            JSONArray cachedAnswers = new JSONArray(madlib.getString("answers"));

            for (int i = 1; i < this.madlibContianerLL.getChildCount(); i++) {
                EditText current = (EditText) this.madlibContianerLL.getChildAt(i);
                current.setText(cachedAnswers.getString(i-1)); // starting at idx = 1 for children, not ans
            }
        }
        catch (Exception e) {
            Log.w("CHITGOPEKAR", e.toString());
        }
    }

    /**
     * Destroys a rendered Madlib by finding the
     * Madlib Container layout and removing all views
     */
    public void destroyMadlib() {
        this.madlibContianerLL.removeAllViews();
    }

    /**
     * Saves all answers to memory.
     */
    public void genAnswers() {
        this.answers = new JSONArray();

        for (int i = 1; i < this.madlibContianerLL.getChildCount(); i++) {
            EditText tv = (EditText) this.madlibContianerLL.getChildAt(i);
            String text = tv.getText().toString();
            this.answers.put(text);
        }
    }

    /**
     * Goes through all the Madlib blanks and makes sure
     * that there is some non-space character in each of them.
     * Returns false if one missed input is found.
     * Pops Toast with missing input field's name.
     * @return boolean, whether or not the Madlib is valid
     */
    public boolean validateMadlib() {
        try {
            for (int i = 0; i < this.answers.length(); i++) {
                // Blank answer -> find TV -> pop toast -> return false
                if (this.answers.getString(i).replace(" ", "").equals("")) {
                    TextView blankTV = (TextView) this.madlibContianerLL.getChildAt(i+1); // TV @ idx = 0, shift up by 1
                    Toast.makeText(
                            this,
                            "Enter a value for '" + blankTV.getHint().toString() + "'!",
                            Toast.LENGTH_SHORT
                    ).show();
                    return false;
                }
            }
        }
        catch (JSONException e) {
            Log.w("CHITGOPEKAR", e.toString());
            return false;
        }
        return true;
    }

    /**
     * Combines user-answers and Madlib template and
     * HTML logic to get a formatted Madlib
     * @return String, the HTML repr of the Madlib
     * @throws JSONException
     */
    public String genSolutionHTML() throws JSONException {
        final TextView madlibTitleTv = findViewById(this.TITLE);

        String html = "<h1>" + madlibTitleTv.getText() + "</h1><p>";

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

    /**
     * If all answers are filled -> sends Madlib (as HTML
     * from MainActivity.genSolutionHTML) to Solution
     * Activity for viewing. If there's a missing answer ->
     * starts a shake animation on the solve button.
     * @param v Button that called the method onClick.
     */
    public void solveMadlib(View v) {
        // Generate Answers
        this.genAnswers();

        // If the user hasn't solved their Madlib yet -> shake btn & return
        if (!this.validateMadlib()) {
            v.startAnimation(AnimationUtils.loadAnimation(this,R.anim.shake));
            return;
        }

        // On validation, generate solution HTML and add Madlib to history

        String solution;

        try {
            solution = this.genSolutionHTML();
            this.saveMadlibToHistory();
        }
        catch (JSONException e) {
            Log.w("CHITGOPEKAR", e.toString());
            solution = "<h1>Unknown Error!</h1><p>Reference: " + e.toString() + "</p>";
        }

        Intent intent = new Intent(getBaseContext(), SolutionActivity.class);
        intent.putExtra(this.SOLUTION, solution);
        startActivity(intent);
    }

    /**
     * Override the default onStop() method and save our data to SP too!
     */
    @Override
    public void onStop() {
        super.onStop();
        this.saveMadlibOnClose();
    }

    /**
     * Creates Madlib from current state
     * @return JSONObject
     */
    public JSONObject getCurrMadlib() {
        JSONObject madlib = new JSONObject(); // return obj

        try {
            final TextView titleTV = (TextView) findViewById(this.TITLE); // title TV

            // Add attrs
            madlib.put("title", titleTV.getText().toString());
            madlib.put("blanks", this.blanks.toString());
            madlib.put("answers", this.answers.toString());
            madlib.put("template", this.template.toString());
        }
        catch (JSONException e) {
            Log.w("CHITGOPEKAR", e.toString());
        }

        return madlib;
    }

    /**
     * Saves the current Madlib being viewed to SP onClose
     * Works regardless of completion status
     */
    public void saveMadlibOnClose() {
        // User hasn't clicked 'Solve' yet
        if (this.answers == null)
            this.genAnswers();

        // Save as MADLIB_LAST
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(this.MADLIB_LAST, this.getCurrMadlib().toString());
        editor.apply();
    }

    /**
     * Saves currently opened Madlib to SP MADLIB_HISTORY
     * only after it has passed MainActivity.validateMadlib()
     */
    public void saveMadlibToHistory() throws JSONException {

        // Get current history
        JSONArray madlibHistory = new JSONArray(
                this.sharedPreferences.getString(this.MADLIB_HISTORY,"[]")
        );

        // Add the current madlib
        madlibHistory.put(this.getCurrMadlib());

        // Get editor and overwrite key to SP
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(this.MADLIB_HISTORY, madlibHistory.toString());
        editor.apply();
    }

    /**
     * Opens Madlib History panel
     * @param v, the button that triggered the method call
     */
    public void openMadlibHistory(View v) {
        Intent intent = new Intent(getBaseContext(), HistoryActivity.class);
        startActivity(intent);
    }
}