package jjwork.jjtest02;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import jjwork.controller.*;
import jjwork.controller.HardwaveBinder.OnUICallback;
import jjwork.jj_test02.R;
import jjwork.tools.TestRecordService;

public class Sub1Activity extends Activity {

	int workingVol = 220;
	int testMinWork, testMaxWork, testTimeout;
	
	TextView ctlMsgTv, timeoutTv, testStateTv;
	LinearLayout llayout;
	Button returnBtn;

	HardwaveBinder hwService = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sub1);

		testParamsInit();
		
		uiInit();
		
		bindService(new Intent(this, HardwaveService.class), conn, BIND_AUTO_CREATE);
	}

	private void testParamsInit() {
		SharedPreferences sharePrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String minWork = sharePrefs.getString("power_test_power_min", "");
		String maxWork = sharePrefs.getString("power_test_power_max", "");
		String testTime = sharePrefs.getString("power_test_time", "");
		

		testMinWork = Integer.parseInt(minWork);
		testMaxWork = Integer.parseInt(maxWork);
		testTimeout = Integer.parseInt(testTime);
		
	}
	private void uiInit() {
		ctlMsgTv = (TextView) findViewById(R.id.ctlMsg_tv);
		timeoutTv = (TextView) findViewById(R.id.timeout_tv);
		testStateTv = (TextView) findViewById(R.id.testState_tv);
		llayout = (LinearLayout) findViewById(R.id.linearLayout1);
		returnBtn = (Button) findViewById(R.id.ret_btn);

		llayout.setBackgroundColor(Color.YELLOW);
		ctlMsgTv.append("\n请设置电磁炉功率为" + testMinWork + "W");
		returnBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Sub1Activity.this.finish();
			}
		});
		setDisplayTimeout(testTimeout, timeoutTv);
	}
	ServiceConnection conn = new ServiceConnection() {		
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
		}
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder binder) {
			hwService = (HardwaveBinder) binder;
			
			hwService.setBoostParam(0, testMinWork, testMaxWork);
			hwService.openBoost();			
			hwService.setInverterParam(0, workingVol);
			hwService.openInverter();
		}
	};
	private void closeHwService(){
		if(hwService == null) return;
		hwService.closeBoost();
		hwService.closeInverter();
		unbindService(conn);
	}

	@Override
	protected void onPause() {
		closeDisplayTimeout();
		closeHwService();
		super.onPause();
	}

	void testFinish() {

		ctlMsgTv.setText("");
		testStateTv.setText("测试结束（");
		llayout.setBackgroundColor(Color.GREEN);
		returnBtn.setEnabled(false);
		
		
		hwService.getBoostParam(new OnUICallback() {			
			@Override
			public void callback(Object obj) {
				BoostHW boost = (BoostHW) obj;	
				ctlMsgTv.append("检测功率:" + (boost.args[0] * boost.args[1]) + "W\n");
			}
		});
		hwService.getInverterParam(new OnUICallback() {			
			@Override
			public void callback(Object obj) {
				InverterHW inverter = (InverterHW) obj;
				ctlMsgTv.append("输入功率:" + inverter.args[2] + "W\n");
				ctlMsgTv.append("测试结果:产品合格");
				
				TestRecordService recordService = TestRecordService.getInstance();
				recordService.recordPowerTest(Sub1Activity.this, testMinWork, inverter.args[2], true);
			}
		});
		

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				finish();
			}
		}, 8000);
	}

	Handler handler = new Handler();
	Runnable displayTimeoutRun;

	void setDisplayTimeout(final int timeout_ms, final TextView count_tv) {
		displayTimeoutRun = new Runnable() {
			int i = timeout_ms;

			@Override
			public void run() {
				if (i >= 0) {
					handler.postDelayed(this, 1000);
					count_tv.setText(Integer.toString(i--));
				} else {
					testFinish();
				}
			}
		};
		handler.postDelayed(displayTimeoutRun, 1000);
	}

	void closeDisplayTimeout() {
		handler.removeCallbacks(displayTimeoutRun);
	}
}
