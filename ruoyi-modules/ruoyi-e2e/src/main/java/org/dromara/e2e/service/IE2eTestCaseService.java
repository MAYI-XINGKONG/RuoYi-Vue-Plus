package org.dromara.e2e.service;

import org.dromara.common.core.domain.PageResult;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.e2e.domain.bo.E2eTestCaseBo;
import org.dromara.e2e.domain.vo.E2eTestCaseHistoryVo;
import org.dromara.e2e.domain.vo.E2eTestCaseVo;

import java.util.List;

/**
 * E2E测试用例 服务层
 */
public interface IE2eTestCaseService {

    /**
     * 分页查询测试用例列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 测试用例分页列表
     */
    PageResult<E2eTestCaseVo> selectPageTestCaseList(E2eTestCaseBo bo, PageQuery pageQuery);

    /**
     * 通过用例ID查询测试用例信息
     *
     * @param caseId 用例ID
     * @return 测试用例信息
     */
    E2eTestCaseVo selectTestCaseById(Long caseId);

    /**
     * 新增测试用例
     *
     * @param bo 测试用例信息
     * @return 影响行数
     */
    int insertTestCase(E2eTestCaseBo bo);

    /**
     * 修改测试用例
     *
     * @param bo 测试用例信息
     * @return 影响行数
     */
    int updateTestCase(E2eTestCaseBo bo);

    /**
     * 批量删除测试用例
     *
     * @param caseIds 需要删除的用例ID
     * @return 影响行数
     */
    int deleteTestCaseByIds(Long[] caseIds);

    /**
     * 同步spec文件到数据库
     */
    void syncSpecFiles();

    /**
     * 根据分组查询测试用例列表
     *
     * @param caseGroup 用例分组
     * @return 测试用例列表
     */
    List<E2eTestCaseVo> selectTestCaseByGroup(String caseGroup);

    /**
     * 查询用例历史版本
     *
     * @param caseId 用例ID
     * @return 历史版本列表
     */
    List<E2eTestCaseHistoryVo> selectCaseHistory(Long caseId);

    /**
     * 根据历史ID查询历史版本
     *
     * @param historyId 历史ID
     * @return 历史版本信息
     */
    E2eTestCaseHistoryVo selectHistoryById(Long historyId);

    /**
     * 回退到指定版本
     *
     * @param caseId    用例ID
     * @param historyId 历史版本ID
     */
    void revertToVersion(Long caseId, Long historyId);

    /**
     * 仅更新用例内容（不影响名称等元数据）
     *
     * @param caseId 用例ID
     * @param content 用例代码内容
     */
    void updateCaseContent(Long caseId, String content);
}
