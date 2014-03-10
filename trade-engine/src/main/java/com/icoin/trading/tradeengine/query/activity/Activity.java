package com.icoin.trading.tradeengine.query.activity;

import com.homhon.base.domain.model.ValueObjectSupport;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static com.homhon.util.Asserts.isTrue;
import static com.homhon.util.Asserts.notNull;
import static com.homhon.util.Collections.isEmpty;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 2/26/14
 * Time: 3:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class Activity extends ValueObjectSupport<Activity> {
    private long maxArchiveInterval;
    private int maxSize;
    private List<ActivityItem> activityItems;

    public Activity(ActivityItem... activityItems) {
        this(5 * 366 * 3, Arrays.asList(activityItems));
    }

    public Activity(List<ActivityItem> activityItems) {
        this(5 * 366 * 3, activityItems);
    }

    public Activity(int maxSize, List<ActivityItem> activityItems) {
        this(5 * 365 * 24 * 60 * 60 * 1000L, maxSize, activityItems);
    }

    @PersistenceConstructor
    public Activity(long maxArchiveInterval, int maxSize, List<ActivityItem> activityItems) {
        isTrue(maxArchiveInterval > 0, "max archive interval should be greater than 0");
        isTrue(maxSize > 0, "max size should be greater than 0");
        this.maxArchiveInterval = maxArchiveInterval;
        this.maxSize = maxSize;
        this.activityItems = isEmpty(activityItems) ? new ArrayList<ActivityItem>() : new ArrayList<ActivityItem>(activityItems);
        sortActivitiesIfNecessary();
        archiveBySizeIfNecessary();
    }

    public long getMaxArchiveInterval() {
        return maxArchiveInterval;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public List<ActivityItem> getActivityItems() {
        return activityItems;
    }

    public Activity addItems(Date currentTime, ActivityItem... items) {
        notNull(currentTime);
        for (ActivityItem item : items) {
            if (item != null) {
                activityItems.add(item);
            }
        }
        sortActivitiesIfNecessary();
        archiveIfNecessary(currentTime);
        return this;
    }

    private Activity sortActivitiesIfNecessary() {
        Collections.sort(activityItems, new Comparator<ActivityItem>() {
            @Override
            public int compare(ActivityItem one, ActivityItem two) {
                return one.getTimestamp().compareTo(two.getTimestamp());
            }
        });

        return this;
    }

    // < toTime
    public ActivityItem nearestUpTo(Date toTime) {
        ActivityItem result = null;
        for (ActivityItem activityItem : activityItems) {
            if (!activityItem.getTimestamp().before(toTime)) {
                break;
            }
            result = activityItem;
        }

        return result;
    }

    //get item count with 24 hours
    public List<ActivityItem> itemsWithin24Hours(Date toTime) {
        return itemsWithin(toTime, new Period().withDays(1));
    }

    //get item count with 24 hours
    public List<ActivityItem> itemsWithin(Date toTime, Period period) {
        LocalDateTime to = new LocalDateTime(toTime);
        LocalDateTime from = to.minus(period);

        ArrayList<ActivityItem> items = new ArrayList<ActivityItem>(3);
        for (ActivityItem activityItem : activityItems) {
            if (activityItem.timestampWithin(from, to)) {
                items.add(activityItem);
            }
        }

        return items;
    }

    //get item count with 24 hours
    public int countWithin24Hours(Date toTime) {
        return countWithin(toTime, new Period().withDays(1));
    }

    //get item count with 24 hours
    public int countWithin(Date toTime, Period period) {
        LocalDateTime to = new LocalDateTime(toTime);
        LocalDateTime from = to.minus(period);

        int count = 0;
        for (ActivityItem activityItem : activityItems) {
            if (activityItem.timestampWithin(from, to)) {
                count++;
            }
        }

        return count;
    }


    //archieve
    public void archiveIfNecessary(Date currentTime) {
        notNull(currentTime);
        if (canArchive(currentTime)) {
            archive(currentTime);
        }
    }

    private boolean canArchive(ActivityItem item, Date currentTime) {
        long time = item.getTimestamp().getTime();
        return (time + maxArchiveInterval) < currentTime.getTime();
    }

    private boolean canArchive(Date currentTime) {
        return isSizeExceeded() || hasExceededTimeItems(currentTime);
    }

    private boolean hasExceededTimeItems(Date currentTime) {
        if (isEmpty(activityItems)) {
            return false;
        }

        return canArchive(activityItems.get(0), currentTime);
    }

    private void archive(Date currentTime) {
        archiveBySizeIfNecessary();

        if (hasExceededTimeItems(currentTime)) {
            int i = 0;
            for (ActivityItem activityItem : activityItems) {
                if (!canArchive(activityItem, currentTime)) {
                    break;
                }
                i++;
            }

            truncateList(activityItems, i, activityItems.size());
        }
    }

    private void archiveBySizeIfNecessary() {
        if (isSizeExceeded()) {
            truncateList(activityItems, activityItems.size() - maxSize, activityItems.size());
        }
    }

    private void truncateList(List<ActivityItem> activityItems, int fromIndex, int toIndex) {
        this.activityItems = activityItems.subList(fromIndex, toIndex);
    }

    private boolean isSizeExceeded() {
        return !isEmpty(activityItems) && activityItems.size() > maxSize;
    }
}