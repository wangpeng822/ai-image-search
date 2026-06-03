CREATE TABLE image_asset (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  image_id VARCHAR(64) NOT NULL UNIQUE,
  tenant_id VARCHAR(64) NOT NULL,
  user_id VARCHAR(64) NOT NULL,
  title VARCHAR(255) NULL,
  oss_bucket VARCHAR(128) NOT NULL,
  oss_key VARCHAR(512) NOT NULL,
  oss_url VARCHAR(1024) NOT NULL,
  content_type VARCHAR(64) NOT NULL,
  file_size BIGINT NOT NULL DEFAULT 0,
  width INT NULL,
  height INT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'active',
  vector_status VARCHAR(32) NOT NULL DEFAULT 'pending',
  vector_retry_count INT NOT NULL DEFAULT 0,
  vector_error TEXT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_image_asset_tenant_user_status (tenant_id, user_id, status),
  KEY idx_image_asset_vector_status (vector_status),
  KEY idx_image_asset_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图片业务主表，MySQL 作为事实数据源';

CREATE TABLE image_vector_task (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  task_id VARCHAR(64) NOT NULL UNIQUE,
  image_id VARCHAR(64) NOT NULL,
  tenant_id VARCHAR(64) NOT NULL,
  user_id VARCHAR(64) NOT NULL,
  oss_url VARCHAR(1024) NOT NULL,
  task_type VARCHAR(32) NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'pending',
  retry_count INT NOT NULL DEFAULT 0,
  error_message TEXT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_vector_task_image_id (image_id),
  KEY idx_vector_task_tenant_status (tenant_id, status),
  KEY idx_vector_task_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图片向量任务表，用于追踪入库、重建和删除任务';

CREATE TABLE image_search_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  search_id VARCHAR(64) NOT NULL UNIQUE,
  tenant_id VARCHAR(64) NOT NULL,
  user_id VARCHAR(64) NOT NULL,
  search_type VARCHAR(32) NOT NULL,
  keyword VARCHAR(512) NULL,
  query_image_oss_url VARCHAR(1024) NULL,
  top_k INT NOT NULL DEFAULT 20,
  result_count INT NOT NULL DEFAULT 0,
  latency_ms INT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'success',
  error_message TEXT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_search_log_tenant_user_created (tenant_id, user_id, created_at),
  KEY idx_search_log_type_created (search_type, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图片搜索日志表，用于排障和统计';
