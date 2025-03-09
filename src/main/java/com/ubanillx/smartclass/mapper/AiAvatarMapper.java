package com.ubanillx.smartclass.mapper;

import com.ubanillx.smartclass.model.entity.AiAvatar;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
* @author liulo
* @description 针对表【ai_avatar(AI分身)】的数据库操作Mapper
* @createDate 2025-03-09 12:07:12
* @Entity com.ubanillx.smartclass.model.entity.AiAvatar
*/
public interface AiAvatarMapper extends BaseMapper<AiAvatar> {

    /**
     * 增加AI分身使用次数
     * @param id AI分身ID
     * @return 影响行数
     */
    @Update("UPDATE ai_avatar SET usage_count = usage_count + 1 WHERE id = #{id} AND is_delete = 0")
    int incrUsageCount(@Param("id") Long id);
}




