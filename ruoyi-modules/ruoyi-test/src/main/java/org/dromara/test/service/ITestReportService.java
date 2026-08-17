package org.dromara.test.service;

import org.dromara.common.core.domain.PageResult;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.test.domain.vo.TestReportVo;

import java.util.Collection;

/**
 * 测试报告Service接口
 *
 * @author autotest
 */
public interface ITestReportService {

    /**
     * 查询单个
     *
     * @param id 主键
     * @return 测试报告视图对象
     */
    TestReportVo queryById(Long id);

    /**
     * 分页查询测试报告列表
     *
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    PageResult<TestReportVo> queryPageList(PageQuery pageQuery);

    /**
     * 校验并删除数据
     *
     * @param ids 主键集合
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids);
}
