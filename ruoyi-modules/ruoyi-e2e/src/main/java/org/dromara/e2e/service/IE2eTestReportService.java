package org.dromara.e2e.service;

import org.dromara.common.core.domain.PageResult;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.e2e.domain.bo.E2eTestTaskBo;
import org.dromara.e2e.domain.vo.E2eTestArtifactVo;
import org.dromara.e2e.domain.vo.E2eTestTaskVo;

import java.util.List;

/**
 * E2E测试报告 服务层
 */
public interface IE2eTestReportService {

    /**
     * 分页查询测试报告列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 测试报告分页列表
     */
    PageResult<E2eTestTaskVo> selectPageReportList(E2eTestTaskBo bo, PageQuery pageQuery);

    /**
     * 通过任务ID查询测试报告信息
     *
     * @param taskId 任务ID
     * @return 测试报告信息
     */
    E2eTestTaskVo selectReportById(Long taskId);

    /**
     * 根据任务ID查询测试产物列表
     *
     * @param taskId 任务ID
     * @return 测试产物列表
     */
    List<E2eTestArtifactVo> selectArtifactsByTaskId(Long taskId);

    /**
     * 批量删除测试报告
     *
     * @param taskIds 需要删除的任务ID
     * @return 影响行数
     */
    int deleteReportByIds(Long[] taskIds);
}
