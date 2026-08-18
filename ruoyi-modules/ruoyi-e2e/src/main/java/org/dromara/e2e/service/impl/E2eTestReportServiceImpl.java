package org.dromara.e2e.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.e2e.domain.E2eTestArtifact;
import org.dromara.e2e.domain.E2eTestTask;
import org.dromara.e2e.domain.bo.E2eTestTaskBo;
import org.dromara.e2e.domain.vo.E2eTestArtifactVo;
import org.dromara.e2e.domain.vo.E2eTestTaskVo;
import org.dromara.e2e.mapper.E2eTestArtifactMapper;
import org.dromara.e2e.mapper.E2eTestTaskMapper;
import org.dromara.e2e.service.IE2eTestReportService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * E2E测试报告 服务层处理
 */
@RequiredArgsConstructor
@Service
public class E2eTestReportServiceImpl implements IE2eTestReportService {

    private final E2eTestTaskMapper testTaskMapper;
    private final E2eTestArtifactMapper testArtifactMapper;

    /**
     * 分页查询测试报告列表
     */
    @Override
    public PageResult<E2eTestTaskVo> selectPageReportList(E2eTestTaskBo bo, PageQuery pageQuery) {
        // 只查询已完成、失败或已停止的任务（作为报告）
        Page<E2eTestTaskVo> page = testTaskMapper.selectVoPage(pageQuery.build(), buildQueryWrapper(bo));
        return PageResult.build(page.getRecords(), page.getTotal());
    }

    /**
     * 通过任务ID查询测试报告信息
     */
    @Override
    public E2eTestTaskVo selectReportById(Long taskId) {
        return testTaskMapper.selectVoById(taskId);
    }

    /**
     * 根据任务ID查询测试产物列表
     */
    @Override
    public List<E2eTestArtifactVo> selectArtifactsByTaskId(Long taskId) {
        LambdaQueryWrapper<E2eTestArtifact> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(E2eTestArtifact::getTaskId, taskId);
        return testArtifactMapper.selectVoList(wrapper);
    }

    /**
     * 批量删除测试报告
     */
    @Override
    public int deleteReportByIds(Long[] taskIds) {
        return testTaskMapper.deleteByIds(Arrays.asList(taskIds));
    }

    /**
     * 根据查询条件构建查询包装器
     */
    private Wrapper<E2eTestTask> buildQueryWrapper(E2eTestTaskBo bo) {
        Map<String, Object> params = bo.getParams();
        return testTaskMapper.lambda()
            .likeIfText(E2eTestTask::getTaskName, bo.getTaskName())
            .eqIfText(E2eTestTask::getStatus, bo.getStatus())
            .betweenParams(E2eTestTask::getCreateTime, params, "beginTime", "endTime")
            .orderByDesc(E2eTestTask::getCreateTime);
    }
}
