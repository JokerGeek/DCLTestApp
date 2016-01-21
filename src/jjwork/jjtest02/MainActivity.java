package jjwork.jjtest02;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import jjwork.jj_test02.R;
import jjwork.tools.*;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		buttonsOnClickInit();
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	boolean isSettingData() {
		SharedPreferences sharePrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String comm_param = sharePrefs.getString("comm_baud_list", "");
		if (comm_param.equals("")) {
			Toast.makeText(this, "请设置参数!!!", Toast.LENGTH_LONG).show();
			return false;
		} else
			return true;
	}

	void buttonsOnClickInit() {

		Button test1Btn = (Button) findViewById(R.id.button1);
		Button test2Btn = (Button) findViewById(R.id.button2);
		Button test3Btn = (Button) findViewById(R.id.button3);
		Button test4Btn = (Button) findViewById(R.id.button4);

		Button allTestBtn = (Button) findViewById(R.id.button5);
		Button settingBtn = (Button) findViewById(R.id.button6);
		Button oupRecoderBtn = (Button) findViewById(R.id.button7);
		
		test1Btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (ButtonTimeout.isFastDoubleClick())
					return;
				if (!isSettingData())
					return;
				startActivity(new Intent(MainActivity.this, Sub1Activity.class));
			}
		});
		test2Btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (ButtonTimeout.isFastDoubleClick())
					return;
				if (!isSettingData())
					return;
				startActivity(new Intent(MainActivity.this, Sub2Activity.class));
			}
		});
		test3Btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (ButtonTimeout.isFastDoubleClick())
					return;
				if (!isSettingData())
					return;
				startActivity(new Intent(MainActivity.this, Sub3Activity.class));
			}
		});
		test4Btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (ButtonTimeout.isFastDoubleClick())
					return;
				if (!isSettingData())
					return;
				startActivity(new Intent(MainActivity.this, Sub4Activity.class));
			}
		});

		allTestBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ButtonTimeout.isFastDoubleClick())
					return;
				if (!isSettingData())
					return;
				startActivity(new Intent(MainActivity.this, Sub1Activity.class));
			}
		});

		settingBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ButtonTimeout.isFastDoubleClick())
					return;
				startActivity(new Intent(MainActivity.this,
						SettingActivity.class));
			}
		});
		
		oupRecoderBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				if (ButtonTimeout.isFastDoubleClick())
					return;
				Log.d("Modbus Ac", getExternalStorageDirectory());
				
			}
		});
	}
	
	
	/**
	 * 获取扩展存储路径，TF卡、U盘
	 */
	public static String getExternalStorageDirectory(){
	    String dir = new String();
	    try {
	        Runtime runtime = Runtime.getRuntime();
	        Process proc = runtime.exec("mount");
	        InputStream is = proc.getInputStream();
	        InputStreamReader isr = new InputStreamReader(is);
	        String line;
	        BufferedReader br = new BufferedReader(isr);
	        while ((line = br.readLine()) != null) {
	            if (line.contains("secure")) continue;
	            if (line.contains("asec")) continue;
	            
	            if (line.contains("fat")) {
	                String columns[] = line.split(" ");
	                if (columns != null && columns.length > 1) {
	                    dir = dir.concat(columns[1] + "\n");
	                }
	            } else if (line.contains("fuse")) {
	                String columns[] = line.split(" ");
	                if (columns != null && columns.length > 1) {
	                    dir = dir.concat(columns[1] + "\n");
	                }
	            }
	        }
	    } catch (Exception e) {
	        Log.d("Mo Ac", e.toString());
	    }
	    return dir;
	}
}
