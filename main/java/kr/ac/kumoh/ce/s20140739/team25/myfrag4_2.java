package kr.ac.kumoh.ce.s20140739.team25;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class myfrag4_2 extends Activity  {
    Button sbtn;
    String result = "";
    protected ArrayList<sroominfo> rArray = new ArrayList<myfrag4_2.sroominfo>();

    String id ="";

    protected JSONObject mResult = null;
    protected ListView mList;
    protected sroomAdapter mAdapter;
    protected RequestQueue mQueue = null;
    protected ImageLoader mImageLoader = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myfrag4_2);
        Intent intent = getIntent();
      id=intent.getStringExtra("id");
        sbtn=(Button)findViewById(R.id.SRR);
        rArray = new ArrayList<sroominfo>();
        mAdapter = new sroomAdapter(this, R.layout.listitem2, rArray);
        mList = (ListView)findViewById(R.id.SR_list);
        mList.setAdapter(mAdapter);

        Cache cache = new DiskBasedCache(this.getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mQueue = new RequestQueue(cache, network);
        mQueue.start();
        mImageLoader = new ImageLoader(mQueue, new LruBitmapCache(LruBitmapCache.getCacheSize(this)));
        back task = new back();
        task.execute(MainActivity.SERVER_IP_PORT+"/host/info/"+id);

    }
    private class back extends AsyncTask<String, Integer, String> {
        @Override
        public String doInBackground(String... urls) {
            Log.i("task", "실행?");

            try {
                URL myFileUrl = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                Log.i("task", "연결?");
                conn.connect();
                Log.i("task", "연결!");

                Log.i("task", "비트맵?");
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
                JSONArray jsonMainNode=jsResult.getJSONArray("rooms");
                for(int i=0 ;i<jsonMainNode.length();i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    String id=jsonChildNode.getString("id");
                    Log.i("id", id);
                    String name=jsonChildNode.getString("name");
                    Log.i("rname", name);
                    String people=jsonChildNode.getString("max");
                    String img = jsonChildNode.getString("img");
                    Log.i("image", img);
                    String description = jsonChildNode.getString("description");
                    Log.i("description", description);
                    String ip = jsonChildNode.getString("ip");
                    Log.i("ip", ip);
                    rArray.add(new sroominfo(id,name,img,people,description,ip));
                }
                mAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Toast.makeText(myfrag4_2.this, "Error" + e.toString(), Toast.LENGTH_LONG).show();
            }
        }
        private String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = "";
            String result = "";
            while ((line = bufferedReader.readLine()) != null)
                result += line;
            inputStream.close();
            Log.i("실행", "완료");
            return result;
        }
    }

    public class sroominfo {

        String id;
        String people;
        String name;
        String description;
        String ip;
        String image;

        public sroominfo( String id,String name,String img,String people,String desc,String ip) {

            this.id=id;
            this.name=name;
            this.description=desc;
            this.people=people;
            this.ip=ip;
            this.image=img;
        }

        public String getId() {
            return id;
        }
        public String getName() {
            return name;
        }
        public String getDescription() {
            return description;
        }

        public String getPeople(){return people;}
         public String getImage() {
            return image;
        }
    }

    static class sRoomViewHolder {
        TextView txroom;
        TextView txpeople;

        TextView txdesc;
        NetworkImageView imimage;
    }

    public class sroomAdapter extends ArrayAdapter<sroominfo> {

        public sroomAdapter(Context context, int resource, List<sroominfo> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            sRoomViewHolder holder;
            if (convertView == null) {
                convertView = myfrag4_2.this.getLayoutInflater().inflate(R.layout.listitem2, parent, false);
                holder = new sRoomViewHolder();
                holder.txroom = (TextView) convertView.findViewById(R.id.sname);
                holder.txpeople = (TextView) convertView.findViewById(R.id.max);
                holder.txdesc = (TextView) convertView.findViewById(R.id.etc);

                holder.imimage = (NetworkImageView) convertView.findViewById(R.id.srimage);
                convertView.setTag(holder);

            } else {
                holder = (sRoomViewHolder) convertView.getTag();
            }
            holder.txroom.setText(getItem(position).getName());
            holder.txpeople.setText(getItem(position).getPeople());
            holder.txdesc.setText(getItem(position).getDescription());
            holder.imimage.setImageUrl(MainActivity.SERVER_IP_PORT+"/" + getItem(position).getImage(), mImageLoader);
            return convertView;
        }
    }

   public void SRRclick(View v){
       Intent intent = new Intent(myfrag4_2.this, myfrag4_3.class);
       intent.putExtra("id",id);
       startActivity(intent);
   }
    public void finishregister(View v){
       //서버랑 연결
    }
}
