package jjwork.jjtest02;

import jjwork.controller.HardwaveBinder;
import jjwork.controller.HardwaveBinder.OnUICallback;
import jjwork.controller.HardwaveService;
import jjwork.controller.InverterHW;
import jjwork.jj_test02.R;
import jjwork.tools.TestRecordService;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Sub2Activity extends Activity {

	HardwaveBinder hwService = null;
	TextView tv;
	EditText ed;
	Button returnBtn, setBtn, getBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub2);
        
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
				Sub2Activity.this.finish();
			}
		});
        
        setBtn = (Button) findViewById(R.id.button2);
        setBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(hwService == null) return;
				String s = ed.getText().toString();
				int arg1 = Integer.parseInt(s);
				hwService.setInverterParam(arg1, 50);
			}
		});
        tv = (TextView)findViewById(R.id.textView1);
        getBtn = (Button) findViewById(R.id.button3);
        getBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(hwService == null)return;
				hwService.getInverterParam(new OnUICallback() {					
					@Override
					public void callback(Object obj) {
						InverterHW inverter = (InverterHW)obj;
						tv.setText("电压:" + inverter.args[2]);
						tv.append("\n电流:" + inverter.args[3] + "\n");
					}
				});
			}
		});
	}
	ServiceConnection conn = new ServiceConnection() {		
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
		}
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder binder) {
			hwService = (HardwaveBinder) binder;
			
			hwService.setInverterParam(220, 50);
		}
	};
}
