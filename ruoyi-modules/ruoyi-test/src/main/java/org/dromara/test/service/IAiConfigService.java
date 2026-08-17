package org.dromara.test.service;

import org.dromara.test.domain.bo.AiConfigBo;
import org.dromara.test.domain.vo.AiConfigVo;

import java.util.Collection;
import java.util.List;

/**
 * AI配置Service接口
 *
 * @author autotest
 */
public interface IAiConfigService {

    /**
     * 查询单个
     *
     * @param id 主键
     * @return AI配置视图对象
     */
    AiConfigVo queryById(Long id);

    /**
     * 查询AI配置列表
     *
     * @return 结果列表
     */
    List<AiConfigVo> queryList();

    /**
     * 获取默认AI配置
     *
     * @return 默认AI配置
     */
    AiConfigVo getDefault();

    /**
     * 根据新增业务对象插入AI配置
     *
     * @param bo AI配置新增业务对象
     * @return 新增记录ID
     */
    Long insertByBo(AiConfigBo bo);

    /**
     * 根据编辑业务对象修改AI配置
     *
     * @param bo AI配置编辑业务对象
     * @return 是否修改成功
     */
    Boolean updateByBo(AiConfigBo bo);

    /**
     * 校验并删除数据
     *
     * @param ids 主键集合
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids);
}
