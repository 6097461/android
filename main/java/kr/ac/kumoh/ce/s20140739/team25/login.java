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
    static String token;
    static String json="", cookieString="";
    loginDB logindb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logindb = new loginDB();


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
            public void onSuccess(LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i("result", object.toString());
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
                finish();
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
                Log.i("출력", accessToken);
                token = accessToken;
                logindb.execute();
                finish();

            } else {
                String errorCode = mOAuthLoginModule.getLastErrorCode(mContext).getCode();
                String errorDesc = mOAuthLoginModule.getLastErrorDesc(mContext);
                Toast.makeText(mContext, "errorCode:" + errorCode
                        + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
            }
        }


    };

    public class loginDB extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... unused) {
            POST();

            return null;
        }

    }

    public static String POST() {
        try {

            Log.i("login", "execute시작!");

            String COOKIES_HEADER = "Set-Cookie";

            String apiURL = "http://192.168.0.58:3003/auth/naver";
//            String apiURL = "http://hmkcode.appspot.com/jsonservlet";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            Log.i("login", "연결!?!?!?!");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestMethod("POST");
            Log.i("login", "연결1/2");

            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setDefaultUseCaches(false);
//            strCookie = con.getHeaderField("Set-Cookie");
//            Log.i("쿠키쿠키", strCookie);
            Log.i("login", "연결직전");
            con.connect();


            Log.i("login", "연결!");

            JSONObject data = new JSONObject();
            data.accumulate("access_token", token);
            json = data.toString();
            Log.i("JSONdata", json);

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

                    CookieManager.getInstance().setCookie("http://192.168.0.58:3003", cookieString);

                }
            }
            Log.i("login", "쓰기성공!");

            int responseCode = con.getResponseCode();
            //         BufferedReader br;
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                //              br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                Log.i("login", "정상");


            } else {  // 에러 발생
                //             br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                Log.i("login", "에러!");

            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }


    public void gbtnclick(View v) {
        Log.i("server request", "google");
    }

}
