package jjwork.tools;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public class TestRecordService{
	private TestRecordService(){}
	private static TestRecordService service = new TestRecordService();
	public static TestRecordService getInstance(){ return service; }
	
	public void recordPowerTest(Context context, double targetWork, double retWork, boolean isSucess){
		TestDataDBHelper dbHelper = new TestDataDBHelper(context);
	    SQLiteDatabase db =	dbHelper.getWritableDatabase();
	    ContentValues cv = new ContentValues();
	    cv.put("test_completed", true);
	    cv.put("target_power", targetWork);
	    cv.put("boost_power", retWork);
	    cv.put("test_result", isSucess);
	    
	    SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss"); 
	    Date curDate = new Date(System.currentTimeMillis());//获取当前时间 
	    cv.put("data_time", formatter.format(curDate));
	    
	    db.insert("power_test", null, cv);
	    db.close();
	}
}
