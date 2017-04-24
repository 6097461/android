package kr.ac.kumoh.ce.s20140739.team25;

import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 60974 on 2017-04-04.
 */

public class myfrag1 extends Fragment implements AdapterView.OnItemClickListener {

    protected ArrayList<roominfo> rArray = new ArrayList<roominfo>();
public static final String ROOMTAG="RoomTag";
    protected JSONObject mResult=null;
    protected  ListView mList;
    protected roomAdapter mAdapter;
    protected RequestQueue mQueue;
    protected ImageLoader mImageLoader=null;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // return super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_my1, container, false);


        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,rArray);
        mAdapter = new roomAdapter(getActivity(),R.layout.listitem1, rArray);
        mList = (ListView) rootView.findViewById(R.id.listview1);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(this);

        Cache cache=new DiskBasedCache(getActivity().getCacheDir(),1024*1024);
        Network network=new BasicNetwork(new HurlStack());
        mQueue=new RequestQueue(cache,network);
        mQueue.start();
        mImageLoader=new ImageLoader(mQueue,new LruBitmapCache(LruBitmapCache.getCacheSize(getActivity())));
        requestRoom();

        return rootView;
    }
    protected void requestRoom(){
        String url="http://127.0.0.1/select.php";
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET,url,null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        mResult=response;
                        drawList();
                    }
                },
        new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"서버에러",Toast.LENGTH_LONG).show();
            }
        }
        );
        jsonObjectRequest.setTag(ROOMTAG);
        mQueue.add(jsonObjectRequest);
    }
public void drawList(){
    rArray.clear();
    try{
        JSONArray jsonMainNode=mResult.getJSONArray("list");
        for(int i=0;i<jsonMainNode.length();i++) {
            JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
            String rname = jsonChildNode.getString("rname");
            Log.i("rname", rname);
            String loc = jsonChildNode.getString("loc");
            Log.i("loc", loc);
            String image = jsonChildNode.getString("image");
            Log.i("image", image);
            rArray.add(new roominfo(rname, loc,image));
        }

        }
    catch(JSONException | NullPointerException e){
        Toast.makeText(getActivity().getApplicationContext(),"Error"+e.toString(),Toast.LENGTH_LONG).show();
        mResult=null;
    }
   mAdapter.notifyDataSetChanged();
}

    @Override
    public void onStop() {
        super.onStop();
        if(mQueue!=null){
            mQueue.cancelAll(ROOMTAG);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {

        Intent intent=new Intent(getActivity(),myfrag1_1.class);
         startActivity(intent);

    }

    public class roominfo {
        String name;
        String loc;
        String image;

        public roominfo(String name, String loc,String image) {
            this.name = name;
            this.loc = loc;
            this.image=image;
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

    public class roomAdapter extends ArrayAdapter<roominfo> {

        public roomAdapter(Context context, int resource, List<roominfo> objects) {
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
                holder.txLoc=(TextView) convertView.findViewById(R.id.location);
               holder.imimage=(NetworkImageView)convertView.findViewById(R.id.image);
                convertView.setTag(holder);

            } else {
                holder = (RoomViewHolder) convertView.getTag();
            }
            holder.txRoom.setText(getItem(position).getName());
            holder.txLoc.setText(getItem(position).getLoc());
            holder.imimage.setImageUrl("http://127.0.0.1/"+getItem(position).getImage(),mImageLoader);
            return convertView;
        }
    }

}
