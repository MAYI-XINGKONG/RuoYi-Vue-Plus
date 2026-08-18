-- ----------------------------
-- E2E 自动化测试管理平台 - 数据库初始化脚本
-- ----------------------------

-- ----------------------------
-- 1、测试用例表
-- ----------------------------
drop table if exists e2e_test_case;
create table e2e_test_case (
    case_id          bigint(20)      not null                   comment '用例ID',
    case_name        varchar(200)    not null                   comment '用例名称',
    spec_file        varchar(500)    not null                   comment 'spec文件相对路径',
    case_group       varchar(100)    default ''                 comment '用例分组',
    description      varchar(500)    default ''                 comment '用例描述',
    status           char(1)         default '0'                comment '状态（0正常 1停用）',
    create_dept      bigint(20)      default null               comment '创建部门',
    create_by        bigint(20)      default null               comment '创建者',
    create_time      datetime                                   comment '创建时间',
    update_by        bigint(20)      default null               comment '更新者',
    update_time      datetime                                   comment '更新时间',
    del_flag         char(1)         default '0'                comment '删除标志（0存在 2删除）',
    primary key (case_id)
) engine=innodb comment = '测试用例表';

-- ----------------------------
-- 2、测试执行任务表
-- ----------------------------
drop table if exists e2e_test_task;
create table e2e_test_task (
    task_id          bigint(20)      not null                   comment '任务ID',
    task_name        varchar(200)    not null                   comment '任务名称',
    browser          varchar(20)     default 'chromium'         comment '浏览器（chromium/firefox/webkit/all）',
    headed           char(1)         default '0'                comment '是否有头模式（0无头 1有头）',
    status           char(1)         default '0'                comment '状态（0待执行 1执行中 2成功 3失败 4已停止）',
    process_id       int             default null               comment 'Node进程PID',
    total_cases      int             default 0                  comment '总用例数',
    passed_cases     int             default 0                  comment '通过数',
    failed_cases     int             default 0                  comment '失败数',
    skipped_cases    int             default 0                  comment '跳过数',
    start_time       datetime        default null               comment '开始执行时间',
    end_time         datetime        default null               comment '结束执行时间',
    report_path      varchar(500)    default ''                 comment 'HTML报告路径',
    result_json_path varchar(500)    default ''                 comment 'JSON结果文件路径',
    error_msg        text                                       comment '错误信息',
    create_dept      bigint(20)      default null               comment '创建部门',
    create_by        bigint(20)      default null               comment '创建者',
    create_time      datetime                                   comment '创建时间',
    update_by        bigint(20)      default null               comment '更新者',
    update_time      datetime                                   comment '更新时间',
    primary key (task_id)
) engine=innodb comment = '测试执行任务表';

-- ----------------------------
-- 3、任务用例关联表
-- ----------------------------
drop table if exists e2e_test_task_case;
create table e2e_test_task_case (
    id               bigint(20)      not null                   comment '主键',
    task_id          bigint(20)      not null                   comment '任务ID',
    case_id          bigint(20)      not null                   comment '用例ID',
    primary key (id),
    key idx_task_id (task_id),
    key idx_case_id (case_id)
) engine=innodb comment = '任务用例关联表';

-- ----------------------------
-- 4、测试产物表
-- ----------------------------
drop table if exists e2e_test_artifact;
create table e2e_test_artifact (
    artifact_id      bigint(20)      not null                   comment '产物ID',
    task_id          bigint(20)      not null                   comment '任务ID',
    case_name        varchar(200)    default ''                 comment '用例名称',
    artifact_type    varchar(20)     not null                   comment '类型（screenshot/video/trace/log）',
    file_path        varchar(500)    not null                   comment '文件路径',
    file_size        bigint          default 0                  comment '文件大小（字节）',
    create_time      datetime                                   comment '创建时间',
    primary key (artifact_id),
    key idx_task_id (task_id)
) engine=innodb comment = '测试产物表';

-- ----------------------------
-- 菜单数据
-- ----------------------------
-- 一级菜单：E2E测试管理
insert into sys_menu values(1761400000000000900, 'E2E测试管理', 0, 6, 'e2e', null, '', 'N', 'Y', 'M', '0', '0', '', 'eye-open', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, 'E2E自动化测试管理目录');

-- 二级菜单：测试用例管理
insert into sys_menu values(1761400000000000901, '测试用例管理', 1761400000000000900, 1, 'testcase', 'e2e/testcase/index', '', 'N', 'Y', 'C', '0', '0', 'e2e:testcase:list', 'list', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '测试用例管理菜单');

-- 二级菜单：测试任务管理
insert into sys_menu values(1761400000000000902, '测试任务管理', 1761400000000000900, 2, 'task', 'e2e/task/index', '', 'N', 'Y', 'C', '0', '0', 'e2e:task:list', 'form', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '测试任务管理菜单');

-- 二级菜单：测试报告管理
insert into sys_menu values(1761400000000000903, '测试报告管理', 1761400000000000900, 3, 'report', 'e2e/report/index', '', 'N', 'Y', 'C', '0', '0', 'e2e:report:list', 'chart', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '测试报告管理菜单');

-- 测试用例管理按钮权限
insert into sys_menu values(1761400000000000911, '用例查询', 1761400000000000901, 1, '#', '', '', 'N', 'Y', 'F', '0', '0', 'e2e:testcase:query', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '');
insert into sys_menu values(1761400000000000912, '用例新增', 1761400000000000901, 2, '#', '', '', 'N', 'Y', 'F', '0', '0', 'e2e:testcase:add', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '');
insert into sys_menu values(1761400000000000913, '用例修改', 1761400000000000901, 3, '#', '', '', 'N', 'Y', 'F', '0', '0', 'e2e:testcase:edit', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '');
insert into sys_menu values(1761400000000000914, '用例删除', 1761400000000000901, 4, '#', '', '', 'N', 'Y', 'F', '0', '0', 'e2e:testcase:remove', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '');
insert into sys_menu values(1761400000000000915, '用例同步', 1761400000000000901, 5, '#', '', '', 'N', 'Y', 'F', '0', '0', 'e2e:testcase:sync', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '');

-- 测试任务管理按钮权限
insert into sys_menu values(1761400000000000921, '任务查询', 1761400000000000902, 1, '#', '', '', 'N', 'Y', 'F', '0', '0', 'e2e:task:query', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '');
insert into sys_menu values(1761400000000000922, '任务执行', 1761400000000000902, 2, '#', '', '', 'N', 'Y', 'F', '0', '0', 'e2e:task:execute', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '');
insert into sys_menu values(1761400000000000923, '任务停止', 1761400000000000902, 3, '#', '', '', 'N', 'Y', 'F', '0', '0', 'e2e:task:stop', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '');
insert into sys_menu values(1761400000000000924, '任务删除', 1761400000000000902, 4, '#', '', '', 'N', 'Y', 'F', '0', '0', 'e2e:task:remove', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '');

-- 测试报告管理按钮权限
insert into sys_menu values(1761400000000000931, '报告查询', 1761400000000000903, 1, '#', '', '', 'N', 'Y', 'F', '0', '0', 'e2e:report:query', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '');
insert into sys_menu values(1761400000000000932, '报告删除', 1761400000000000903, 2, '#', '', '', 'N', 'Y', 'F', '0', '0', 'e2e:report:remove', '#', '', '', 1761000000000000103, 1761100000000000001, sysdate(), null, null, '');

-- ----------------------------
-- 5、日志持久化：为任务表新增日志内容字段
-- ----------------------------
ALTER TABLE e2e_test_task ADD COLUMN log_content TEXT COMMENT '执行日志内容';

-- ----------------------------
-- 6、用例内容字段
-- ----------------------------
ALTER TABLE e2e_test_case ADD COLUMN content TEXT COMMENT '用例代码内容';

-- ----------------------------
-- 7、用例历史版本表
-- ----------------------------
drop table if exists e2e_test_case_history;
create table e2e_test_case_history (
    history_id     bigint(20)    not null                   comment '历史ID',
    case_id        bigint(20)    not null                   comment '用例ID',
    content        text                                       comment '用例代码内容',
    version        int           default 1                  comment '版本号',
    create_by      bigint(20)    default null               comment '创建者',
    create_time    datetime                                 comment '创建时间',
    primary key (history_id),
    key idx_case_id (case_id)
) engine=innodb comment = '用例历史版本表';
