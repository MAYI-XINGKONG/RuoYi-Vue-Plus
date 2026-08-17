package org.dromara.test.service;

import java.util.List;

/**
 * AI服务接口
 *
 * @author autotest
 */
public interface IAiService {

    /**
     * 获取可用模型列表
     *
     * @param configId AI配置ID
     * @return 模型列表
     */
    List<String> fetchModels(Long configId);

    /**
     * 通过API地址和密钥获取可用模型列表（用于新增配置时）
     *
     * @param apiUrl API地址
     * @param apiKey API密钥
     * @return 模型列表
     */
    List<String> fetchModelsByUrlAndKey(String apiUrl, String apiKey);

    /**
     * AI生成测试用例
     *
     * @param description 测试用例描述
     * @param configId    AI配置ID
     * @return 生成的测试用例JSON
     */
    String generateTestCase(String description, Long configId);
}
