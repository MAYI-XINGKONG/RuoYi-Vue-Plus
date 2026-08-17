package org.dromara.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.test.domain.TestSuite;
import org.dromara.test.domain.bo.TestSuiteBo;
import org.dromara.test.domain.vo.TestSuiteVo;
import org.dromara.test.mapper.TestSuiteMapper;
import org.dromara.test.service.ITestSuiteService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 测试套件Service业务层处理
 *
 * @author autotest
 */
@RequiredArgsConstructor
@Service
public class TestSuiteServiceImpl implements ITestSuiteService {

    private final TestSuiteMapper testSuiteMapper;

    /**
     * 根据主键查询测试套件详情
     *
     * @param id 主键
     * @return 测试套件视图对象
     */
    @Override
    public TestSuiteVo queryById(Long id) {
        return testSuiteMapper.selectVoById(id);
    }

    /**
     * 分页查询测试套件列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    @Override
    public PageResult<TestSuiteVo> queryPageList(TestSuiteBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<TestSuite> lqw = buildQueryWrapper(bo);
        Page<TestSuiteVo> result = testSuiteMapper.selectVoPage(pageQuery.build(), lqw);
        return PageResult.build(result.getRecords(), result.getTotal());
    }

    /**
     * 查询符合条件的测试套件列表
     *
     * @param bo 查询条件
     * @return 结果列表
     */
    @Override
    public List<TestSuiteVo> queryList(TestSuiteBo bo) {
        return testSuiteMapper.selectVoList(buildQueryWrapper(bo));
    }

    /**
     * 构建测试套件动态查询条件
     *
     * @param bo 查询条件
     * @return 查询条件包装器
     */
    private LambdaQueryWrapper<TestSuite> buildQueryWrapper(TestSuiteBo bo) {
        LambdaQueryWrapper<TestSuite> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getSuiteName()), TestSuite::getSuiteName, bo.getSuiteName());
        lqw.eq(StringUtils.isNotBlank(bo.getSuiteType()), TestSuite::getSuiteType, bo.getSuiteType());
        lqw.eq(StringUtils.isNotBlank(bo.getMethod()), TestSuite::getMethod, bo.getMethod());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), TestSuite::getStatus, bo.getStatus());
        lqw.like(StringUtils.isNotBlank(bo.getTags()), TestSuite::getTags, bo.getTags());
        lqw.orderByAsc(TestSuite::getOrderNum);
        return lqw;
    }

    /**
     * 新增测试套件数据
     *
     * @param bo 新增业务对象
     * @return 新增记录ID
     */
    @Override
    public Long insertByBo(TestSuiteBo bo) {
        TestSuite add = MapstructUtils.convert(bo, TestSuite.class);
        boolean flag = testSuiteMapper.insert(add) > 0;
        if (flag) {
            return add.getSuiteId();
        }
        return null;
    }

    /**
     * 更新测试套件数据
     *
     * @param bo 编辑业务对象
     * @return 是否更新成功
     */
    @Override
    public Boolean updateByBo(TestSuiteBo bo) {
        TestSuite update = MapstructUtils.convert(bo, TestSuite.class);
        return testSuiteMapper.updateById(update) > 0;
    }

    /**
     * 按主键集合删除测试套件数据
     *
     * @param ids 主键集合
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids) {
        return testSuiteMapper.deleteByIds(ids) > 0;
    }
}
