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

INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('jiyunomegami.jpg', '/images/jiyunomegami.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'じゆうのめがみ');

INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('kokkaigijido.jpg', '/images/kokkaigijido.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'こっかいぎじどう');

INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('panda.jpg', '/images/panda.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'ぱんだ');

INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('retasu.jpg', '/images/retasu.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'れたす');

INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('araiguma.jpg', '/images/araiguma.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'あらいぐま');

INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('firenchikuru-ra-.jpg', '/images/firenchikuru-ra-.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'ふれんちくるーらー');
INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('goryoukaku.jpg', '/images/goryoukaku.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'ごりょうかく');
INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('keisityou.jpg', '/images/keisityou.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'けいしちょう');
INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('kokuban.jpg', '/images/kokuban.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'こくばん');
INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('kuroneko.jpg', '/images/kuroneko.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'くろねこ');
INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('pentagon.jpg', '/images/pentagon.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'ぺんたごん');
INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('pyramid.jpg', '/images/pyramid.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'ぴらみっど');
INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('ressa-panda.jpg', '/images/ressa-panda.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'れっさーぱんだ');
INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('shironeko.jpg', '/images/shironeko.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'しろねこ');
INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('skytree.jpg', '/images/skytree.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'すかいつりー');
INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('sphinx.jpg', '/images/sphinx.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'すふぃんくす');
INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('tanuki.jpg', '/images/tanuki.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'たぬき');
INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('ti-ta-.jpg', '/images/ti-ta-.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'ちーたー');
INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('tora.jpg', '/images/tora.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'とら');

INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('tougarashi.jpg', '/images/tougarashi.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'とうがらし');

INSERT INTO images (file_name, file_path, owner_id, content_type, created_at, answer_kana)
VALUES ('whitehouse.jpg', '/images/whitehouse.jpg', 'kash', 'image/jpeg', CURRENT_TIMESTAMP, 'ほわいとはうす');
