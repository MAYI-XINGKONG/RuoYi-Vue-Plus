package org.dromara.test.service.impl;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.test.domain.vo.AiConfigVo;
import org.dromara.test.service.IAiConfigService;
import org.dromara.test.service.IAiService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * AI服务业务层处理
 *
 * @author autotest
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AiServiceImpl implements IAiService {

    private final IAiConfigService aiConfigService;

    /**
     * 获取可用模型列表
     *
     * @param configId AI配置ID
     * @return 模型列表
     */
    @Override
    public List<String> fetchModels(Long configId) {
        AiConfigVo config = getConfig(configId);
        return doFetchModels(config.getApiUrl(), config.getApiKey());
    }

    /**
     * 通过API地址和密钥获取可用模型列表
     */
    @Override
    public List<String> fetchModelsByUrlAndKey(String apiUrl, String apiKey) {
        return doFetchModels(apiUrl, apiKey);
    }

    /**
     * 执行获取模型列表的HTTP请求
     */
    private List<String> doFetchModels(String apiUrl, String apiKey) {
        // 兼容用户填写完整URL或基础URL的情况
        String baseUrl = apiUrl.replaceAll("/+$", "");
        if (baseUrl.endsWith("/v1")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 3);
        }
        String url = baseUrl + "/v1/models";

        log.info("获取模型列表, 请求URL: {}", url);
        try {
            HttpResponse response = HttpRequest.get(url)
                .header(Header.AUTHORIZATION, "Bearer " + apiKey)
                .timeout(30000)
                .execute();

            log.info("获取模型列表, 响应状态码: {}", response.getStatus());
            if (response.isOk()) {
                JSONObject json = JSONUtil.parseObj(response.body());
                JSONArray data = json.getJSONArray("data");
                if (data != null) {
                    List<String> models = new ArrayList<>();
                    for (int i = 0; i < data.size(); i++) {
                        JSONObject model = data.getJSONObject(i);
                        models.add(model.getStr("id"));
                    }
                    log.info("获取模型列表成功, 共{}个模型", models.size());
                    return models;
                } else {
                    log.warn("获取模型列表, 响应中无data字段: {}", response.body());
                }
            } else {
                log.warn("获取模型列表失败, 状态码: {}, 响应: {}", response.getStatus(), response.body());
            }
        } catch (Exception e) {
            log.error("获取模型列表异常, URL: {}", url, e);
            throw new RuntimeException("获取模型列表失败: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * AI生成测试用例
     *
     * @param description 测试用例描述
     * @param configId    AI配置ID
     * @return 生成的测试用例JSON
     */
    @Override
    public String generateTestCase(String description, Long configId) {
        AiConfigVo config = getConfig(configId);
        String url = config.getApiUrl() + "/v1/chat/completions";

        String systemPrompt = "你是一个测试用例生成专家。根据用户提供的测试需求描述，生成测试用例JSON。" +
            "返回格式为JSON数组，每个元素包含：name(用例名称)、method(请求方法)、url(请求地址)、" +
            "headers(请求头JSON对象)、body(请求体)、expectCode(期望状态码)、expectBody(期望响应包含的关键内容)。" +
            "只返回JSON，不要其他内容。";

        JSONObject requestBody = JSONUtil.createObj()
            .set("model", config.getModelName())
            .set("messages", JSONUtil.createArray()
                .put(JSONUtil.createObj().set("role", "system").set("content", systemPrompt))
                .put(JSONUtil.createObj().set("role", "user").set("content", description)))
            .set("temperature", 0.7);

        HttpResponse response = HttpRequest.post(url)
            .header(Header.AUTHORIZATION, "Bearer " + config.getApiKey())
            .header(Header.CONTENT_TYPE, "application/json")
            .body(requestBody.toString())
            .timeout(120000)
            .execute();

        if (response.isOk()) {
            JSONObject json = JSONUtil.parseObj(response.body());
            JSONArray choices = json.getJSONArray("choices");
            if (choices != null && !choices.isEmpty()) {
                String content = choices.getJSONObject(0)
                    .getJSONObject("message")
                    .getStr("content");
                // 提取JSON块
                return extractJson(content);
            }
        }
        return response.body();
    }

    /**
     * 获取AI配置
     *
     * @param configId 配置ID，为null时使用默认配置
     * @return AI配置
     */
    private AiConfigVo getConfig(Long configId) {
        if (configId != null) {
            return aiConfigService.queryById(configId);
        }
        AiConfigVo defaultConfig = aiConfigService.getDefault();
        if (defaultConfig == null) {
            throw new RuntimeException("未找到AI配置，请先配置AI服务");
        }
        return defaultConfig;
    }

    /**
     * 从文本中提取JSON内容
     *
     * @param content 原始文本
     * @return JSON字符串
     */
    private String extractJson(String content) {
        if (content == null) {
            return null;
        }
        // 尝试提取```json ... ```块
        int start = content.indexOf("```json");
        if (start >= 0) {
            start = content.indexOf("\n", start) + 1;
            int end = content.indexOf("```", start);
            if (end > start) {
                return content.substring(start, end).trim();
            }
        }
        // 尝试提取[...]或{...}
        start = content.indexOf("[");
        if (start >= 0) {
            int end = content.lastIndexOf("]");
            if (end > start) {
                return content.substring(start, end + 1);
            }
        }
        start = content.indexOf("{");
        if (start >= 0) {
            int end = content.lastIndexOf("}");
            if (end > start) {
                return content.substring(start, end + 1);
            }
        }
        return content;
    }
}
