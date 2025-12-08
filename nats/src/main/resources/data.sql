-- users の初期データ（パスワードは現在プレーンテキストで格納）
-- 注意: Spring Security で DB を認証に使う場合、パスワードは BCrypt 等でエンコードして保存する必要があります。
INSERT INTO users (username, password, role, created_at) VALUES
  ('kash', 'nats', 'USER', CURRENT_TIMESTAMP),
  ('admin', 'adminpass', 'ADMIN', CURRENT_TIMESTAMP);

INSERT INTO images (file_name, file_path, owner_id, content_type, created_at)
VALUES ('onigiri.jpg','/images/onigiri.jpg','kash','image/jpeg', CURRENT_TIMESTAMP);
INSERT INTO images (file_name, file_path, owner_id, content_type, created_at)
VALUES ('kinkakuji.jpg','/images/kinkakuji.jpg','kash','image/jpeg', CURRENT_TIMESTAMP);
