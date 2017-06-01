package kr.ac.kumoh.ce.s20140739.team25;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import static kr.ac.kumoh.ce.s20140739.team25.R.id.button;

/**
 * Created by 60974 on 2017-04-04.
 */

public class myfrag1_2 extends Activity implements View.OnClickListener {
    Button b[] = new Button[24];
    int button[]= {R.id.b1,R.id.b2,R.id.b3,R.id.b4,R.id.b5,R.id.b6,R.id.b7,R.id.b8,R.id.b9,R.id.b10,R.id.b11,R.id.b12,R.id.b13,R.id.b14,R.id.b15,R.id.b16,R.id.b17,R.id.b18,R.id.b19,R.id.b20,R.id.b21,R.id.b22,R.id.b23,R.id.b24};
    boolean check[]=new boolean[24];
    int count=0;

    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my1_2);
        tv=(TextView)findViewById(R.id.tx);
        CalendarView calendar= (CalendarView)findViewById(R.id.calendar);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int date) {
                Toast.makeText(getApplicationContext(),date+"/"+month+"/"+year,Toast.LENGTH_LONG).show();;
            }
        });


        for(int i=0;i<24;i++) {

            b[i] = (Button) findViewById(button[i]);
            b[i].setOnClickListener(this);
            b[i].setBackgroundColor(Color.parseColor("#ffffff"));
            check[i]=true;
        }


    }

    public void onClick(View v){
        for(int i=0;i<24;i++) {
            if (v.getId() ==button[i]) {
                if(check[i]==true) {
                    b[i].setBackgroundColor(Color.parseColor("#C9CDF8"));
                    check[i]=false;
                }
                else{
                    b[i].setBackgroundColor(Color.parseColor("#ffffff"));
                    check[i]=true;
                }
            }
        }
    }
    public void minus(View v){
        if(count<=0) {
            count = 0;
            tv.setText(Integer.toString(count));
        }
        else {
            count--;
            tv.setText(Integer.toString(count));
        }
    }
    public void plus(View v){
        count++;
        tv.setText(Integer.toString(count));
    }
}
