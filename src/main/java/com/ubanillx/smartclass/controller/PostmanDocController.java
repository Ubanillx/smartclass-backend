package com.ubanillx.smartclass.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.config.WebSocketEndpointConfig;
import com.ubanillx.smartclass.exception.BusinessException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Postman 接口文档控制器
 * 用于生成Postman Collection文件，支持REST和WebSocket接口调试
 *
 * @author ubanillx
 */
@RestController
@RequestMapping("/postman-collections")
@Api(tags = "接口文档")
@Slf4j
public class PostmanDocController {

    
    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    private RequestMappingHandlerMapping handlerMapping;

    @Value("${server.port:12345}")
    private String serverPort;

    @Value("${server.servlet.context-path:/api}")
    private String contextPath;

    @Value("${netty.websocket.port:12346}")
    private String websocketPort;
    
    @Resource
    private Map<String, WebSocketEndpointConfig.WebSocketEndpointDefinition> webSocketEndpoints;

    /**
     * 生成Postman Collection文件
     *
     * @param request HTTP请求
     * @return Postman Collection文件响应
     */
    @GetMapping
    @ApiOperation(value = "导出Postman Collection", notes = "生成可导入Postman的接口文档，包含REST和WebSocket接口")
    public ResponseEntity<Object> generatePostmanCollection(HttpServletRequest request) {
        log.info("开始生成Postman Collection文件");
        try {
            String host = getBaseUrl(request);
            
            // 创建Collection对象
            JSONObject collection = new JSONObject();
            collection.put("info", createInfo());
            
            // 添加HTTP和WebSocket接口
            List<JSONObject> items = new ArrayList<>();
            items.add(createHttpFolder(host));
            items.add(createWebSocketFolder(host));
            
            collection.put("item", items);
            collection.put("variable", createGlobalVariables(host));
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=smartclass-postman-collection.json");
            
            // 格式化输出
            String jsonOutput = JSON.toJSONString(collection, SerializerFeature.PrettyFormat);
            log.info("Postman Collection文件生成成功");
            return ResponseEntity.ok().headers(headers).body(jsonOutput);
            
        } catch (Exception e) {
            log.error("生成Postman Collection失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成Postman Collection失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取服务器信息
     *
     * @param request HTTP请求
     * @return 服务器信息
     */
    @GetMapping("/server")
    @ApiOperation(value = "获取服务器信息", notes = "返回服务器Host、Port等信息，用于Postman配置")
    public BaseResponse<Map<String, String>> getServerInfo(HttpServletRequest request) {
        log.info("获取服务器信息");
        Map<String, String> result = new HashMap<>();
        try {
            String baseUrl = getBaseUrl(request);
            result.put("baseUrl", baseUrl);
            result.put("apiBase", baseUrl + contextPath);
            result.put("wsUrl", "ws://" + getHostAddress() + ":" + websocketPort + "/ws");
            
            log.info("获取服务器信息成功: {}", result);
            return ResultUtils.success(result);
        } catch (Exception e) {
            log.error("获取服务器信息失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取服务器信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 提供WebSocket接口列表
     *
     * @return WebSocket接口列表响应
     */
    @GetMapping("/websocket-endpoints")
    @ApiOperation(value = "获取WebSocket接口列表", notes = "返回所有注册的WebSocket消息类型及其接口定义")
    public BaseResponse<List<Map<String, Object>>> listWebSocketEndpoints() {
        log.info("获取WebSocket接口列表");
        List<Map<String, Object>> endpointsList = new ArrayList<>();
        
        for (Map.Entry<String, WebSocketEndpointConfig.WebSocketEndpointDefinition> entry : webSocketEndpoints.entrySet()) {
            Map<String, Object> endpoint = new HashMap<>();
            endpoint.put("type", entry.getKey());
            endpoint.put("name", entry.getValue().getName());
            endpoint.put("description", entry.getValue().getDescription());
            endpoint.put("exampleRequest", entry.getValue().getExampleRequest());
            endpointsList.add(endpoint);
        }
        
        log.info("获取WebSocket接口列表成功，共 {} 个接口", endpointsList.size());
        return ResultUtils.success(endpointsList);
    }
    
    /**
     * 动态生成Postman Collection文件
     *
     * @param request HTTP请求
     * @return Postman Collection文件响应
     */
    @GetMapping("/dynamic")
    @ApiOperation(value = "动态生成Postman Collection", notes = "实时生成最新的Postman Collection文件，包含最新的REST和WebSocket接口")
    public ResponseEntity<Object> generateDynamicPostmanCollection(HttpServletRequest request) {
        log.info("开始动态生成Postman Collection文件");
        return generatePostmanCollection(request);
    }
    
    /**
     * 创建Collection信息
     *
     * @return Collection信息对象
     */
    private JSONObject createInfo() {
        JSONObject info = new JSONObject();
        info.put("_postman_id", UUID.randomUUID().toString());
        info.put("name", "智慧课堂API");
        info.put("description", "智慧课堂后端接口文档，包含REST和WebSocket接口");
        info.put("schema", "https://schema.getpostman.com/json/collection/v2.1.0/collection.json");
        return info;
    }
    
    /**
     * 创建HTTP接口文件夹
     *
     * @return HTTP接口文件夹对象
     */
    private JSONObject createHttpFolder(String host) {
        JSONObject httpFolder = new JSONObject();
        httpFolder.put("name", "HTTP接口");
        httpFolder.put("description", "RESTful API接口");
        httpFolder.put("item", getHttpEndpoints());
        return httpFolder;
    }
    
    /**
     * 创建WebSocket接口文件夹
     *
     * @return WebSocket接口文件夹对象
     */
    private JSONObject createWebSocketFolder(String host) {
        JSONObject wsFolder = new JSONObject();
        wsFolder.put("name", "WebSocket接口");
        wsFolder.put("description", "实时通信WebSocket接口");
        wsFolder.put("item", getWebSocketEndpoints());
        return wsFolder;
    }
    
    /**
     * 获取HTTP接口
     *
     * @return HTTP接口列表
     */
    private JSONArray getHttpEndpoints() {
        JSONArray items = new JSONArray();
        
        // 获取所有HTTP接口
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
        
        // 按照Controller分组
        Map<String, List<JSONObject>> controllerMap = new HashMap<>();
        
        handlerMethods.forEach((requestMappingInfo, handlerMethod) -> {
            if (requestMappingInfo.getPatternsCondition() != null) {
                // 获取Controller类名作为分组名
                String controllerName = handlerMethod.getBeanType().getSimpleName();
                if (controllerName.endsWith("Controller")) {
                    controllerName = controllerName.substring(0, controllerName.length() - 10);
                }
                
                // 创建接口项
                JSONObject item = new JSONObject();
                String methodName = handlerMethod.getMethod().getName();
                item.put("name", methodName);
                
                // 获取接口描述
                ApiOperation apiOperation = handlerMethod.getMethodAnnotation(ApiOperation.class);
                if (apiOperation != null) {
                    item.put("description", apiOperation.value() + "\n" + apiOperation.notes());
                }
                
                // 设置请求信息
                JSONObject request = new JSONObject();
                request.put("method", getHttpMethod(requestMappingInfo));
                
                // 设置URL
                String url = "";
                if (requestMappingInfo.getPatternsCondition() != null && !requestMappingInfo.getPatternsCondition().getPatterns().isEmpty()) {
                    url = new ArrayList<>(requestMappingInfo.getPatternsCondition().getPatterns()).get(0);
                }
                
                JSONObject urlObj = new JSONObject();
                urlObj.put("raw", "{{apiBase}}" + url);
                urlObj.put("host", Collections.singletonList("{{apiBase}}"));
                String[] pathSegments = url.split("/");
                List<String> pathList = new ArrayList<>();
                for (String segment : pathSegments) {
                    if (!segment.isEmpty()) {
                        pathList.add(segment);
                    }
                }
                urlObj.put("path", pathList);
                request.put("url", urlObj);
                
                // 默认添加Header
                JSONArray headers = new JSONArray();
                JSONObject header = new JSONObject();
                header.put("key", "Content-Type");
                header.put("value", "application/json");
                header.put("type", "text");
                headers.add(header);
                
                // 添加认证Cookie
                JSONObject authHeader = new JSONObject();
                authHeader.put("key", "Cookie");
                authHeader.put("value", "JSESSIONID={{sessionId}}");
                authHeader.put("type", "text");
                authHeader.put("description", "添加登录后的JSESSIONID进行认证");
                headers.add(authHeader);
                
                request.put("header", headers);
                
                item.put("request", request);
                
                // 添加到对应控制器分组
                controllerMap.computeIfAbsent(controllerName, k -> new ArrayList<>()).add(item);
            }
        });
        
        // 为每个控制器创建一个文件夹
        for (Map.Entry<String, List<JSONObject>> entry : controllerMap.entrySet()) {
            JSONObject folder = new JSONObject();
            folder.put("name", entry.getKey());
            folder.put("item", entry.getValue());
            items.add(folder);
        }
        
        return items;
    }
    
    /**
     * 获取WebSocket接口
     *
     * @return WebSocket接口列表
     */
    private JSONArray getWebSocketEndpoints() {
        JSONArray items = new JSONArray();
        
        // WebSocket连接示例
        JSONObject wsConnectItem = new JSONObject();
        wsConnectItem.put("name", "建立WebSocket连接");
        wsConnectItem.put("description", "建立与服务器的WebSocket连接，需要在URL中添加token参数进行认证");
        
        JSONObject wsRequest = new JSONObject();
        wsRequest.put("method", "GET");
        
        JSONObject urlObj = new JSONObject();
        urlObj.put("raw", "{{wsUrl}}?token={{token}}");
        urlObj.put("host", Collections.singletonList("{{wsUrl}}"));
        
        // 添加查询参数
        JSONArray queryParams = new JSONArray();
        JSONObject tokenParam = new JSONObject();
        tokenParam.put("key", "token");
        tokenParam.put("value", "{{token}}");
        tokenParam.put("description", "用户认证令牌");
        queryParams.add(tokenParam);
        urlObj.put("query", queryParams);
        
        wsRequest.put("url", urlObj);
        wsRequest.put("description", "建立WebSocket连接，连接成功后可以发送和接收消息");
        
        wsConnectItem.put("request", wsRequest);
        items.add(wsConnectItem);
        
        // 从注册的WebSocket接口定义中动态生成接口文档
        for (Map.Entry<String, WebSocketEndpointConfig.WebSocketEndpointDefinition> entry : webSocketEndpoints.entrySet()) {
            WebSocketEndpointConfig.WebSocketEndpointDefinition endpoint = entry.getValue();
            
            JSONObject item = new JSONObject();
            item.put("name", endpoint.getName());
            item.put("description", endpoint.getDescription());
            
            JSONObject request = new JSONObject();
            request.put("method", "WEBSOCKET-REQUEST");
            
            JSONObject body = new JSONObject();
            body.put("mode", "raw");
            body.put("raw", endpoint.getExampleRequest());
            
            request.put("body", body);
            request.put("url", urlObj);
            
            item.put("request", request);
            items.add(item);
        }
        
        return items;
    }
    
    /**
     * 创建全局变量
     *
     * @param host 主机地址
     * @return 全局变量列表
     */
    private JSONArray createGlobalVariables(String host) {
        JSONArray variables = new JSONArray();
        
        // API基础URL变量
        JSONObject apiBaseVar = new JSONObject();
        apiBaseVar.put("key", "apiBase");
        apiBaseVar.put("value", host + contextPath);
        apiBaseVar.put("type", "string");
        variables.add(apiBaseVar);
        
        // WebSocket URL变量
        JSONObject wsUrlVar = new JSONObject();
        wsUrlVar.put("key", "wsUrl");
        wsUrlVar.put("value", "ws://" + host.replace("http://", "") + ":" + websocketPort + "/ws");
        wsUrlVar.put("type", "string");
        variables.add(wsUrlVar);
        
        // 会话ID变量
        JSONObject sessionIdVar = new JSONObject();
        sessionIdVar.put("key", "sessionId");
        sessionIdVar.put("value", "");
        sessionIdVar.put("type", "string");
        variables.add(sessionIdVar);
        
        // Token变量
        JSONObject tokenVar = new JSONObject();
        tokenVar.put("key", "token");
        tokenVar.put("value", "");
        tokenVar.put("type", "string");
        variables.add(tokenVar);
        
        return variables;
    }
    
    /**
     * 获取HTTP方法
     *
     * @param mappingInfo 请求映射信息
     * @return HTTP方法名称
     */
    private String getHttpMethod(RequestMappingInfo mappingInfo) {
        if (!mappingInfo.getMethodsCondition().getMethods().isEmpty()) {
            return mappingInfo.getMethodsCondition().getMethods().iterator().next().name();
        }
        return "GET";
    }
    
    /**
     * 获取基础URL
     *
     * @param request HTTP请求
     * @return 基础URL
     */
    private String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        
        // 尝试获取真实IP，防止反向代理影响
        if ("localhost".equals(serverName) || "127.0.0.1".equals(serverName)) {
            try {
                serverName = getHostAddress();
            } catch (UnknownHostException e) {
                log.warn("获取主机地址失败，使用默认地址");
            }
        }
        
        return scheme + "://" + serverName + ":" + serverPort;
    }
    
    /**
     * 获取主机地址
     *
     * @return 主机IP地址
     * @throws UnknownHostException 如果无法确定主机地址
     */
    private String getHostAddress() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }
} 