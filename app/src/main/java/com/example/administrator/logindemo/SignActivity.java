package com.example.administrator.logindemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mob.MobSDK;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

/**
 * Created by Administrator on 2017/9/26.
 */

public class SignActivity extends AppCompatActivity {

    private EditText account_sign;
    private EditText password_sign;
    private Document document;
    private String htmlString,show;
    private Handler handler;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign);
        MobSDK.init(this, "2105b86be8810", "2c413dc32edbca60e2d7b14256d51964");

        account_sign = (EditText) findViewById(R.id.account_sign);
        password_sign = (EditText) findViewById(R.id.password_sign);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what)
                {
                    case 0:
                        Toast.makeText(SignActivity.this,show,Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SignActivity.this,SignActivity.class);
                        startActivity(intent);
                }

            }
        };
    }


    public void sign(View view) {

        if (account_sign.getText().toString().length() <= 0  ||password_sign.getText().toString().length() <= 0) {
            Toast.makeText(SignActivity.this, "请检查注册信息是否填写完整", Toast.LENGTH_LONG).show();}
        else if(account_sign.getText().toString().length() <4  ||password_sign.getText().toString().length() <6) {
            Toast.makeText(SignActivity.this, "用户名或密码过于简单", Toast.LENGTH_LONG).show();}

        else{

            RegisterPage registerPage = new RegisterPage();

            //注册回调事件
            registerPage.setRegisterCallback(new EventHandler() {
                //事件完成后调用
                @Override
                public void afterEvent(int event, int result, Object data) {

//                判断是否读取到数据
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {

                                    HttpURLConnection httpURLConnection = null;
                                    InputStream inputStream = null;
                                    URL url = null;
                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    url = new URL("http://112.74.181.159/dmstudio/register?registerName=" + account_sign.getText().toString() + "&registerPassword=" + password_sign.getText().toString());
                                    httpURLConnection = (HttpURLConnection) url.openConnection();
                                    httpURLConnection.connect();
                                    if (httpURLConnection.getResponseCode() == 200) {
                                        inputStream = httpURLConnection.getInputStream();
                                        byte[] data = new byte[1024];
                                        int len = 0;
                                        while ((len = inputStream.read(data)) != -1) {     //URL输入流
                                            byteArrayOutputStream.write(data, 0, len);        //写进byteArrayOutputStream

                                        }
                                    }

                                    htmlString = byteArrayOutputStream.toString();

                                    document = Jsoup.parse(htmlString);
                                    show = document.body().getElementsByTag("body").text();
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
                }
            });
            //显示注册界面
            registerPage.show(this);


        }
    }

    public void back(View view) {
        Intent intent = new Intent(SignActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}
