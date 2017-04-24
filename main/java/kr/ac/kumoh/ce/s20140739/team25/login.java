package kr.ac.kumoh.ce.s20140739.team25;

import android.app.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class login extends Activity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mylogin);


    }
    public void nbtnclick(View v){
        Log.i("server request","naver");
    }
    public void fbtnclick(View v){
        Log.i("server request","facebook");
    }
    public void gbtnclick(View v){
        Log.i("server request","google");
    }





}
