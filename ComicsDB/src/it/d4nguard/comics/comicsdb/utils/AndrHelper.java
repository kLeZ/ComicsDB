package it.d4nguard.comics.comicsdb.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class AndrHelper
{
	public static ProgressDialog showProgressDialog(Context mContext, CharSequence processMessage)
	{
		ProgressDialog pDlg = new ProgressDialog(mContext);
		pDlg.setMessage(processMessage);
		pDlg.setProgressDrawable(WallpaperManager.getInstance(mContext).getDrawable());
		pDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDlg.setCancelable(false);
		return pDlg;
	}

	public void hideKeyboard(Activity caller)
	{
		InputMethodManager inputManager = (InputMethodManager) caller.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(caller.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
}
