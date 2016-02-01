package jjwork.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import jjwork.modbus.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


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
	
	public static Context errorContext;
	public static void recordError(byte[] sendData, Exception ex, byte[] recvData){
		if(errorContext == null) return;
		TestDataDBHelper dbHelper = new TestDataDBHelper(errorContext);
	    SQLiteDatabase db =	dbHelper.getWritableDatabase();

	    ContentValues cv = new ContentValues();
	    SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss"); 
	    Date curDate = new Date(System.currentTimeMillis());//获取当前时间 
	    cv.put("data_time", formatter.format(curDate));
	    
	    if(sendData != null)
	    	cv.put("send_data", Utils.printBytes(sendData));
	    if(recvData != null)
	    	cv.put("recv_data", Utils.printBytes(recvData));
	    
	    cv.put("exception", ex.toString());
	    
	    db.insert("comm_err", null, cv);
	    db.close();
	}
	
	public static void ExportToCSV(Context context, String StorageDirectory) {

		int rowCount = 0;
		int colCount = 0;
		FileWriter fw;
		BufferedWriter bfw;
		File sdCardDir = new File(StorageDirectory);
		File saveFile = new File(sdCardDir, "test.csv");
		TestDataDBHelper dbHelper = new TestDataDBHelper(context);
	    SQLiteDatabase db =	dbHelper.getWritableDatabase();
		Cursor c = db.rawQuery("select * from power_test", null);
		try {

			rowCount = c.getCount();
			colCount = c.getColumnCount();
			fw = new FileWriter(saveFile);
			bfw = new BufferedWriter(fw);
			Log.d("Record", "row="+rowCount+" col="+colCount);
			if (rowCount > 0) {
				c.moveToFirst();
				// 写入表头
				for (int i = 0; i < colCount; i++) {
					if (i != colCount - 1)
					   bfw.write(c.getColumnName(i) + ',');
					else
					   bfw.write(c.getColumnName(i));					
				}
				// 写好表头后换行
				bfw.newLine();
				// 写入数据
				for (int i = 0; i < rowCount; i++) {
					c.moveToPosition(i);
					// Toast.makeText(mContext, "正在导出第"+(i+1)+"条",
					// Toast.LENGTH_SHORT).show();
					Log.v("Record", "正在导出第" + (i + 1) + "条");
					for (int j = 0; j < colCount; j++) {
						if(c.getString(j).equals("null")){
							if (j != colCount - 1)
								bfw.write(",");
						}
						else{
							if (j != colCount - 1)
								bfw.write(c.getString(j) + ',');
							else
								bfw.write(c.getString(j));
						}
					}
					// 写好每条记录后换行
					bfw.newLine();
				}
			}
			// 将缓存数据写入文件
			bfw.flush();
			// 释放缓存
			bfw.close();
			// Toast.makeText(mContext, "导出完毕！", Toast.LENGTH_SHORT).show();
			Log.v("Record", "导出完毕！");
		} catch (IOException e) {
			Log.d("Record", e.toString());
		} finally {
			c.close();
		}
	}
}
