package com.github.pfichtner.dact;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

public class DelegateInterceptor {

	private final Object delegate;
	private final Recorder recorder;

	public DelegateInterceptor(Object delegate, Recorder recorder) {
		this.delegate = delegate;
		this.recorder = recorder;
	}

	@RuntimeType
	public Object intercept(@Origin Method method, @AllArguments Object[] args, @SuperCall Callable<Object> zuper)
			throws Exception {
		Object result = method.invoke(delegate, args);
		recorder.recordInterception(delegate, method, args, result);
		return method.getReturnType().isInstance(delegate) ? zuper.call() : result;
	}

	public Object getDelegate() {
		return delegate;
	}

	public Recorder getRecorder() {
		return recorder;
	}

}
