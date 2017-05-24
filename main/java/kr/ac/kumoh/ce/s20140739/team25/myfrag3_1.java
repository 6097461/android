package kr.ac.kumoh.ce.s20140739.team25;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by 60974 on 2017-04-04.
 */

public class myfrag3_1 extends Activity {

    TextView tvIsConnected, tvResult;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my3_1);
        tvIsConnected = (TextView) findViewById(R.id.tvUserInfo);
        tvResult = (TextView) findViewById(R.id.tvUserReservation);
     //   if(checkNetworkConnection())
            // perform HTTP GET request
        new HTTPAsyncTask().execute("http://192.168.0.58:3003/my/info");
    }

    // check network connection
    public boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean isConnected = false;
        if (networkInfo != null && (isConnected = networkInfo.isConnected())) {
            // show "Connected" & type of network "WIFI or MOBILE"
            tvIsConnected.setText("Connected "+networkInfo.getTypeName());
            // change background color to red
            tvIsConnected.setBackgroundColor(0xFF7CCC26);


        } else {
            // show "Not Connected"
            tvIsConnected.setText("Not Connected");
            // change background color to green
            tvIsConnected.setBackgroundColor(0xFFFF0000);
        }

        return isConnected;
    }

    private class HTTPAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return HttpGet(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the resulfts of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.i("result!!!", result);
            tvResult.setText(result);
        }
    }

    private String HttpGet(String myUrl) throws IOException {
        InputStream inputStream = null;
        String result = "";

        URL url = new URL(myUrl);

        // create HttpURLConnection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        // make GET request to the given URL
        conn.connect();
        Log.i("connect", "성공");
        // receive response as inputStream
        inputStream = conn.getInputStream();
        Log.i("connect", "보내기성공");

        // convert inputstream to string
        if(inputStream != null) {
            result = convertInputStreamToString(inputStream);
            Log.i("connect", "읽기성공");
        }
        else {
            result = "Did not work!";
            Log.i("connect", "읽기실패");
        }
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }
}
/*
myinfoDB myinfodb = new myinfoDB();
    TextView tvUserInfo, tvUserReservation;
    String userInfo, userReservation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvUserInfo = (TextView)findViewById(R.id.tvUserInfo);
        tvUserReservation = (TextView)findViewById(R.id.tvUserReservation);

        new myinfoDB().execute("http://192.168.0.62:3003/my/home");
    }

    // check network connection

    private class myinfoDB extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject user = new JSONObject();
                JSONArray jaUser = user.getJSONArray("user");
                JSONObject reservation = new JSONObject();
                JSONArray jaReservation = reservation.getJSONArray("reservations");
                for(int i=0; i < jaUser.length(); i++) {
                    JSONObject jsonObject = jaUser.getJSONObject(i);
                    String user = jsonObject.getString("user");

                    tvUserInfo.setText(tvUserInfo.getText() + ", " + user);
                }
                for(int i=0; i<jaReservation.length(); i++) {
                    JSONObject jsonObject = jaReservation.getJSONObject(i);
                    String reservations = jsonObject.getString("reservations");
                    tvUserReservation.setText(tvUserReservation.getText() + ", " + reservations);

                }
            }
            catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error" + e.toString(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                return HttpGet(urls[0]);
            } catch (IOException e) {
                return "";
            }
        }
    }

    private String HttpGet(String myUrl) throws IOException {
        InputStream inputStream = null;

        URL url = new URL(myUrl);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        conn.connect();

        inputStream = conn.getInputStream();

        // convert inputstream to string
        if(inputStream != null)
            Log.i("받아오기", "성공");
        else
            Log.i("받아오기", "실패");
        return "";
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;

    }






 */
