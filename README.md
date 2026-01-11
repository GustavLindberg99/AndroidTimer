# AndroidTimer

In Android, the recommended way of scheduling timer tasks is to use Android's `Handler` class rather than `java.util.Timer`, as `java.util.Timer` runs on its own thread. However, `Handler` doesn't provide any easy built-in way to schedule recurring tasks or to reliably cancel tasks (it's still doable, but requires a bit of extra code).

This library is an easy-to-use wrapper around Android's `Handler` class, which allows you to easily schedule one-time or recurring tasks on the main thread.

## Installation

To use this library in your Android project, add the following to `settings.gradle` (if not already done):

```gradle
dependencyResolutionManagement {
    //...
    repositories {
        //...
        maven { url 'https://jitpack.io' }
    }
}
```

And add the following to `app/build.gradle`:

```gradle
dependencies {
    //...
    implementation 'com.github.gustavlindberg99:androidtimer:1.0.0'
}
```

This library requires at least API version 21.

## Usage
This library contains the class `com.github.gustavlindberg99.androidtimer.Timer` which contains timer functionality.

### Example
Hello World example in Java:

```java
import com.github.gustavlindberg99.androidtimer.Timer;

//Schedule a callback to be run once after 10 seconds
Timer timer1 = new Timer();
timer.setTimeout(() -> System.out.println("Hello World!"), 10000);

//Schedule a callback to be run periodically every 10 seconds
Timer timer2 = new Timer();
timer.setInterval(() -> System.out.println("Hello World!"), 10000);
```

Hello World example in Kotlin:

```kotlin
import com.github.gustavlindberg99.androidtimer.Timer

//Schedule a callback to be run once after 10 seconds
val timer1 = Timer()
timer.setTimeout({println("Hello World!")}, 10000)

//Schedule a callback to be run periodically every 10 seconds
val timer2 = Timer()
timer.setInterval({println("Hello World!")}, 10000)
```

### Documentation for the `Timer` class
- `Timer()`

    Constructor, creates a new timer. Does not start the timer, use `setTimeout` or `setInterval` for that.

- `void setTimeout(@NonNull Runnable callback, long milliseconds)`

    Schedules a one-time task to be executed after a specified delay. Throws IllegalStateException if the timer is already running.

- `void setTimeout(@NonNull Runnable callback, @NonNull Duration duration)`

    Schedules a one-time task to be executed after a specified delay. Throws IllegalStateException if the timer is already running. This overload is only available in API 26 and above.

- `void setInterval(@NonNull Runnable callback, long milliseconds)`

    Schedules a recurring task to be executed with a specified interval. The task is first executed `interval` milliseconds after this method is called. Throws IllegalStateException if the timer is already running.

- `void setInterval(@NonNull Runnable callback, @NonNull Duration duration)`

    Schedules a recurring task to be executed with a specified interval. The task is first executed `duration` after this method is called. Throws IllegalStateException if the timer is already running. This overload is only available in API 26 and above.

- `void stop()`

    Stops the timer, cancelling any pending tasks. Does nothing if the timer is already stopped.

- `boolean isRunning()`

    Checks if the timer is running, i.e. if a callback will be executed if we wait long enough.
