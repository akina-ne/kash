# システム仕様書

作成日: 2025-11-11

概要:
- 本プロジェクトは Spring Boot をベースとした Web アプリケーションである。
- 認証には Spring Security を利用し、まずは in-memory のユーザ管理で検証を行う。

認証・認可:
- 認証方式: in-memory 認証（開発検証用）
- 登録ユーザ:
  - ユーザ名: `kash`
  - パスワード: `nats`（BCrypt によるハッシュで管理）
  - ロール: `USER`
- パスワードエンコーダ: `BCryptPasswordEncoder` を使用する。
- ログイン画面: Spring Security のデフォルトのフォームログイン画面を使用する（カスタム画面は未採用）。
- 保護対象: すべての HTTP リクエストは認証を必要とする（認証済みユーザのみアクセス可）

主要な実装ファイル（ワークスペースルートからの相対パス）:
- `src/main/java/team2/nats/config/SecurityConfig.java`
  - BCrypt を使った `PasswordEncoder` の Bean 定義
  - in-memory ユーザ `kash` の登録
  - `SecurityFilterChain` によるフォームログイン設定（デフォルトログインページ使用）
- `src/main/resources/application.properties`
  - 必要に応じたアプリケーション設定（例: Thymeleaf のキャッシュ設定など）
- `src/main/resources/static/index.html`
  - 必要に応じた案内ページ（未変更）

動作仕様:
- アプリケーション起動後、未認証ユーザが任意のページにアクセスするとログインページへリダイレクトされる。
- 正しい資格情報（kash / nats）を入力すると、認証済みとしてリダイレクトされる。
- 誤った資格情報ではログインに失敗しエラーメッセージ（デフォルト）を表示する。

実行手順:
1. main ブランチの最新状態を取得し、新規ブランチ（例: `feat/add-login-kash`）を作成する。
2. ターミナルでプロジェクトルートに移動し、開発用シェル（bash.exe）で以下を実行する:
   - `./gradlew bootRun`
3. ブラウザで `http://localhost:8080/` にアクセスする。ログイン画面が表示される。
4. ユーザ名 `kash`、パスワード `nats` でログインを行う。

検証基準（DoD）:
- `./gradlew bootRun` でアプリが起動し、ビルドエラーがないこと。
- ブラウザでアクセスしてデフォルトのログイン画面が表示されること。
- `kash` / `nats` でログインが成功し、保護されたページにアクセスできること。
- 誤った資格情報でログインが失敗し、エラーメッセージが表示されること。

運用上の注意事項:
- in-memory 認証は開発検証用であり、本番環境ではデータベース等による永続的なユーザ管理に移行すること。
- パスワードはリポジトリに平文で残さない。実装では BCrypt を用いてハッシュ化して登録すること。
- 将来的にカスタムログイン画面を導入する場合は `SecurityConfig` の `formLogin().loginPage("/login")` を設定し、`src/main/resources/templates/login.html` を作成する。

ドキュメント管理:
- 実装完了後は `docs/reports/done/done_YYYY-MM-DD_ログイン実装.md` に実施内容と検証手順を記載すること。
- 本仕様書は実装内容に合わせて更新し、変更履歴を残すこと。
