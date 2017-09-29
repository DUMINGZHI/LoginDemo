package com.example.administrator.logindemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2017/9/25.
 */

public class LoginActivity extends AppCompatActivity {

    private TextView show;
    private EditText accountEdit;
    private EditText passwordEdit;
    private Button loginBtn;
    private Button signBtn;
    private String account;
    private String password;
    private String htmlString;
    private Document document;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

    accountEdit = (EditText) findViewById(R.id.account);
    passwordEdit = (EditText) findViewById(R.id.password);
    loginBtn = (Button) findViewById(R.id.login);
    signBtn = (Button) findViewById(R.id.sign);


    handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 0:
                    if(password.equalsIgnoreCase(passwordEdit.getText().toString())&&password!=null&&password.length()>0)
                        Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(LoginActivity.this,"账号密码错误或无效",Toast.LENGTH_LONG).show();
            }

        }
    };

}



    //登录
    public void login(View view) {

        new Thread()
        {
            @Override
            public void run() {
                super.run();
                try {

                    HttpURLConnection httpURLConnection = null;
                    InputStream inputStream = null;
                    URL url = null;
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    url = new URL("http://112.74.181.159/dmstudio/Imp?loginname=" + URLEncoder.encode(accountEdit.getText().toString(),"UTF-8"));
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.connect();
                    if (httpURLConnection.getResponseCode() == 200) {
                        inputStream = httpURLConnection.getInputStream();
                        byte[] data = new byte[1024];
                        int len = 0;
                        while ((len = inputStream.read(data)) != -1) {     //URL输入流
                            byteArrayOutputStream.write(data,0,len);        //写进byteArrayOutputStream

                        }
                    }

                    htmlString = byteArrayOutputStream.toString();

                    document = Jsoup.parse(htmlString);
                    password = document.body().getElementsByTag("body").text();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    handler.sendEmptyMessage(0);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    //注册
    public void enterSign(View view) {
        Intent intent = new Intent(LoginActivity.this,SignActivity.class);
        startActivity(intent);
    }
}
