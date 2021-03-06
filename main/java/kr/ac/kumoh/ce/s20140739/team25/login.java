package kr.ac.kumoh.ce.s20140739.team25;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.nhn.android.naverlogin.OAuthLogin.mOAuthLoginHandler;


public class login extends Activity {
    public static OAuthLogin mOAuthLoginModule;
    private static OAuthLoginButton mOAuthLoginButton;
    private static Context mContext;
    private CallbackManager callbackManager = CallbackManager.Factory.create();
    String token;
    String id;
    String name;
    static String json="", cookieString="";

    private static final int NAVER_LOGIN = 1;
    private static final int FACEBOOK_LOGIN = 1;

    int login_i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        mContext = getApplicationContext();
        setContentView(R.layout.mylogin);

        mOAuthLoginModule = OAuthLogin.getInstance();
            mOAuthLoginModule.init(
            mContext
            , "XrgiqeOS9rEeJJ35jxmt"
                    , "PyWxIsxzTq"
                    , "6097461"
                                      );
            mOAuthLoginButton = (OAuthLoginButton) findViewById(R.id.buttonOAuthLoginImg);
            mOAuthLoginButton.setOAuthLoginHandler(mOAuthLoginHandler);
            mOAuthLoginButton.setBgResourceId(R.drawable.naverbtn);
            //facebook
            LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        try{
                            id = object.getString("id");
                            name = object.getString("name");
                            login_i = FACEBOOK_LOGIN;
                            facebookloginDB logindb = new facebookloginDB();
                            logindb.execute();
                            Intent intent =new Intent(login.this,MainActivity.class);
                            startActivity(intent);
                        }
                        catch (Exception e) {

                        }
                    }

                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.e("LoginErr", error.toString());
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                String accessToken = mOAuthLoginModule.getAccessToken(mContext);
                String refreshToken = mOAuthLoginModule.getRefreshToken(mContext);
                long expiresAt = mOAuthLoginModule.getExpiresAt(mContext);
                String tokenType = mOAuthLoginModule.getTokenType(mContext);

                token = accessToken;
                login_i = NAVER_LOGIN;
                naverloginDB logindb = new naverloginDB();
                logindb.execute();
                Intent intent =new Intent(login.this,MainActivity.class);
                startActivity(intent);

            } else {
                String errorCode = mOAuthLoginModule.getLastErrorCode(mContext).getCode();
                String errorDesc = mOAuthLoginModule.getLastErrorDesc(mContext);
                Toast.makeText(mContext, "errorCode:" + errorCode
                        + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
            }
        }
    };

     public class naverloginDB extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... unused) {
            naverPOST();
            return null;
        }
    }

    public String naverPOST() {
        try {
            String apiURL = "";


            String COOKIES_HEADER = "Set-Cookie";
            apiURL = MainActivity.SERVER_IP_PORT+"/auth/naver";

            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestMethod("POST");

            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setDefaultUseCaches(false);


            con.connect();


            JSONObject data = new JSONObject();

            data.accumulate("access_token", token);
            json = data.toString();


            OutputStream wr = con.getOutputStream();

            wr.write(json.getBytes("utf-8"));
            wr.flush();
            wr.close();

            Map<String, List<String>> headerFields = con.getHeaderFields();
            List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

            if(cookiesHeader != null) {
                for (String cookie : cookiesHeader) {
                    String cookieName = HttpCookie.parse(cookie).get(0).getName();
                    String cookieValue = HttpCookie.parse(cookie).get(0).getValue();

                    cookieString = cookieName + "=" + cookieValue;

                    CookieManager.getInstance().setCookie(MainActivity.SERVER_IP_PORT, cookieString);

                }
            }


            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출

            } else {  Log.i("login", "에러!");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public class facebookloginDB extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... unused) {
            facebookPOST();
            return null;
        }
    }

    public String facebookPOST() {
        try {
            String apiURL = "";


            String COOKIES_HEADER = "Set-Cookie";

            apiURL = MainActivity.SERVER_IP_PORT+"/auth/facebook";

            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestMethod("POST");

            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setDefaultUseCaches(false);


            con.connect();


            JSONObject data = new JSONObject();

            data.accumulate("id", id);
            data.accumulate("name", name);

            json = data.toString();


            OutputStream wr = con.getOutputStream();

            wr.write(json.getBytes("utf-8"));
            wr.flush();
            wr.close();

            Map<String, List<String>> headerFields = con.getHeaderFields();
            List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

            if(cookiesHeader != null) {
                for (String cookie : cookiesHeader) {
                    String cookieName = HttpCookie.parse(cookie).get(0).getName();
                    String cookieValue = HttpCookie.parse(cookie).get(0).getValue();

                    cookieString = cookieName + "=" + cookieValue;

                    CookieManager.getInstance().setCookie(MainActivity.SERVER_IP_PORT, cookieString);

                }
            }


            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출

            } else {

            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }



}
