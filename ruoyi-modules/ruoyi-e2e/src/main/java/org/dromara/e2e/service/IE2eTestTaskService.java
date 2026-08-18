package org.dromara.e2e.service;

import org.dromara.common.core.domain.PageResult;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.e2e.domain.bo.E2eTestTaskBo;
import org.dromara.e2e.domain.vo.E2eTestTaskVo;

import java.util.List;

/**
 * E2E测试任务 服务层
 */
public interface IE2eTestTaskService {

    /**
     * 分页查询测试任务列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 测试任务分页列表
     */
    PageResult<E2eTestTaskVo> selectPageTaskList(E2eTestTaskBo bo, PageQuery pageQuery);

    /**
     * 通过任务ID查询测试任务信息
     *
     * @param taskId 任务ID
     * @return 测试任务信息
     */
    E2eTestTaskVo selectTaskById(Long taskId);

    /**
     * 创建并执行测试任务
     *
     * @param bo 任务信息
     * @return 任务ID
     */
    Long createAndExecuteTask(E2eTestTaskBo bo);

    /**
     * 停止测试任务
     *
     * @param taskId 任务ID
     */
    void stopTask(Long taskId);

    /**
     * 批量删除测试任务
     *
     * @param taskIds 需要删除的任务ID
     * @return 影响行数
     */
    int deleteTaskByIds(Long[] taskIds);

    /**
     * 获取任务日志
     *
     * @param taskId 任务ID
     * @return 日志行列表
     */
    List<String> getTaskLogs(Long taskId);

    /**
     * 异步执行测试任务（由createAndExecuteTask通过代理调用，确保@Async生效）
     *
     * @param taskId  任务ID
     * @param browser 浏览器
     * @param headed  是否有头模式
     * @param caseIds 用例ID列表
     */
    void executeTaskAsync(Long taskId, String browser, String headed, List<Long> caseIds);
}
