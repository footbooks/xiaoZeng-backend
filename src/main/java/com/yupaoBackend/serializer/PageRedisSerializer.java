package com.yupaoBackend.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 序列化分页数据
 * @param <T>
 */
public class PageRedisSerializer<T> implements RedisSerializer<Page<T>> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(Page<T> page) throws SerializationException {
        try {
            return objectMapper.writeValueAsBytes(page);
        } catch (Exception e) {
            throw new SerializationException("Error serializing object to byte array", e);
        }
    }

    @Override
    public Page<T> deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null) {
            return null;
        }
        try {
            return objectMapper.readValue(bytes, Page.class);
        } catch (Exception e) {
            throw new SerializationException("Error deserializing byte array to object", e);
        }
    }
}