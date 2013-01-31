package it.d4nguard.comics.comicsdb.logic;

import it.d4nguard.comics.beans.bo.Comics;
import it.d4nguard.comics.comicsdb.utils.AndrHelper;
import it.d4nguard.comics.comicsdb.utils.WebClientUtils;
import it.d4nguard.michelle.utils.web.ComicsUtils;
import it.d4nguard.michelle.utils.web.WebUtils;

import java.io.InputStream;
import java.net.URI;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

/**
 * @author kLeZ-hAcK
 */
public class FetchComicsTask extends AsyncTask<URI, Integer, Comics>
{
	public interface FetchComicsTaskListener
	{
		FetchComicsTask getParent();
	}

	public interface OnProgressUpdateListener extends FetchComicsTaskListener
	{
		void doProgressUpdate(Integer... values);
	}

	public interface OnSomeActionListener extends FetchComicsTaskListener
	{
		void doSomeAction();
	}

	public interface OnSomeActionWithResultListener extends FetchComicsTaskListener
	{
		void doSomeActionWithResult(Comics result);
	}

	public static final int HTTPCLIENT_TYPE = 1;
	public static final int HTTPURL_TYPE = 2;

	private static final String TAG = "it.d4nguard.comics.comicsdb.logic.FetchComicsTask";

	private int taskType = WebClientUtils.GET_TASK;
	private int httpRequestType = HTTPURL_TYPE;
	private Context mContext = null;
	private String processMessage = "Processing...";

	private ProgressDialog pDlg = null;
	private Activity caller = null;

	private OnSomeActionListener cancelled;
	private OnSomeActionWithResultListener cancelledWithResult;
	private OnSomeActionWithResultListener postExecute;
	private OnSomeActionListener preExecute;
	private OnProgressUpdateListener progressUpdate;

	public FetchComicsTask(int taskType, int httpRequestType, Context mContext, Activity caller, String processMessage)
	{
		this.taskType = taskType;
		this.httpRequestType = httpRequestType;
		this.mContext = mContext;
		this.processMessage = processMessage;
		this.caller = caller;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		pDlg = AndrHelper.showProgressDialog(mContext, processMessage);
		if (pDlg != null)
		{
			pDlg.show();
		}
		if (preExecute != null)
		{
			preExecute.doSomeAction();
		}
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Comics doInBackground(URI... params)
	{
		Comics comics = new Comics();
		for (int i = 0; i < params.length; i++)
		{
			if (isCancelled())
			{
				break;
			}

			URI uri = params[i];
			Log.d(TAG, WebUtils.uriToString(uri));

			InputStream ws_response = null;

			switch (httpRequestType)
			{
				case HTTPCLIENT_TYPE:
					ws_response = WebClientUtils.executeRequestWithHttpClient(uri, taskType);
					break;
				case HTTPURL_TYPE:
					ws_response = WebClientUtils.executeRequestWithURLConnection(uri);
					break;
				default:
					ws_response = WebClientUtils.executeRequestWithHttpClient(uri, taskType);
					break;
			}

			Comics current = new Comics();
			if (ws_response != null)
			{
				current = ComicsUtils.getComicsFromJson(WebClientUtils.inputStreamToString(ws_response), true);
			}
			comics.addAll(current);
			publishProgress(current.size(), comics.size(), i + 1, params.length);
		}
		return comics;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
	 */
	@Override
	protected void onProgressUpdate(Integer... values)
	{
		super.onProgressUpdate(values);
		if (progressUpdate != null)
		{
			progressUpdate.doProgressUpdate(values);
		}
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onCancelled()
	 */
	@Override
	protected void onCancelled()
	{
		super.onCancelled();
		if (pDlg != null)
		{
			pDlg.dismiss();
		}
		if (cancelled != null)
		{
			cancelled.doSomeAction();
		}
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onCancelled(java.lang.Object)
	 */
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void onCancelled(Comics result)
	{
		super.onCancelled(result);
		if (pDlg != null)
		{
			pDlg.dismiss();
		}
		if (cancelledWithResult != null)
		{
			cancelledWithResult.doSomeActionWithResult(result);
		}
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(Comics result)
	{
		super.onPostExecute(result);
		if (pDlg != null)
		{
			pDlg.dismiss();
		}
		if (postExecute != null)
		{
			postExecute.doSomeActionWithResult(result);
		}
	}

	public ProgressDialog getProgressDialog()
	{
		return pDlg;
	}

	public Activity getCaller()
	{
		return caller;
	}

	public void setOnCancelledListener(OnSomeActionListener cancelled)
	{
		this.cancelled = cancelled;
	}

	public void setOnCancelledWithResultListener(OnSomeActionWithResultListener cancelledWithResult)
	{
		this.cancelledWithResult = cancelledWithResult;
	}

	public void setOnPostExecuteListener(OnSomeActionWithResultListener postExecute)
	{
		this.postExecute = postExecute;
	}

	public void setOnPreExecuteListener(OnSomeActionListener preExecute)
	{
		this.preExecute = preExecute;
	}

	public void setOnProgressUpdateListener(OnProgressUpdateListener progressUpdate)
	{
		this.progressUpdate = progressUpdate;
	}
}
