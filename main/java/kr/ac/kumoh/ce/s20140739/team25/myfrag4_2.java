package kr.ac.kumoh.ce.s20140739.team25;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by 60974 on 2017-04-04.
 */

public class myfrag4_2 extends Activity {
    Button sbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myfrag4_2);
        sbtn=(Button)findViewById(R.id.SRR);


    }
   public void SRRclick(View v){
       Intent intent = new Intent(myfrag4_2.this, myfrag4_3.class);
       startActivity(intent);
   }
}
