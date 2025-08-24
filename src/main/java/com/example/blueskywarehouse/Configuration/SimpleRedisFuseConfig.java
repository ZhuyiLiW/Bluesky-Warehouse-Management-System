package com.example.blueskywarehouse.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

@EnableCaching
@Configuration
public class SimpleRedisFuseConfig extends CachingConfigurerSupport {

    private static final Logger log = LoggerFactory.getLogger(SimpleRedisFuseConfig.class);
    // true: 用 Redis；false: 熔断到 NoOp（直到重启）
    private final AtomicBoolean redisEnabled = new AtomicBoolean(true);

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory cf) {
        RedisCacheConfiguration cfg = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10));

        CacheManager redis = RedisCacheManager.builder(cf).cacheDefaults(cfg).build();
        CacheManager noop  = new NoOpCacheManager();

        return new CacheManager() {
            private CacheManager delegate() { return redisEnabled.get() ? redis : noop; }
            @Override public Cache getCache(String name) { return delegate().getCache(name); }
            @Override public Collection<String> getCacheNames() { return delegate().getCacheNames(); }
        };
    }

    //Wichtig: Überschreiben Sie die errorHandler()-Methode von CachingConfigurerSupport und deklarieren Sie keine zusätzliche @Bean dafür.
    @Override
    public CacheErrorHandler errorHandler() {
        return new org.springframework.cache.interceptor.SimpleCacheErrorHandler() {

            private boolean isRedisDown(RuntimeException ex) {
                return ex instanceof RedisConnectionFailureException
                        || (ex.getCause() != null
                        && ex.getCause().getClass().getName().contains("RedisConnection"));
            }

            private void fuseOffIfConnError(RuntimeException ex, Cache cache, String phase) {
                if (isRedisDown(ex)) {
                    if (redisEnabled.compareAndSet(true, false)) {
                        log.warn("Redis connection error on {} (cache='{}'). FUSING OFF Redis globally until restart.",
                                phase, cache != null ? cache.getName() : "n/a", ex);
                    } else {
                        log.warn("Redis already fused off. {} error ignored. cache='{}'",
                                phase, cache != null ? cache.getName() : "n/a");
                    }
                } else {
                    throw ex;
                }
            }

            @Override public void handleCacheGetError(RuntimeException ex, Cache cache, Object key) {
                fuseOffIfConnError(ex, cache, "GET");
            }
            @Override public void handleCachePutError(RuntimeException ex, Cache cache, Object key, Object value) {
                fuseOffIfConnError(ex, cache, "PUT");
            }
            @Override public void handleCacheEvictError(RuntimeException ex, Cache cache, Object key) {
                fuseOffIfConnError(ex, cache, "EVICT");
            }
            @Override public void handleCacheClearError(RuntimeException ex, Cache cache) {
                fuseOffIfConnError(ex, cache, "CLEAR");
            }
        };
    }
}
