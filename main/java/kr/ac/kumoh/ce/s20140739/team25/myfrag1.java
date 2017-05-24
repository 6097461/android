package kr.ac.kumoh.ce.s20140739.team25;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

public class myfrag1 extends Fragment  {
    TextView name, address;
   // TextView id,img,adminId;
    ImageView imView;
    String imgUrl = "", result;
    Bitmap bmImg;
    back task;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // return super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.listitem1,null);
        imView = (ImageView)rootView.findViewById(R.id.iii);
       // id = (TextView)rootView.findViewById(R.id.id);
       // img = (TextView)rootView.findViewById(R.id.img);
        name = (TextView)rootView.findViewById(R.id.name);
        address = (TextView)rootView.findViewById(R.id.address);
        //adminId = (TextView)rootView.findViewById(R.id.adminId);

        task = new back();
//        task.execute(imgUrl+"img1");
        task.execute("http://192.168.0.58:3003/home/list");
        return rootView;
    }
    private class back extends AsyncTask<String, Integer, String> {
        @Override
        public String doInBackground(String... urls) {
            // TODO Auto-generated method stub
            // 이미지 URL받고 나머지 JSON값 받아서 띄우기
            Log.i("task", "실행?");

            try{
                URL myFileUrl = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection)myFileUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                Log.i("task", "연결?");

                conn.connect();
                Log.i("task", "연결!");

                Log.i("task", "비트맵?");
                InputStream inputStream = conn.getInputStream();

                if(inputStream != null)
                    result = convertInputStreamToString(inputStream);
                else
                    result = "Did not work!";


//                bmImg = BitmapFactory.decodeStream(is);
                Log.i("task", "비트맵!");

            }catch(IOException e){
                e.printStackTrace();
            }
            return result;
        }

        protected void onPostExecute(String str){
            private ArrayList<infoo> infoList;
            infoList=new ArrayList<infoo>();
            imView.setImageBitmap(bmImg);
            try {
                JSONObject jsResult = new JSONObject(str);
                JSONArray infolist = jsResult.getJSONArray("list");

                for(int i=0; i < infolist.length(); i++) {
                    JSONObject jsonObject = infolist.getJSONObject(i);


                  //  String idd = jsonObject.getString("id");
                   // String imgg = jsonObject.getString("img");
                    String namee = jsonObject.getString("name");
                    String addresss = jsonObject.getString("address");
                   // String adminIdd = jsonObject.getString("adminId");

                    //id.setText(id.getText() + ", " + idd);
                   // img.setText(img.getText() + ", " + imgg);
                    name.setText(name.getText() + ", " + namee);
                    address.setText(address.getText() + ", " + addresss);
                    //adminId.setText(adminId.getText() + ", " + adminIdd);
                }
            }
            catch (JSONException e) {
                Toast.makeText(getActivity(), "Error" + e.toString(), Toast.LENGTH_LONG).show();
            }
        }
        private String convertInputStreamToString(InputStream inputStream) throws IOException{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null)
                result += line;
            inputStream.close();
            return result;

        }
    }
}
