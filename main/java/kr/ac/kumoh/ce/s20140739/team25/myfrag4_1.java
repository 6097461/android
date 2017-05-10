package kr.ac.kumoh.ce.s20140739.team25;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;


public class myfrag4_1 extends Activity {
    final int PICK_IMAGE = 100;
    TextView tv;
    EditText e1;
    EditText e2;
    EditText e3;
    Button btn;
    String name_Str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.myfrag4_1);
        e1 = (EditText) findViewById(R.id.name);
        e2 = (EditText) findViewById(R.id.address);
        e3 = (EditText) findViewById(R.id.detail);
        btn = (Button) findViewById(R.id.btn);
        tv = (TextView) findViewById(R.id.tv);
        btn.setOnClickListener(myClickListner);


    }



    public void imgclick(View v) {
        doTakeAlbumAction();
    }

    public void doTakeAlbumAction() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                  name_Str = getImageNameToUri(data.getData());

                    Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    ImageView image = (ImageView) findViewById(R.id.rimg);

                    //배치해놓은 ImageView에 set
                    image.setImageBitmap(image_bitmap);


                    //Toast.makeText(getBaseContext(), "name_Str : "+name_Str , Toast.LENGTH_SHORT).show();


                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
    public String getImageNameToUri(Uri data)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);

        return imgName;
    }
    /*View.OnClickListener myClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String temp = "{\"img\""+":"+"\""+name_Str+"\""+","+"\"name\"" + ":" + "\"" + e1.getText().toString() + "\"" + "," + "\"address\"" + ":" + "\"" + e2.getText().toString() +
                    "\"" + "," + "\"상세정보\"" + ":" + "\"" + e3.getText().toString() + "\"" + "}";
            tv.setText(temp);
        }
    };*/
    View.OnClickListener myClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(myfrag4_1.this, myfrag4_2.class);
            startActivity(intent);        }
    };

}

