SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE audit_log;
TRUNCATE TABLE charge_record;
TRUNCATE TABLE prescription_item;
TRUNCATE TABLE prescription;
TRUNCATE TABLE simple_reservation;
TRUNCATE TABLE doctor_schedule;
TRUNCATE TABLE drug;
TRUNCATE TABLE medical_record;
TRUNCATE TABLE patient;
TRUNCATE TABLE doctor;
TRUNCATE TABLE userinfo;
TRUNCATE TABLE department;
TRUNCATE TABLE consulting_room;
TRUNCATE TABLE user;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO user (id, username, password, email, phone, status, create_time, update_time, role) VALUES
(1, 'admin', '$2a$10$5RSLVc3.ZMeva8ye1WlSp.2w.pAK/cbnb3h1PZ4x7hA1cYK1Kk0Pq', 'admin@his.local', '13800000000', 1, DATE_SUB(NOW(), INTERVAL 120 DAY), NOW(), 'admin'),
(2, 'doctor', '$2a$10$Tquw5Y7aKXgnximaBqzsv.3DwC881lWwfocYah7Gsa4qntnHW12ny', 'zhangwei@his.local', '13800000002', 1, DATE_SUB(NOW(), INTERVAL 110 DAY), NOW(), 'doctor'),
(3, 'doctor02', '$2a$10$Tquw5Y7aKXgnximaBqzsv.3DwC881lWwfocYah7Gsa4qntnHW12ny', 'lina@his.local', '13800000003', 1, DATE_SUB(NOW(), INTERVAL 105 DAY), NOW(), 'doctor'),
(4, 'doctor03', '$2a$10$Tquw5Y7aKXgnximaBqzsv.3DwC881lWwfocYah7Gsa4qntnHW12ny', 'wangqiang@his.local', '13800000004', 1, DATE_SUB(NOW(), INTERVAL 100 DAY), NOW(), 'doctor'),
(5, 'doctor04', '$2a$10$Tquw5Y7aKXgnximaBqzsv.3DwC881lWwfocYah7Gsa4qntnHW12ny', 'liuyang@his.local', '13800000005', 1, DATE_SUB(NOW(), INTERVAL 95 DAY), NOW(), 'doctor'),
(6, 'doctor05', '$2a$10$Tquw5Y7aKXgnximaBqzsv.3DwC881lWwfocYah7Gsa4qntnHW12ny', 'chenjing@his.local', '13800000006', 1, DATE_SUB(NOW(), INTERVAL 90 DAY), NOW(), 'doctor'),
(7, 'doctor06', '$2a$10$Tquw5Y7aKXgnximaBqzsv.3DwC881lWwfocYah7Gsa4qntnHW12ny', 'yangfan@his.local', '13800000007', 1, DATE_SUB(NOW(), INTERVAL 85 DAY), NOW(), 'doctor'),
(8, 'doctor07', '$2a$10$Tquw5Y7aKXgnximaBqzsv.3DwC881lWwfocYah7Gsa4qntnHW12ny', 'zhaomin@his.local', '13800000008', 1, DATE_SUB(NOW(), INTERVAL 80 DAY), NOW(), 'doctor'),
(9, 'doctor08', '$2a$10$Tquw5Y7aKXgnximaBqzsv.3DwC881lWwfocYah7Gsa4qntnHW12ny', 'zhoujie@his.local', '13800000009', 1, DATE_SUB(NOW(), INTERVAL 75 DAY), NOW(), 'doctor'),
(10, 'doctor09', '$2a$10$Tquw5Y7aKXgnximaBqzsv.3DwC881lWwfocYah7Gsa4qntnHW12ny', 'wulei@his.local', '13800000010', 1, DATE_SUB(NOW(), INTERVAL 70 DAY), NOW(), 'doctor'),
(11, 'doctor10', '$2a$10$Tquw5Y7aKXgnximaBqzsv.3DwC881lWwfocYah7Gsa4qntnHW12ny', 'xuli@his.local', '13800000011', 1, DATE_SUB(NOW(), INTERVAL 65 DAY), NOW(), 'doctor'),
(12, 'cashier', '$2a$10$v8w/2Cn.DkU6ILeESkEubuHyjQQvtedTsENFAdeoaq8x6uQxO4cn.', 'lijing@his.local', '13800000012', 1, DATE_SUB(NOW(), INTERVAL 60 DAY), NOW(), 'cashier'),
(13, 'pharmacist', '$2a$10$AL417JxnqYLb.i7.VNWWr.f/tvxHsPzu.RoBqgQOzGsPatmCR52l2', 'wangmin@his.local', '13800000013', 1, DATE_SUB(NOW(), INTERVAL 55 DAY), NOW(), 'pharmacist'),
(14, 'patient', '$2a$10$T9B225sIaxAxG3PW4ahi8eLwcS1o5sK82jfZSxiBlfLc1vvn5pL82', 'wangfang@his.local', '13800000014', 1, DATE_SUB(NOW(), INTERVAL 50 DAY), NOW(), 'patient'),
(15, 'patient02', '$2a$10$T9B225sIaxAxG3PW4ahi8eLwcS1o5sK82jfZSxiBlfLc1vvn5pL82', 'lijianguo@his.local', '13800000015', 1, DATE_SUB(NOW(), INTERVAL 48 DAY), NOW(), 'patient'),
(16, 'patient03', '$2a$10$T9B225sIaxAxG3PW4ahi8eLwcS1o5sK82jfZSxiBlfLc1vvn5pL82', 'zhangxiaoming@his.local', '13800000016', 1, DATE_SUB(NOW(), INTERVAL 46 DAY), NOW(), 'patient'),
(17, 'patient04', '$2a$10$T9B225sIaxAxG3PW4ahi8eLwcS1o5sK82jfZSxiBlfLc1vvn5pL82', 'liusiyu@his.local', '13800000017', 1, DATE_SUB(NOW(), INTERVAL 44 DAY), NOW(), 'patient'),
(18, 'patient05', '$2a$10$T9B225sIaxAxG3PW4ahi8eLwcS1o5sK82jfZSxiBlfLc1vvn5pL82', 'chenchen@his.local', '13800000018', 1, DATE_SUB(NOW(), INTERVAL 42 DAY), NOW(), 'patient'),
(19, 'patient06', '$2a$10$T9B225sIaxAxG3PW4ahi8eLwcS1o5sK82jfZSxiBlfLc1vvn5pL82', 'yangxue@his.local', '13800000019', 1, DATE_SUB(NOW(), INTERVAL 40 DAY), NOW(), 'patient'),
(20, 'patient07', '$2a$10$T9B225sIaxAxG3PW4ahi8eLwcS1o5sK82jfZSxiBlfLc1vvn5pL82', 'zhaolei@his.local', '13800000020', 1, DATE_SUB(NOW(), INTERVAL 38 DAY), NOW(), 'patient'),
(21, 'patient08', '$2a$10$T9B225sIaxAxG3PW4ahi8eLwcS1o5sK82jfZSxiBlfLc1vvn5pL82', 'zhoumin@his.local', '13800000021', 1, DATE_SUB(NOW(), INTERVAL 36 DAY), NOW(), 'patient'),
(22, 'patient09', '$2a$10$T9B225sIaxAxG3PW4ahi8eLwcS1o5sK82jfZSxiBlfLc1vvn5pL82', 'wutao@his.local', '13800000022', 1, DATE_SUB(NOW(), INTERVAL 34 DAY), NOW(), 'patient'),
(23, 'patient10', '$2a$10$T9B225sIaxAxG3PW4ahi8eLwcS1o5sK82jfZSxiBlfLc1vvn5pL82', 'xujing@his.local', '13800000023', 1, DATE_SUB(NOW(), INTERVAL 32 DAY), NOW(), 'patient'),
(24, 'patient11', '$2a$10$T9B225sIaxAxG3PW4ahi8eLwcS1o5sK82jfZSxiBlfLc1vvn5pL82', 'sunhao@his.local', '13800000024', 1, DATE_SUB(NOW(), INTERVAL 30 DAY), NOW(), 'patient'),
(25, 'patient12', '$2a$10$T9B225sIaxAxG3PW4ahi8eLwcS1o5sK82jfZSxiBlfLc1vvn5pL82', 'mali@his.local', '13800000025', 1, DATE_SUB(NOW(), INTERVAL 28 DAY), NOW(), 'patient'),
(26, 'patient13', '$2a$10$T9B225sIaxAxG3PW4ahi8eLwcS1o5sK82jfZSxiBlfLc1vvn5pL82', 'zhuqiang@his.local', '13800000026', 1, DATE_SUB(NOW(), INTERVAL 26 DAY), NOW(), 'patient'),
(27, 'patient14', '$2a$10$T9B225sIaxAxG3PW4ahi8eLwcS1o5sK82jfZSxiBlfLc1vvn5pL82', 'huyue@his.local', '13800000027', 1, DATE_SUB(NOW(), INTERVAL 24 DAY), NOW(), 'patient'),
(28, 'patient15', '$2a$10$T9B225sIaxAxG3PW4ahi8eLwcS1o5sK82jfZSxiBlfLc1vvn5pL82', 'guopeng@his.local', '13800000028', 1, DATE_SUB(NOW(), INTERVAL 22 DAY), NOW(), 'patient');

INSERT INTO userinfo (id, userid, realname, gender, avatar) VALUES
(1, 2, '张伟', 1, NULL),
(2, 3, '李娜', 2, NULL),
(3, 4, '王强', 1, NULL),
(4, 5, '刘洋', 2, NULL),
(5, 6, '陈静', 2, NULL),
(6, 7, '杨帆', 1, NULL),
(7, 8, '赵敏', 2, NULL),
(8, 9, '周杰', 1, NULL),
(9, 10, '吴磊', 1, NULL),
(10, 11, '徐丽', 2, NULL),
(11, 12, '李静', 2, NULL),
(12, 13, '王敏', 2, NULL),
(13, 1, '系统管理员', 1, NULL),
(14, 14, '王芳', 2, NULL),
(15, 15, '李建国', 1, NULL),
(16, 16, '张小明', 1, NULL),
(17, 17, '刘思雨', 2, NULL),
(18, 18, '陈晨', 1, NULL),
(19, 19, '杨雪', 2, NULL),
(20, 20, '赵磊', 1, NULL),
(21, 21, '周敏', 2, NULL),
(22, 22, '吴涛', 1, NULL),
(23, 23, '徐静', 2, NULL),
(24, 24, '孙浩', 1, NULL),
(25, 25, '马丽', 2, NULL),
(26, 26, '朱强', 1, NULL),
(27, 27, '胡月', 2, NULL),
(28, 28, '郭鹏', 1, NULL);

INSERT INTO department (department_id, department_name, introduction, location, phone) VALUES
('D001', '内科', '诊治呼吸、消化、心血管、内分泌等内科常见病与多发病。', '门诊楼2层东侧', '0731-88910001'),
('D002', '外科', '开展普外科、泌尿外科常见疾病诊疗及门诊手术。', '门诊楼3层东侧', '0731-88910002'),
('D003', '儿科', '诊治儿童呼吸、消化、感染及生长发育相关疾病。', '门诊楼1层西侧', '0731-88910003'),
('D004', '妇产科', '提供妇科常见病、孕期保健及产科门诊服务。', '门诊楼4层东侧', '0731-88910004'),
('D005', '眼科', '诊治结膜炎、干眼症、白内障及视力异常等眼病。', '门诊楼3层西侧', '0731-88910005'),
('D006', '骨科', '诊治颈肩腰腿痛、关节炎、骨折及运动损伤。', '门诊楼2层西侧', '0731-88910006'),
('D007', '皮肤科', '诊治湿疹、痤疮、荨麻疹、皮炎等皮肤疾病。', '门诊楼1层东侧', '0731-88910007'),
('D008', '耳鼻喉科', '诊治鼻炎、咽炎、中耳炎、耳鸣及听力障碍。', '门诊楼4层西侧', '0731-88910008'),
('D009', '中医科', '提供中医内科、针灸推拿及慢病调理服务。', '门诊楼5层东侧', '0731-88910009'),
('D010', '急诊科', '承担急危重症患者的首诊、抢救与转诊工作。', '急诊楼1层', '0731-88910010');

INSERT INTO doctor (doctor_id, user_id, name, department_id, title, userinfo_id) VALUES
(1, '2', '张伟', 'D001', '主任医师', 1),
(2, '3', '李娜', 'D003', '副主任医师', 2),
(3, '4', '王强', 'D002', '主治医师', 3),
(4, '5', '刘洋', 'D004', '副主任医师', 4),
(5, '6', '陈静', 'D005', '主治医师', 5),
(6, '7', '杨帆', 'D006', '主任医师', 6),
(7, '8', '赵敏', 'D007', '主治医师', 7),
(8, '9', '周杰', 'D008', '副主任医师', 8),
(9, '10', '吴磊', 'D001', '住院医师', 9),
(10, '11', '徐丽', 'D009', '主任医师', 10);

INSERT INTO patient (patient_id, name, gender, age, address, id_card, contact, create_time, doctor_id) VALUES
(1, '王芳', '女', 34, '长沙市岳麓区麓山南路', '43010419920315****', '13800000014', DATE_SUB(NOW(), INTERVAL 20 DAY), 1),
(2, '李建国', '男', 58, '长沙市芙蓉区五一大道', '43010219670522****', '13800000015', DATE_SUB(NOW(), INTERVAL 19 DAY), 1),
(3, '张小明', '男', 8, '长沙市天心区韶山路', '43010320170608****', '13800000016', DATE_SUB(NOW(), INTERVAL 18 DAY), 2),
(4, '刘思雨', '女', 26, '长沙市开福区湘江中路', '43010519980912****', '13800000017', DATE_SUB(NOW(), INTERVAL 17 DAY), 4),
(5, '陈晨', '男', 42, '长沙市雨花区万家丽路', '43011119820130****', '13800000018', DATE_SUB(NOW(), INTERVAL 16 DAY), 6),
(6, '杨雪', '女', 31, '长沙市岳麓区梅溪湖路', '43010419930218****', '13800000019', DATE_SUB(NOW(), INTERVAL 15 DAY), 5),
(7, '赵磊', '男', 45, '长沙市芙蓉区人民路', '43010219780805****', '13800000020', DATE_SUB(NOW(), INTERVAL 14 DAY), 7),
(8, '周敏', '女', 39, '长沙市天心区芙蓉南路', '43010319841211****', '13800000021', DATE_SUB(NOW(), INTERVAL 13 DAY), 8),
(9, '吴涛', '男', 52, '长沙市开福区芙蓉北路', '43010519710307****', '13800000022', DATE_SUB(NOW(), INTERVAL 12 DAY), 3),
(10, '徐静', '女', 29, '长沙市雨花区香樟路', '43011119950520****', '13800000023', DATE_SUB(NOW(), INTERVAL 11 DAY), 10),
(11, '孙浩', '男', 61, '长沙市望城区雷锋大道', '43012219631128****', '13800000024', DATE_SUB(NOW(), INTERVAL 10 DAY), 9),
(12, '马丽', '女', 24, '长沙市岳麓区桐梓坡路', '43010420000116****', '13800000025', DATE_SUB(NOW(), INTERVAL 9 DAY), 4),
(13, '朱强', '男', 37, '长沙市天心区友谊路', '43010319860909****', '13800000026', DATE_SUB(NOW(), INTERVAL 8 DAY), 6),
(14, '胡月', '女', 19, '长沙市开福区东风路', '43010520050403****', '13800000027', DATE_SUB(NOW(), INTERVAL 7 DAY), 7),
(15, '郭鹏', '男', 48, '长沙市雨花区劳动路', '43011119760214****', '13800000028', DATE_SUB(NOW(), INTERVAL 6 DAY), 3);

INSERT INTO medical_record (record_id, patient_id, chief_complaint, present_illness, past_history, physical_exam, auxiliary_exam, diagnosis, entry_time) VALUES
(1, 1, '发热咳嗽3天', '3天前受凉后出现发热，最高体温38.6℃，伴咳嗽、咽痛，无胸闷气促。', '既往体健，否认药物过敏史。', '体温38.2℃，咽部充血，双肺呼吸音粗。', '血常规示白细胞正常，淋巴细胞比例升高。', '急性上呼吸道感染', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(2, 2, '头晕1周', '1周前无明显诱因出现头晕，晨起明显，伴颈项不适，无恶心呕吐。', '高血压病史8年，规律服用降压药。', '血压158/96mmHg，心肺听诊未见明显异常。', '心电图示窦性心律，左心室高电压。', '高血压2级', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 3, '咳嗽伴喘息2天', '2天前出现阵发性咳嗽，夜间加重，伴轻度喘息，无发热。', '既往有过敏性鼻炎史。', '双肺可闻及少量哮鸣音。', '胸片示双肺纹理增多。', '急性支气管炎', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(4, 4, '停经12周', '停经12周，伴轻微恶心，无腹痛及阴道流血。', '孕1产0，既往月经规律。', '腹软，子宫增大与孕周相符。', 'B超提示宫内单活胎，符合12周妊娠。', '妊娠12周', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(5, 5, '腰痛伴右下肢放射痛1月', '1月前久坐后出现腰痛，向右下肢放射，弯腰及咳嗽时加重。', '既往体健，长期伏案工作。', '腰椎活动受限，右侧直腿抬高试验阳性。', '腰椎MRI示L4/5椎间盘突出。', '腰椎间盘突出症', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(6, 6, '双眼红肿分泌物增多3天', '3天前出现双眼红肿、异物感，晨起分泌物增多。', '否认眼部外伤及手术史。', '双眼结膜充血，可见少量脓性分泌物。', '裂隙灯检查示结膜充血水肿。', '急性结膜炎', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(7, 7, '双上肢皮疹瘙痒1周', '1周前接触新洗涤用品后出现双上肢皮疹，伴明显瘙痒。', '既往有荨麻疹病史。', '双前臂可见散在红色丘疹，部分抓痕。', '过敏原检测示尘螨阳性。', '湿疹', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(8, 8, '鼻塞流涕反复2年', '2年来反复鼻塞、流清涕，遇冷空气加重，伴嗅觉减退。', '慢性鼻炎病史，未规律治疗。', '双侧鼻黏膜充血肿胀，下鼻甲肥大。', '鼻内镜示双下鼻甲肿大，鼻道少量分泌物。', '慢性鼻炎', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(9, 9, '转移性右下腹痛1天', '1天前出现脐周疼痛，后转移至右下腹，伴恶心、食欲下降。', '既往体健，无腹部手术史。', '右下腹麦氏点压痛、反跳痛，腹肌紧张。', '腹部B超示阑尾增粗，血常规白细胞升高。', '急性阑尾炎', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(10, 10, '入睡困难1月', '近1月工作压力大，入睡困难，易醒，白天乏力。', '既往体健，无精神疾病史。', '神志清楚，心肺未见明显异常。', '焦虑抑郁量表提示轻度焦虑。', '失眠', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(11, 11, '多饮多尿3月', '3月前出现多饮、多尿，伴体重下降约4kg。', '高血压病史5年。', '体型偏胖，双下肢无水肿。', '空腹血糖9.8mmol/L，糖化血红蛋白8.1%。', '2型糖尿病', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(12, 12, '月经紊乱2月', '近2月月经周期不规律，经量时多时少，无腹痛。', '既往月经规律，未婚。', '妇科检查未见明显异常。', '妇科B超示子宫内膜增厚。', '月经不调', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(13, 13, '双膝关节疼痛2年', '2年来双膝关节反复疼痛，上下楼加重，休息后缓解。', '长期从事体力劳动。', '双膝关节压痛，活动时有摩擦感。', '双膝X线示关节间隙变窄，骨质增生。', '膝关节骨性关节炎', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(14, 14, '面部丘疹1月', '1月前面部出现散在丘疹、粉刺，偶有红肿。', '既往体健，近期熬夜较多。', '面颊及额头可见丘疹、粉刺及少量脓疱。', '未见明显内分泌异常。', '痤疮', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(15, 15, '右侧腹股沟可复性包块1年', '1年前发现右侧腹股沟包块，站立及咳嗽时突出，平卧可回纳。', '既往体健，无腹部手术史。', '右侧腹股沟可触及约3cm包块，质软，可回纳。', '腹股沟B超示右侧腹股沟疝。', '腹股沟疝', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(16, 2, '高血压复诊', '规律服药后血压控制在140/85mmHg左右，偶有头晕。', '高血压病史8年。', '血压142/88mmHg，心肺未见明显异常。', '肾功能及电解质未见明显异常。', '高血压2级（复诊）', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(17, 6, '结膜炎复诊', '用药后眼红、分泌物明显减少，仍有轻微异物感。', '急性结膜炎病史。', '双眼结膜轻度充血。', '裂隙灯检查示结膜充血较前减轻。', '急性结膜炎（复诊）', NOW());

INSERT INTO doctor_schedule (schedule_id, doctor_id, schedule_date, shift_type, start_time, end_time, department_id, room_number, max_patients, remaining_slots, status)
SELECT ROW_NUMBER() OVER (ORDER BY doc.doctor_id, w.n, d.n, s.n) AS schedule_id,
       doc.doctor_id,
       DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL (w.n * 7 + d.n) DAY),
       CASE WHEN s.n = 0 THEN '上午' ELSE '下午' END,
       CASE WHEN s.n = 0 THEN '08:00' ELSE '14:00' END,
       CASE WHEN s.n = 0 THEN '12:00' ELSE '17:30' END,
       doc.department_id,
       CONCAT('R', LPAD(doc.doctor_id, 2, '0'), CASE WHEN s.n = 0 THEN 'A' ELSE 'B' END),
       CASE WHEN s.n = 0 THEN 20 ELSE 15 END,
       CASE WHEN s.n = 0 THEN 20 ELSE 15 END,
       1
FROM doctor doc
CROSS JOIN (SELECT 0 AS n UNION ALL SELECT 1) w
CROSS JOIN (SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4) d
CROSS JOIN (SELECT 0 AS n UNION ALL SELECT 1) s
CROSS JOIN (SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3) o
WHERE doc.doctor_id = (((((d.n * 4 + s.n * 4) % 10) + o.n) % 10) + 1);

INSERT INTO doctor_schedule (schedule_id, doctor_id, schedule_date, shift_type, start_time, end_time, department_id, room_number, max_patients, remaining_slots, status) VALUES
(81, 1, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 5 DAY), '上午', '08:00', '12:00', 'D001', 'R01W', 15, 15, 1),
(82, 5, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 5 DAY), '上午', '08:00', '12:00', 'D005', 'R05W', 15, 15, 1),
(83, 2, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 5 DAY), '下午', '14:00', '17:30', 'D003', 'R02W', 12, 12, 1),
(84, 6, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 5 DAY), '下午', '14:00', '17:30', 'D006', 'R06W', 12, 12, 1),
(85, 3, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 6 DAY), '上午', '08:00', '12:00', 'D002', 'R03W', 15, 15, 1),
(86, 7, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 6 DAY), '上午', '08:00', '12:00', 'D007', 'R07W', 15, 15, 1),
(87, 4, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 6 DAY), '下午', '14:00', '17:30', 'D004', 'R04W', 12, 12, 1),
(88, 8, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 6 DAY), '下午', '14:00', '17:30', 'D008', 'R08W', 12, 12, 1),
(89, 9, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 12 DAY), '上午', '08:00', '12:00', 'D001', 'R09W', 15, 15, 1),
(90, 3, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 12 DAY), '上午', '08:00', '12:00', 'D002', 'R03W', 15, 15, 1),
(91, 10, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 12 DAY), '下午', '14:00', '17:30', 'D009', 'R10W', 12, 12, 1),
(92, 4, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 12 DAY), '下午', '14:00', '17:30', 'D004', 'R04W', 12, 12, 1),
(93, 5, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 13 DAY), '上午', '08:00', '12:00', 'D005', 'R05W', 15, 15, 1),
(94, 1, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 13 DAY), '上午', '08:00', '12:00', 'D001', 'R01W', 15, 15, 1),
(95, 6, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 13 DAY), '下午', '14:00', '17:30', 'D006', 'R06W', 12, 12, 1),
(96, 2, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 13 DAY), '下午', '14:00', '17:30', 'D003', 'R02W', 12, 12, 1);

INSERT INTO consulting_room (department_id, room_number, room_type, capacity, location, status, remark)
SELECT department_id, CONCAT(department_id, '-1'), '普通诊室', 4, CONCAT(department_name, '诊区1号房间'), 1, '常规门诊'
FROM department;

INSERT INTO consulting_room (department_id, room_number, room_type, capacity, location, status, remark)
SELECT department_id, CONCAT(department_id, '-2'), '专家诊室', 2, CONCAT(department_name, '诊区2号房间'), 1, '专家门诊'
FROM department;

INSERT INTO drug (drug_id, drug_name, drug_type, indication, applicable_department, dosage_form, specification, manufacturer, unit_price, stock_quantity, status) VALUES
(1, '阿莫西林胶囊', '抗生素', '敏感菌所致的呼吸道、泌尿道及皮肤软组织感染。', 'D001', '胶囊', '0.25g*24粒', '华北制药股份有限公司', 12.50, 120, 1),
(2, '头孢克肟分散片', '抗生素', '敏感菌引起的支气管炎、肺炎、尿路感染等。', 'D001', '片剂', '0.1g*12片', '广州白云山制药股份有限公司', 28.00, 80, 1),
(3, '布洛芬缓释胶囊', '解热镇痛', '缓解轻至中度疼痛及普通感冒引起的发热。', 'D001', '胶囊', '0.3g*20粒', '中美天津史克制药有限公司', 18.50, 150, 1),
(4, '对乙酰氨基酚片', '解热镇痛', '用于普通感冒或流行性感冒引起的发热及轻中度疼痛。', 'D001', '片剂', '0.5g*20片', '哈药集团制药总厂', 9.80, 200, 1),
(5, '复方氨酚烷胺片', '感冒用药', '缓解感冒引起的发热、头痛、鼻塞、流涕等症状。', 'D001', '片剂', '12片', '吉林修正药业集团', 15.00, 90, 1),
(6, '奥美拉唑肠溶胶囊', '消化系统', '用于胃溃疡、十二指肠溃疡及反流性食管炎。', 'D001', '胶囊', '20mg*14粒', '阿斯利康制药有限公司', 22.00, 70, 1),
(7, '蒙脱石散', '消化系统', '用于成人及儿童急、慢性腹泻。', 'D003', '散剂', '3g*10袋', '博福-益普生（天津）制药有限公司', 16.50, 60, 1),
(8, '盐酸左氧氟沙星片', '抗生素', '用于敏感菌引起的呼吸道、泌尿道及皮肤感染。', 'D001', '片剂', '0.5g*6片', '扬子江药业集团有限公司', 32.00, 50, 1),
(9, '阿奇霉素分散片', '抗生素', '用于敏感菌引起的支气管炎、肺炎及皮肤软组织感染。', 'D003', '片剂', '0.25g*6片', '辉瑞制药有限公司', 26.00, 65, 1),
(10, '氯雷他定片', '抗过敏', '缓解过敏性鼻炎、荨麻疹及其他过敏性皮肤病症状。', 'D007', '片剂', '10mg*10片', '西安杨森制药有限公司', 14.00, 110, 1),
(11, '硝苯地平控释片', '心血管', '用于高血压及冠心病慢性稳定型心绞痛。', 'D001', '片剂', '30mg*7片', '拜耳医药保健有限公司', 35.00, 40, 1),
(12, '缬沙坦胶囊', '心血管', '用于治疗轻中度原发性高血压。', 'D001', '胶囊', '80mg*14粒', '北京诺华制药有限公司', 42.00, 35, 1),
(13, '二甲双胍片', '降糖药', '用于2型糖尿病，尤其适用于肥胖患者。', 'D001', '片剂', '0.5g*20片', '中美上海施贵宝制药有限公司', 19.00, 75, 1),
(14, '阿卡波糖片', '降糖药', '配合饮食控制用于2型糖尿病。', 'D001', '片剂', '50mg*30片', '拜耳医药保健有限公司', 46.00, 30, 1),
(15, '维生素C片', '维生素', '用于预防和治疗维生素C缺乏症。', 'D001', '片剂', '0.1g*100片', '华中药业股份有限公司', 8.00, 200, 1),
(16, '复方甘草片', '呼吸系统', '用于镇咳祛痰。', 'D001', '片剂', '100片', '桂林南药股份有限公司', 12.00, 85, 1),
(17, '急支糖浆', '呼吸系统', '清热化痰、宣肺止咳，用于急性支气管炎。', 'D001', '糖浆剂', '200ml', '太极集团重庆涪陵制药厂', 24.00, 55, 1),
(18, '云南白药气雾剂', '外用', '用于跌打损伤、瘀血肿痛及肌肉酸痛。', 'D006', '气雾剂', '85g', '云南白药集团股份有限公司', 38.00, 45, 1),
(19, '碘伏消毒液', '外用', '用于皮肤和黏膜的消毒。', 'D002', '消毒剂', '500ml', '山东利尔康医疗科技股份有限公司', 15.00, 60, 1),
(20, '红霉素软膏', '外用', '用于脓疱疮等化脓性皮肤病及小面积烧伤感染。', 'D007', '软膏剂', '10g', '白云山制药总厂', 6.50, 100, 1),
(21, '地塞米松磷酸钠注射液', '激素类', '用于过敏性与炎症性疾病。', 'D001', '注射剂', '1ml:5mg*10支', '天津金耀药业有限公司', 21.00, 25, 1),
(22, '0.9%氯化钠注射液', '输液', '用于补充水分和电解质。', 'D002', '注射剂', '500ml', '湖南科伦制药有限公司', 4.50, 300, 1),
(23, '5%葡萄糖注射液', '输液', '用于补充能量和体液。', 'D002', '注射剂', '500ml', '湖南科伦制药有限公司', 5.00, 280, 1),
(24, '小儿氨酚黄那敏颗粒', '儿科用药', '用于缓解儿童普通感冒引起的发热、头痛、鼻塞。', 'D003', '颗粒剂', '6g*10袋', '葵花药业集团股份有限公司', 13.00, 95, 1),
(25, '保和丸', '中成药', '消食导滞和胃，用于食积停滞、脘腹胀满。', 'D009', '丸剂', '6g*10丸', '北京同仁堂科技发展股份有限公司', 18.00, 50, 1),
(26, '藿香正气水', '中成药', '解表化湿、理气和中，用于外感风寒、内伤湿滞。', 'D009', '酊剂', '10ml*10支', '太极集团重庆涪陵制药厂', 12.00, 70, 1),
(27, '复方丹参滴丸', '中成药', '活血化瘀、理气止痛，用于胸中憋闷、心绞痛。', 'D009', '滴丸剂', '27g', '天士力医药集团股份有限公司', 28.00, 40, 1),
(28, '氯霉素滴眼液', '眼科用药', '用于敏感菌引起的眼部感染。', 'D005', '滴眼剂', '8ml', '山东博士伦福瑞达制药有限公司', 7.50, 80, 1),
(29, '氧氟沙星滴耳液', '耳鼻喉用药', '用于敏感菌引起的中耳炎、外耳道炎。', 'D008', '滴耳剂', '5ml', '珠海联邦制药股份有限公司', 9.00, 60, 1),
(30, '创可贴', '外用', '用于小面积擦伤、割伤的临时包扎。', 'D002', '贴剂', '100片', '云南白药集团股份有限公司', 10.00, 150, 1);

INSERT INTO simple_reservation (id, department_id, doctor_id, patient_id, schedule_id, patient_name, patient_age, patient_phone, reservation_status, visit_status, register_status, creat_time) VALUES
(1, 'D001', 1, 1, 1, '王芳', 34, '13800000014', 1, 1, 2, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(2, 'D001', 1, 2, 2, '李建国', 58, '13800000015', 1, 0, 1, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 'D003', 2, 3, 6, '张小明', 8, '13800000016', 1, 1, 2, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(4, 'D004', 4, 4, 16, '刘思雨', 26, '13800000017', 1, 0, 1, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(5, 'D006', 6, 5, 26, '陈晨', 42, '13800000018', 1, 1, 2, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(6, 'D005', 5, 6, 21, '杨雪', 31, '13800000019', 1, 0, 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(7, 'D007', 7, 7, 31, '赵磊', 45, '13800000020', 1, 1, 2, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(8, 'D008', 8, 8, 36, '周敏', 39, '13800000021', 1, 0, 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(9, 'D002', 3, 9, 11, '吴涛', 52, '13800000022', 1, 1, 2, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(10, 'D009', 10, 10, 46, '徐静', 29, '13800000023', 1, 0, 1, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(11, 'D001', 9, 11, 41, '孙浩', 61, '13800000024', 1, 0, 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(12, 'D004', 4, 12, 17, '马丽', 24, '13800000025', 1, 0, 1, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(13, 'D006', 6, 13, 27, '朱强', 37, '13800000026', 1, 1, 2, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(14, 'D007', 7, 14, 32, '胡月', 19, '13800000027', 1, 0, 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(15, 'D002', 3, 15, 12, '郭鹏', 48, '13800000028', 1, 1, 2, DATE_SUB(NOW(), INTERVAL 3 DAY));

UPDATE simple_reservation r
SET r.schedule_id = (
    SELECT s.schedule_id
    FROM doctor_schedule s
    WHERE s.doctor_id = r.doctor_id
    ORDER BY ABS(DATEDIFF(s.schedule_date, DATE(r.creat_time))) ASC, s.schedule_id ASC
    LIMIT 1
);

INSERT INTO prescription (prescription_id, register_id, patient_id, doctor_id, prescription_date, drug_info, diagnosis, total_fee, status) VALUES
(1, 1, 1, 1, DATE_SUB(NOW(), INTERVAL 3 DAY), '阿莫西林胶囊 x2；布洛芬缓释胶囊 x1', '急性上呼吸道感染', 43.50, 2),
(2, 2, 2, 1, DATE_SUB(NOW(), INTERVAL 2 DAY), '硝苯地平控释片 x1；缬沙坦胶囊 x1', '高血压2级', 77.00, 1),
(3, 3, 3, 2, DATE_SUB(NOW(), INTERVAL 3 DAY), '阿奇霉素分散片 x1；急支糖浆 x1', '急性支气管炎', 50.00, 2),
(4, 4, 4, 4, DATE_SUB(NOW(), INTERVAL 2 DAY), '维生素C片 x1', '妊娠12周', 8.00, 1),
(5, 5, 5, 6, DATE_SUB(NOW(), INTERVAL 4 DAY), '布洛芬缓释胶囊 x2；云南白药气雾剂 x1', '腰椎间盘突出症', 75.00, 2),
(6, 6, 6, 5, DATE_SUB(NOW(), INTERVAL 1 DAY), '氯霉素滴眼液 x1', '急性结膜炎', 7.50, 1),
(7, 7, 7, 7, DATE_SUB(NOW(), INTERVAL 3 DAY), '氯雷他定片 x1；红霉素软膏 x1', '湿疹', 20.50, 2),
(8, 8, 8, 8, DATE_SUB(NOW(), INTERVAL 1 DAY), '氧氟沙星滴耳液 x1；氯雷他定片 x1', '慢性鼻炎', 23.00, 1),
(9, 9, 9, 3, DATE_SUB(NOW(), INTERVAL 3 DAY), '头孢克肟分散片 x2；0.9%氯化钠注射液 x2', '急性阑尾炎', 65.00, 2),
(10, 10, 10, 10, DATE_SUB(NOW(), INTERVAL 2 HOUR), '复方丹参滴丸 x1；维生素C片 x1', '失眠', 36.00, 0),
(11, 11, 11, 9, DATE_SUB(NOW(), INTERVAL 1 DAY), '二甲双胍片 x2；阿卡波糖片 x1', '2型糖尿病', 84.00, 1),
(12, 12, 12, 4, DATE_SUB(NOW(), INTERVAL 1 HOUR), '保和丸 x1；维生素C片 x1', '月经不调', 26.00, 0),
(13, 13, 13, 6, DATE_SUB(NOW(), INTERVAL 4 DAY), '布洛芬缓释胶囊 x1；云南白药气雾剂 x1', '膝关节骨性关节炎', 56.50, 2),
(14, 14, 14, 7, DATE_SUB(NOW(), INTERVAL 1 DAY), '红霉素软膏 x1', '痤疮', 6.50, 1),
(15, 15, 15, 3, DATE_SUB(NOW(), INTERVAL 3 DAY), '头孢克肟分散片 x1；0.9%氯化钠注射液 x1', '腹股沟疝', 32.50, 2);

INSERT INTO prescription_item (prescription_id, medicine_id, quantity, unit_price) VALUES
(1, 1, 2, 12.50),
(1, 3, 1, 18.50),
(2, 11, 1, 35.00),
(2, 12, 1, 42.00),
(3, 9, 1, 26.00),
(3, 17, 1, 24.00),
(4, 15, 1, 8.00),
(5, 3, 2, 18.50),
(5, 18, 1, 38.00),
(6, 28, 1, 7.50),
(7, 10, 1, 14.00),
(7, 20, 1, 6.50),
(8, 29, 1, 9.00),
(8, 10, 1, 14.00),
(9, 2, 2, 28.00),
(9, 22, 2, 4.50),
(10, 27, 1, 28.00),
(10, 15, 1, 8.00),
(11, 13, 2, 19.00),
(11, 14, 1, 46.00),
(12, 25, 1, 18.00),
(12, 15, 1, 8.00),
(13, 3, 1, 18.50),
(13, 18, 1, 38.00),
(14, 20, 1, 6.50),
(15, 2, 1, 28.00),
(15, 22, 1, 4.50);

INSERT INTO charge_record (charge_id, prescription_id, charge_time, amount, cashier_id) VALUES
(1, 1, DATE_ADD(DATE_SUB(NOW(), INTERVAL 3 DAY), INTERVAL 30 MINUTE), 43.50, 12),
(2, 2, DATE_ADD(DATE_SUB(NOW(), INTERVAL 2 DAY), INTERVAL 30 MINUTE), 77.00, 12),
(3, 3, DATE_ADD(DATE_SUB(NOW(), INTERVAL 3 DAY), INTERVAL 30 MINUTE), 50.00, 12),
(4, 4, DATE_ADD(DATE_SUB(NOW(), INTERVAL 2 DAY), INTERVAL 30 MINUTE), 8.00, 12),
(5, 5, DATE_ADD(DATE_SUB(NOW(), INTERVAL 4 DAY), INTERVAL 30 MINUTE), 75.00, 12),
(6, 6, DATE_ADD(DATE_SUB(NOW(), INTERVAL 1 DAY), INTERVAL 30 MINUTE), 7.50, 12),
(7, 7, DATE_ADD(DATE_SUB(NOW(), INTERVAL 3 DAY), INTERVAL 30 MINUTE), 20.50, 12),
(8, 8, DATE_ADD(DATE_SUB(NOW(), INTERVAL 1 DAY), INTERVAL 30 MINUTE), 23.00, 12),
(9, 9, DATE_ADD(DATE_SUB(NOW(), INTERVAL 3 DAY), INTERVAL 30 MINUTE), 65.00, 12),
(10, 11, DATE_ADD(DATE_SUB(NOW(), INTERVAL 1 DAY), INTERVAL 30 MINUTE), 84.00, 12),
(11, 13, DATE_ADD(DATE_SUB(NOW(), INTERVAL 4 DAY), INTERVAL 30 MINUTE), 56.50, 12),
(12, 14, DATE_ADD(DATE_SUB(NOW(), INTERVAL 1 DAY), INTERVAL 30 MINUTE), 6.50, 12),
(13, 15, DATE_ADD(DATE_SUB(NOW(), INTERVAL 3 DAY), INTERVAL 30 MINUTE), 32.50, 12);

INSERT INTO audit_log (operator_id, operator_name, role, action, detail, created_time)
SELECT u.id, ui.realname, 'patient', 'REGISTER',
       CONCAT('挂号单ID=', r.id, '，患者=', r.patient_name, '，医生ID=', r.doctor_id, '，排班ID=', r.schedule_id),
       r.creat_time
FROM simple_reservation r
JOIN userinfo ui ON ui.realname = r.patient_name
JOIN user u ON u.id = ui.userid;

INSERT INTO audit_log (operator_id, operator_name, role, action, detail, created_time)
SELECT CAST(d.user_id AS UNSIGNED), d.name, 'doctor', 'PRESCRIBE',
       CONCAT('处方ID=', p.prescription_id, '，挂号单ID=', p.register_id, '，金额=', p.total_fee),
       p.prescription_date
FROM prescription p
JOIN doctor d ON p.doctor_id = d.doctor_id;

INSERT INTO audit_log (operator_id, operator_name, role, action, detail, created_time)
SELECT 12, '李静', 'cashier', 'CHARGE',
       CONCAT('处方ID=', c.prescription_id, '，收费金额=', c.amount),
       c.charge_time
FROM charge_record c;

INSERT INTO audit_log (operator_id, operator_name, role, action, detail, created_time)
SELECT 13, '王敏', 'pharmacist', 'DISPENSE',
       CONCAT('处方ID=', p.prescription_id, '，发药完成'),
       DATE_ADD(p.prescription_date, INTERVAL 1 DAY)
FROM prescription p
WHERE p.status = 2;
