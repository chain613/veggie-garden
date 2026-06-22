CREATE DATABASE IF NOT EXISTS veggie_garden
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE veggie_garden;

-- 用户
CREATE TABLE t_user (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  phone VARCHAR(11) NOT NULL UNIQUE,
  nickname VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 蔬菜品种
CREATE TABLE t_vegetable (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  growth_days INT NOT NULL,
  season SET('春','夏','秋','冬') NOT NULL,
  water_need INT COMMENT '1-5 需水量',
  sun_need INT COMMENT '1-5 需光量',
  nutrient_need INT COMMENT '1-5 需肥量',
  height_level INT COMMENT '1-3 高度等级',
  is_vine TINYINT(1) DEFAULT 0,
  need_frame TINYINT(1) DEFAULT 0,
  need_topping TINYINT(1) DEFAULT 0,
  topping_forbidden TINYINT(1) DEFAULT 0,
  harvest_type ENUM('whole','continuous') NOT NULL DEFAULT 'whole',
  fruit_interval_days INT DEFAULT NULL,
  fruit_mature_days INT DEFAULT NULL,
  fruit_overripe_days INT DEFAULT NULL
);

-- 农家小院
CREATE TABLE t_garden (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  region ENUM('zhongyuan','zhongnan') NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES t_user(id)
);

-- 植株实例
CREATE TABLE t_plant (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  garden_id BIGINT NOT NULL,
  vegetable_id INT NOT NULL,
  grid_x INT NOT NULL,
  grid_y INT NOT NULL,
  planted_at DATE NOT NULL,
  growth_stage TINYINT DEFAULT 0 COMMENT '0种子 1苗 2生长 3成熟',
  health_score DECIMAL(3,2) DEFAULT 1.00,
  water_level DECIMAL(3,2) DEFAULT 0.50,
  fertilizer_level DECIMAL(3,2) DEFAULT 0.50,
  root_mass DECIMAL(5,2) DEFAULT 0.05 COMMENT '根系质量',
  leaf_mass DECIMAL(5,2) DEFAULT 0.05 COMMENT '叶片质量',
  stem_mass DECIMAL(5,2) DEFAULT 0.02 COMMENT '茎干质量',
  glucose DECIMAL(5,2) DEFAULT 0 COMMENT '当日光合产物',
  micro_index DECIMAL(3,2) DEFAULT 0.80 COMMENT '微量元素综合指数',
  ventilation DECIMAL(3,2) DEFAULT 1.00 COMMENT '通风系数',
  bolting TINYINT(1) DEFAULT 0 COMMENT '抽薹标志',
  is_fruiting TINYINT(1) DEFAULT 0,
  FOREIGN KEY (garden_id) REFERENCES t_garden(id),
  FOREIGN KEY (vegetable_id) REFERENCES t_vegetable(id)
);

-- 杂草
CREATE TABLE t_weed (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  garden_id BIGINT NOT NULL,
  grid_x INT NOT NULL,
  grid_y INT NOT NULL,
  growth_stage TINYINT DEFAULT 0 COMMENT '0幼苗 1成株 2结籽',
  appeared_at DATE NOT NULL,
  FOREIGN KEY (garden_id) REFERENCES t_garden(id)
);

-- 果实（连续采收型）
CREATE TABLE t_plant_fruit (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  plant_id BIGINT NOT NULL,
  appeared_at DATE NOT NULL,
  mature_at DATE NOT NULL,
  overripe_at DATE NOT NULL,
  harvested_at DATE,
  quality_score DECIMAL(3,2) DEFAULT 1.00,
  FOREIGN KEY (plant_id) REFERENCES t_plant(id)
);

-- 仓库
CREATE TABLE t_warehouse (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  vegetable_id INT NOT NULL,
  quantity INT DEFAULT 0,
  quality_avg DECIMAL(3,2) DEFAULT 0,
  harvested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES t_user(id),
  FOREIGN KEY (vegetable_id) REFERENCES t_vegetable(id)
);

-- 知识库
CREATE TABLE t_knowledge (
  id INT AUTO_INCREMENT PRIMARY KEY,
  vegetable_id INT,
  category VARCHAR(50) NOT NULL,
  title VARCHAR(200) NOT NULL,
  content_text TEXT NOT NULL,
  FOREIGN KEY (vegetable_id) REFERENCES t_vegetable(id)
);

-- NPC 对话库
CREATE TABLE t_npc_dialog (
  id INT AUTO_INCREMENT PRIMARY KEY,
  trigger_event VARCHAR(100) NOT NULL,
  vegetable_id INT,
  season VARCHAR(10),
  dialog_text TEXT NOT NULL
);

-- 玩家已解锁知识
CREATE TABLE t_player_knowledge (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  knowledge_id INT NOT NULL,
  unlocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES t_user(id),
  FOREIGN KEY (knowledge_id) REFERENCES t_knowledge(id)
);

-- 物资定义
CREATE TABLE t_item (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  type ENUM('seed','tool','fertilizer','pesticide','organic') NOT NULL,
  description VARCHAR(500)
);

-- 背包
CREATE TABLE t_inventory (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  item_id INT NOT NULL,
  quantity INT DEFAULT 0,
  FOREIGN KEY (user_id) REFERENCES t_user(id),
  FOREIGN KEY (item_id) REFERENCES t_item(id)
);

-- 好友
CREATE TABLE t_friend (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  friend_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES t_user(id),
  FOREIGN KEY (friend_id) REFERENCES t_user(id)
);

-- 访问记录
CREATE TABLE t_visit (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  visitor_id BIGINT NOT NULL,
  garden_id BIGINT NOT NULL,
  visited_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (visitor_id) REFERENCES t_user(id),
  FOREIGN KEY (garden_id) REFERENCES t_garden(id)
);

-- ========================
-- 种子数据
-- ========================

-- 10 种蔬菜
INSERT INTO t_vegetable (name, growth_days, season, water_need, sun_need, nutrient_need, height_level, is_vine, need_frame, need_topping, topping_forbidden, harvest_type, fruit_interval_days, fruit_mature_days, fruit_overripe_days) VALUES
('菠菜', 40, '春,秋', 3, 3, 2, 1, 0, 0, 0, 0, 'whole', NULL, NULL, NULL),
('胡萝卜', 90, '秋,春', 2, 4, 3, 1, 0, 0, 0, 0, 'whole', NULL, NULL, NULL),
('番茄', 70, '春,夏', 4, 5, 4, 3, 0, 0, 1, 0, 'continuous', 7, 5, 7),
('茄子', 75, '春,夏', 4, 5, 4, 2, 0, 0, 0, 0, 'continuous', 8, 5, 6),
('油菜', 30, '春,秋', 2, 3, 2, 1, 0, 0, 0, 0, 'whole', NULL, NULL, NULL),
('黄瓜', 65, '春,夏', 4, 5, 4, 3, 1, 1, 0, 0, 'continuous', 5, 5, 8),
('西葫芦', 55, '春,夏', 3, 5, 4, 2, 0, 0, 0, 0, 'continuous', 7, 5, 6),
('白菜', 60, '秋', 3, 3, 3, 1, 0, 0, 0, 1, 'whole', NULL, NULL, NULL),
('香菜', 40, '春,秋', 2, 3, 2, 1, 0, 0, 0, 0, 'continuous', 5, 3, 4),
('葱', 80, '春,秋', 2, 4, 2, 2, 0, 0, 0, 0, 'continuous', 10, 5, 5);

-- 基础物资（19 项）
INSERT INTO t_item (name, type, description) VALUES
('油菜种子', 'seed', '新手入门，易种难精'),
('菠菜种子', 'seed', '春秋播种'),
('胡萝卜种子', 'seed', '根茎类蔬菜'),
('番茄种子', 'seed', '需打顶管理，错打主茎不结果'),
('茄子种子', 'seed', '连续结果型'),
('黄瓜种子', 'seed', '藤蔓植物，需搭架'),
('西葫芦种子', 'seed', '连续结果型'),
('白菜种子', 'seed', '秋播，禁打顶'),
('香菜种子', 'seed', '可多次采收叶'),
('葱种子', 'seed', '可多次采收'),
('水壶', 'tool', '浇水工具'),
('小锄头', 'tool', '除草工具'),
('有机肥', 'organic', '绿色施肥，品质加成高'),
('化肥', 'fertilizer', '化学肥料，速效'),
('农药', 'pesticide', '化学杀虫'),
('小苏打', 'organic', '天然除虫，兑水喷洒'),
('大蒜', 'organic', '天然除虫，捣碎兑水'),
('稻草人', 'tool', '放置防鸟'),
('竹竿', 'tool', '搭架材料');

-- NPC 对话（9 条初始）
INSERT INTO t_npc_dialog (trigger_event, vegetable_id, season, dialog_text) VALUES
('首次进入', NULL, NULL, '欢迎来到你的农家小院！种菜如做人，用心才能有好收成。'),
('首次种植', NULL, NULL, '先种棵油菜吧，这菜皮实，浇水就长。不过想收成好，还得下功夫。'),
('浇水过多', NULL, NULL, '水不是越多越好。中午大太阳底下浇水，菜根会烫伤的。'),
('发现虫害', NULL, NULL, '有虫子了！别急着上农药，试试小苏打兑水喷一喷。'),
('连续未浇水', NULL, NULL, '几天没浇水了，菜都蔫了。快去看看吧。'),
('中午浇水', NULL, NULL, '等等！现在中午大太阳，浇水会伤根的。等傍晚凉快了再说。'),
('密植过度', NULL, NULL, '菜种得太密了，互相抢光抢肥，都长不好。该间苗了。'),
('藤蔓需搭架', NULL, NULL, '黄瓜藤蔓长高了，该给它搭个架子爬了。'),
('大风过后', NULL, NULL, '昨晚风不小，去看看有没有倒伏的菜。');
