package com.ndm.serve.services.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RedisService {

    /**
     * @param key
     * @param value
     * @purpose Save key and value to redis
     */
    void set(String key, Object value);

    /**
     * @param key
     * @param timeoutInMinutes
     * @purpose After amount of time, data with that key will be deleted
     */
    void setTimeToLive(String key, long timeoutInMinutes);

    void hashSet(String key, String field, Object value);

    boolean hashExists(String key, String field);

    Object get(String key);

    public Map<String, Object> getField(String key);

    Object hashGet(String key, String field);

    List<Object> hashGetByFieldPrefix(String key, String fieldPrefix);

    Set<String> getFieldPrefix(String key);

    void delete(String key);

    void delete(String key, String field);

    void delete(String key, List<String> fields);
}
