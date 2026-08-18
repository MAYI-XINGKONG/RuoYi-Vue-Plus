package org.dromara.e2e.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.redis.annotation.RepeatSubmit;
import org.dromara.common.web.core.BaseController;
import org.dromara.e2e.domain.bo.E2eTestTaskBo;
import org.dromara.e2e.domain.vo.E2eTestTaskVo;
import org.dromara.e2e.manager.E2eSseManager;
import org.dromara.e2e.service.IE2eTestTaskService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * E2E测试任务操作处理
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/e2e/task")
public class E2eTestTaskController extends BaseController {

    private final IE2eTestTaskService testTaskService;
    private final E2eSseManager e2eSseManager;

    /**
     * 分页查询测试任务列表
     */
    @SaCheckPermission("e2e:task:list")
    @GetMapping("/list")
    public R<PageResult<E2eTestTaskVo>> list(E2eTestTaskBo bo, PageQuery pageQuery) {
        return R.ok(testTaskService.selectPageTaskList(bo, pageQuery));
    }

    /**
     * 根据任务编号获取详细信息
     */
    @SaCheckPermission("e2e:task:query")
    @GetMapping(value = "/{taskId}")
    public R<E2eTestTaskVo> getInfo(@NotNull @PathVariable Long taskId) {
        return R.ok(testTaskService.selectTaskById(taskId));
    }

    /**
     * 创建并执行测试任务
     */
    @SaCheckPermission("e2e:task:execute")
    @Log(title = "E2E测试任务", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping
    public R<Long> execute(@Validated @RequestBody E2eTestTaskBo bo) {
        Long taskId = testTaskService.createAndExecuteTask(bo);
        return R.ok(taskId);
    }

    /**
     * 停止测试任务
     */
    @SaCheckPermission("e2e:task:stop")
    @Log(title = "E2E测试任务-停止", businessType = BusinessType.UPDATE)
    @PostMapping("/stop/{taskId}")
    public R<Void> stop(@NotNull @PathVariable Long taskId) {
        testTaskService.stopTask(taskId);
        return R.ok();
    }

    /**
     * 删除测试任务
     */
    @SaCheckPermission("e2e:task:remove")
    @Log(title = "E2E测试任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{taskIds}")
    public R<Void> remove(@PathVariable Long[] taskIds) {
        return toAjax(testTaskService.deleteTaskByIds(taskIds));
    }

    /**
     * 获取任务日志（轮询接口，返回JSON数组）
     */
    @SaCheckPermission("e2e:task:query")
    @GetMapping("/logs/{taskId}")
    public R<List<String>> getTaskLogsJson(@NotNull @PathVariable Long taskId) {
        return R.ok(testTaskService.getTaskLogs(taskId));
    }

    /**
     * 获取任务实时日志（SSE）
     */
    @SaCheckPermission("e2e:task:query")
    @GetMapping("/log/{taskId}")
    public SseEmitter getTaskLogs(@NotNull @PathVariable Long taskId) {
        // 先获取历史日志
        List<String> logs = testTaskService.getTaskLogs(taskId);

        // 如果任务已结束（没有活跃的SSE连接），创建一次性emitter发送历史日志后关闭
        SseEmitter emitter = e2eSseManager.subscribe(taskId);

        // 发送历史日志
        for (String log : logs) {
            try {
                emitter.send(SseEmitter.event().data(log));
            } catch (Exception e) {
                break;
            }
        }

        // 如果任务已结束（不在运行中），发送完成标记后关闭
        // 这样前端知道日志已全部发送
        if (!logs.isEmpty()) {
            try {
                E2eTestTaskVo task = testTaskService.selectTaskById(taskId);
                if (task != null && !"1".equals(task.getStatus())) {
                    // 任务已结束，发送一条结束提示
                    emitter.send(SseEmitter.event().data("--- 任务已结束 ---"));
                    emitter.complete();
                }
            } catch (Exception ignored) {
            }
        }

        return emitter;
    }
}
