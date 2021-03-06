package kr.ac.kumoh.ce.s20140739.team25;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class myfrag4_3 extends Activity {
    final int PICK_IMAGE = 100;
    TextView tv;
    EditText e1, e2, e3,e4;
    Button btn;
    String name_Str, pickpath;
    Uri uri;
    String id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.myfrag4_3);
        Intent intent=getIntent();
        id=intent.getStringExtra("id");

        e1 = (EditText) findViewById(R.id.sname);
        e2 = (EditText) findViewById(R.id.people);
        e3 = (EditText) findViewById(R.id.etc);
        e4=(EditText)findViewById(R.id.ip);
        btn = (Button) findViewById(R.id.btn);

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

    public String POST(String url){
        InputStream is = null;
        String result = "";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        try {
            // open connection
            URL urlCon = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)urlCon.openConnection();
            conn.setDoInput(true); //input 허용
            conn.setDoOutput(true);  // output 허용
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("Cookie", login.cookieString);

            conn.connect();

            // write data
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

            //text 전송
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"name\"\r\n\r\n" + URLEncoder.encode(e1.getText().toString(), "utf-8"));
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"max\"\r\n\r\n" + URLEncoder.encode(e2.getText().toString(), "utf-8"));
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"description\"\r\n\r\n" + URLEncoder.encode(e3.getText().toString(), "utf-8"));
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"ip\"\r\n\r\n" + URLEncoder.encode(e4.getText().toString(), "utf-8"));
            dos.writeBytes(lineEnd);



            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + pickpath + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            String realpath = pickpath;
            FileInputStream fileInputStream = new FileInputStream(realpath);

            int bytesAvailable = fileInputStream.available();
            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                dos.flush(); // finish upload...
            }
            fileInputStream.close();

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            dos.flush();


        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0]);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    uri = data.getData();
                    name_Str = getImageNameToUri(uri);
                    pickpath = getRealPathFromURI(uri);
                    Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    ImageView image = (ImageView) findViewById(R.id.rimg);

                    image.setImageBitmap(image_bitmap);


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        cursor.moveToNext();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
        Uri uri = Uri.fromFile(new File(path));

        cursor.close();

        return path;
    }

    public String getImageNameToUri(Uri data) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);

        return imgName;
    }
    View.OnClickListener myClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            HttpAsyncTask httpTask = new HttpAsyncTask();
            httpTask.execute(MainActivity.SERVER_IP_PORT+"/host/add/"+id);
            Intent intent = new Intent(myfrag4_3.this,myfrag4_2.class);
            intent.putExtra("id",id);
            startActivity(intent);
            finish();
        }
    };

}
