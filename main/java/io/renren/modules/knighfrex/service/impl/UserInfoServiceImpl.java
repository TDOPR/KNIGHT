package io.renren.modules.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.modules.demo.dao.UserInfoDao;
import io.renren.modules.demo.dto.UserInfoDTO;
import io.renren.modules.demo.entity.UserInfoEntity;
import io.renren.modules.demo.service.UserInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2022-09-13
 */
@Service
public class UserInfoServiceImpl extends CrudServiceImpl<UserInfoDao, UserInfoEntity, UserInfoDTO> implements UserInfoService {

    @Override
    public QueryWrapper<UserInfoEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<UserInfoEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


}