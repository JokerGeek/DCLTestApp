package jjwork.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class TestDataDBHelper extends SQLiteOpenHelper {
	class TestTable{
		public TestTable(String table_name) {
			this.table_name = table_name;
		}
		public String table_name;
		private Map<String, String> testDatas = new HashMap<String, String>();
		public void put(String key, String value){
			testDatas.put(key, value);
		}
		
		public String toSQL(){
			StringBuffer sb = new StringBuffer();
			sb.append("CREATE TABLE " + table_name + "("
					+ "id INTEGER primary key autoincrement, "
					+ "data_time VARCHAR(20), "
					+ "test_completed INTEGER, "
					+ "test_error VARCHAR(20),");
			for(Entry<String, String> data : testDatas.entrySet()){
				sb.append(data.getKey() + " " + data.getValue() + ",");
			}
			sb.append("test_result INTEGER)");
			return sb.toString();
		}
	}
	public static final int VERSION = 1;  
	public static final String DATABASE_NAME = "dcl_test";
	public TestDataDBHelper(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {		
		TestTable powerTest = new TestTable("power_test");
		powerTest.put("target_power", "DOUBLE");
		powerTest.put("boost_power", "DOUBLE");
		powerTest.put("inverter_power", "DOUBLE");
		db.execSQL(powerTest.toSQL());		
		
		TestTable bigObjTest = new TestTable("big_obj_test");
		bigObjTest.put("setting_res", "DOUBLE");
		db.execSQL(bigObjTest.toSQL());
		
		TestTable smallObjTest = new TestTable("small_obj_test");
		smallObjTest.put("setting_res", "DOUBLE");
		db.execSQL(smallObjTest.toSQL());
		
		TestTable volFluTest = new TestTable("vol_flu_test");
		volFluTest.put("max_vol", "DOUBLE");
		volFluTest.put("min_vol", "DOUBLE");
		volFluTest.put("vol_flu_num", "INTEGER");
		db.execSQL(volFluTest.toSQL());
		
		TestTable hVolProtectTest = new TestTable("high_vol_protect_test");
		hVolProtectTest.put("voltage", "DOUBLE");
		db.execSQL(hVolProtectTest.toSQL());
		
		TestTable lVolProtectTest = new TestTable("low_vol_protect_test");
		lVolProtectTest.put("voltage", "DOUBLE");
		db.execSQL(lVolProtectTest.toSQL());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
