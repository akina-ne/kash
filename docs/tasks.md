# 実装計画: H2（インメモリ）導入と h2-console の全員アクセス許可

作成日: 2025-11-17

目的:
- H2 データベースをインメモリで導入する（永続化なし）。
- H2 コンソール（`/h2-console`）へのアクセスを誰でも許可する（開発環境のみ）。
- データベース接続情報: ユーザ名 `kash`、パスワード `nats` を利用する。

前提条件:
- 本計画は開発環境向けであり、本番環境では h2-console を無効化すること。

実装概要:
1. 依存追加（Gradle）: H2 と Spring Data JPA を追加
2. 設定追加: `application.properties` にインメモリ接続情報を追加
3. セキュリティ設定の調整: `/h2-console/**` を許可、CSRF とフレームの設定を行う
4. 起動と動作確認: `./gradlew bootRun` で起動し、`/h2-console` に接続できることを確認


タスク一覧（詳細）:



タスク 2: 依存関係の追加
- 目的: H2 と JPA を使用可能にする
- 具体的作業:
  1. `build.gradle` に以下を追加（`dependencies` セクション）:
     - `implementation 'org.springframework.boot:spring-boot-starter-data-jpa'`
     - `runtimeOnly 'com.h2database:h2'`
  2. 依存追加後、`./gradlew dependencies` で解決確認
- 関連ファイル:
  - `build.gradle`
- DoD: `./gradlew build` が依存解決できること

タスク 3: application.properties の追加/編集
- 目的: H2 をインメモリで起動する設定を追加する
- 具体的作業:
  1. `src/main/resources/application.properties` を編集し、以下を追記:
     ```properties
     # H2（インメモリ）設定
     spring.datasource.url=jdbc:h2:mem:kashdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
     spring.datasource.driver-class-name=org.h2.Driver
     spring.datasource.username=kash
     spring.datasource.password=nats

     # JPA 設定（開発向け）
     spring.jpa.hibernate.ddl-auto=update

     # H2 コンソール有効化
     spring.h2.console.enabled=true
     spring.h2.console.path=/h2-console
     ```
  2. ファイル保存
- 関連ファイル:
  - `src/main/resources/application.properties`
- DoD: アプリ起動時に H2 がインメモリで初期化されること

タスク 4: セキュリティ設定の調整
- 目的: `/h2-console/**` への誰でもアクセスを許可する（開発環境限定）
- 具体的作業:
  1. `src/main/java/team2/nats/config/SecurityConfig.java` を編集または作成
  2. 以下の点を実装/確認:
     - `SecurityFilterChain` で `/h2-console/**` を `permitAll()` にする
     - CSRF を H2 コンソール時に無効化する（例: `csrf().ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**"))`）
     - フレーム表示を許可する: `headers().frameOptions().sameOrigin()`
     - 開発用のみに限定するため、プロファイルやプロパティで制御可能にする（推奨）
  3. 既存の認証設定（in-memory 認証やフォームログイン）がある場合は共存するよう設定する
- 関連ファイル:
  - `src/main/java/team2/nats/config/SecurityConfig.java`
- DoD: 未認証ユーザがブラウザで `/h2-console` にアクセスでき、ログイン画面やコンソール画面が表示されること

タスク 5: 起動と接続確認
- 目的: 実際に動作することを確認する
- 具体的作業:
  1. 端末で `./gradlew bootRun` を実行
  2. ブラウザで `http://localhost:8080/h2-console` にアクセス
  3. 接続情報を入力（Driver: org.h2.Driver、JDBC URL: `jdbc:h2:mem:kashdb`、User: `kash`、Password: `nats`）
  4. コンソールにログインし、テーブル一覧やクエリ実行が可能なことを確認
- テスト観点:
  - 指定した JDBC URL で接続できる
  - 認証情報 `kash`/`nats` でログインできる
  - CSRF・frameOptions の設定によりコンソールが正常に表示される
- DoD: ブラウザから `h2-console` に接続し、SQL を実行できること

specs.md` に反映されていること

セキュリティ上の注意（必ず守ること）:
- h2-console を開発環境以外で有効にしないこと。本番では必ず無効化すること。
- H2 のユーザ/パスワードは開発用であることを明記し、機密情報は環境変数やシークレット管理を検討すること。

実施の進め方（提案）:
- フェーズ A: タスク 1〜3 を実施し、H2 の依存と設定を追加する
- フェーズ B: タスク 4 を実施してセキュリティ設定を調整
- フェーズ C: タスク 5〜6 を実施して検証とドキュメント化を行う

次にユーザが選べるアクション:
- 「実装」を指示してください。
- セキュリティをプロファイルで切り替える実装（例: `application-dev.properties`）を希望する場合はその旨を指示してください。

# 作業計画（テキスト入力をDBに保存する機能）

## 概要
Webページのテキストボックスに入力した文字列をデータベースに保存する機能を実装するための計画書。調査フェーズの内容（docs/reports/investigate/2025-11-25_テキスト保存調査.md）を踏まえて、最小単位に分割したタスク、関連ファイル、優先順位、DoD を記載する。

## 前提条件
- 開発段階は組み込み H2 DB を使用する。

## 関連ファイル（読み込み対象）
- docs/reports/investigate/2025-11-25_テキスト保存調査.md
- docs/specs.md
- src/main/java/oit/is/kash/model/Message.java
- src/main/java/oit/is/kash/repository/MessageRepository.java
- src/main/java/oit/is/kash/service/MessageService.java
- src/main/java/oit/is/kash/controller/MessageController.java
- src/main/resources/templates/messageForm.html
- src/main/resources/application.properties
- src/main/resources/schema.sql
- docs/reports/done/ (完了レポート出力先)

> 注意: 上記 Java パッケージ名はプロジェクトの慣例に合わせて調整すること。

## タスク一覧（優先度順、最小単位）


2. スキーマ準備（中）
   - 目的: messages テーブルを用意する（開発中は spring.jpa.hibernate.ddl-auto=update でも可）。
   - 関連ファイル:
     - src/main/resources/schema.sql （任意）
   - DoD: H2 コンソールで messages テーブルが確認できる。

3. エンティティ作成（高）
   - 目的: Message エンティティを作成する。
   - 関連ファイル:
     - src/main/java/oit/is/kash/model/Message.java
   - DoD: Message クラスに @Entity, id, content, createdAt が定義されていること。

4. リポジトリ作成（高）
   - 目的: Message を保存するための JpaRepository を作成する。
   - 関連ファイル:
     - src/main/java/oit/is/kash/repository/MessageRepository.java
   - DoD: MessageRepository が JpaRepository<Message, Long> を継承していること。



6. コントローラ作成（高）
   - 目的: フォーム表示（GET）と保存処理（POST）を実装する。
   - 関連ファイル:
     - src/main/java/oit/is/kash/controller/MessageController.java
     - src/main/resources/templates/messageForm.html
   - DoD: /message/form にアクセスするとフォームが表示され、投稿すると DB にレコードが追加される。

7. テンプレート実装（高）
   - 目的: Thymeleaf のフォームを作成する（入力欄、送信ボタン、エラーメッセージ）。
   - 関連ファイル:
     - src/main/resources/templates/messageForm.html
   - DoD: フォームから POST でき、バリデーションエラーが表示される。

8. フロントでのバリデーションと XSS 対策（中）
   - 目的: 必要最小限の入力長チェック、サニタイズの確認。
   - 関連ファイル:
     - templates と controller 関連ファイル
   - DoD: 長すぎるまたは空の入力はバリデーションエラーとなる。

9. テスト（高）
   - 目的: 単体テスト・統合テストで保存処理を検証する。
   - 関連ファイル:
     - src/test/java/.../MessageRepositoryTest.java
     - src/test/java/.../MessageControllerTest.java
   - DoD: テストが通ること。

10. ドキュメント更新（必須）
   - 目的: 実装完了後に完了レポートと specs 更新を行う。
   - 関連ファイル:
     - docs/reports/done/done_YYYY-MM-DD_テキスト保存実装.md
     - docs/specs.md
   - DoD: 完了レポートが作成され、docs/specs.md に機能が反映されていること。

## 各タスクの詳細手順（省略せず最低限）
- 環境設定
  - application.properties に以下を追加（例）: H2 の URL、ユーザ名、パスワード、spring.jpa.hibernate.ddl-auto=update
  - H2 コンソール有効化を行う（spring.h2.console.enabled=true）

- エンティティ/リポジトリ/コントローラ作成
  - Message.java に content の長さ制約や @CreationTimestamp 等を付与する。
  - MessageRepository は基本の save/find を使う。
  - MessageController の POST は CSRF トークン対応、POST-Redirect-GET パターンを使う。

- テンプレート
  - Thymeleaf で th:action / th:field を利用し、エスケープは自動で行われることを確認する。

- テスト
  - H2 を使ったインメモリでのリポジトリテスト、MockMvc を使ったコントローラテストを作成する。

## テスト手順（DoD を満たすための確認手順）
1. main ブランチ上で作業開始を確認し、新規ブランチを作成する。
2. gradle bootRun を実行してアプリケーションを起動する。
3. ブラウザで http://localhost:8080/message/form にアクセスする。
4. テキストを入力して送信する。
5. H2 コンソール（http://localhost:8080/h2-console）で messages テーブルに新規レコードが追加されていることを確認する。
6. 単体テスト・統合テストを実行し、全て通過することを確認する。

## 受け渡し・報告
- 実装完了時は以下を作成すること:
  - docs/reports/done/done_YYYY-MM-DD_テキスト保存実装.md （実装手順、確認手順、使用したブランチ名を記載）
  - docs/specs.md を更新し、UI と API の仕様を反映する。

---
作成者: GitHub Copilot
作成日: 2025-11-25
