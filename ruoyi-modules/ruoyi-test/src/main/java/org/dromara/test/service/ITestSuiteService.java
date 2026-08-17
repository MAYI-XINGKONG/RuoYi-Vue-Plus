package org.dromara.test.service;

import org.dromara.common.core.domain.PageResult;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.test.domain.bo.TestSuiteBo;
import org.dromara.test.domain.vo.TestSuiteVo;

import java.util.Collection;
import java.util.List;

/**
 * 测试套件Service接口
 *
 * @author autotest
 */
public interface ITestSuiteService {

    /**
     * 查询单个
     *
     * @param id 主键
     * @return 测试套件视图对象
     */
    TestSuiteVo queryById(Long id);

    /**
     * 分页查询测试套件列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    PageResult<TestSuiteVo> queryPageList(TestSuiteBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的测试套件列表
     *
     * @param bo 查询条件
     * @return 结果列表
     */
    List<TestSuiteVo> queryList(TestSuiteBo bo);

    /**
     * 根据新增业务对象插入测试套件
     *
     * @param bo 测试套件新增业务对象
     * @return 新增记录ID
     */
    Long insertByBo(TestSuiteBo bo);

    /**
     * 根据编辑业务对象修改测试套件
     *
     * @param bo 测试套件编辑业务对象
     * @return 是否修改成功
     */
    Boolean updateByBo(TestSuiteBo bo);

    /**
     * 校验并删除数据
     *
     * @param ids 主键集合
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids);
}
