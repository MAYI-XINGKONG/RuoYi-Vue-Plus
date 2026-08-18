package org.dromara.e2e.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * E2E SSE管理器 - 管理Server-Sent Events连接
 */
@Slf4j
@Component
public class E2eSseManager {

    /**
     * taskId -> SSE emitter列表
     */
    private final ConcurrentHashMap<Long, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

    /**
     * 订阅任务日志
     *
     * @param taskId 任务ID
     * @return SseEmitter
     */
    public SseEmitter subscribe(Long taskId) {
        SseEmitter emitter = new SseEmitter(0L); // 无超时
        emitter.onCompletion(() -> removeEmitter(taskId, emitter));
        emitter.onTimeout(() -> removeEmitter(taskId, emitter));
        emitter.onError(e -> removeEmitter(taskId, emitter));
        emitters.computeIfAbsent(taskId, k -> new CopyOnWriteArrayList<>()).add(emitter);
        return emitter;
    }

    /**
     * 向指定任务的所有订阅者发送数据
     *
     * @param taskId 任务ID
     * @param data   数据内容
     */
    public void send(Long taskId, String data) {
        CopyOnWriteArrayList<SseEmitter> list = emitters.get(taskId);
        if (list != null) {
            for (SseEmitter emitter : list) {
                try {
                    emitter.send(SseEmitter.event().data(data));
                } catch (IOException e) {
                    log.debug("SSE发送失败，移除emitter", e);
                    removeEmitter(taskId, emitter);
                }
            }
        }
    }

    /**
     * 完成指定任务的所有SSE连接
     *
     * @param taskId 任务ID
     */
    public void complete(Long taskId) {
        CopyOnWriteArrayList<SseEmitter> list = emitters.remove(taskId);
        if (list != null) {
            list.forEach(SseEmitter::complete);
        }
    }

    /**
     * 移除指定emitter
     */
    private void removeEmitter(Long taskId, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> list = emitters.get(taskId);
        if (list != null) {
            list.remove(emitter);
        }
    }
}
