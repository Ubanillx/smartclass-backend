package com.ubanillx.smartclass.utils;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * IP地理位置解析工具类
 */
@Slf4j
@Component
public class GeoIPUtils {

    /**
     * 根据IP地址获取地理位置信息
     *
     * @param ip IP地址
     * @return 包含国家和城市的数组 [国家, 城市]
     */
    public static String[] getLocationByIp(String ip) {
        String[] location = new String[]{"Unknown", "Unknown"};
        
        // 如果IP为空，直接返回默认值
        if (ip == null || ip.isEmpty()) {
            return location;
        }
        
        // 优先使用国际API解析，确保能够区分国内外IP
        if (tryParseWithIpApi(ip, location)) {
            return location;
        }
        
        // 如果国际API解析失败，尝试备用API
        if (tryParseWithIpInfo(ip, location)) {
            return location;
        }
        
        // 最后尝试国内API
        tryParseWithPconline(ip, location);
        
        return location;
    }
    
    /**
     * 使用ipapi.co解析IP地址
     *
     * @param ip IP地址
     * @param location 存储位置信息的数组
     * @return 是否成功解析
     */
    private static boolean tryParseWithIpApi(String ip, String[] location) {
        try {
            String url = "https://ipapi.co/" + ip + "/json/";
            String result = HttpUtil.get(url);
            
            if (result == null || result.isEmpty() || result.contains("\"error\"")) {
                return false;
            }
            
            JSONObject json = JSONUtil.parseObj(result);
            boolean success = false;
            
            // 提取国家
            if (json.containsKey("country_name")) {
                String countryName = json.getStr("country_name", "");
                if (!countryName.isEmpty()) {
                    location[0] = countryName;
                    success = true;
                }
            }
            
            // 提取城市
            if (json.containsKey("city")) {
                String city = json.getStr("city", "");
                if (!city.isEmpty()) {
                    if (json.containsKey("region") && !json.getStr("region", "").isEmpty()) {
                        String region = json.getStr("region");
                        if (!region.equals(city)) {
                            location[1] = region + " " + city;
                        } else {
                            location[1] = city;
                        }
                    } else {
                        location[1] = city;
                    }
                    success = true;
                }
            } else if (json.containsKey("region")) {
                String region = json.getStr("region", "");
                if (!region.isEmpty()) {
                    location[1] = region;
                    success = true;
                }
            }
            
            return success;
        } catch (Exception e) {
            log.error("ipapi.co API解析IP失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 使用ipinfo.io解析IP地址
     *
     * @param ip IP地址
     * @param location 存储位置信息的数组
     * @return 是否成功解析
     */
    private static boolean tryParseWithIpInfo(String ip, String[] location) {
        try {
            String url = "https://ipinfo.io/" + ip + "/json";
            String result = HttpUtil.get(url);
            
            if (result == null || result.isEmpty() || result.contains("\"error\"")) {
                return false;
            }
            
            JSONObject json = JSONUtil.parseObj(result);
            boolean success = false;
            
            // 提取国家
            if (json.containsKey("country")) {
                String countryCode = json.getStr("country", "");
                if (!countryCode.isEmpty()) {
                    location[0] = getCountryNameByCode(countryCode);
                    success = true;
                }
            }
            
            // 提取城市
            if (json.containsKey("city")) {
                String city = json.getStr("city", "");
                if (!city.isEmpty()) {
                    if (json.containsKey("region") && !json.getStr("region", "").isEmpty()) {
                        String region = json.getStr("region");
                        if (!region.equals(city)) {
                            location[1] = region + " " + city;
                        } else {
                            location[1] = city;
                        }
                    } else {
                        location[1] = city;
                    }
                    success = true;
                }
            } else if (json.containsKey("region")) {
                String region = json.getStr("region", "");
                if (!region.isEmpty()) {
                    location[1] = region;
                    success = true;
                }
            }
            
            return success;
        } catch (Exception e) {
            log.error("ipinfo.io API解析IP失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 使用pconline接口解析IP地址 (主要用于国内IP)
     *
     * @param ip IP地址
     * @param location 存储位置信息的数组
     * @return 是否成功解析
     */
    private static boolean tryParseWithPconline(String ip, String[] location) {
        try {
            String url = "http://whois.pconline.com.cn/ipJson.jsp?ip=" + ip + "&json=true";
            String result = HttpUtil.get(url);
            
            if (result == null || result.isEmpty()) {
                return false;
            }
            
            JSONObject json = JSONUtil.parseObj(result);
            boolean success = false;
            
            // 仅当国家未知时，才设置国家信息
            if ("Unknown".equals(location[0])) {
                // 判断是否为中国IP
                if (json.containsKey("addr") && json.getStr("addr", "").contains("中国")) {
                    location[0] = "中国";
                    success = true;
                }
            }
            
            // 仅当城市未知时，才设置城市信息
            if ("Unknown".equals(location[1])) {
                if (json.containsKey("pro") && json.containsKey("city")) {
                    String province = json.getStr("pro", "");
                    String city = json.getStr("city", "");
                    
                    if (!province.isEmpty() && !city.isEmpty() && !"0".equals(city)) {
                        if (province.equals(city)) {
                            location[1] = province;
                        } else {
                            location[1] = province + " " + city;
                        }
                        success = true;
                    } else if (!province.isEmpty()) {
                        location[1] = province;
                        success = true;
                    } else if (!city.isEmpty() && !"0".equals(city)) {
                        location[1] = city;
                        success = true;
                    }
                }
            }
            
            return success;
        } catch (Exception e) {
            log.error("pconline API解析IP失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 根据国家代码获取国家名称
     *
     * @param countryCode 国家代码
     * @return 国家名称
     */
    private static String getCountryNameByCode(String countryCode) {
        switch (countryCode) {
            case "CN": return "中国";
            case "US": return "美国";
            case "JP": return "日本";
            case "KR": return "韩国";
            case "GB": return "英国";
            case "DE": return "德国";
            case "FR": return "法国";
            case "CA": return "加拿大";
            case "AU": return "澳大利亚";
            case "RU": return "俄罗斯";
            case "IN": return "印度";
            case "BR": return "巴西";
            case "SG": return "新加坡";
            case "MY": return "马来西亚";
            case "TH": return "泰国";
            case "VN": return "越南";
            case "IT": return "意大利";
            case "ES": return "西班牙";
            case "NL": return "荷兰";
            case "SE": return "瑞典";
            case "CH": return "瑞士";
            // 更多国家代码可以按需添加
            default: return countryCode;
        }
    }
} 