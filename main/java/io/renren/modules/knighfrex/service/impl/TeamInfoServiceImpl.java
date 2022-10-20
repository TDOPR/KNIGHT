package io.renren.modules.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.modules.demo.dao.TeamInfoDao;
import io.renren.modules.demo.dto.TeamInfoDTO;
import io.renren.modules.demo.entity.TeamInfoEntity;
import io.renren.modules.demo.service.TeamInfoService;
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
public class TeamInfoServiceImpl extends CrudServiceImpl<TeamInfoDao, TeamInfoEntity, TeamInfoDTO> implements TeamInfoService {

    @Override
    public QueryWrapper<TeamInfoEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<TeamInfoEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


}