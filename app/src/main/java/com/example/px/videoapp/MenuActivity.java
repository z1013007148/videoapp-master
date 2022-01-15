package com.example.px.videoapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {
    private Button saveBtn,backBtn;
    private EditText forTimeDelay,backTimeDelay,betweenTimeDelay,loopTimes,bitRate;
    private float  forTimeDelay_time,backTimeDelay_time,betweenTimeDelay_time;
    private int loopTimes_num,bitRate_num;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        forTimeDelay=findViewById(R.id.forTimeDelay);
        backTimeDelay=findViewById(R.id.backTimeDelay);
        betweenTimeDelay=findViewById(R.id.betweenTimeDelay);
        loopTimes=findViewById(R.id.loopTimes);
        bitRate=findViewById(R.id.bitRate);

        Intent intent=getIntent();
        forTimeDelay_time=intent.getFloatExtra("forTimeDelay",0);
        backTimeDelay_time=intent.getFloatExtra("betweenTimeDelay",0);
        betweenTimeDelay_time=intent.getFloatExtra("betweenTimeDelay",0);
        loopTimes_num=intent.getIntExtra("loopTimes",1);
        bitRate_num=intent.getIntExtra("bitRate",5);

        forTimeDelay.setText(String.valueOf(forTimeDelay_time));
        backTimeDelay.setText(String.valueOf(backTimeDelay_time));
        betweenTimeDelay.setText(String.valueOf(betweenTimeDelay_time));
        loopTimes.setText(String.valueOf(loopTimes_num));
        bitRate.setText(String.valueOf(bitRate_num));
    }
    public void save(View view) {

        forTimeDelay_time=Float.parseFloat(forTimeDelay.getText().toString());
        backTimeDelay_time=Float.parseFloat(backTimeDelay.getText().toString());
        betweenTimeDelay_time=Float.parseFloat(betweenTimeDelay.getText().toString());
        loopTimes_num=Integer.parseInt(loopTimes.getText().toString());
        bitRate_num=Integer.parseInt(bitRate.getText().toString());

        Toast.makeText(MenuActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
    }
    public void back(View view) {
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("forTimeDelay",forTimeDelay_time);
        intent.putExtra("backTimeDelay",backTimeDelay_time);
        intent.putExtra("betweenTimeDelay",betweenTimeDelay_time);
        intent.putExtra("loopTime",loopTimes_num);
        intent.putExtra("birRate",bitRate_num);

        startActivity(intent);
    }

}