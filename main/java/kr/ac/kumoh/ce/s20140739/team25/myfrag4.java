package kr.ac.kumoh.ce.s20140739.team25;

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

public class myfrag4 extends Fragment implements AdapterView.OnItemClickListener {

    protected ArrayList<roominfo> rArray = new ArrayList<roominfo>();
    protected ArrayList<rpeopleinfo> rpArray = new ArrayList<rpeopleinfo>();
    String result="";


    protected ListView mList;
    protected registerAdapter mAdapter;
    protected RequestQueue mQueue = null;
    protected ImageLoader mImageLoader = null;
    protected ListView rList;
    protected rpAdapter rAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       // return super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_my4, container, false);
        rArray = new ArrayList<roominfo>();
        rpArray=new ArrayList<rpeopleinfo>();
        mAdapter = new registerAdapter(getActivity(), R.layout.listitem1, rArray);
        rAdapter = new rpAdapter(getActivity(), R.layout.listitem4, rpArray);
        Cache cache = new DiskBasedCache(getActivity().getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mQueue = new RequestQueue(cache, network);
        mQueue.start();
        mImageLoader = new ImageLoader(mQueue, new LruBitmapCache(LruBitmapCache.getCacheSize(getActivity())));
        mList = (ListView) rootView.findViewById(R.id.R_list);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(this);
        rList = (ListView) rootView.findViewById(R.id.RP_list);
        rList.setAdapter(rAdapter);


        Button loginbtn = (Button) rootView.findViewById(R.id.RR);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), myfrag4_1.class);
                startActivity(intent);
            }
        });
       back task = new back();
        task.execute("http://192.168.0.58:3003/host/info");
        return rootView;
    }
    private class back extends AsyncTask<String, Integer, String> {
        @Override
        public String doInBackground(String... urls) {
            Log.i("task", "실행?");

            try{
                URL myFileUrl = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection)myFileUrl.openConnection();
                conn.setRequestMethod("GET");
                if(login.cookieString != "")
                    conn.setRequestProperty("Cookie", login.cookieString);
                conn.setDoInput(true);

                Log.i("task", "연결?");
                conn.connect();
                Log.i("task", "연결!");
                if(conn.getResponseCode() == 404){
                    return null;
                }
                Log.i("task", "비트맵?");
                Log.i("responce code",""+conn.getResponseCode());

                InputStream inputStream = conn.getInputStream();

                if(inputStream != null)
                    result = convertInputStreamToString(inputStream);
                else
                    result = "Did not work!";

            }catch(IOException e){
                e.printStackTrace();
            }
            return result;
        }
        @Override
        protected void onPostExecute(String str) {
            if(str == null) return;
            try {
                JSONObject jsResult = new JSONObject(str);
                JSONArray studylist = jsResult.getJSONArray("studyrooms");
                JSONArray rpeoplelist = jsResult.getJSONArray("reservations");


                for(int i=0; i < studylist.length(); i++) {
                    JSONObject jsonObject = studylist.getJSONObject(i);
                    String id=jsonObject.getString("id");
                    String img= jsonObject.getString("img");
                    String name = jsonObject.getString("name");
                    String address = jsonObject.getString("address");

                    rArray.add(new roominfo(id,img,name,address));

                }
                for(int i=0; i < rpeoplelist.length(); i++) {
                    JSONObject jsonObject = rpeoplelist.getJSONObject(i);
                    String id=jsonObject.getString("id");
                    String user= jsonObject.getString("user");
                    String rname = jsonObject.getString("studyroom");
                    String sname = jsonObject.getString("room");
                    String address = jsonObject.getString("address");
                    String date = jsonObject.getString("date");
                    String time = jsonObject.getString("time");
                    String people = jsonObject.getString("number");

                    rpArray.add(new rpeopleinfo(user,rname,sname,address,date,time,people));


                }
                Log.i("post끗", "끝");

                mAdapter.notifyDataSetChanged();
                rAdapter.notifyDataSetChanged();
                Log.i("post끗", "진짜끝");

            }
            catch (JSONException e) {
                Toast.makeText(getActivity(), "Error" + e.toString(), Toast.LENGTH_LONG).show();
                Log.i("EEEEEEEEEEEEE", e.toString());
            }
        }
        private String convertInputStreamToString(InputStream inputStream) throws IOException{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null)
                result += line;
            inputStream.close();
            Log.i("실행", "완료");
            return result;
        }
    }
    public class roominfo {

        String id;
        String name;
        String loc;
        String image;

        public roominfo(String id, String img, String name, String address) {

            this.id = id;
            this.image = img;
            this.name = name;
            this.loc = address;

        }


        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getLoc() {
            return loc;
        }

        public String getImage() {
            return image;
        }
    }

    static class RoomViewHolder {
        TextView txRoom;
        TextView txLoc;
        NetworkImageView imimage;
    }

    public class registerAdapter extends ArrayAdapter<roominfo> {

        public registerAdapter(Context context, int resource, List<roominfo> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RoomViewHolder holder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.listitem1, parent, false);
                holder = new RoomViewHolder();
                holder.txRoom = (TextView) convertView.findViewById(R.id.name);
                holder.txLoc = (TextView) convertView.findViewById(R.id.address);
                holder.imimage = (NetworkImageView) convertView.findViewById(R.id.studyroomimage);
                convertView.setTag(holder);

            } else {
                holder = (RoomViewHolder) convertView.getTag();
            }
            holder.txRoom.setText(getItem(position).getName());
            holder.txLoc.setText(getItem(position).getLoc());
            holder.imimage.setImageUrl("http://192.168.0.58:3003/" + getItem(position).getImage(), mImageLoader);
            return convertView;
        }
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        String id = mAdapter.getItem(pos).getId();
        String img=mAdapter.getItem(pos).getImage();
        Intent intent=new Intent(getActivity(),myfrag4_2.class);
        intent.putExtra("id",id);
        intent.putExtra("img",img);
        startActivity(intent);
    }
    public class rpeopleinfo {

        String user;
        String rname;
        String sname;
        String address;
        String date;
        String time;
        String people;
        public rpeopleinfo(String user, String rname,String sname, String address, String date,String time, String people) {
            this.user=user;
            this.rname=rname;
            this.sname=sname;
            this.address=address;
            this.date=date;
            this.time=time;
            this.people=people;

        }
        public String getUser(){return user;}
        public String getRname(){return rname;}
        public String getSname(){return sname;}
        public String getAddress(){return address;}
        public String getDate(){return date;}
        public String getTime(){return time;}
        public String getPeople(){return people;}
    }

    static class RegisterViewHolder {
        TextView txuser;
        TextView txrname;
        TextView txsname;
        TextView txaddr;
        TextView txdate;
        TextView txtime;
        TextView txpeople;

    }

    public class rpAdapter extends ArrayAdapter<rpeopleinfo> {

        public rpAdapter(Context context, int resource, List<rpeopleinfo> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RegisterViewHolder holder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.listitem4, parent, false);
                holder = new RegisterViewHolder();
                holder.txuser = (TextView) convertView.findViewById(R.id.user);
                holder.txrname = (TextView) convertView.findViewById(R.id.rname);
                holder.txsname = (TextView) convertView.findViewById(R.id.sname);
                holder.txaddr = (TextView) convertView.findViewById(R.id.address);
                holder.txdate = (TextView) convertView.findViewById(R.id.date);
                holder.txtime = (TextView) convertView.findViewById(R.id.time);
                holder.txpeople=(TextView) convertView.findViewById(R.id.people);

                convertView.setTag(holder);

            } else {
                holder = (RegisterViewHolder) convertView.getTag();
            }
            holder.txuser.setText(getItem(position).getUser());
            holder.txrname.setText(getItem(position).getRname());
            holder.txsname.setText(getItem(position).getSname());
            holder.txaddr.setText(getItem(position).getAddress());
            holder.txdate.setText(getItem(position).getDate());
            holder.txtime.setText(getItem(position).getTime());
            holder.txpeople.setText(getItem(position).getPeople());


            return convertView;
        }
    }

}
