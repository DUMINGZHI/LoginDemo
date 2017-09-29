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

        account_sign = (EditText) findViewById(R.id.account_sign);
        password_sign = (EditText) findViewById(R.id.password_sign);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what)
                {
                    case 0:
                        Toast.makeText(SignActivity.this,show,Toast.LENGTH_LONG).show();

                }

            }
        };
    }


    public void sign(View view) {
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
                    url = new URL("http://112.74.181.159/dmstudio/register?registerName=" + account_sign.getText().toString() + "&registerPassword=" +password_sign.getText().toString());
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

    public void back(View view) {
        Intent intent = new Intent(SignActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}
