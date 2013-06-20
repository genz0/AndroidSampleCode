package jp.gr.java_conf.genzo.android.sample;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import jp.gr.java_conf.genzo.android.util.Closer;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void onButton1(View view) {

		byte[] b = "あいうえお".getBytes(Charset.defaultCharset());

		// ヘルパークラスのインスタンスを用意する
		Closer closer = new Closer();

		try {
			// Closeable系のリソースのインスタンスを生成するとき、Closerを経由する
			InputStream in = closer.put(new ByteArrayInputStream(b));
			Writer w = closer.put(new StringWriter());

			byte[] buf = new byte[128];
			in.read(buf);
			w.write(new String(buf, Charset.defaultCharset()));

			// DBOpenHelperはCloseableではないので専用メソッド経由
			SQLiteOpenHelper helper = closer
					.putDbHelper(new DBOpenHelper(this));
			// SQLiteDatabaseはCloseableではないので専用メソッド経由
			SQLiteDatabase db = closer.putDb(helper.getReadableDatabase());
			Cursor cursor = closer.put(db.rawQuery("select * from TEST_TABLE",
					new String[] {}));
			if (cursor.moveToFirst()) {
				// 処理
			}

		} catch (IOException e) {
			Log.e("closer", "error!!", e);
		} finally {
			// 内包するリソースを開放する
			closer.close();
		}
	}

	public void onButton1x(View view) {

		byte[] b = "あいうえお".getBytes(Charset.defaultCharset());

		InputStream in = null;
		Writer w = null;

		SQLiteOpenHelper helper = null;
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			in = new ByteArrayInputStream(b);
			w = new StringWriter();

			byte[] buf = new byte[128];
			in.read(buf);
			w.write(new String(buf, Charset.defaultCharset()));

			helper = new DBOpenHelper(this);
			db = helper.getReadableDatabase();
			cursor = db.rawQuery("select * from TEST_TABLE", new String[] {});
			if (cursor.moveToFirst()) {
				// 処理
			}

		} catch (IOException e) {
			Log.e("closer", "error!!", e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (db != null) {
				db.close();
			}
			if (helper != null) {
				helper.close();
			}
			if (w != null) {
				try {
					w.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

	static class DBOpenHelper extends SQLiteOpenHelper {

		public DBOpenHelper(Context context) {
			super(context, "test.db", null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// テーブルの生成
			db.execSQL("CREATE TABLE TEST_TABLE(id INTEGER PRIMARY KEY, memo TEXT NOT NULL, latitude REAL, longitude REAL);");

			// 初期データの投入
			db.execSQL("INSERT INTO TEST_TABLE(memo) values('hoge1')");
			db.execSQL("INSERT INTO TEST_TABLE(memo) values('hoge2')");
			db.execSQL("INSERT INTO TEST_TABLE(memo) values('hoge3')");
			db.execSQL("INSERT INTO TEST_TABLE(memo) values('hoge4')");
			db.execSQL("INSERT INTO TEST_TABLE(memo) values('hoge5')");
			db.execSQL("INSERT INTO TEST_TABLE(memo) values('hoge6')");
			db.execSQL("INSERT INTO TEST_TABLE(memo) values('hoge7')");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// nop
		}
	}

	public void onButton2(View view) {

		// DBをオープンして、簡単なクエリーを投げる
		SQLiteOpenHelper helper = new DBOpenHelper(this);
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db
				.rawQuery("select * from TEST_TABLE", new String[] {});

		// 方式1：forを使ったループ
		for (boolean next = cursor.moveToFirst(); next; next = cursor
				.moveToNext()) {
			// ・・・処理
			Log.d("sql", "code1 data=" + cursor.getString(1));
		}

		// 方式2：whileを使ったループ
		boolean next = cursor.moveToFirst();
		while (next) {
			// ・・・処理
			Log.d("sql", "code2 data=" + cursor.getString(1));

			// 次のレコード
			next = cursor.moveToNext();
		}

		cursor.close();
		db.close();
		helper.close();

	}

}
