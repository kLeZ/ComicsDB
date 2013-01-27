package it.d4nguard.comics.comicsdb.logic;

import it.d4nguard.comics.beans.bo.Comics;
import it.d4nguard.michelle.utils.web.ComicsUtils;
import it.d4nguard.michelle.utils.web.WebUtils;

import java.net.URI;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

/**
 * @author kLeZ-hAcK
 */
public class FetchComicsTask extends AsyncTask<URI, Integer, Comics>
{
	public interface OnProgressUpdateListener
	{
		void doProgressUpdate(Integer... values);
	}

	public interface OnSomeActionListener
	{
		void doSomeAction();
	}

	public interface OnSomeActionWithResultListener
	{
		void doSomeActionWithResult(Comics result);
	}

	private OnSomeActionListener cancelled;
	private OnSomeActionWithResultListener cancelledWithResult;
	private OnSomeActionWithResultListener postExecute;
	private OnSomeActionListener preExecute;
	private OnProgressUpdateListener progressUpdate;

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
			String ws_response = WebUtils.excuteGet(uri.toASCIIString());
			Comics current = ComicsUtils.getComicsFromJson(ws_response, true);
			comics.addAll(current);
			publishProgress(current.size(), comics.size(), i + 1, params.length);
		}
		return comics;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onCancelled()
	 */
	@Override
	protected void onCancelled()
	{
		super.onCancelled();
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
		if (postExecute != null)
		{
			postExecute.doSomeActionWithResult(result);
		}
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		if (preExecute != null)
		{
			preExecute.doSomeAction();
		}
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
