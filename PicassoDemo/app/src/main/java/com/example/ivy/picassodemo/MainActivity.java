package com.example.ivy.picassodemo;

import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import java.io.File;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv =   (ImageView) findViewById(R.id.iv);

        Button btAss = (Button) findViewById(R.id.bt_ass);
        Button btRes = (Button) findViewById(R.id.bt_res);
        Button btNet = (Button) findViewById(R.id.bt_net);
        Button btSd = (Button) findViewById(R.id.bt_sd);

        btAss.setOnClickListener(this);
        btRes.setOnClickListener(this);
        btNet.setOnClickListener(this);
        btSd.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_ass:
                Picasso picasso = new Picasso.Builder(this)
                        .memoryCache(new LruCache(10 << 20))
                        .indicatorsEnabled(true)
                        .build();


                picasso.with(this).load("file:///android_asset/Penguins.jpg")
                        .config(Bitmap.Config.RGB_565)
                        //.resize(20,20)
                        .into(iv);
                break;

            case R.id.bt_res:
                Picasso.with(this).load(R.mipmap.pic2)
                        .rotate(90)

                        .into(iv);
                break;

            case R.id.bt_net:

//                String url = "http://b.hiphotos.baidu.com/image/pic/item/9825bc315c6034a857770337ce134954082376ea.jpg";
//                Picasso.with(this).load(url)
//                        .placeholder(R.mipmap.loading)
//                        .error(R.mipmap.error)
//                        .noFade().into(iv);
                String KEY_URL = "http://www.jcodecraeer.com/uploads/20140731/67391406772378.png";
                Picasso.with(this).load(KEY_URL).into(iv);

                break;

            case R.id.bt_sd:
                Picasso.with(this).load(new File(Environment.getExternalStorageDirectory(),"1.jpg")).into(iv);
                break;
        }
    }
}
