-- ----------------------------
-- 自动化测试模块建表及菜单SQL
-- ----------------------------

-- ----------------------------
-- 1、测试用例表
-- ----------------------------
create table test_suite
(
    suite_id        bigint(20)     not null                 comment '用例ID',
    suite_name      varchar(200)   not null                 comment '用例名称',
    suite_type      tinyint(1)     default 1                comment '用例类型（1=接口测试 2=E2E测试）',
    method          varchar(10)    default null             comment '请求方法（GET/POST/PUT/DELETE等）',
    url             varchar(500)   default null             comment '请求地址',
    headers         text                                     comment '请求头（JSON格式）',
    body            text                                     comment '请求体（JSON格式）',
    expect_code     int(4)         default null             comment '期望状态码',
    expect_body     text                                     comment '期望响应体（JSON格式）',
    script_path     varchar(500)   default null             comment 'Playwright脚本路径',
    tags            varchar(500)   default null             comment '标签（逗号分隔）',
    status          char(1)        default '0'              comment '用例状态（0正常 1停用）',
    order_num       int(4)         default 0                comment '显示顺序',
    create_by       bigint(20)     default null             comment '创建者',
    create_time     datetime                                comment '创建时间',
    update_by       bigint(20)     default null             comment '更新者',
    update_time     datetime                                comment '更新时间',
    remark          varchar(500)   default null             comment '备注',
    primary key (suite_id)
) engine=innodb comment = '测试用例表';

-- ----------------------------
-- 2、测试执行记录表
-- ----------------------------
create table test_execution
(
    execution_id    bigint(20)     not null                 comment '执行ID',
    suite_id        bigint(20)     default null             comment '用例ID',
    exec_type       tinyint(1)     default 1                comment '执行类型（1=单个 2=全部 3=按标签）',
    browser         varchar(50)    default 'chromium'       comment '浏览器类型',
    status          varchar(20)    default 'pending'        comment '执行状态（pending/running/pass/fail/error）',
    total           int(4)         default 0                comment '总用例数',
    passed          int(4)         default 0                comment '通过数',
    failed          int(4)         default 0                comment '失败数',
    skipped         int(4)         default 0                comment '跳过数',
    duration        bigint(20)     default 0                comment '执行时长（毫秒）',
    log_output      longtext                                 comment '执行日志输出',
    report_path     varchar(500)   default null             comment '报告文件路径',
    start_time      datetime       default null             comment '开始时间',
    end_time        datetime       default null             comment '结束时间',
    create_by       bigint(20)     default null             comment '创建者',
    create_time     datetime                                comment '创建时间',
    primary key (execution_id)
) engine=innodb comment = '测试执行记录表';

-- ----------------------------
-- 3、测试报告表
-- ----------------------------
create table test_report
(
    report_id       bigint(20)     not null                 comment '报告ID',
    execution_id    bigint(20)     default null             comment '执行ID',
    report_name     varchar(200)   default null             comment '报告名称',
    report_type     varchar(50)    default null             comment '报告类型',
    file_path       varchar(500)   default null             comment '报告文件路径',
    file_size       bigint(20)     default 0                comment '文件大小（字节）',
    summary         text                                     comment '报告摘要（JSON格式）',
    create_by       bigint(20)     default null             comment '创建者',
    create_time     datetime                                comment '创建时间',
    primary key (report_id)
) engine=innodb comment = '测试报告表';

-- ----------------------------
-- 4、AI配置表
-- ----------------------------
create table ai_config
(
    config_id       bigint(20)     not null                 comment '配置ID',
    config_name     varchar(100)   not null                 comment '配置名称',
    api_url         varchar(500)   not null                 comment 'API接口地址',
    api_key         varchar(500)   default null             comment 'API密钥',
    model_name      varchar(100)   default null             comment '模型名称',
    model_list      text                                     comment '可用模型列表（JSON格式）',
    extra_params    text                                     comment '额外参数（JSON格式）',
    status          char(1)        default '0'              comment '配置状态（0正常 1停用）',
    is_default      tinyint(1)     default 0                comment '是否默认配置（0否 1是）',
    create_by       bigint(20)     default null             comment '创建者',
    create_time     datetime                                comment '创建时间',
    update_by       bigint(20)     default null             comment '更新者',
    update_time     datetime                                comment '更新时间',
    remark          varchar(500)   default null             comment '备注',
    primary key (config_id)
) engine=innodb comment = 'AI配置表';

-- ----------------------------
-- 菜单 SQL
-- ----------------------------

-- 自动化测试 目录
insert into sys_menu values(1761400000000000200, '自动化测试', 0, 6, 'autotest', null, '', 'N', 'Y', 'M', '0', '0', '', 'auto-test', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '自动化测试目录');

-- 测试用例 菜单
insert into sys_menu values(1761400000000000201, '测试用例', 1761400000000000200, 1, 'suite', 'test/suite/index', '', 'N', 'Y', 'C', '0', '0', 'test:suite:list', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '测试用例菜单');
-- 测试用例 按钮权限
insert into sys_menu values(1761400000000000211, '测试用例查询', 1761400000000000201, 1, '#', '', '', 'N', 'Y', 'F', '0', '0', 'test:suite:query', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '');
insert into sys_menu values(1761400000000000212, '测试用例新增', 1761400000000000201, 2, '#', '', '', 'N', 'Y', 'F', '0', '0', 'test:suite:add', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '');
insert into sys_menu values(1761400000000000213, '测试用例修改', 1761400000000000201, 3, '#', '', '', 'N', 'Y', 'F', '0', '0', 'test:suite:edit', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '');
insert into sys_menu values(1761400000000000214, '测试用例删除', 1761400000000000201, 4, '#', '', '', 'N', 'Y', 'F', '0', '0', 'test:suite:remove', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '');
insert into sys_menu values(1761400000000000215, '测试用例执行', 1761400000000000201, 5, '#', '', '', 'N', 'Y', 'F', '0', '0', 'test:suite:execute', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '');
insert into sys_menu values(1761400000000000216, 'AI生成用例', 1761400000000000201, 6, '#', '', '', 'N', 'Y', 'F', '0', '0', 'test:suite:aiGenerate', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '');

-- 测试执行 菜单
insert into sys_menu values(1761400000000000202, '测试执行', 1761400000000000200, 2, 'execution', 'test/execution/index', '', 'N', 'Y', 'C', '0', '0', 'test:execution:list', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '测试执行菜单');
-- 测试执行 按钮权限
insert into sys_menu values(1761400000000000221, '测试执行查询', 1761400000000000202, 1, '#', '', '', 'N', 'Y', 'F', '0', '0', 'test:execution:query', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '');
insert into sys_menu values(1761400000000000222, '测试执行运行', 1761400000000000202, 2, '#', '', '', 'N', 'Y', 'F', '0', '0', 'test:execution:run', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '');

-- 测试报告 菜单
insert into sys_menu values(1761400000000000203, '测试报告', 1761400000000000200, 3, 'report', 'test/report/index', '', 'N', 'Y', 'C', '0', '0', 'test:report:list', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '测试报告菜单');
-- 测试报告 按钮权限
insert into sys_menu values(1761400000000000231, '测试报告查询', 1761400000000000203, 1, '#', '', '', 'N', 'Y', 'F', '0', '0', 'test:report:query', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '');
insert into sys_menu values(1761400000000000232, '测试报告删除', 1761400000000000203, 2, '#', '', '', 'N', 'Y', 'F', '0', '0', 'test:report:remove', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '');
insert into sys_menu values(1761400000000000233, '测试报告下载', 1761400000000000203, 3, '#', '', '', 'N', 'Y', 'F', '0', '0', 'test:report:download', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '');

-- AI配置 菜单
insert into sys_menu values(1761400000000000204, 'AI配置', 1761400000000000200, 4, 'ai-config', 'test/ai-config/index', '', 'N', 'Y', 'C', '0', '0', 'test:aiConfig:list', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, 'AI配置菜单');
-- AI配置 按钮权限
insert into sys_menu values(1761400000000000241, 'AI配置查询', 1761400000000000204, 1, '#', '', '', 'N', 'Y', 'F', '0', '0', 'test:aiConfig:query', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '');
insert into sys_menu values(1761400000000000242, 'AI配置新增', 1761400000000000204, 2, '#', '', '', 'N', 'Y', 'F', '0', '0', 'test:aiConfig:add', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '');
insert into sys_menu values(1761400000000000243, 'AI配置修改', 1761400000000000204, 3, '#', '', '', 'N', 'Y', 'F', '0', '0', 'test:aiConfig:edit', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '');
insert into sys_menu values(1761400000000000244, 'AI配置删除', 1761400000000000204, 4, '#', '', '', 'N', 'Y', 'F', '0', '0', 'test:aiConfig:remove', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '');
insert into sys_menu values(1761400000000000245, '获取模型列表', 1761400000000000204, 5, '#', '', '', 'N', 'Y', 'F', '0', '0', 'test:aiConfig:fetchModels', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '');
