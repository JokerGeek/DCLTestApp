package jjwork.jjtest02;

import jjwork.controller.BoostHW;
import jjwork.controller.HardwaveBinder;
import jjwork.controller.HardwaveService;
import jjwork.controller.InverterHW;
import jjwork.controller.HardwaveBinder.OnUICallback;
import jjwork.jj_test02.R;
import jjwork.tools.TestRecordService;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Sub3Activity extends Activity {
	HardwaveBinder hwService = null;
	TextView tv;
	Button returnBtn, setBtn, getBtn, openBtn, closeBtn;
	EditText ed;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub3);
        TestRecordService.errorContext = this;
        uiInit();
		bindService(new Intent(this, HardwaveService.class), conn, BIND_AUTO_CREATE);
    }
    @Override
    protected void onDestroy() {
    	unbindService(conn);
    	super.onDestroy();
    }
	private void uiInit() {
		ed = (EditText)findViewById(R.id.editText1);
        
        returnBtn = (Button)findViewById(R.id.button1);
        returnBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Sub3Activity.this.finish();
			}
		});
        
        setBtn = (Button) findViewById(R.id.button2);
        setBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(hwService == null) return;
				String s = ed.getText().toString();
				int arg1 = Integer.parseInt(s) *10;
				hwService.setBoostParam(0, arg1);
			}
		});
        tv = (TextView)findViewById(R.id.textView3);
        getBtn = (Button) findViewById(R.id.button3);
        getBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(hwService == null)return;
				hwService.getBoostParam(new OnUICallback() {					
					@Override
					public void callback(Object obj) {
						BoostHW boost = (BoostHW)obj;
						tv.setText("电压:" + boost.args[2]);
						tv.append("\n电流:" + boost.args[3] + "\n");
					}
				});
			}
		});
        openBtn = (Button)findViewById(R.id.button4);
        openBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(hwService == null)return;
				hwService.openBoost();
			}
		});
        closeBtn = (Button)findViewById(R.id.button5);
        closeBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(hwService == null)return;
				hwService.closeBoost();
			}
		});
	}
	ServiceConnection conn = new ServiceConnection() {		
		@Override
		public void onServiceDisconnected(ComponentName arg0) { }
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder binder) {
			hwService = (HardwaveBinder) binder;			
		}
	};
}
