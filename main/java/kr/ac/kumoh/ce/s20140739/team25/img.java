package kr.ac.kumoh.ce.s20140739.team25;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 60974 on 2017-04-04.
 */

public class img extends Activity  {
    protected ArrayList<imageinfo> rArray = new ArrayList<imageinfo>();
    protected ListView mList;
    protected imageAdapter mAdapter;
    protected RequestQueue mQueue = null;
    protected ImageLoader mImageLoader = null;
    String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image);
        rArray = new ArrayList<imageinfo>();
        mAdapter = new imageAdapter(this, R.layout.listitem5, rArray);
        mList = (ListView) findViewById(R.id.checkimage);
        mList.setAdapter(mAdapter);
        Cache cache = new DiskBasedCache(this.getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mQueue = new RequestQueue(cache, network);
        mQueue.start();
        mImageLoader = new ImageLoader(mQueue, new LruBitmapCache(LruBitmapCache.getCacheSize(this)));

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        back task = new back();
        task.execute(MainActivity.SERVER_IP_PORT+"/host/camera/"+id);
    }
    private class back extends AsyncTask<String, Integer, String> {
        @Override
        public String doInBackground(String... urls) {

            try {
                URL myFileUrl = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);


                conn.connect();



                InputStream inputStream = conn.getInputStream();

                if (inputStream != null)
                    result = convertInputStreamToString(inputStream);
                else
                    result = "Did not work!";

            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String str) {
            try {
                JSONObject jsResult = new JSONObject(str);
                JSONArray jsonMainNode=jsResult.getJSONArray("camera");
                for(int i=0 ;i<jsonMainNode.length();i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    String image = jsonChildNode.getString("img");

                    rArray.add(new imageinfo(image));
                }
                mAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error" + e.toString(), Toast.LENGTH_LONG).show();
            }
        }

        private String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = "";
            String result = "";
            while ((line = bufferedReader.readLine()) != null)
                result += line;
            inputStream.close();
            return result;
        }
    }
    public class imageinfo {
       String image;

        public imageinfo(String image) {
            this.image = image;
        }

        public String getImage() {
            return image;
        }
    }

    static class imageviewholder {

        NetworkImageView imimage;
    }

    public class imageAdapter extends ArrayAdapter<imageinfo> {

        public imageAdapter(Context context, int resource, List<imageinfo> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            imageviewholder holder;
            if (convertView == null) {
                convertView = img.this.getLayoutInflater().inflate(R.layout.listitem5, parent, false);
                holder = new imageviewholder();

                holder.imimage = (NetworkImageView) convertView.findViewById(R.id.cimage);
                convertView.setTag(holder);

            } else {
                holder = (imageviewholder) convertView.getTag();
            }

            holder.imimage.setImageUrl(MainActivity.SERVER_IP_PORT+"/" + getItem(position).getImage(), mImageLoader);
            return convertView;
        }
    }
}
