package org.ayfaar.app.utils;

import org.ayfaar.app.controllers.search.cache.DBCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class Scheduler {
    @Autowired
    private DBCache dbCache;

    /**
     * при старте приложения ждёт до 3-х часов ночи и вызывает updateCache(ScheduledExecutorService scheduler)
     */
    @PostConstruct
    public void updateCache() {
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                updateCache(scheduler);
            }
        }, getDelay(), TimeUnit.MILLISECONDS);

    }

    /**
     * выполняет обновление кеша раз в сутки
     */
    private void updateCache(ScheduledExecutorService scheduler) {
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                dbCache.update();
            }
        }, 0, 24, TimeUnit.HOURS);
    }

    private int getDelay() {
        int oneDay = 1000*60*60*24;
        int threeHours = 1000*60*60*3;

        Calendar currentDate = Calendar.getInstance();

        int offset = currentDate.getTimeZone().getRawOffset();
        long today = currentDate.getTimeInMillis() % oneDay;
        int delay = oneDay - (int)today + threeHours - offset;

        return delay;
    }
}
