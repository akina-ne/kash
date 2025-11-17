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
