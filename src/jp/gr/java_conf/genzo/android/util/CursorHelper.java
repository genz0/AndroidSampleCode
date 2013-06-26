package jp.gr.java_conf.genzo.android.util;

import android.database.Cursor;

/**
 * カーソルのヘルパー.
 * 
 */
public class CursorHelper {

	/** 初回フラグ. */
	private boolean mIsFirst = true;

	/** 操作対象のカーソル. */
	private final Cursor mCursor;

	/**
	 * 次有無判定
	 * 
	 * @return true:次あり false:次なし
	 */
	public boolean hasNext() {
		if (!mIsFirst) {
			// ２回目以降
			return mCursor.moveToNext();
		}

		mIsFirst = false;
		// 初回
		return mCursor.moveToFirst();
	}

	/**
	 * コンストラクタ.
	 * 
	 * @param cursor
	 *            操作対象のカーソル
	 */
	public CursorHelper(Cursor cursor) {
		mCursor = cursor;
	}
}
