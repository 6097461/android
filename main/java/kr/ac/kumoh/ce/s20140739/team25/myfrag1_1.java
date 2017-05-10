package kr.ac.kumoh.ce.s20140739.team25;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.R.attr.data;

/**
 * Created by 60974 on 2017-04-04.
 */

public class myfrag1_1 extends Activity {
    protected ListView nList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my1_1);
        nList = (ListView) findViewById(R.id.listview2);
        ArrayList<Listviewitem> data = new ArrayList<>();
        Listviewitem one = new Listviewitem("A룸", "최대인원:5명", "에어컨 있음");
        Listviewitem two = new Listviewitem("B", "최대인원:8명", "빔프로젝트 있음");
        Listviewitem three = new Listviewitem("C룸", "최대인원:6명", "LED조명");
        data.add(one);
        data.add(two);
        data.add(three);
        ListviewAdapter adapter = new ListviewAdapter(this, R.layout.listitem2, data);
        nList.setAdapter(adapter);
        nList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(myfrag1_1.this, myfrag1_2.class);
                startActivity(intent);
            }
        });

    }

    public class Listviewitem {
        private String sroom;
        private String max;
        private String etc;

        public String getSroom() {
            return sroom;
        }

        public String getMax() {
            return max;
        }

        public String getEtc() {
            return etc;
        }

        public Listviewitem(String sroom, String max, String etc) {
            this.sroom = sroom;
            this.max = max;
            this.etc = etc;

        }

    }

    public class ListviewAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<Listviewitem> data;
        private int layout;

        public ListviewAdapter(Context context, int layout, ArrayList<Listviewitem> data) {
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.data = data;
            this.layout = layout;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public String getItem(int position) {
            return data.get(position).getSroom();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(layout, parent, false);
            }
            Listviewitem listviewitem = data.get(position);
            TextView sroom = (TextView) convertView.findViewById(R.id.sname);
            TextView max = (TextView) convertView.findViewById(R.id.max);
            TextView etc = (TextView) convertView.findViewById(R.id.etc);
            sroom.setText(listviewitem.getSroom());
            max.setText(listviewitem.getMax());
            etc.setText(listviewitem.getEtc());
            return convertView;

        }

    }

}
