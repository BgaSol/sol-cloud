package com.bgasol.plugin.redis.codec;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import org.redisson.client.codec.BaseCodec;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义 JSON Jackson 编解码器
 * <p>
 * 特性：
 * - 不使用默认类型处理，避免与实体类的 @JsonTypeInfo 注解冲突
 * - 支持多态类型的自动识别（基于 @JsonTypeInfo 和 @JsonSubTypes）
 * - 缓存类对象，提升反序列化性能
 * - 支持自定义 ObjectMapper 配置
 */
public class CustomJsonJacksonCodec extends BaseCodec {

    private final ObjectMapper mapper;

    /**
     * 多态类型基类的缓存
     * key: 类型标识字段名（如 "taskType", "type"）
     * value: 对应的基类 Class 对象
     */
    private final Map<String, Class<?>> polymorphicTypeCache = new ConcurrentHashMap<>();
    private final Encoder encoder = new Encoder() {
        @Override
        public ByteBuf encode(Object in) throws IOException {
            ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
            try {
                ByteBufOutputStream os = new ByteBufOutputStream(out);
                mapper.writeValue((OutputStream) os, in);
                return os.buffer();
            } catch (IOException e) {
                out.release();
                throw e;
            } catch (Exception e) {
                out.release();
                throw new IOException("Failed to encode object: " + in.getClass().getName(), e);
            }
        }
    };

    private final Decoder<Object> decoder = new Decoder<Object>() {
        @Override
        public Object decode(ByteBuf buf, State state) throws IOException {
            ByteBufInputStream inputStream = new ByteBufInputStream(buf);
            // 先读取为 JsonNode，然后根据内容决定如何反序列化
            JsonNode node = mapper.readTree(inputStream);

            // 优先支持 Jackson DefaultTyping
            if (node.has("@class")) {
                // 让 Jackson 自动根据 @class 还原真实类型
                String className = node.get("@class").asText();
                try {
                    Class<?> clazz = Class.forName(className);
                    return mapper.convertValue(node, clazz);
                } catch (ClassNotFoundException e) {
                    // 类不存在时 fallback
                    return mapper.convertValue(node, Object.class);
                }
            }


            // 尝试识别多态类型
            Class<?> targetClass = detectPolymorphicType(node);
            if (targetClass == null) {
                return mapper.convertValue(node, Object.class); // mapper 根据 @class 自动反序列化
            }
            // 使用识别到的类型进行反序列化
            return mapper.treeToValue(node, targetClass);
        }
    };

    /**
     * 使用默认配置创建 Codec
     */
    public CustomJsonJacksonCodec() {
        this(createDefaultObjectMapper());
    }

    /**
     * 使用自定义 ObjectMapper 创建 Codec
     *
     * @param mapper 自定义的 ObjectMapper
     */
    public CustomJsonJacksonCodec(ObjectMapper mapper) {
        this.mapper = mapper;
        initPolymorphicTypeCache();
    }

    /**
     * 初始化多态类型缓存
     * 可以在这里预加载已知的多态类型基类
     */
    private void initPolymorphicTypeCache() {
        registerPolymorphicType("taskType", "com.bgasol.model.freeway.videoSourceTask.base.entity.VideoSourceTask");
        registerPolymorphicType("videoSourceType", "com.bgasol.model.camera.videoSource.vs.entity.VideoSource");
    }

    /**
     * 注册多态类型
     *
     * @param typeField 类型标识字段名
     * @param className 基类全限定名
     */
    private void registerPolymorphicType(String typeField, String className) {
        try {
            Class<?> clazz = Class.forName(className);
            polymorphicTypeCache.put(typeField, clazz);
        } catch (ClassNotFoundException e) {
            // 类不存在时忽略，可能在其他模块中使用
        }
    }

    /**
     * 检测多态类型
     * 根据 JSON 中的类型标识字段，返回对应的基类
     *
     * @param node JSON 节点
     * @return 目标类型，如果无法识别则返回 Object.class
     */
    private Class<?> detectPolymorphicType(JsonNode node) {

        if (node.has("@class")) {
            return null; // 返回 null，交给 mapper 自动推断
        }

        // 检查缓存中注册的类型字段
        for (Map.Entry<String, Class<?>> entry : polymorphicTypeCache.entrySet()) {
            if (node.has(entry.getKey())) {
                return entry.getValue();
            }
        }

        // 返回 null，表示“不指定目标类型”
        return null;
    }

    /**
     * 创建默认配置的 ObjectMapper
     *
     * @return 配置好的 ObjectMapper 实例
     */
    private static ObjectMapper createDefaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // 注册 Java 8 时间模块
        mapper.registerModule(new JavaTimeModule());

        // 配置序列化选项
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // 配置反序列化选项
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);

        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );


        return mapper;
    }

    @Override
    public Decoder<Object> getValueDecoder() {
        return decoder;
    }

    @Override
    public Encoder getValueEncoder() {
        return encoder;
    }
}
