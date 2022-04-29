package xyz.coolcsm.mydubbo.protocol;

import com.caucho.hessian.io.*;
import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.protocol
 * @since 2022/4/23 23:08
 */

public interface Serializer {

    /**
     * 反序列化方法
     * @param clazz
     * @param bytes
     * @param <T>
     * @return
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);

    /**
     * 序列化方法
     * @param object
     * @return
     */
    <T> byte[] serialize(T object);

    enum Algorithm implements Serializer {

        Java {
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                try {
                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
                    return (T) ois.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException("反序列化失败", e);
                }
            }

            @Override
            public <T> byte[] serialize(T object) {
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(object);
                    return bos.toByteArray();
                } catch (IOException e) {
                    throw new RuntimeException("序列化失败", e);
                }
            }
        },

        Json {
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new Serializer.ClassCodec()).create();
                String json = new String(bytes, StandardCharsets.UTF_8);
                return gson.fromJson(json, clazz);
            }

            @Override
            public <T> byte[] serialize(T object) {
                Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new Serializer.ClassCodec()).create();
                String json = gson.toJson(object);
                return json.getBytes(StandardCharsets.UTF_8);
            }
        },

        Hessian2 {
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                ByteArrayInputStream is = new ByteArrayInputStream(bytes);
                AbstractHessianInput in = new Hessian2Input(is);

                in.setSerializerFactory(new SerializerFactory());
                T value;
                try {
                    value = (T) in.readObject();
                } catch (IOException e) {
                    throw new RuntimeException("反序列化失败", e);
                } finally {
                    try {
                        in.close();
                        is.close();
                    } catch (IOException e) {
                        throw new RuntimeException("反序列化失败", e);
                    }
                }
                return value;
            }

            @Override
            public <T> byte[] serialize(T object) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                AbstractHessianOutput out = new Hessian2Output(os);

                out.setSerializerFactory(new SerializerFactory());
                try {
                    out.writeObject(object);
                } catch (IOException e) {
                    throw new RuntimeException("序列化失败", e);
                } finally {
                    try {
                        out.close();
                        os.close();
                    } catch (IOException e) {
                        throw new RuntimeException("序列化失败", e);
                    }
                }
                return os.toByteArray();
            }
        }

    }
    class ClassCodec implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {

        @Override
        public Class<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                String str = json.getAsString();
                return Class.forName(str);
            } catch (ClassNotFoundException e) {
                throw new JsonParseException(e);
            }
        }

        @Override
        public JsonElement serialize(Class<?> src, Type typeOfSrc, JsonSerializationContext context) {
            // class -> json
            return new JsonPrimitive(src.getName());
        }
    }
}
