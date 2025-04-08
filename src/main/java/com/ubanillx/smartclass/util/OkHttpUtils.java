package com.ubanillx.smartclass.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * OkHttp 工具类
 */
@Slf4j
@Component
public class OkHttpUtils {

    private final OkHttpClient client;
    
    public OkHttpUtils() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }
    
    /**
     * 创建没有读取超时的客户端，用于流式响应
     * @return OkHttpClient 实例
     */
    public OkHttpClient createNoTimeoutClient() {
        return client.newBuilder()
                .readTimeout(0, TimeUnit.SECONDS) // 无限读取超时
                .build();
    }
    
    /**
     * 发送 JSON POST 请求
     * @param url 请求 URL
     * @param jsonBody JSON 请求体
     * @param headers 请求头
     * @return 响应对象
     */
    public Response postJson(String url, String jsonBody, Map<String, String> headers) {
        try {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, jsonBody);
            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .post(body);
            
            // 添加请求头
            if (headers != null) {
                headers.forEach(requestBuilder::header);
            }
            
            return client.newCall(requestBuilder.build()).execute();
        } catch (IOException e) {
            log.error("发送 JSON 请求异常: {}", e.getMessage(), e);
            throw new RuntimeException("发送 JSON 请求失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 发送表单 POST 请求
     * @param url 请求 URL
     * @param formData 表单数据
     * @param headers 请求头
     * @return 响应对象
     */
    public Response postForm(String url, Map<String, String> formData, Map<String, String> headers) {
        try {
            FormBody.Builder formBuilder = new FormBody.Builder();
            
            // 添加表单数据
            if (formData != null) {
                formData.forEach(formBuilder::add);
            }
            
            RequestBody body = formBuilder.build();
            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .post(body);
            
            // 添加请求头
            if (headers != null) {
                headers.forEach(requestBuilder::header);
            }
            
            return client.newCall(requestBuilder.build()).execute();
        } catch (IOException e) {
            log.error("发送表单请求异常: {}", e.getMessage(), e);
            throw new RuntimeException("发送表单请求失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 发送多部分表单请求（包含文件）
     * @param url 请求 URL
     * @param formData 表单数据
     * @param fileField 文件字段名
     * @param filename 文件名
     * @param fileStream 文件流
     * @param mimeType 文件类型
     * @param headers 请求头
     * @return 响应对象
     */
    public Response postMultipart(String url, Map<String, String> formData, String fileField, 
                                String filename, InputStream fileStream, String mimeType,
                                Map<String, String> headers) {
        try {
            MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            
            // 添加文本字段
            if (formData != null) {
                formData.forEach(multipartBuilder::addFormDataPart);
            }
            
            // 添加文件
            if (fileStream != null && fileField != null && filename != null) {
                // 使用兼容 Java 8 的方式读取输入流
                byte[] bytes = toByteArray(fileStream);
                MediaType mediaType = MediaType.parse(mimeType);
                RequestBody fileBody = RequestBody.create(mediaType, bytes);
                multipartBuilder.addFormDataPart(fileField, filename, fileBody);
            }
            
            RequestBody requestBody = multipartBuilder.build();
            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .post(requestBody);
            
            // 添加请求头
            if (headers != null) {
                headers.forEach(requestBuilder::header);
            }
            
            return client.newCall(requestBuilder.build()).execute();
        } catch (IOException e) {
            log.error("发送多部分表单请求异常: {}", e.getMessage(), e);
            throw new RuntimeException("发送多部分表单请求失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 将输入流转换为字节数组，兼容 Java 8
     * @param inputStream 输入流
     * @return 字节数组
     * @throws IOException IO异常
     */
    private byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[4096];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }
    
    /**
     * 发送 DELETE 请求
     * @param url 请求 URL
     * @param jsonBody JSON 请求体
     * @param headers 请求头
     * @return 响应对象
     */
    public Response delete(String url, String jsonBody, Map<String, String> headers) {
        try {
            RequestBody body = null;
            if (jsonBody != null) {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                body = RequestBody.create(JSON, jsonBody);
            }
            
            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .delete(body);
            
            // 添加请求头
            if (headers != null) {
                headers.forEach(requestBuilder::header);
            }
            
            return client.newCall(requestBuilder.build()).execute();
        } catch (IOException e) {
            log.error("发送 DELETE 请求异常: {}", e.getMessage(), e);
            throw new RuntimeException("发送 DELETE 请求失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 发送流式 POST 请求
     * @param url 请求 URL
     * @param jsonBody JSON 请求体
     * @param headers 请求头
     * @return 响应对象（包含流）
     */
    public Response postJsonStream(String url, String jsonBody, Map<String, String> headers) {
        try {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, jsonBody);
            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .post(body);
            
            // 添加请求头
            if (headers != null) {
                headers.forEach(requestBuilder::header);
            }
            
            // 使用无超时客户端发送请求
            return createNoTimeoutClient().newCall(requestBuilder.build()).execute();
        } catch (IOException e) {
            log.error("发送流式 JSON 请求异常: {}", e.getMessage(), e);
            throw new RuntimeException("发送流式 JSON 请求失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 发送流式表单 POST 请求
     * @param url 请求 URL
     * @param formData 表单数据
     * @param headers 请求头
     * @return 响应对象（包含流）
     */
    public Response postFormStream(String url, Map<String, String> formData, Map<String, String> headers) {
        try {
            FormBody.Builder formBuilder = new FormBody.Builder();
            
            // 添加表单数据
            if (formData != null) {
                formData.forEach(formBuilder::add);
            }
            
            RequestBody body = formBuilder.build();
            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .post(body);
            
            // 添加请求头
            if (headers != null) {
                headers.forEach(requestBuilder::header);
            }
            
            // 使用无超时客户端发送请求
            return createNoTimeoutClient().newCall(requestBuilder.build()).execute();
        } catch (IOException e) {
            log.error("发送流式表单请求异常: {}", e.getMessage(), e);
            throw new RuntimeException("发送流式表单请求失败: " + e.getMessage(), e);
        }
    }
} 