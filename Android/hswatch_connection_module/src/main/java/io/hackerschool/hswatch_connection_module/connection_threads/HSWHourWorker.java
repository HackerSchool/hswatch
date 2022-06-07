package io.hackerschool.hswatch_connection_module.connection_threads;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import io.hackerschool.hswatch_connection_module.HSWService;

public class HSWHourWorker extends Worker {

    public HSWHourWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            HSWService.sendTime();
            return Result.success();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return Result.failure();
        }
    }
}
