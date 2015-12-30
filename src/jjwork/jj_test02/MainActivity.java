package jjwork.jj_test02;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import jjwork.tools.*;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        buttonsOnClickInit();
    }
    
    void buttonsOnClickInit() {

        Button test1Btn = (Button)findViewById(R.id.button1);
        Button test2Btn = (Button)findViewById(R.id.button2);
        Button test3Btn = (Button)findViewById(R.id.button3);
        Button test4Btn = (Button)findViewById(R.id.button4);

        Button allTestBtn = (Button)findViewById(R.id.button5);
        Button settingBtn = (Button)findViewById(R.id.button6);
        
        test1Btn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				if(ButtonTimeout.isFastDoubleClick()) return;
				startActivity(new Intent(MainActivity.this, Sub1Activity.class));
			}
		});
        test2Btn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				if(ButtonTimeout.isFastDoubleClick()) return;
				startActivity(new Intent(MainActivity.this, Sub2Activity.class));
			}
		});
        test3Btn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				if(ButtonTimeout.isFastDoubleClick()) return;
				startActivity(new Intent(MainActivity.this, Sub3Activity.class));
			}
		});
        test4Btn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				if(ButtonTimeout.isFastDoubleClick()) return;
				startActivity(new Intent(MainActivity.this, Sub4Activity.class));
			}
		});
        
        allTestBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(ButtonTimeout.isFastDoubleClick()) return;
				startActivity(new Intent(MainActivity.this, Sub1Activity.class));
			}
		});
        
        settingBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(ButtonTimeout.isFastDoubleClick()) return;
				startActivity(new Intent(MainActivity.this, SettingActivity.class));
			}
		});
    }
}
