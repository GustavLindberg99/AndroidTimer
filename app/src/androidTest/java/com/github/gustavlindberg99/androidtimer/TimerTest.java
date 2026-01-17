package com.github.gustavlindberg99.androidtimer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(AndroidJUnit4.class)
public final class TimerTest {
    @Test
    public void testSetTimeout() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(2);
        final AtomicInteger numberOfRuns = new AtomicInteger(0);
        final Timer timer = new Timer();

        assertFalse("An empty timer should not be running",
                timer.isRunning());

        timer.setTimeout(() -> {
            numberOfRuns.set(numberOfRuns.get() + 1);
            latch.countDown();
        }, Duration.ofSeconds(1));

        assertEquals("The timer should not have fired yet.",
                0, numberOfRuns.get());
        assertTrue("The timer should be running.",
                timer.isRunning());

        //Wait for the latch to be counted down by the timer's callback. This will wait for max 5 seconds.
        final boolean timedOut = !latch.await(5, TimeUnit.SECONDS);

        assertTrue("The latch should have timed out because the latch expects two countdowns but the callback should only be called once.",
                timedOut);
        assertEquals("The callback should have been executed exactly once during the 5 seconds.",
                1, numberOfRuns.get());
        assertFalse("The timer should no longer be running.",
                timer.isRunning());
    }

    @Test
    public void testSetInterval() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(3);
        final AtomicInteger numberOfRuns = new AtomicInteger(0);
        final Timer timer = new Timer();

        assertFalse("An empty timer should not be running",
                timer.isRunning());

        timer.setInterval(() -> {
            numberOfRuns.set(numberOfRuns.get() + 1);
            latch.countDown();
        }, Duration.ofSeconds(1));

        assertEquals("The timer should not have fired yet.",
                0, numberOfRuns.get());
        assertTrue("The timer should be running.",
                timer.isRunning());

        //Wait for the latch to be counted down by the timer's callback. This will wait for max 5 seconds.
        final boolean timedOut = !latch.await(5, TimeUnit.SECONDS);

        assertFalse("The latch should not have timed out because the latch expects three countdowns which should have happened since we have 5 seconds.",
                timedOut);
        assertEquals("The callback should have been executed exactly three times during the 5 seconds (after the third time `latch.countDown()` becomes zero and the latch will exit).",
                3, numberOfRuns.get());
        assertTrue("The timer should still be running.",
                timer.isRunning());
    }

    @Test
    public void testStop() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(3);
        final AtomicInteger numberOfRuns = new AtomicInteger(0);
        final Timer timer = new Timer();

        assertFalse("An empty timer should not be running",
                timer.isRunning());

        timer.setInterval(() -> {
            numberOfRuns.set(numberOfRuns.get() + 1);
            if (numberOfRuns.get() >= 2) {
                timer.stop();
            }
            latch.countDown();
        }, Duration.ofSeconds(1));

        assertEquals("The timer should not have fired yet.",
                0, numberOfRuns.get());
        assertTrue("The timer should be running.",
                timer.isRunning());

        //Wait for the latch to be counted down by the timer's callback. This will wait for max 5 seconds.
        final boolean timedOut = !latch.await(5, TimeUnit.SECONDS);

        assertTrue("The latch should have timed out because the latch expects three countdowns but the callback should only be called twice.",
                timedOut);
        assertEquals("The callback should have been executed exactly twice times during the 5 seconds since the timer is stopped the third time.",
                2, numberOfRuns.get());
        assertFalse("The timer should no longer be running.",
                timer.isRunning());
    }

    @Test
    public void testAlreadyRunningExceptions() {
        final Timer timer = new Timer();
        final int oneSecond = 1000;

        timer.setTimeout(() -> {}, oneSecond);

        //Test that an exception is thrown when the timer is already running.
        assertThrows(
                IllegalStateException.class,
                () -> timer.setTimeout(() -> {}, oneSecond)
        );
        assertThrows(
                IllegalStateException.class,
                () -> timer.setInterval(() -> {}, oneSecond)
        );

        //Test that no exception is thrown when the timer is not running.
        timer.stop();
        timer.setTimeout(() -> {}, oneSecond);
        timer.stop();
        timer.setInterval(() -> {}, oneSecond);
    }
}