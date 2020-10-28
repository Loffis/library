package se.ecutb.loffe.library.config;

import lombok.extern.slf4j.Slf4j;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;

@Slf4j
public class CacheLogger implements CacheEventListener<Object, Object> {

    @Override
    public void onEvent(CacheEvent<?, ?> cacheEvent) {
        log.info("Key: {}\nEventType: {}\nOld value: {}\nNew value: {}",
                cacheEvent.getKey(),
                cacheEvent.getType(),
                cacheEvent.getOldValue(),
                cacheEvent.getNewValue());
    }
}
