package jp.gr.java_conf.genzo.android.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.Stack;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Closeableのコンテナ.
 * 
 * Closeableまたはクローズしたい対象をスタックしておき、{@link #close()}でクローズする。
 * 
 */
public class Closer {

	private final Stack<Closeable> mTargets = new Stack<Closeable>();

	/**
	 * Closeableの登録.
	 * 
	 * @param closeable
	 *            対象
	 * @return
	 * @return 入力と同じ
	 */
	public <T extends Closeable> T put(T closeable) {
		if (closeable == null) {
			return closeable;
		}
		mTargets.push(closeable);
		return closeable;
	}

	/**
	 * SQLiteDatabaseの登録.
	 * 
	 * @param db
	 *            対象
	 * @return 入力と同じ
	 */
	public SQLiteDatabase putDb(SQLiteDatabase db) {
		if (db == null) {
			return db;
		}
		mTargets.push(new SQLiteDatabaseCloser(db));
		return db;
	}

	/**
	 * SQLiteDatabaseの登録.
	 * 
	 * @param helper
	 *            対象
	 * @return 入力と同じ
	 */
	public SQLiteOpenHelper putDbHelper(SQLiteOpenHelper helper) {
		if (helper == null) {
			return helper;
		}
		mTargets.push(new SQLiteOpenHelperCloser(helper));
		return helper;
	}

	/**
	 * クローズ.
	 * 
	 * 登録と逆順にクローズする.
	 */
	public void close() {
		while (!mTargets.empty()) {
			try {
				mTargets.pop().close();
			} catch (IOException e) {
				Log.w("Closer", "Closer#close error!!", e);
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		if (!mTargets.isEmpty()) {
			Log.w("Closer", "Closer#close not call!!");
			close();
		}
		super.finalize();
	}

	/**
	 * SQLiteDatabaseのラッパー.
	 */
	private class SQLiteDatabaseCloser implements Closeable {
		private final SQLiteDatabase mTarget;

		SQLiteDatabaseCloser(SQLiteDatabase target) {
			mTarget = target;
		}

		@Override
		public void close() throws IOException {
			mTarget.close();
		}
	}

	/**
	 * SQLiteOpenHelperのラッパー.
	 */
	private class SQLiteOpenHelperCloser implements Closeable {
		private final SQLiteOpenHelper mTarget;

		SQLiteOpenHelperCloser(SQLiteOpenHelper target) {
			mTarget = target;
		}

		@Override
		public void close() throws IOException {
			mTarget.close();
		}
	}

}
