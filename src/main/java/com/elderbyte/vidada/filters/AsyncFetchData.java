package com.elderbyte.vidada.filters;

import archimedes.core.events.EventArgsG;
import archimedes.core.events.EventHandlerEx;
import archimedes.core.events.IEvent;
import archimedes.core.concurrent.CancelableTask;
import archimedes.core.concurrent.CancellationTokenSource.CancellationToken;
import archimedes.core.concurrent.CancellationTokenSource.OperationCanceledException;

/**
 * Represents a cancelable task which executes a query
 * @author IsNull
 *
 * @param <T>
 */
public abstract class AsyncFetchData<T> extends CancelableTask<T>{


	public static class CancelTokenEventArgs<T>  extends EventArgsG<T>
	{
		private final CancellationToken token;

		public CancelTokenEventArgs(T val, CancellationToken token) {
			super(val);
			this.token = token;
		}

		public CancellationToken getToken() {
			return token;
		}
	}

	private EventHandlerEx<CancelTokenEventArgs<T>> fetchingCompleteEvent = new  EventHandlerEx<CancelTokenEventArgs<T>>();

	/**
	 * Raised when the fetching of the data has been completed
	 * @return
	 */
	public IEvent<CancelTokenEventArgs<T>> getFetchingCompleteEvent(){return fetchingCompleteEvent; }

	public AsyncFetchData(CancellationToken token){
		super(token);
	}


	@Override
	public T runCancelable(CancellationToken token) throws OperationCanceledException {

		T fetchedData = null;

		token.ThrowIfCancellationRequested();

		if(!token.isCancellationRequested())
		{
			try {
				fetchedData = fetchData(token);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		fetchingCompleteEvent.fireEvent(this, new CancelTokenEventArgs<T>(fetchedData, token));
		return fetchedData;
	}

	protected abstract T fetchData(CancellationToken token) throws OperationCanceledException;
}
