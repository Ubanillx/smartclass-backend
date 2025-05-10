package com.ubanillx.smartclass.utils;

import java.util.Random;
import java.util.UUID;

/**
 * ID生成器工具类
 */
public class IdGenerator {
    
    private static final Random RANDOM = new Random();
    
    /**
     * 生成8位数字ID
     * 使用UUID的hashCode取绝对值，然后取模保证是8位数字
     * 
     * @return 8位数字ID
     */
    public static String generateEightDigitId() {
        // 生成UUID
        String uuid = UUID.randomUUID().toString();
        
        // 获取UUID的hashCode
        int hashCode = uuid.hashCode();
        
        // 取绝对值，确保是正数
        hashCode = Math.abs(hashCode);
        
        // 对10^8取模，确保是8位数
        int eightDigitId = hashCode % 100000000;
        
        // 如果得到的数字不足8位，则在前面补0
        String idStr = String.format("%08d", eightDigitId);
        
        return idStr;
    }
    
    /**
     * 生成8位纯数字ID - 使用随机数的方法
     * 
     * @return 8位数字ID
     */
    public static String generateRandomEightDigitId() {
        // 生成8位随机数（范围是10000000到99999999）
        int randomNum = 10000000 + RANDOM.nextInt(90000000);
        return String.valueOf(randomNum);
    }
    
    /**
     * 生成指定长度的纯数字ID
     * 
     * @param length ID长度
     * @return 指定长度的数字ID
     */
    public static String generateNumericId(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("长度必须大于0");
        }
        
        StringBuilder sb = new StringBuilder(length);
        // 确保第一位不是0
        sb.append(1 + RANDOM.nextInt(9));
        
        // 生成剩余的数字
        for (int i = 1; i < length; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        
        return sb.toString();
    }
} 