package io.renren.modules.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.modules.demo.dao.ReferralInfoDao;
import io.renren.modules.demo.dto.ReferralInfoDTO;
import io.renren.modules.demo.entity.ReferralInfoEntity;
import io.renren.modules.demo.service.ReferralInfoService;
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
public class ReferralInfoServiceImpl extends CrudServiceImpl<ReferralInfoDao, ReferralInfoEntity, ReferralInfoDTO> implements ReferralInfoService {

    @Override
    public QueryWrapper<ReferralInfoEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<ReferralInfoEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


}