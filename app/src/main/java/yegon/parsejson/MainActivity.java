package yegon.parsejson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView parsedJson;

    // URL to get contacts JSON
    private static String url = "https://api.myjson.com/bins/1dlc0l";

    ArrayList<HashMap<String, String>> recyclerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerList = new ArrayList<>();

        parsedJson = findViewById(R.id.parsedJson);

        new GetContacts().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Fetching reports...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {

                    // Getting JSON Array node
                    JSONArray report = new JSONArray(jsonStr);

                    // looping through All Contacts
                    for (int i = 0; i < report.length(); i++) {
                        JSONObject c = report.getJSONObject(i);

                        String id = c.getString("id");
                        String title = c.getString("title");
                        String brief = c.getString("brief");
                        String filesource = c.getString("filesource");

                        // tmp hash map for single contact
                        HashMap<String, String> reports = new HashMap<>();

                        // adding each child node to HashMap key => value
                        reports.put("id", id);
                        reports.put("title", title);
                        reports.put("brief", brief);
                        reports.put("filesource", filesource);

                        // adding contact to contact list
                        recyclerList.add(reports);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, recyclerList,
                    R.layout.parsed_list, new String[]{"id", "title",
                    "brief","filesource"}, new int[]{R.id.id,
                    R.id.title, R.id.brief,R.id.filesource});

            parsedJson.setAdapter(adapter);
        }

    }
}