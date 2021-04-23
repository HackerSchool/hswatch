package com.hswatch.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.hswatch.refactor.MainServico;

public class HoraWorker extends Worker {

    public HoraWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

    }

    @NonNull
    @Override
    public Result doWork() {
        try{
            MainServico.sendTime();
            return Result.success();
        } catch (Throwable throwable){
            throwable.printStackTrace();
            return Result.failure();
        }
    }
}
