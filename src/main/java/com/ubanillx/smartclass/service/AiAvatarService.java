package com.ubanillx.smartclass.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ubanillx.smartclass.model.entity.AiAvatar;

import java.util.List;

/**
* @author liulo
* @description 针对表【ai_avatar(AI分身)】的数据库操作Service
* @createDate 2025-03-09 11:50:27
*/
public interface AiAvatarService extends IService<AiAvatar> {

    /**
     * 添加AI分身
     * @param aiAvatar AI分身信息
     * @return 新增AI分身的ID
     */
    long addAiAvatar(AiAvatar aiAvatar);

    /**
     * 删除AI分身（逻辑删除）
     * @param id AI分身ID
     * @return 是否成功
     */
    boolean deleteAiAvatar(long id);

    /**
     * 更新AI分身信息
     * @param aiAvatar AI分身信息
     * @return 是否成功
     */
    boolean updateAiAvatar(AiAvatar aiAvatar);

    /**
     * 根据ID获取AI分身信息
     * @param id AI分身ID
     * @return AI分身信息
     */
    AiAvatar getAiAvatarById(long id);

    /**
     * 分页查询AI分身列表
     * @param category 分类
     * @param keyword 关键词
     * @param current 当前页码
     * @param size 每页大小
     * @return 分页结果
     */
    Page<AiAvatar> listAiAvatarByPage(String category, String keyword, int current, int size);

    /**
     * 获取热门AI分身列表
     * @param limit 限制数量
     * @return AI分身列表
     */
    List<AiAvatar> listPopularAiAvatars(int limit);

    /**
     * 根据分类获取AI分身列表
     * @param category 分类
     * @param limit 限制数量
     * @return AI分身列表
     */
    List<AiAvatar> listAiAvatarsByCategory(String category, int limit);

    /**
     * 更新AI分身使用次数
     * @param id AI分身ID
     * @return 是否成功
     */
    boolean updateAiAvatarUsageCount(long id);
    
    /**
     * 校验AI分身信息
     * @param aiAvatar AI分身信息
     * @param add 是否为新增操作
     */
    void validAiAvatar(AiAvatar aiAvatar, boolean add);
}
