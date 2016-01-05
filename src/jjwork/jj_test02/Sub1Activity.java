package jjwork.jj_test02;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import jjwork.ctlModules.*;

public class Sub1Activity extends Activity {

	private BoostHW boostHW;
	private InverterHW inverterHW;
	
	TextView ctlMsgTv, timeoutTv, testStateTv;
	LinearLayout llayout;
	Button returnBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sub1);

		SharedPreferences sharePrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String minWork = sharePrefs.getString("power_test_power_min", "");
		String maxWork = sharePrefs.getString("power_test_power_max", "");
		String testTime = sharePrefs.getString("power_test_time", "");

		ctlMsgTv = (TextView) findViewById(R.id.ctlMsg_tv);
		timeoutTv = (TextView) findViewById(R.id.timeout_tv);
		testStateTv = (TextView) findViewById(R.id.testState_tv);
		llayout = (LinearLayout) findViewById(R.id.linearLayout1);
		returnBtn = (Button) findViewById(R.id.ret_btn);

		llayout.setBackgroundColor(Color.YELLOW);
		ctlMsgTv.append("\n请设置电磁炉功率为" + minWork + "W");
		returnBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Sub1Activity.this.finish();
			}
		});
		setDisplayTimeout(Integer.parseInt(testTime), timeoutTv);

		int testWork = 0;
		try {
			testWork = Integer.parseInt(minWork);
		} catch (Exception ex) {
		}

		int workingVol = 220;
		boostHW = BoostHW.getInstance();
		inverterHW = InverterHW.getInstance();

		boostHW.open();
		boostHW.setParam(BoostHW.TestMode.workTest, testWork);
		inverterHW.open();
		inverterHW.setInputVoltage(workingVol);
	}


	@Override
	protected void onPause() {
		closeDisplayTimeout();
		boostHW.close();
		inverterHW.close();
		super.onPause();
	}
	void testFinish(){
		BoostHW.BHWState bstate = boostHW.getState();
		InverterHW.IHWState istate = inverterHW.getState();
		
		StringBuilder sb = new StringBuilder();
		sb.append("测试结果:产品合格\n");
		sb.append("检测功率:" + (bstate.voltage * bstate.current) + "W\n");
		sb.append("输入功率:" + istate.outputWork + "W");
		ctlMsgTv.setText(sb.toString());
		testStateTv.setText("测试结束（");
		llayout.setBackgroundColor(Color.GREEN);
		returnBtn.setEnabled(false);
		
		handler.postDelayed(new Runnable() {			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				finish();
			}
		}, 5000);
	}
	Handler handler;
	Runnable displayTimeoutRun;
	void setDisplayTimeout(final int timeout_ms, final TextView count_tv) {
		handler = new Handler();
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
