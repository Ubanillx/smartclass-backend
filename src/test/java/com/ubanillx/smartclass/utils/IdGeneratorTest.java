package com.ubanillx.smartclass.utils;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class IdGeneratorTest {

    @Test
    void testGenerateEightDigitId() {
        String id = IdGenerator.generateEightDigitId();
        
        // 测试生成的ID长度为8
        assertEquals(8, id.length());
        
        // 测试生成的ID只包含数字
        assertTrue(id.matches("\\d{8}"));
    }

    @Test
    void testGenerateRandomEightDigitId() {
        String id = IdGenerator.generateRandomEightDigitId();
        
        // 测试生成的ID长度为8
        assertEquals(8, id.length());
        
        // 测试生成的ID只包含数字
        assertTrue(id.matches("\\d{8}"));
    }
    
    @Test
    void testIdUniqueness() {
        // 测试生成1000个ID的唯一性
        Set<String> idSet = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            String id = IdGenerator.generateRandomEightDigitId();
            assertFalse(idSet.contains(id), "ID不应该重复：" + id);
            idSet.add(id);
        }
    }
    
    @Test
    void testGenerateNumericId() {
        // 测试生成各种长度的ID
        for (int length = 1; length <= 10; length++) {
            String id = IdGenerator.generateNumericId(length);
            
            // 测试生成的ID长度符合预期
            assertEquals(length, id.length());
            
            // 测试生成的ID只包含数字
            assertTrue(id.matches("\\d{" + length + "}"));
            
            // 测试第一位不是0
            assertFalse(id.startsWith("0"));
        }
    }
    
    @Test
    void testInvalidLength() {
        // 测试长度参数为0或负数时抛出异常
        assertThrows(IllegalArgumentException.class, () -> {
            IdGenerator.generateNumericId(0);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            IdGenerator.generateNumericId(-1);
        });
    }
} 