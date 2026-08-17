package org.dromara.test.service;

import org.dromara.common.core.domain.PageResult;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.test.domain.bo.TestExecutionBo;
import org.dromara.test.domain.vo.TestExecutionVo;

/**
 * 测试执行Service接口
 *
 * @author autotest
 */
public interface ITestExecutionService {

    /**
     * 查询单个
     *
     * @param id 主键
     * @return 测试执行视图对象
     */
    TestExecutionVo queryById(Long id);

    /**
     * 分页查询测试执行列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    PageResult<TestExecutionVo> queryPageList(TestExecutionBo bo, PageQuery pageQuery);

    /**
     * 运行测试
     *
     * @param bo 测试执行业务对象
     * @return 执行记录ID
     */
    Long runTest(TestExecutionBo bo);
}
