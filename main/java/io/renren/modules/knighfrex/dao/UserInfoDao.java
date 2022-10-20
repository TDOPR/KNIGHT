package io.renren.modules.demo.dao;

import io.renren.common.dao.BaseDao;
import io.renren.modules.demo.entity.UserInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2022-09-13
 */
@Mapper
public interface UserInfoDao extends BaseDao<UserInfoEntity> {
	
}