package com.github.pfichtner.pacto.matchers;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.mockito.internal.progress.ThreadSafeMockingProgress.mockingProgress;

import java.lang.annotation.Retention;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.github.pfichtner.pacto.matchers.PostCleanMatcherStack.PostCleanMatcherStackExtension;

@Retention(RUNTIME)
@ExtendWith(PostCleanMatcherStackExtension.class)
public @interface PostCleanMatcherStack {

	static class PostCleanMatcherStackExtension implements AfterEachCallback {

		@Override
		public void afterEach(ExtensionContext context) throws Exception {
			mockingProgress().getArgumentMatcherStorage().reset();
		}

	}

}
