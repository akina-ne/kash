# 実装計画: ユーザ認証（kash / nats）

作成日: 2025-11-11

目的:
- in-memory 認証を用いて、ユーザ名 `kash` / パスワード `nats` でログインできるようにする。
- 実装は調査レポート（docs/reports/investigate/2025-11-11_ログイン実装調査.md）の方針に従う。

前提条件:
- 実装開始前に `main` ブランチにいることを確認すること。
- 実装は新しいブランチを切って行う（例: `feat/add-login-kash`）。

全体の流れ（概要）:
1. ブランチ準備
2. セキュリティ設定ファイル作成
3. ログイン画面作成
4. アプリ起動と動作確認
5. 完了レポート作成・仕様書更新

タスク一覧（細分化）:

タスク 1: main ブランチの確認と新規ブランチ作成
- 目的: 安全に実装できる新しいブランチを作成する。
- 具体的作業:
  1. ローカルで `git status` を実行して作業ツリーがクリーンか確認する。
  2. `git switch main`（または `git checkout main`）で main に切り替える。
  3. `git pull` で最新の main を取得する。
  4. `git switch -c feat/add-login-kash` で新規ブランチを作成する。
- 関連ファイル: なし
- DoD:
  - 新規ブランチがローカルに作成され、HEAD がそのブランチを指していること。

タスク 2: セキュリティ設定クラス作成
- 目的: Spring Security の設定を追加し、in-memory ユーザ `kash` を登録する。
- 具体的作業:
  1. ファイルを作成/編集: `src/main/java/team2/nats/config/SecurityConfig.java`
  2. `PasswordEncoder`（例: `BCryptPasswordEncoder`）を @Bean として定義する。
  3. in-memory ユーザを登録する（ユーザ名: `kash`, パスワード: encoder でエンコードした `nats`）。
  4. `SecurityFilterChain` を @Bean として定義し、フォームログインを有効化する。カスタムログインページを使用する場合は `loginPage("/login")` を指定する。
- 関連ファイル:
  - `src/main/java/team2/nats/config/SecurityConfig.java`
- DoD:
  - コンパイルエラーがなく、`gradle build` が成功すること。
  - Security 設定により未認証アクセスがログインページへリダイレクトされること（起動時に確認）。

タスク 3: ログイン画面作成
- 目的: ユーザがログイン情報を入力できるページを用意する。
- 具体的作業:
  1. ファイル作成: `src/main/resources/templates/login.html`（Thymeleaf を想定）
  2. フォームは POST を `/login`（Spring Security のデフォルト）に送信する。
  3. CSRF トークンを含める（Thymeleaf の場合は `th:action` と `th:field` 、`_csrf` の埋め込みなど）。
  4. ログイン失敗時メッセージ表示を実装する（例: `?error` パラメータの判定）。
- 関連ファイル:
  - `src/main/resources/templates/login.html`
  - 既存の `src/main/resources/static/index.html` を必要に応じて案内ページとして残すかリダイレクトする。
- DoD:
  - ブラウザで `/login` にアクセスするとログインフォームが表示されること。

タスク 4: application.properties の必要設定確認
- 目的: 必要に応じた設定を application.properties に追記する。
- 具体的作業:
  1. `src/main/resources/application.properties` を確認。
  2. 必要なら `spring.thymeleaf.cache=false` を開発用に設定（テンプレート更新の反映のため）。
  3. カスタムログイン成功後の遷移やデフォルトのセッション設定を変更する場合はここに記載する。
- 関連ファイル:
  - `src/main/resources/application.properties`
- DoD:
  - 設定変更後、アプリが正しく起動すること。

タスク 5: アプリ起動と手動動作確認
- 目的: 実際にアプリを起動してログイン動作を確認する。
- 具体的作業:
  1. 端末で `./gradlew bootRun` を実行（Windows の bash.exe を使用）。
  2. ブラウザで `http://localhost:8080/` または `http://localhost:8080/login` にアクセスする。
  3. ユーザ名 `kash` 、パスワード `nats` でログインを試行する。
  4. 認証が成功すると保護されたページにアクセスできることを確認する（例: `/` にリダイレクトされる等）。
- テスト観点:
  - 正しい資格情報でログイン成功するか。
  - 誤った資格情報でログイン失敗しエラーメッセージが表示されるか。
  - CSRF の保護が有効か（ログインフォームに CSRF トークンが存在するか）。
- 関連ファイル:
  - なし（起動して挙動を確認する作業）
- DoD:
  - `./gradlew bootRun` で起動でき、`kash`/`nats` でログインして認証済みページにアクセスできること。

タスク 6: 完了レポートと仕様書更新
- 目的: 実装内容と検証手順を記録し、ドキュメントを更新する。
- 具体的作業:
  1. `docs/reports/done/done_YYYY-MM-DD_ログイン実装.md` を作成し、実装内容、ブランチ名、動作確認手順を記載する。
  2. `docs/specs.md` を読み込み、今回の認証機能を反映して更新する。
- 関連ファイル:
  - `docs/reports/done/done_YYYY-MM-DD_ログイン実装.md`
  - `docs/specs.md`
- DoD:
  - 完了レポートが作成され、`docs/specs.md` に実装内容が反映されていること。

追加の注意事項:
- コード中のパスワードは直接平文で残さないこと。今回の in-memory 設定でも `PasswordEncoder` を使用してハッシュ化した値を登録することを推奨する。
- Spring Boot のバージョンにより実装方法が変わるため、`build.gradle` を確認して適切な実装パターン（`SecurityFilterChain` ベース）を採用すること。

実施の進め方（提案）:
- このタスクは 2〜3 ステップに分けて段階的に実施することを推奨します。
  - フェーズ A: タスク 1、2 を実施してコンパイルが通る状態にする。
  - フェーズ B: タスク 3、4 を実施して画面を整える。
  - フェーズ C: タスク 5、6 を実施して検証とドキュメント化を行う。

次にユーザが選べるアクション:
- 「実装」を指示してください。指示があれば、まず main ブランチ確認後に `feat/add-login-kash` ブランチを作成し、フェーズ A から順に実装を開始します。
- 他の認証方式（JDBC 等）に変更する場合は要件を提示してください。
