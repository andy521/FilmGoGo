package com.example.administrator.filmgogo;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class MainActivity extends Activity  {

    private Button login_btn, manager_btn, oldmovie_btn, manager_delete_btn, setVoteZero_btn;
    private Button register_btn, manager_add_votemovies_btn, manager_delete_votemovies_btn;
    private EditText muserName;
    private EditText mPassword;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setListener();
    }

    private void init() {
        login_btn= (Button) findViewById(R.id.sign_in_button);
        register_btn= (Button) findViewById(R.id.sign_up_button);
        muserName = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        oldmovie_btn= (Button) findViewById(R.id.oldmoviePage);
        manager_btn= (Button) findViewById(R.id.manager);
        manager_delete_btn= (Button) findViewById(R.id.manager_delete);
        manager_add_votemovies_btn= (Button) findViewById(R.id.manager_add_votemovies);
        manager_delete_votemovies_btn= (Button) findViewById(R.id.manager_delete_votemovies);
        setVoteZero_btn= (Button) findViewById(R.id.setVotezero);
    }

    private void setListener() {
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MainActivity.this, Register_Activity.class);
                startActivity(intent);
            }
        });
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignIn();
            }
        });
        manager_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2= new Intent(MainActivity.this, Manager_Activity.class);
                startActivity(intent2);
            }
        });
        manager_delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent4= new Intent(MainActivity.this, Manager_Delete.class);
                startActivity(intent4);
            }
        });
        oldmovie_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3= new Intent(MainActivity.this, OldMovieInfo.class);
                startActivity(intent3);
            }
        });
        manager_add_votemovies_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent5= new Intent(MainActivity.this, Manager_add_votemovies.class);
                startActivity(intent5);
            }
        });
        manager_delete_votemovies_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent6= new Intent(MainActivity.this, Manager_delete_votemovies.class);
                startActivity(intent6);
            }
        });
        setVoteZero_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(setVoteZero_Task).start();
            }
        });

    }

    private void attemptSignIn() {
        muserName.setError(null);
        mPassword.setError(null);
        new Thread(networkTask).start();
    }

    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            String baseURL = "http://172.18.71.17:8080/FilmGoGo/login";
            String TAG = "Login";
            String retSrc = "null";
            try {
                HttpPost request = new HttpPost(baseURL);
                // 先封装一个 JSON 对象
                JSONObject param = new JSONObject();
                param.put("name", muserName.getText().toString());
                param.put("password", mPassword.getText().toString());
                // 绑定到请求 Entry
                StringEntity se = new StringEntity(param.toString(),"UTF-8");
                Log.i(TAG, se.toString());
                request.setEntity(se);
                // 发送请求
                HttpResponse httpResponse = new DefaultHttpClient().execute(request);
                // 得到应答的字符串，这也是一个 JSON 格式保存的数据
                retSrc = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
                Log.i(TAG, retSrc);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            // 在这里进行 http request.网络请求相关操作
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value", retSrc);
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            //Log.i("mylog", "请求结果为-->" + val);
            String TAG = "json";
            try{
                JSONObject result = new JSONObject(val);
                boolean exist= result.getBoolean("exist");
                boolean success = result.getBoolean("loginAble");
                if (success) {
                    Intent intent = new Intent(MainActivity.this, Home_Activity.class);
                    startActivity(intent);
                } else {
                    if (exist) {
                        mPassword.setError(getString(R.string.error_invalid_password));
                    } else {
                        muserName.setError(getString(R.string.error_username_notexist));
                    }
                }
            }
            catch (Exception e) {
                Log.i(TAG, e.toString());
            }
        }
    };

    Runnable setVoteZero_Task= new Runnable() {
        @Override
        public void run() {
            String baseURL = "http://172.18.71.17:8080/FilmGoGo/votemovie";
            int movie_id= 43;  //这里指定要票数清零的电影id
            String result= "";
            try{
                String url = baseURL + "/setVoteZero/"+ movie_id;
                HttpGet httpGet = new HttpGet(url);
                HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);
                result = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value", result);
            msg.setData(data);
            VoteMovie_handler.sendMessage(msg);
        }
    };
    Handler VoteMovie_handler= new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            String TAG = "json";
            String result= "";
            try{
                result= new JSONObject(val).getString("setVoteZero");
                if (result.equals("success")) {
                    Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
                } else  {
                    Toast.makeText(MainActivity.this, "false", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e) {
                Log.i(TAG, e.toString());
            }
        }
    };

}
