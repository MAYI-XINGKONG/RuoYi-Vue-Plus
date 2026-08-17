package org.dromara.test.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.test.domain.TestReport;
import org.dromara.test.domain.vo.TestReportVo;
import org.dromara.test.mapper.TestReportMapper;
import org.dromara.test.service.ITestReportService;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * 测试报告Service业务层处理
 *
 * @author autotest
 */
@RequiredArgsConstructor
@Service
public class TestReportServiceImpl implements ITestReportService {

    private final TestReportMapper testReportMapper;

    /**
     * 根据主键查询测试报告详情
     *
     * @param id 主键
     * @return 测试报告视图对象
     */
    @Override
    public TestReportVo queryById(Long id) {
        return testReportMapper.selectVoById(id);
    }

    /**
     * 分页查询测试报告列表
     *
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    @Override
    public PageResult<TestReportVo> queryPageList(PageQuery pageQuery) {
        Page<TestReportVo> result = testReportMapper.selectVoPage(pageQuery.build(),
            Wrappers.lambdaQuery(TestReport.class).orderByDesc(TestReport::getCreateTime));
        return PageResult.build(result.getRecords(), result.getTotal());
    }

    /**
     * 按主键集合删除测试报告数据
     *
     * @param ids 主键集合
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids) {
        return testReportMapper.deleteByIds(ids) > 0;
    }
}
