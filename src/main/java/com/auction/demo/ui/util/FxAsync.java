package com.auction.demo.ui.util;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

import javafx.concurrent.Task;

public final class FxAsync {

	private FxAsync() {
	}

	public static <T> void run(Callable<T> call, Consumer<T> onSuccess, Consumer<Throwable> onError) {
		Task<T> task = new Task<>() {
			@Override
			protected T call() throws Exception {
				return call.call();
			}
		};
		task.setOnSucceeded(e -> onSuccess.accept(task.getValue()));
		task.setOnFailed(e -> {
			Throwable ex = task.getException();
			onError.accept(ex.getCause() == null ? ex : ex.getCause());
		});
		Thread thread = new Thread(task, "ui-api");
		thread.setDaemon(true);
		thread.start();
	}
}
