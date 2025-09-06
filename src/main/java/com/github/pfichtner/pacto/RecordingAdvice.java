package com.github.pfichtner.pacto;

import static com.github.pfichtner.pacto.util.Reflections.copyFields;

import java.lang.reflect.Method;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

public class RecordingAdvice {

	public static final String FIELDNAME_RECORDER = "$pacto$recorder";
	public static final String FIELDNAME_DELEGATE = "$pacto$delegate";

	@Advice.OnMethodExit(onThrowable = Throwable.class)
	static void onExit(@Advice.Origin Method method, @Advice.AllArguments Object[] args,
			@Advice.Return(readOnly = false, typing = Assigner.Typing.DYNAMIC) Object returned,
			@Advice.This Object proxy, @Advice.FieldValue(FIELDNAME_DELEGATE) Object delegate,
			@Advice.FieldValue(FIELDNAME_RECORDER) Recorder recorder) throws IllegalAccessException {
		recorder.recordInterception(delegate, method, args, returned);

		if (returned != null //
				&& method.getReturnType().isAssignableFrom(delegate.getClass()) //
				&& returned == delegate) {
			returned = proxy;
		}

		copyFields(delegate, proxy);
	}

}
