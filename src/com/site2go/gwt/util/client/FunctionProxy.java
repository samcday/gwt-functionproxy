package com.site2go.gwt.util.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * This overlay will relay a Javascript function object into a Java callback.
 * This means that each time this function is invoked, the execution will be
 * passed into a given handler in GWT java code. This provides us a convenient
 * way of passing in a scoped Java callback into a javascript lib.<br>
 * <br>
 * 
 * The most common use for this method is to create a bridge between event
 * handlers from native libs into our GWT code.
 * 
 * @author Sam
 * 
 */
public class FunctionProxy
	extends JavaScriptObject
{
	public static interface FunctionHandler
	{
		public Object onFunctionCall(FunctionProxy func, FunctionArguments args);
	}
	
	public static class FunctionArguments
		extends JavaScriptObject
	{
		protected FunctionArguments() { }

		/**
		 * Gets argument at specified index.
		 */
		public final native <T> T getArg(int index)
		/*-{
			if(index >= this.args.length)
				throw @java.lang.IndexOutOfBoundsException::new()();
			return $wnd._gwtMarshalTypeFromJS(this.args[index]);
		}-*/;

		public final native int getArgCount() /*-{
			return this.args.length;
		}-*/;
	}

	/**
	 * Primary way of creating a function wrapper.
	 * 
	 * @param handler
	 * @return
	 */
	public static final FunctionProxy create(FunctionHandler handler)
	{
		TypeMarshaller.init();
		return createImpl(handler);
	}

	public static final native FunctionProxy createImpl(FunctionHandler handler)
	/*-{
		var f = function() {
			var me = arguments.callee;

			// Save the arguments into ourselves so the call() proxy can reference them as needed.
			var args = {};
			args.args = [];
			for(var i = 0; i < arguments.length; i++)
			{
				args.args[i] = arguments[i];
			}

			// Call the proxy.
			try
			{
				var ret = handler.@com.site2go.gwt.util.client.FunctionProxy.FunctionHandler::onFunctionCall(Lcom/site2go/gwt/util/client/FunctionProxy;Lcom/site2go/gwt/util/client/FunctionProxy$FunctionArguments;)(me, args);
				ret = $wnd._gwtMarshalTypeFromJava(ret);
				return ret;
			}
			catch(e)
			{
				var gwtExceptionHandler = @com.google.gwt.core.client.GWT::getUncaughtExceptionHandler()();
				if(gwtExceptionHandler)
				{
					if(!@com.site2go.gwt.util.client.FunctionProxy::isThrowableAlready(Ljava/lang/Object;)(e))
						e = @com.google.gwt.core.client.JavaScriptException::new(Ljava/lang/Object;)(e);

					gwtExceptionHandler.@com.google.gwt.core.client.GWT.UncaughtExceptionHandler::onUncaughtException(Ljava/lang/Throwable;)(e);
				}
			}
		};

		return f;
	}-*/;

	private static final boolean isThrowableAlready(Object jso)
	{
		return (jso instanceof Throwable);
	}

	/**
	 * Convenience method, creates the function wrapper then sets it as a property on a JSO.
	 * 
	 * @param handler
	 * @param el
	 * @param property
	 * @return
	 */
	public static final FunctionProxy createAndAttach(FunctionHandler handler,
			JavaScriptObject jso, String property)
	{
		FunctionProxy wrapper = FunctionProxy.create(handler);
		FunctionProxy.attach(jso, property, wrapper);
		return wrapper;
	}

	private static final native void attach(JavaScriptObject jso, String property,
			FunctionProxy wrapper)
	/*-{
		jso[property] = wrapper;
	}-*/;

	protected FunctionProxy() {}
}
