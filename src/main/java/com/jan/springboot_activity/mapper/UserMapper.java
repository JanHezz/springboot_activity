package com.jan.springboot_activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jan.springboot_activity.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * <p>
 * 管理员表 Mapper 接口
 * </p>
 *
 * @author jan 橙寂
 * @since 2018-12-07
 */
public interface UserMapper extends BaseMapper<User> {


}
