package com.example.redditclone;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Lớp quản lý các luồng trong ứng dụng.
 * Sử dụng mẫu Singleton để đảm bảo chỉ có một instance duy nhất.
 * Cung cấp các Executor cho tác vụ nền (disk I/O) và tác vụ trên luồng chính (main thread).
 */
public class AppExecutors {

    // Hằng số cho số lượng luồng trong pool
    private static final int THREAD_COUNT = 3;

    // Biến instance duy nhất của lớp (Singleton pattern)
    private static AppExecutors sInstance;

    // Executor cho các tác vụ nền (đọc/ghi file, database, network)
    private final Executor diskIO;

    // Executor để chạy các tác vụ trên luồng chính (cập nhật UI)
    private final Executor mainThread;

    // Constructor riêng tư để ngăn việc tạo instance từ bên ngoài
    private AppExecutors(Executor diskIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.mainThread = mainThread;
    }

    // Phương thức tĩnh để lấy instance duy nhất của lớp
    public static synchronized AppExecutors getInstance() {
        if (sInstance == null) {
            sInstance = new AppExecutors(Executors.newFixedThreadPool(THREAD_COUNT),
                    new MainThreadExecutor());
        }
        return sInstance;
    }

    // Cung cấp Executor cho luồng nền
    public Executor diskIO() {
        return diskIO;
    }

    // Cung cấp Executor cho luồng chính
    public Executor mainThread() {
        return mainThread;
    }

    /**
     * Một Executor tùy chỉnh để đảm bảo các tác vụ được chạy trên Main Thread.
     */
    private static class MainThreadExecutor implements Executor {
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
