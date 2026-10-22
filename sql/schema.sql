-- ============================================================
-- 云医院门诊管理系统 - 数据库初始化脚本
-- MySQL 8.0 / utf8mb4
--
-- 用法：
--   mysql -uroot -p -e "CREATE DATABASE cloud_hospital DEFAULT CHARSET utf8mb4;"
--   mysql -uroot -p cloud_hospital < sql/schema.sql
--
-- 内容：11 张表结构 + 科室/医生/收费项目三类基础字典数据
--      不含任何患者、挂号、病历等业务数据
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ==================== 建表 ====================
DROP TABLE IF EXISTS `application`;
DROP TABLE IF EXISTS `charge_item`;
DROP TABLE IF EXISTS `department`;
DROP TABLE IF EXISTS `doctor`;
DROP TABLE IF EXISTS `fee_record`;
DROP TABLE IF EXISTS `medical_record`;
DROP TABLE IF EXISTS `patient`;
DROP TABLE IF EXISTS `prescription`;
DROP TABLE IF EXISTS `prescription_detail`;
DROP TABLE IF EXISTS `prescription_item`;
DROP TABLE IF EXISTS `registration`;

CREATE TABLE `application` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `registration_id` bigint NOT NULL COMMENT '挂号ID',
  `application_type` tinyint NOT NULL COMMENT '类型 1-检查 2-检验 3-处置',
  `item_id` bigint NOT NULL COMMENT '项目ID(关联charge_item)',
  `exec_dept` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '执行科室',
  `urgency` tinyint DEFAULT '0' COMMENT '是否加急 0-否 1-是',
  `purpose` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '目的',
  `status` tinyint DEFAULT '0' COMMENT '0-未收费 1-已收费 2-已执行 3-已取消',
  `result` text COLLATE utf8mb4_unicode_ci COMMENT '结果（检验/检查结果）',
  `apply_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `exec_time` datetime DEFAULT NULL COMMENT '执行时间',
  PRIMARY KEY (`id`),
  KEY `registration_id` (`registration_id`),
  KEY `item_id` (`item_id`),
  CONSTRAINT `application_ibfk_1` FOREIGN KEY (`registration_id`) REFERENCES `registration` (`id`),
  CONSTRAINT `application_ibfk_2` FOREIGN KEY (`item_id`) REFERENCES `charge_item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='申请单';
CREATE TABLE `charge_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `item_code` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '项目编码',
  `item_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '项目名称',
  `item_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '类型 drug/check/test/treatment',
  `price` decimal(10,2) NOT NULL COMMENT '单价',
  `spec` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '规格',
  `unit` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '单位',
  PRIMARY KEY (`id`),
  UNIQUE KEY `item_code` (`item_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收费项目字典';
CREATE TABLE `department` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `dept_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `parent_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='科室';
CREATE TABLE `doctor` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `dept_id` bigint DEFAULT NULL,
  `title` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生';
CREATE TABLE `fee_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `registration_id` bigint NOT NULL COMMENT '挂号记录ID',
  `detail_id` bigint NOT NULL COMMENT '处方明细ID',
  `fee_type` tinyint DEFAULT NULL COMMENT '1-收费 2-退费',
  `amount` decimal(10,2) DEFAULT NULL COMMENT '金额',
  `pay_method` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '支付方式',
  `operate_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `operator` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作员',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='费用流水';
CREATE TABLE `medical_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `registration_id` bigint NOT NULL COMMENT '挂号ID',
  `chief_complaint` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '主诉',
  `present_illness` text COLLATE utf8mb4_unicode_ci COMMENT '现病史',
  `past_history` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '既往史',
  `allergy_history` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '过敏史',
  `physical_exam` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '体格检查',
  `treatment_status` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '现病治疗情况',
  `exam_advice` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '检查建议',
  `precautions` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '注意事项',
  `diagnosis_icd` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '诊断ICD编码',
  `diagnosis_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '诊断名称',
  `diagnosis_date` date DEFAULT NULL COMMENT '发病时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `registration_id` (`registration_id`),
  CONSTRAINT `medical_record_ibfk_1` FOREIGN KEY (`registration_id`) REFERENCES `registration` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='门诊病历';
CREATE TABLE `patient` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '患者ID',
  `patient_no` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '病历号',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
  `gender` char(1) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '性别 0-未知 1-男 2-女',
  `birth_date` date DEFAULT NULL COMMENT '出生日期',
  `id_card` varchar(18) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `address` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '家庭住址',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `patient_no` (`patient_no`),
  UNIQUE KEY `id_card` (`id_card`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='患者信息';
CREATE TABLE `prescription` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `registration_id` bigint NOT NULL COMMENT '挂号ID',
  `prescription_no` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '处方编号',
  `status` tinyint DEFAULT '0' COMMENT '0-草稿 1-已提交 2-已收费',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `prescription_no` (`prescription_no`),
  KEY `registration_id` (`registration_id`),
  CONSTRAINT `prescription_ibfk_1` FOREIGN KEY (`registration_id`) REFERENCES `registration` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='处方';
CREATE TABLE `prescription_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `registration_id` bigint NOT NULL COMMENT '挂号记录ID',
  `item_id` bigint NOT NULL COMMENT '收费项目ID',
  `quantity` int DEFAULT '1' COMMENT '数量',
  `amount` decimal(10,2) DEFAULT NULL COMMENT '金额',
  `status` tinyint DEFAULT '0' COMMENT '0-未收费 1-已收费 2-已退费',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `registration_id` (`registration_id`),
  KEY `item_id` (`item_id`),
  CONSTRAINT `prescription_detail_ibfk_1` FOREIGN KEY (`registration_id`) REFERENCES `registration` (`id`),
  CONSTRAINT `prescription_detail_ibfk_2` FOREIGN KEY (`item_id`) REFERENCES `charge_item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='处方/申请明细';
CREATE TABLE `prescription_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `prescription_id` bigint NOT NULL COMMENT '处方ID',
  `item_id` bigint NOT NULL COMMENT '药品项目ID',
  `quantity` int DEFAULT '1' COMMENT '数量',
  `dosage` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用法用量',
  `amount` decimal(10,2) DEFAULT NULL COMMENT '金额',
  `status` tinyint DEFAULT '0' COMMENT '0-未收费 1-已收费 2-已退费',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `prescription_id` (`prescription_id`),
  KEY `item_id` (`item_id`),
  CONSTRAINT `prescription_item_ibfk_1` FOREIGN KEY (`prescription_id`) REFERENCES `prescription` (`id`),
  CONSTRAINT `prescription_item_ibfk_2` FOREIGN KEY (`item_id`) REFERENCES `charge_item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='处方明细';
CREATE TABLE `registration` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `patient_id` bigint NOT NULL COMMENT '患者ID',
  `visit_date` date NOT NULL COMMENT '就诊日期',
  `noon` char(1) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '午别 1-上午 2-下午',
  `dept_id` bigint NOT NULL COMMENT '科室ID',
  `doctor_id` bigint NOT NULL COMMENT '医生ID',
  `reg_level` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '号别 普通/专家',
  `reg_fee` decimal(10,2) DEFAULT NULL COMMENT '挂号费',
  `status` tinyint DEFAULT '0' COMMENT '0-未就诊 1-已就诊 2-已退号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `patient_id` (`patient_id`),
  CONSTRAINT `registration_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='挂号记录';

-- ==================== 基础字典数据 ====================
INSERT INTO `department` (`id`, `dept_name`, `parent_id`) VALUES (1,'内科',NULL);
INSERT INTO `department` (`id`, `dept_name`, `parent_id`) VALUES (2,'外科',NULL);
INSERT INTO `department` (`id`, `dept_name`, `parent_id`) VALUES (3,'妇科',NULL);
INSERT INTO `department` (`id`, `dept_name`, `parent_id`) VALUES (4,'儿科',NULL);
INSERT INTO `doctor` (`id`, `name`, `dept_id`, `title`) VALUES (1,'张医生',1,NULL);
INSERT INTO `doctor` (`id`, `name`, `dept_id`, `title`) VALUES (2,'李医生',2,NULL);
INSERT INTO `doctor` (`id`, `name`, `dept_id`, `title`) VALUES (3,'王医生',3,'主治医师');
INSERT INTO `doctor` (`id`, `name`, `dept_id`, `title`) VALUES (4,'赵医生',3,'副主任医师');
INSERT INTO `doctor` (`id`, `name`, `dept_id`, `title`) VALUES (5,'刘医生',4,'主治医师');
INSERT INTO `doctor` (`id`, `name`, `dept_id`, `title`) VALUES (6,'陈医生',4,'住院医师');
INSERT INTO `charge_item` (`id`, `item_code`, `item_name`, `item_type`, `price`, `spec`, `unit`) VALUES (1,'D001','头孢','drug',10.00,'50mg*100片',NULL);
INSERT INTO `charge_item` (`id`, `item_code`, `item_name`, `item_type`, `price`, `spec`, `unit`) VALUES (2,'D002','急支糖浆','drug',10.00,'50ml*12支',NULL);
INSERT INTO `charge_item` (`id`, `item_code`, `item_name`, `item_type`, `price`, `spec`, `unit`) VALUES (3,'C001','血常规','test',20.00,'',NULL);
INSERT INTO `charge_item` (`id`, `item_code`, `item_name`, `item_type`, `price`, `spec`, `unit`) VALUES (4,'C002','脑部CT','check',260.00,'',NULL);
INSERT INTO `charge_item` (`id`, `item_code`, `item_name`, `item_type`, `price`, `spec`, `unit`) VALUES (5,'C003','胸部X光','check',80.00,'','次');
INSERT INTO `charge_item` (`id`, `item_code`, `item_name`, `item_type`, `price`, `spec`, `unit`) VALUES (6,'C004','腹部B超','check',120.00,'','次');
INSERT INTO `charge_item` (`id`, `item_code`, `item_name`, `item_type`, `price`, `spec`, `unit`) VALUES (7,'C005','心电图','check',50.00,'','次');
INSERT INTO `charge_item` (`id`, `item_code`, `item_name`, `item_type`, `price`, `spec`, `unit`) VALUES (8,'C006','核磁共振','check',800.00,'','次');
INSERT INTO `charge_item` (`id`, `item_code`, `item_name`, `item_type`, `price`, `spec`, `unit`) VALUES (9,'T001','尿常规','test',15.00,'','次');
INSERT INTO `charge_item` (`id`, `item_code`, `item_name`, `item_type`, `price`, `spec`, `unit`) VALUES (10,'T002','便常规','test',10.00,'','次');
INSERT INTO `charge_item` (`id`, `item_code`, `item_name`, `item_type`, `price`, `spec`, `unit`) VALUES (11,'T003','肝功能','test',60.00,'','次');
INSERT INTO `charge_item` (`id`, `item_code`, `item_name`, `item_type`, `price`, `spec`, `unit`) VALUES (12,'T004','肾功能','test',60.00,'','次');
INSERT INTO `charge_item` (`id`, `item_code`, `item_name`, `item_type`, `price`, `spec`, `unit`) VALUES (13,'T005','血糖','test',20.00,'','次');
INSERT INTO `charge_item` (`id`, `item_code`, `item_name`, `item_type`, `price`, `spec`, `unit`) VALUES (14,'T006','血脂','test',40.00,'','次');
INSERT INTO `charge_item` (`id`, `item_code`, `item_name`, `item_type`, `price`, `spec`, `unit`) VALUES (15,'D003','阿莫西林','drug',15.00,'0.25g*24粒','盒');
INSERT INTO `charge_item` (`id`, `item_code`, `item_name`, `item_type`, `price`, `spec`, `unit`) VALUES (16,'D004','布洛芬','drug',12.00,'0.1g*20片','盒');
INSERT INTO `charge_item` (`id`, `item_code`, `item_name`, `item_type`, `price`, `spec`, `unit`) VALUES (17,'D005','感冒灵','drug',8.00,'10g*9袋','盒');
INSERT INTO `charge_item` (`id`, `item_code`, `item_name`, `item_type`, `price`, `spec`, `unit`) VALUES (18,'P001','静脉输液','treatment',30.00,'','次');
INSERT INTO `charge_item` (`id`, `item_code`, `item_name`, `item_type`, `price`, `spec`, `unit`) VALUES (19,'P002','清创缝合','treatment',200.00,'','次');

SET FOREIGN_KEY_CHECKS = 1;
