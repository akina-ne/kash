# タスク: 画像ファイル保存実装（static/images + images テーブル）

作成日: 2025-11-24
作成者: 青山

目的:
- 開発・検証環境で画像ファイルをプロジェクト内に配置し、DB にファイル名と相対パスを保存してアプリから参照できるようにする。
- 将来的に S3+CDN へ移行可能な設計を維持する。

前提:
- プロジェクトは Spring Boot（Java）。
- H2 と JPA の設定が未実施の場合は先に H2 実装タスクを完了させる。

推奨ブランチ名: feat/add-images-table

短期タスク一覧:

1) ブランチ作成
- main を最新化し、`git switch -c feat/add-images-table` を作成する。
- DoD: ローカルに新ブランチが作成されていること。

2) schema.sql に images テーブル追加
- `src/main/resources/schema.sql` に images テーブル定義を追記する（例: id, file_name, file_path, owner_id, content_type, created_at）。
- DoD: 起動後に images テーブルが存在すること（H2 コンソールで確認）。

3) static/images にサンプル画像追加
- `src/main/resources/static/images/` を作成し、`sample1.jpg` を配置する。
- DoD: ブラウザで `http://localhost:8080/images/sample1.jpg` が表示されること。

4) data.sql に初期レコード追加（任意）
- `src/main/resources/data.sql` に INSERT を追加（例: file_name='sample1.jpg', file_path='images/sample1.jpg'）。
- DoD: 起動後に images テーブルにサンプルレコードが存在すること。

5) エンティティ・リポジトリ・Controller・テンプレート実装
- 追加ファイル:
  - `src/main/java/team2/nats/entity/Image.java`
  - `src/main/java/team2/nats/repository/ImageRepository.java`
  - `src/main/java/team2/nats/controller/ImageController.java`
  - `src/main/resources/templates/images.html`（または既存テンプレート）
- 機能: DB から画像レコードを読み取り、`/images` で一覧表示する。
- DoD: `http://localhost:8080/images` で DB のレコードに応じた画像一覧が表示されること。

6) 起動と動作確認
- `./gradlew bootRun` で起動し、上記 URL を確認する。
- DoD: 画像個別 URL と一覧が期待通りに表示されること。

7) ドキュメント作成
- `docs/reports/done/done_YYYY-MM-DD_画像保存実装.md` を作成し、変更点・検証手順・ブランチ名を記録する。
- `docs/specs.md` を必要に応じて更新する。

運用上の注意:
- 大容量・更新頻度の高い画像をリポジトリにコミットしない。
- 複数インスタンス運用時は早期に外部ストレージへ移行する。

次のアクション:
- 今すぐ実装を開始する場合は「続行」と指示してください。私が実装を進めます（ブランチ作成 -> schema.sql 追記 -> サンプル画像追加 -> 最小限の Controller/Entity を追加）。
