package org.dromara.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.test.domain.AiConfig;
import org.dromara.test.domain.bo.AiConfigBo;
import org.dromara.test.domain.vo.AiConfigVo;
import org.dromara.test.mapper.AiConfigMapper;
import org.dromara.test.service.IAiConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * AI配置Service业务层处理
 *
 * @author autotest
 */
@RequiredArgsConstructor
@Service
public class AiConfigServiceImpl implements IAiConfigService {

    private final AiConfigMapper aiConfigMapper;

    /**
     * 根据主键查询AI配置详情
     *
     * @param id 主键
     * @return AI配置视图对象
     */
    @Override
    public AiConfigVo queryById(Long id) {
        return aiConfigMapper.selectVoById(id);
    }

    /**
     * 查询AI配置列表
     *
     * @return 结果列表
     */
    @Override
    public List<AiConfigVo> queryList() {
        return aiConfigMapper.selectVoList();
    }

    /**
     * 获取默认AI配置
     *
     * @return 默认AI配置
     */
    @Override
    public AiConfigVo getDefault() {
        LambdaQueryWrapper<AiConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(AiConfig::getIsDefault, true);
        lqw.eq(AiConfig::getStatus, "0");
        return aiConfigMapper.selectVoOne(lqw);
    }

    /**
     * 新增AI配置数据
     *
     * @param bo 新增业务对象
     * @return 新增记录ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long insertByBo(AiConfigBo bo) {
        // 如果设为默认，先清除其他默认配置
        if (Boolean.TRUE.equals(bo.getIsDefault())) {
            clearDefaultConfig();
        }
        AiConfig add = MapstructUtils.convert(bo, AiConfig.class);
        boolean flag = aiConfigMapper.insert(add) > 0;
        if (flag) {
            return add.getConfigId();
        }
        return null;
    }

    /**
     * 更新AI配置数据
     *
     * @param bo 编辑业务对象
     * @return 是否更新成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateByBo(AiConfigBo bo) {
        // 如果设为默认，先清除其他默认配置
        if (Boolean.TRUE.equals(bo.getIsDefault())) {
            clearDefaultConfig();
        }
        AiConfig update = MapstructUtils.convert(bo, AiConfig.class);
        return aiConfigMapper.updateById(update) > 0;
    }

    /**
     * 按主键集合删除AI配置数据
     *
     * @param ids 主键集合
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids) {
        return aiConfigMapper.deleteByIds(ids) > 0;
    }

    /**
     * 清除所有默认配置标记
     */
    private void clearDefaultConfig() {
        AiConfig update = new AiConfig();
        update.setIsDefault(false);
        LambdaQueryWrapper<AiConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(AiConfig::getIsDefault, true);
        aiConfigMapper.update(update, lqw);
    }
}
