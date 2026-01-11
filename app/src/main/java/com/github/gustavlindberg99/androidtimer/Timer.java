package com.github.gustavlindberg99.androidtimer;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.time.Duration;

/**
 * A simple timer class that can be used to schedule one-time or recurring tasks on the main thread.
 */
public final class Timer {
    private final @NonNull Handler _handler;
    private @Nullable Runnable _callback = null;

    /**
     * Creates a new timer. Does not start the timer, use `setTimeout` or `setInterval` for that.
     */
    public Timer() {
        this._handler = new Handler(Looper.getMainLooper());
    }

    /**
     * Schedules a one-time task to be executed after a specified delay.
     *
     * @param callback     The task to be executed.
     * @param milliseconds The delay in milliseconds.
     * @throws IllegalStateException if the timer is already running.
     */
    public void setTimeout(@NonNull Runnable callback, long milliseconds) {
        if (this.isRunning()) {
            throw new IllegalStateException("Timer is already running.");
        }
        this._callback = () -> {
            if (this._callback != null) {
                callback.run();
                this._callback = null;
            }
        };
        this._handler.postDelayed(this._callback, milliseconds);
    }

    /**
     * Schedules a one-time task to be executed after a specified delay. Only available in API 26 and above.
     *
     * @param callback The task to be executed.
     * @param duration The delay.
     * @throws IllegalStateException if the timer is already running.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setTimeout(@NonNull Runnable callback, @NonNull Duration duration) {
        this.setTimeout(callback, duration.toMillis());
    }

    /**
     * Schedules a recurring task to be executed with a specified interval. The task is first executed `milliseconds` milliseconds after this method is called.
     *
     * @param callback     The task to be executed.
     * @param milliseconds The interval in milliseconds.
     * @throws IllegalStateException if the timer is already running.
     */
    public void setInterval(@NonNull Runnable callback, long milliseconds) {
        if (this.isRunning()) {
            throw new IllegalStateException("Timer is already running.");
        }
        this._callback = () -> {
            if (this._callback != null) {
                callback.run();
                this._handler.postDelayed(this._callback, milliseconds);
            }
        };
        this._handler.postDelayed(this._callback, milliseconds);
    }

    /**
     * Schedules a recurring task to be executed with a specified interval. The task is first executed `duration` after this method is called. Only available in API 26 and above.
     *
     * @param callback The task to be executed.
     * @param duration The interval.
     * @throws IllegalStateException if the timer is already running.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setInterval(@NonNull Runnable callback, @NonNull Duration duration) {
        this.setInterval(callback, duration.toMillis());
    }

    /**
     * Stops the timer, cancelling any pending tasks. Does nothing if the timer is already stopped.
     */
    public void stop() {
        if (this._callback != null) {
            this._handler.removeCallbacks(this._callback);
            this._callback = null;
        }
    }

    /**
     * Checks if the timer is running, i.e. if a callback will be executed if we wait long enough.
     *
     * @return True if the timer is running, false otherwise.
     */
    public boolean isRunning() {
        return this._callback != null;
    }
}