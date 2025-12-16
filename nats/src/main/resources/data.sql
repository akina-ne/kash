-- users の初期データ（パスワードは現在プレーンテキストで格納）
-- 注意: Spring Security で DB を認証に使う場合、パスワードは BCrypt 等でエンコードして保存する必要があります。
INSERT INTO users (username, password, role, created_at) VALUES
  ('kash', 'nats', 'USER', CURRENT_TIMESTAMP),
  ('admin', 'adminpass', 'ADMIN', CURRENT_TIMESTAMP);

INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('onigiri.jpg', '/images/onigiri.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'おにぎり');

INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('kinkakuji.jpg', '/images/kinkakuji.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'きんかくじ');

INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('goghs_sunflowers.jpg', '/images/goghs_sunflowers.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'ごっほのひまわり');

INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('harrypotter.jpg', '/images/harrypotter.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'はりーぽったー');

INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('kyabetsu.jpg', '/images/kyabetsu.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'きゃべつ');

INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('lion.jpg', '/images/lion.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'らいおん');

INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('Mtfuji.jpg', '/images/Mtfuji.jpg', 'kash', 'image/jpeg
', CURRENT_TIMESTAMP, 'ふじさん');

INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('medama_yaki.jpg', '/images/medama_yaki.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'めだまやき');

INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('tokyo_tower.jpg', '/images/tokyo_tower.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'とうきょうたわー');

INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('hepfive.jpg', '/images/hepfive.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'へっぷふぁいぶ');
