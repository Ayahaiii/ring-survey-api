/*
 Date: 2020-04-23
 修改: rs_project_publish_config
 修改：rs_project_module
 修改：rs_project_questionnaire
*/





/*
 Date: 2020-04-17
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for rs_buy_record
-- ----------------------------
DROP TABLE IF EXISTS `rs_buy_record`;
CREATE TABLE `rs_buy_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` int(11) NOT NULL,
  `pay_type` int(1) DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `pay_order_id` int(11) NOT NULL,
  `expire_time` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `is_delete` int(11) DEFAULT NULL,
  `project_id` int(11) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for rs_info_prov_city
-- ----------------------------
DROP TABLE IF EXISTS `rs_info_prov_city`;
CREATE TABLE `rs_info_prov_city` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `province_code` bigint(20) DEFAULT NULL COMMENT '省',
  `province_name` varchar(255) DEFAULT NULL,
  `city_code` bigint(20) DEFAULT NULL COMMENT '市',
  `city_name` varchar(255) DEFAULT NULL,
  `district_code` bigint(20) DEFAULT NULL COMMENT '区',
  `district_name` varchar(255) DEFAULT NULL,
  `town_code` bigint(20) DEFAULT NULL COMMENT '乡镇',
  `town_name` varchar(255) DEFAULT NULL,
  `village_code` bigint(20) DEFAULT NULL COMMENT '村',
  `village_name` varchar(255) DEFAULT NULL,
  `city_town_classify_code` int(4) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `town_code` (`town_code`),
  KEY `province_code` (`province_code`),
  KEY `province_name` (`province_name`),
  KEY `city_index` (`city_code`),
  KEY `province_index` (`province_code`),
  KEY `district_index` (`district_code`),
  KEY `town_index` (`town_code`),
  KEY `village_index` (`village_code`),
  KEY `city_name_index` (`city_name`),
  KEY `province_name_index` (`province_name`),
  KEY `district_name_index` (`district_name`),
  KEY `town_name_index` (`town_name`),
  KEY `village_name_index` (`village_name`)
) ENGINE=InnoDB AUTO_INCREMENT=667087 DEFAULT CHARSET=utf8 COMMENT='省事行政区表';

-- ----------------------------
-- Table structure for rs_pay_order
-- ----------------------------
DROP TABLE IF EXISTS `rs_pay_order`;
CREATE TABLE `rs_pay_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `out_trade_no` varchar(255) NOT NULL COMMENT '交易记录流水号',
  `user_id` int(11) NOT NULL COMMENT '购买用户id',
  `type` int(1) NOT NULL COMMENT '购买类型：\r\n2：VIP\r\n2：企业',
  `pay_way` int(1) NOT NULL COMMENT '支付方式：\r\n1：支付宝\r\n2：微信\r\n3：其他',
  `pay_type` int(1) DEFAULT NULL,
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  `return_amount` decimal(10,2) DEFAULT NULL COMMENT '回调金额',
  `return_order_no` varchar(255) DEFAULT NULL COMMENT '对应平台的订单编号',
  `status` int(1) NOT NULL COMMENT '订单状态：\r\n1：未支付\r\n2：已支付\r\n3：支付失败\r\n4：支付成功但存在异常\r\n5：已退款',
  `error_message` varchar(255) DEFAULT NULL COMMENT '错误信息',
  `extra_data` varchar(255) DEFAULT NULL COMMENT '额外参数',
  `update_time` datetime NOT NULL COMMENT '跟新时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=322 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for rs_permission
-- ----------------------------
DROP TABLE IF EXISTS `rs_permission`;
CREATE TABLE `rs_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role` int(11) NOT NULL COMMENT '角色',
  `survey_type` varchar(255) NOT NULL COMMENT '调查类型',
  `free_num` int(11) DEFAULT NULL,
  `q_num` int(11) NOT NULL COMMENT '问卷题目数数量',
  `data_store_time` int(11) NOT NULL COMMENT '数据存储时间',
  `q_design_version` int(11) NOT NULL COMMENT '问卷设计版本',
  `team_num` int(11) NOT NULL COMMENT '团队成员数量',
  `if_bonus` int(11) NOT NULL COMMENT '是否支持自定义抽奖',
  `if_logo` int(11) NOT NULL COMMENT '是否支持自定义logo背景',
  `if_q_multiple` int(11) NOT NULL COMMENT '是否支持多问卷调查',
  `if_quota` int(11) NOT NULL COMMENT '是否支持配额',
  `if_interviewer` int(11) NOT NULL COMMENT '是否支持协作访问员',
  `if_sample_expand` int(11) NOT NULL COMMENT '是否支持样本扩展信息',
  `if_history` int(11) NOT NULL COMMENT '是否支持答卷历史',
  `if_deploy` int(11) NOT NULL COMMENT '是否独立部署',
  `price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='许可\r\n\r\n';

INSERT INTO `rs_permission` (`id`, `role`, `survey_type`, `free_num`, `q_num`, `data_store_time`, `q_design_version`, `team_num`, `if_bonus`, `if_logo`, `if_q_multiple`, `if_quota`, `if_interviewer`, `if_sample_expand`, `if_history`, `if_deploy`, `price`) VALUES ('1', '1', '[\"cawi\"]', '1', '50', '1', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0.00');
INSERT INTO `rs_permission` (`id`, `role`, `survey_type`, `free_num`, `q_num`, `data_store_time`, `q_design_version`, `team_num`, `if_bonus`, `if_logo`, `if_q_multiple`, `if_quota`, `if_interviewer`, `if_sample_expand`, `if_history`, `if_deploy`, `price`) VALUES ('2', '2', '[\"cawi\",\"capi\",\"cadi\"]', '100', '100', '100', '2', '10', '1', '1', '0', '0', '0', '0', '0', '0', '2980.00');
INSERT INTO `rs_permission` (`id`, `role`, `survey_type`, `free_num`, `q_num`, `data_store_time`, `q_design_version`, `team_num`, `if_bonus`, `if_logo`, `if_q_multiple`, `if_quota`, `if_interviewer`, `if_sample_expand`, `if_history`, `if_deploy`, `price`) VALUES ('3', '3', '[\"cawi\",\"capi\",\"cadi\",\"caxi\",\"cati\"]', '1000', '1000', '100', '3', '50', '1', '1', '1', '1', '1', '1', '1', '0', '8980.00');
INSERT INTO `rs_permission` (`id`, `role`, `survey_type`, `free_num`, `q_num`, `data_store_time`, `q_design_version`, `team_num`, `if_bonus`, `if_logo`, `if_q_multiple`, `if_quota`, `if_interviewer`, `if_sample_expand`, `if_history`, `if_deploy`, `price`) VALUES ('4', '4', '[\"cawi\",\"capi\",\"cadi\",\"caxi\",\"cati\"]', '10000', '10000', '100', '3', '100', '1', '1', '1', '1', '1', '1', '1', '1', '29800.00');

-- ----------------------------
-- Table structure for rs_project
-- ----------------------------
DROP TABLE IF EXISTS `rs_project`;
CREATE TABLE `rs_project` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `invite_code` varchar(20) DEFAULT NULL,
  `code_expire_time` datetime DEFAULT NULL,
  `code_auto_audit` int(11) DEFAULT NULL,
  `publish_code` varchar(20) DEFAULT NULL,
  `num_of_team` int(11) NOT NULL,
  `num_of_sample` int(11) NOT NULL,
  `file_size` mediumtext NOT NULL,
  `num_of_answer` int(11) NOT NULL,
  `answer_time_len` mediumtext NOT NULL,
  `label_text` varchar(255) DEFAULT NULL,
  `type` varchar(20) NOT NULL,
  `config` text,
  `begin_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `pause_time` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `create_user` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_modify_user` int(11) DEFAULT NULL,
  `last_modify_time` datetime DEFAULT NULL,
  `is_delete` int(1) DEFAULT NULL,
  `delete_user` int(11) DEFAULT NULL,
  `delete_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for rs_project_analysis_module
-- ----------------------------
DROP TABLE IF EXISTS `rs_project_analysis_module`;
CREATE TABLE `rs_project_analysis_module` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `type` varchar(255) NOT NULL,
  `base_search` text,
  `create_user` int(11) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for rs_project_export_history
-- ----------------------------
DROP TABLE IF EXISTS `rs_project_export_history`;
CREATE TABLE `rs_project_export_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL,
  `type` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `file_size` mediumtext NOT NULL,
  `file_type` varchar(255) NOT NULL,
  `file_path` varchar(255) NOT NULL,
  `create_user` varchar(255) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=186 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for rs_project_module
-- ----------------------------
DROP TABLE IF EXISTS `rs_project_module`;
CREATE TABLE `rs_project_module` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL,
  `code` varchar(50) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `max_version` int(11) NOT NULL,
  `module_dependency` int(11) DEFAULT NULL,
  `sample_dependency` varchar(255) DEFAULT NULL,
  `quota_min` int(11) DEFAULT NULL,
  `quota_max` int(11) DEFAULT NULL,
  `is_allowed_manual_add` int(1) DEFAULT NULL,
  `group_id` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `edit_content` longtext DEFAULT NULL,
  `edit_flag` int(11) DEFAULT NULL,
  `create_user` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_modify_user` int(11) DEFAULT NULL,
  `last_modify_time` datetime DEFAULT NULL,
  `is_delete` int(1) DEFAULT NULL,
  `delete_user` int(11) DEFAULT NULL,
  `delete_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for rs_project_module_group
-- ----------------------------
DROP TABLE IF EXISTS `rs_project_module_group`;
CREATE TABLE `rs_project_module_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `create_user` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_modify_user` int(11) DEFAULT NULL,
  `last_modify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for rs_project_permission
-- ----------------------------
DROP TABLE IF EXISTS `rs_project_permission`;
CREATE TABLE `rs_project_permission` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `code` varchar(255) NOT NULL,
  `if_root` int(1) NOT NULL,
  `create_user` int(11) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4;

BEGIN;
INSERT INTO `rs_project_permission` VALUES (1, '项目基本信息详情', 'PROJECT_VIEW', 1, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (2, '项目基本信息编辑', 'PROJECT_EDIT', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (3, '项目配置管理', 'PROJECT_CONFIG_ADMIN', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (4, '样本列表', 'SAMPLE_LIST', 1, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (5, '样本添加', 'SAMPLE_ADD', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (6, '样本详情', 'SAMPLE_VIEW', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (7, '样本编辑', 'SAMPLE_EDIT', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (8, '样本删除', 'SAMPLE_DELETE', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (9, '样本导出', 'SAMPLE_EXPORT', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (10, '样本属性', 'SAMPLE_PROPERTY_ADMIN', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (11, '样本分派', 'SAMPLE_ASSIGN_ADMIN', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (12, '样本外部数据', 'SAMPLE_OUTDATA_ADMIN', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (13, '样本接触记录 地址 联系人', 'SAMPLE_MOREDATA_ADMIN', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (14, '团队列表', 'MEMBER_LIST', 1, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (15, '团队添加', 'MEMBER_ADD', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (16, '团队详情', 'MEMBER_VIEW', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (17, '团队编辑', 'MEMBER_EDIT', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (18, '团队删除', 'MEMBER_DELETE', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (19, '团队导出', 'MEMBER_EXPORT', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (20, '团队分组管理', 'MEMBER_GROUP_ADMIN', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (21, '团队邀请码', 'MEMBER_INVITECODE_ADMIN', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (22, '问卷列表', 'QNAIRE_LIST', 1, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (23, '问卷导入', 'QNAIRE_IMPORT', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (24, '问卷预览', 'QNAIRE_VIEW', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (25, '问卷编辑', 'QNAIRE_EDIT', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (26, '问卷发布', 'QNAIRE_ISSUE', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (27, '问卷导出', 'QNAIRE_EXPORT', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (28, '问卷分组', 'QNAIRE_GROUP', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (29, '答卷列表', 'ANSWER_LIST', 1, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (30, '答卷重置 替换', 'ANSWER_EDIT', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (31, '答卷审核', 'ANSWER_AUDIT', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (32, '答卷导出', 'ANSWER_EXPORT', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (33, '答卷录音', 'ANSWER_VOICE', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (34, '答卷附件', 'ANSWER_FILE', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (35, '配额列表', 'QUOTA_LIST', 1, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (36, '配额添加', 'QUOTA_ADD', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (37, '配额编辑 启用 禁用', 'QUOTA_EDIT', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (38, '配额 删除', 'QUOTA_DELETE', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (39, '配额 导出', 'QUOTA_EXPORT', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (40, '发布', 'ISSUE_LIST', 1, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (41, '发布 短信订单', 'ISSUE_SMS_ORDER', 0, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (42, '红包', 'REDPACKET_LIST', 1, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (43, '分析', 'STAT_LIST', 1, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_project_permission` VALUES (44, '报表', 'MONITOR_LIST', 1, 0, '2020-03-26 14:57:40');
COMMIT;

-- ----------------------------
-- Table structure for rs_project_property
-- ----------------------------
DROP TABLE IF EXISTS `rs_project_property`;
CREATE TABLE `rs_project_property` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL,
  `all_property` text,
  `use_property` text,
  `list_property` text,
  `mark_property` text,
  `create_user` int(11) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for rs_project_property_template
-- ----------------------------
DROP TABLE IF EXISTS `rs_project_property_template`;
CREATE TABLE `rs_project_property_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `all_property` text NOT NULL,
  `use_property` text NOT NULL,
  `list_property` text NOT NULL,
  `mark_property` text NOT NULL,
  `create_user` int(11) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for rs_project_publish_config
-- ----------------------------
DROP TABLE IF EXISTS `rs_project_publish_config`;
CREATE TABLE `rs_project_publish_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) DEFAULT NULL,
  `type` int(1) DEFAULT NULL,
  `total_amount` decimal(10,2) DEFAULT NULL,
  `num` int(11) DEFAULT NULL,
  `subject` varchar(255) DEFAULT NULL,
  `content` text,
  `send_time` datetime DEFAULT NULL,
  `send_num` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_modify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for rs_project_questionnaire
-- ----------------------------
DROP TABLE IF EXISTS `rs_project_questionnaire`;
CREATE TABLE `rs_project_questionnaire` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL,
  `module_id` int(11) NOT NULL,
  `questionnaire_id` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `xml_content` longtext COMMENT '问卷xml',
  `question_num` int(11) DEFAULT NULL,
  `page_num` int(11) DEFAULT NULL,
  `welcome_text` text,
  `end_text` text,
  `logo_url` varchar(255) DEFAULT NULL,
  `bg_url` varchar(255) DEFAULT NULL,
  `create_user` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_modify_user` int(11) DEFAULT NULL,
  `last_modify_time` datetime DEFAULT NULL,
  `is_delete` int(1) DEFAULT NULL,
  `delete_user` int(11) DEFAULT NULL,
  `delete_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for rs_project_quota
-- ----------------------------
DROP TABLE IF EXISTS `rs_project_quota`;
CREATE TABLE `rs_project_quota` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL,
  `questionnaire_id` int(11) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `type` int(1) DEFAULT NULL COMMENT '1:问卷 2:样本 3:问卷+样本',
  `questionnaire_quota` text,
  `questionnaire_quota_survml` text,
  `rule_survml_id` text,
  `sample_quota` text,
  `upper_limit` int(11) DEFAULT NULL,
  `lower_limit` int(11) DEFAULT NULL,
  `current_quantity` int(11) DEFAULT NULL,
  `status` int(1) DEFAULT NULL,
  `create_user` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_modify_user` int(11) DEFAULT NULL,
  `last_modify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for rs_project_redpacket_config
-- ----------------------------
DROP TABLE IF EXISTS `rs_project_redpacket_config`;
CREATE TABLE `rs_project_redpacket_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL,
  `per_amount` decimal(10,2) DEFAULT NULL,
  `number` int(11) DEFAULT NULL,
  `total_amount` decimal(10,2) DEFAULT NULL,
  `min_amount` decimal(10,2) DEFAULT NULL,
  `max_amount` decimal(10,2) DEFAULT NULL,
  `rate` int(11) DEFAULT NULL,
  `interval_number` int(11) DEFAULT NULL,
  `send_type` int(1) DEFAULT NULL,
  `winning_type` int(1) DEFAULT NULL,
  `amount_type` int(1) DEFAULT NULL,
  `area_limit` text,
  `system_day_times` int(11) DEFAULT NULL,
  `wechat_total_times` int(11) DEFAULT NULL,
  `wechat_day_times` int(1) DEFAULT NULL,
  `send_name` varchar(255) DEFAULT NULL,
  `act_name` varchar(255) DEFAULT NULL,
  `wish` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for rs_project_sample_status_record
-- ----------------------------
DROP TABLE IF EXISTS `rs_project_sample_status_record`;
CREATE TABLE `rs_project_sample_status_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL,
  `sample_guid` varchar(255) NOT NULL,
  `status` int(11) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=166 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for rs_project_team_group
-- ----------------------------
DROP TABLE IF EXISTS `rs_project_team_group`;
CREATE TABLE `rs_project_team_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `parent_id` int(11) DEFAULT NULL,
  `create_user` int(11) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for rs_project_team_user
-- ----------------------------
DROP TABLE IF EXISTS `rs_project_team_user`;
CREATE TABLE `rs_project_team_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `telephone` varchar(255) DEFAULT NULL,
  `sample_auth` int(11) NOT NULL,
  `auth_condition` text,
  `apply_time` datetime NOT NULL,
  `approve_time` datetime DEFAULT NULL,
  `approve_user` int(11) DEFAULT NULL,
  `auth_type` int(11) NOT NULL,
  `auth_end_time` datetime DEFAULT NULL,
  `status` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=97 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for rs_project_team_user_role
-- ----------------------------
DROP TABLE IF EXISTS `rs_project_team_user_role`;
CREATE TABLE `rs_project_team_user_role` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL,
  `role_id` varchar(255) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1001 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for rs_project_team_user_to_group
-- ----------------------------
DROP TABLE IF EXISTS `rs_project_team_user_to_group`;
CREATE TABLE `rs_project_team_user_to_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for rs_questionnaire
-- ----------------------------
DROP TABLE IF EXISTS `rs_questionnaire`;
CREATE TABLE `rs_questionnaire` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `label_text` varchar(255) DEFAULT NULL,
  `identity` text,
  `xml_content` longtext COMMENT '问卷xml',
  `is_library` int(1) DEFAULT NULL,
  `logo_url` varchar(255) DEFAULT NULL,
  `bg_url` varchar(255) DEFAULT NULL,
  `question_num` int(11) DEFAULT NULL,
  `page_num` int(11) DEFAULT NULL,
  `welcome_text` text,
  `end_text` text,
  `create_user` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_modify_user` int(11) DEFAULT NULL,
  `last_modify_time` datetime DEFAULT NULL,
  `is_delete` int(1) DEFAULT NULL,
  `delete_user` int(11) DEFAULT NULL,
  `delete_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for rs_questionnaire_library
-- ----------------------------
DROP TABLE IF EXISTS `rs_questionnaire_library`;
CREATE TABLE `rs_questionnaire_library` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `questionnaire_id` int(11) DEFAULT NULL COMMENT 'rs_questionnaire外键',
  `name` varchar(255) DEFAULT NULL COMMENT '问卷名称',
  `status` int(11) DEFAULT '0',
  `description` longtext COMMENT '描述',
  `xml_content` longtext COMMENT '问卷xml',
  `rate` double(10,2) DEFAULT '0.00' COMMENT '评分',
  `price` decimal(10,2) DEFAULT NULL COMMENT '价格',
  `image_url` varchar(255) DEFAULT NULL,
  `if_free` int(1) DEFAULT NULL COMMENT '是否免费',
  `sales` int(1) DEFAULT '0' COMMENT '销量',
  `star_count` int(11) DEFAULT '0' COMMENT '收藏量',
  `view_count` int(11) DEFAULT '0' COMMENT '访问量',
  `comment_count` int(11) DEFAULT '0' COMMENT '评论数量',
  `create_user` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `logo_url` varchar(255) DEFAULT NULL,
  `bg_url` varchar(255) DEFAULT NULL,
  `question_num` int(11) DEFAULT NULL,
  `page_num` int(11) DEFAULT NULL,
  `welcome_text` text,
  `end_text` text,
  `label_text` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for rs_questionnaire_resource
-- ----------------------------
DROP TABLE IF EXISTS `rs_questionnaire_resource`;
CREATE TABLE `rs_questionnaire_resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `url` varchar(255) NOT NULL,
  `tag_id` text,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for rs_questionnaire_resource_tag
-- ----------------------------
DROP TABLE IF EXISTS `rs_questionnaire_resource_tag`;
CREATE TABLE `rs_questionnaire_resource_tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for rs_role
-- ----------------------------
DROP TABLE IF EXISTS `rs_role`;
CREATE TABLE `rs_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `create_user` int(11) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;

BEGIN;
INSERT INTO `rs_role` VALUES (1, '拥有者', 0, '2020-02-25 09:33:21');
INSERT INTO `rs_role` VALUES (2, '管理员', 0, '2020-02-20 10:24:02');
INSERT INTO `rs_role` VALUES (3, '分析员', 0, '2020-02-20 10:24:36');
INSERT INTO `rs_role` VALUES (4, '督导员', 0, '2020-02-20 10:24:22');
INSERT INTO `rs_role` VALUES (5, '访问员', 0, '2020-02-20 10:24:53');
COMMIT;

-- ----------------------------
-- Table structure for rs_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `rs_role_permission`;
CREATE TABLE `rs_role_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL,
  `create_user` int(11) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8mb4;

BEGIN;
INSERT INTO `rs_role_permission` VALUES (1, 2, 1, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (2, 2, 2, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (4, 2, 4, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (5, 2, 5, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (6, 2, 6, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (7, 2, 7, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (8, 2, 8, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (9, 2, 9, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (10, 2, 10, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (11, 2, 11, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (12, 2, 12, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (13, 2, 13, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (14, 2, 14, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (15, 2, 15, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (16, 2, 16, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (17, 2, 17, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (18, 2, 18, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (19, 2, 19, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (20, 2, 20, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (21, 2, 21, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (22, 2, 22, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (23, 2, 23, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (24, 2, 24, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (25, 2, 25, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (26, 2, 26, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (27, 2, 27, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (28, 2, 28, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (29, 2, 29, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (30, 2, 30, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (31, 2, 31, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (32, 2, 32, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (33, 2, 33, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (34, 2, 34, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (35, 2, 35, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (36, 2, 36, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (37, 2, 37, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (38, 2, 38, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (39, 2, 39, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (40, 2, 40, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (42, 2, 42, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (43, 2, 43, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (44, 2, 44, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (45, 3, 1, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (46, 3, 4, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (47, 3, 6, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (48, 3, 14, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (49, 3, 16, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (50, 3, 29, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (51, 3, 43, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (52, 4, 1, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (53, 4, 4, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (54, 4, 5, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (55, 4, 6, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (56, 4, 7, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (57, 4, 8, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (58, 4, 11, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (59, 4, 12, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (60, 4, 13, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (61, 4, 14, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (62, 4, 16, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (63, 4, 29, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (64, 4, 30, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (65, 4, 31, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (66, 4, 33, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (67, 4, 34, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (68, 4, 40, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (69, 5, 1, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (70, 5, 4, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (71, 5, 6, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (72, 5, 12, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (73, 5, 13, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (74, 5, 14, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (75, 5, 16, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (76, 5, 29, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (77, 5, 30, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (78, 5, 33, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (79, 5, 34, 0, '2020-03-26 14:57:40');
INSERT INTO `rs_role_permission` VALUES (80, 5, 40, 0, '2020-03-26 14:57:40');
COMMIT;

-- ----------------------------
-- Table structure for rs_user
-- ----------------------------
DROP TABLE IF EXISTS `rs_user`;
CREATE TABLE `rs_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `gender` int(1) DEFAULT NULL,
  `birthday` varchar(255) DEFAULT NULL,
  `status` int(1) NOT NULL COMMENT '0:禁用 1:启用',
  `email` varchar(100) DEFAULT NULL,
  `telephone` varchar(11) DEFAULT NULL,
  `company` varchar(255) DEFAULT NULL,
  `education` int(1) DEFAULT NULL,
  `college` varchar(255) DEFAULT NULL,
  `role` int(1) DEFAULT NULL,
  `use_free_num` int(11) DEFAULT '0',
  `expire_time` datetime DEFAULT NULL,
  `last_login_time` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `project_auth` text COMMENT '创建项目权限（用于本地部署）',
  `avatar_path` varchar(255) DEFAULT NULL COMMENT '用户头像的存储路径，具体到文件名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=254 DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- Table structure for rs_jstat_project
-- ----------------------------
DROP TABLE IF EXISTS `rs_jstat_project`;
CREATE TABLE `rs_jstat_project` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `project_id` int(11) DEFAULT NULL,
  `questionnaire_id` int(11) DEFAULT NULL,
  `joint_project_id` varchar(255) DEFAULT NULL,
  `create_user` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `delete_status` int(11) DEFAULT '0',
  `last_modify_user` int(11) DEFAULT NULL,
  `last_modify_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='jstat项目表';

-- ----------------------------
-- Table structure for rs_data_comparison_project
-- ----------------------------
DROP TABLE IF EXISTS `rs_data_comparison_project`;
CREATE TABLE `rs_data_comparison_project` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `origin_project_id` int(11) DEFAULT NULL,
  `origin_project_name` varchar(255) DEFAULT NULL,
  `comp_project_id` int(11) DEFAULT NULL,
  `comp_project_name` varchar(255) DEFAULT NULL,
  `different_count` int(11) DEFAULT NULL,
  `comp_setting` text DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `create_user` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_user` int(11) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `is_delete` int(1) DEFAULT NULL,
  `delete_user` int(11) DEFAULT NULL,
  `delete_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for rs_data_comparison_result
-- ----------------------------
DROP TABLE IF EXISTS `rs_data_comparison_result`;
CREATE TABLE `rs_data_comparison_result` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dcp_id` int(11) NOT NULL,
  `origin_resp_id` int(11) DEFAULT NULL,
  `comp_resp_id` int(11) DEFAULT NULL,
  `different_count` int(11) DEFAULT NULL,
  `resp_question_ids` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `comp_result` int(1) DEFAULT NULL,
  `type` int(1) DEFAULT NULL,
  `create_user` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_user` int(11) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='数据对比结果表';

-- ----------------------------
-- Table structure for n_s_sampling
-- ----------------------------
DROP TABLE IF EXISTS `n_s_sampling`;
CREATE TABLE `n_s_sampling` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `province` varchar(100) DEFAULT NULL COMMENT '对应地区行政编码',
  `sampling_num` int(11) DEFAULT NULL COMMENT '生成抽样数量',
  `sampling_customer_number_type` int(11) DEFAULT NULL COMMENT '自定义抽样生成号码方式-1.总数量生成2.后缀数量生成',
  `sampling_type` int(11) DEFAULT NULL COMMENT '抽样方式-随机或者等距',
  `number_type` int(11) DEFAULT NULL COMMENT '抽样类型--自定义抽样、随机抽样',
  `is_delete` int(4) DEFAULT '0',
  `delete_time` datetime DEFAULT NULL,
  `delete_user` varchar(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_user` varchar(11) DEFAULT NULL,
  `last_modify_time` datetime DEFAULT NULL,
  `last_modify_user` varchar(11) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL COMMENT '城市',
  `types` varchar(50) DEFAULT NULL COMMENT '类型',
  `prefix` longtext COMMENT '自定义抽样号码前缀',
  `suffix` int(11) DEFAULT NULL COMMENT '自定义抽样后缀号码位数',
  `sampling_name` varchar(100) DEFAULT NULL COMMENT '抽样名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='号码抽样表';

-- ----------------------------
-- Table structure for rs_questionnaire_library
-- ----------------------------
DROP TABLE IF EXISTS `rs_questionnaire_library`;
CREATE TABLE `rs_questionnaire_library` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `questionnaire_id` int(11) DEFAULT NULL COMMENT 'rs_questionnaire外键',
  `name` varchar(255) DEFAULT NULL COMMENT '问卷名称',
  `status` int(11) DEFAULT '0',
  `description` longtext COMMENT '描述',
  `xml_content` longtext COMMENT '问卷xml',
  `rate` double(10,2) DEFAULT '0.00' COMMENT '评分',
  `price` decimal(10,2) DEFAULT NULL COMMENT '价格',
  `image_url` varchar(255) DEFAULT NULL,
  `if_free` int(1) DEFAULT NULL COMMENT '是否免费',
  `sales` int(1) DEFAULT '0' COMMENT '销量',
  `star_count` int(11) DEFAULT '0' COMMENT '收藏量',
  `view_count` int(11) DEFAULT '0' COMMENT '访问量',
  `comment_count` int(11) DEFAULT '0' COMMENT '评论数量',
  `create_user` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `logo_url` varchar(255) DEFAULT NULL,
  `bg_url` varchar(255) DEFAULT NULL,
  `question_num` int(11) DEFAULT NULL,
  `page_num` int(11) DEFAULT NULL,
  `welcome_text` text,
  `end_text` text,
  `label_text` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for rs_library_relation
-- ----------------------------
DROP TABLE IF EXISTS `rs_library_relation`;
CREATE TABLE `rs_library_relation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `library_id` int(11) DEFAULT NULL,
  `type` int(1) DEFAULT NULL COMMENT '关系类型 1:购买 2:收藏',
  `profit` double(10,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for rs_questionnaire_comments
-- ----------------------------
DROP TABLE IF EXISTS `rs_questionnaire_comments`;
CREATE TABLE `rs_questionnaire_comments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `reply_id` int(11) DEFAULT NULL COMMENT '父评论id',
  `library_id` int(11) NOT NULL COMMENT '被评论的资源id',
  `user_id` int(11) DEFAULT NULL COMMENT '评论者id',
  `commented_id` int(11) DEFAULT NULL COMMENT '被评论者id',
  `content` varchar(255) DEFAULT NULL COMMENT '评论内容',
  `score` decimal(10,2) DEFAULT NULL COMMENT '评分',
  `create_time` datetime DEFAULT NULL COMMENT '评论时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for rs_sample_base_property
-- ----------------------------
DROP TABLE IF EXISTS `rs_sample_base_property`;
CREATE TABLE `rs_sample_base_property` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sample_base_id` int(11) NOT NULL,
  `code` varchar(50) DEFAULT '' COMMENT '样本属性编码',
  `name` varchar(200) DEFAULT NULL COMMENT '样本属性名称',
  `type` int(11) DEFAULT NULL COMMENT '字段类型：1.int；2.bigint；3.decimal；4.varchar；5.text',
  `length` int(11) DEFAULT '0' COMMENT '字段存储数据的默认长度，除varchar外都不需要设置长度',
  `serial_no` int(11) DEFAULT NULL COMMENT '属性展示顺序',
  `create_time` datetime DEFAULT NULL,
  `create_user` int(11) DEFAULT NULL,
  `last_modify_time` datetime DEFAULT NULL,
  `last_modify_user` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='样本库属性表';

-- ----------------------------
-- Table structure for rs_sample_base
-- ----------------------------
DROP TABLE IF EXISTS `rs_sample_base`;
CREATE TABLE `rs_sample_base` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(500) DEFAULT NULL COMMENT '样本库名',
  `description` varchar(2000) DEFAULT NULL COMMENT '描述',
  `type` varchar(50) DEFAULT NULL COMMENT '样本库类型：1.标准库；2.空号库',
  `create_time` datetime DEFAULT NULL,
  `create_user` int(11) DEFAULT NULL,
  `last_modify_time` datetime DEFAULT NULL,
  `last_modify_user` int(11) DEFAULT NULL,
  `is_delete` tinyint(1) DEFAULT '0' COMMENT '数据是否删除：0.没删除 1.已删除',
  `delete_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `delete_user` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='样本库表';

-- ----------------------------
-- Table structure for rs_dictionary_sample_property
-- ----------------------------
DROP TABLE IF EXISTS `rs_dictionary_sample_property`;
CREATE TABLE `rs_dictionary_sample_property` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(50) NOT NULL COMMENT '字段编码',
  `name` varchar(100) NOT NULL COMMENT '字段名称',
  `type` int(11) DEFAULT NULL COMMENT '字段类型：1.int；2.bigint；3.decimal；4.varchar；5.text',
  `length` int(11) DEFAULT '0' COMMENT '字段存储数据的默认长度，除varchar外都不需要设置长度',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='样本属性表';

-- ----------------------------
-- Table structure for rs_number_rule
-- ----------------------------
DROP TABLE IF EXISTS `rs_number_rule`;
CREATE TABLE `rs_number_rule` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) DEFAULT NULL COMMENT '项目ID',
  `name` varchar(100) DEFAULT NULL COMMENT '名称',
  `area_code` varchar(100) DEFAULT NULL COMMENT '关联区号',
  `phone_number` varchar(100) DEFAULT NULL COMMENT '关联电话号码',
  `ext_num` varchar(100) DEFAULT NULL COMMENT '关联分机号',
  `serial_no` int(11) DEFAULT NULL COMMENT '排序号',
  `create_user` int(11) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL,
  `last_modify_user` int(11) DEFAULT NULL COMMENT '最后修改人',
  `last_modify_time` datetime DEFAULT NULL,
  `is_delete` int(1) DEFAULT '0' COMMENT '是否已删除',
  `delete_user` int(11) DEFAULT NULL,
  `delete_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='号码组合规则表';
