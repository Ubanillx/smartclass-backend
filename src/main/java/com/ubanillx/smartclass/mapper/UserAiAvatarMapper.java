package com.ubanillx.smartclass.mapper;

import com.ubanillx.smartclass.model.entity.UserAiAvatar;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
* @author liulo
* @description 针对表【user_ai_avatar(用户AI分身关联)】的数据库操作Mapper
* @createDate 2025-03-09 12:07:12
* @Entity com.ubanillx.smartclass.model.entity.UserAiAvatar
*/
public interface UserAiAvatarMapper extends BaseMapper<UserAiAvatar> {

    /**
     * 增加用户使用AI分身的次数，并更新最后使用时间
     * @param userId 用户ID
     * @param aiAvatarId AI分身ID
     * @return 影响行数
     */
    @Update("UPDATE user_ai_avatar SET use_count = use_count + 1, last_use_time = NOW() WHERE user_id = #{userId} AND ai_avatar_id = #{aiAvatarId}")
    int incrUseCount(@Param("userId") Long userId, @Param("aiAvatarId") Long aiAvatarId);
}




