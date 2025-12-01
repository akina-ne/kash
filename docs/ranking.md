# ranking 実装メモ

目的
- `http://localhost:80/ranking.html` にアクセスすると、クイズの正答までにかかった時間（短い順）で参加者の順位を表示する。

前提（実装時に確認すること）
- サーバーはどの技術で動いているか（静的ホスティングか、Java/Gradle の Web サーバーか、node/express か）。
- 参加者の正答時間はどこに保存されているか（RDB、ファイル、メモリ）。
- 既存のルーティングとテンプレート構成（`/ranking.html` を新規に作るのか、既存テンプレートを流用するのか）。

高レベル設計
1. データ取得（バックエンド）
   - 参加者ごとの「正答までにかかった時間」をソートして返す API を作成する。
   - 例：GET `/api/ranking` → JSON で [{"rank":1, "name":"Alice", "time_ms":12345}, ...]
   - SQL（Postgres 等）の例：
     ```sql
     SELECT participant_name, MIN(elapsed_ms) AS best_time
     FROM attempts
     WHERE correct = true
     GROUP BY participant_name
     ORDER BY best_time ASC;
     ```
   - タイの扱い：同タイムは同順位（または登録順でブレーク）を仕様で決める。

2. 表示（フロントエンド）
   - `ranking.html` を作成し、ブラウザ側で `/api/ranking` を fetch してテーブルで表示する。
   - 列：順位、参加者名、タイム（mm:ss.SSS 形式）
   - サーバーサイドで HTML をレンダリングしてもよい（テンプレートにランキングを埋め込む）。

3. エンドポイントとルーティング
   - 静的ファイルが用いられている場合：`ranking.html` を `public/` 配下に置き、JS で `/api/ranking` を呼ぶ。
   - 動的サーバーがある場合：ルーティングに `GET /ranking.html` を追加してサーバー側でレンダリングするか、静的 HTML と API を組み合わせる。

4. テストと手順
   - 単体テスト：API が正しくソートした JSON を返す。
   - E2E：ローカル起動後に `http://localhost:80/ranking.html` にアクセスして、表示が短い順になっていることを確認。

実装タスク（推奨順）
- [ ] データ構造・DB スキーマを確認／必要ならマイグレーション
- [ ] `GET /api/ranking` (または既存のデータ取得 API) を作る
- [ ] `ranking.html` を作成し、JS で API を呼んで描画
- [ ] スタイル（CSS）を整える
- [ ] タイのフォーマット、同タイ処理、エラーハンドリングを追加
- [ ] テスト／マニュアル確認

エッジケース
- 正答がない参加者は除外するか特別表示する。
- 大量データ時のページネーションやキャッシュを検討。

Done Criteria（DoD）
- ブラウザで `http://localhost:80/ranking.html` にアクセスすると、クイズの正答までにかかった時間の短い順に順位が表示される。
- 表示は参加者名と所要時間（読みやすいフォーマット）を含む。
- API/表示は異常時にユーザに分かるエラーメッセージを示す。

実装メモ（参考コードの断片）
- フロントエンド fetch の例（擬似コード）:
  ```js
  fetch('/api/ranking')
    .then(r=>r.json())
    .then(list => renderTable(list));
  ```

- 時間表示変換例（ms → mm:ss.SSS）
  ```js
  function formatMs(ms){
    const s = Math.floor(ms/1000);
    const mm = Math.floor(s/60).toString().padStart(2,'0');
    const ss = (s%60).toString().padStart(2,'0');
    const ms3 = (ms%1000).toString().padStart(3,'0');
    return `${mm}:${ss}.${ms3}`;
  }
  ```

次のアクション提案
- 環境（サーバー種類、DB）を教えてください。確認できれば、具体的なファイル編集案と PR 用の変更セットを作成します。

# サーバーで start を保持して elapsed を計算する方式（採用）

決定理由
- クライアント改ざんのリスクを低減するため、elapsed_ms はサーバー側で算出して `results` テーブルに保存する。
- `/message/form` は Message の createdAt を自動保存する設計になっているため、開始時刻をサーバーで保持して差分を取るのが自然。

フロー（簡潔）
1. ユーザがクイズを開始したときにサーバーへ通知する（例: POST `/game/start`）。
   - サーバーは HttpSession に属性 `quizStart` を Epoch ミリ秒で保存する（または必要なら DB に保存してユーザ単位で管理する）。
2. ユーザが `/message/form` から解答を送信すると、MessageController が Message を保存する（`createdAt` 自動設定）。
3. Message の保存直後にサーバー側で session の `quizStart` と `message.getCreatedAt()` を比較して `elapsed_ms` を計算。
4. 計算結果（participant_name, elapsed_ms, correct）を `results` テーブル（Result エンティティ）に保存する。
5. 必要であれば session の `quizStart` を削除または更新する。

実装上のポイント
- セッションが無ければ: ログを残して結果を保存しない、または `elapsed_ms` を NULL 扱いするルールを決める。
- correct 判定: 既存の正答判定ロジックを使う（例: Message の内容と正答の比較）。
- セッション vs DB: 同一ユーザが複数セッションでプレイする可能性がある場合は DB に start を保存してユーザ単位で管理する方が安全。
- 入力検証: 極端に短い時間（例 0-50ms）は不正の可能性があるためフィルタする。
- トランザクション: Message 保存と Result 保存はサービス層で同一トランザクションにまとめると整合性が保たれる。

必要なファイル変更（候補）
- `team2.nats.entity.Result`（エンティティ）
- `team2.nats.repository.ResultRepository`（JPA リポジトリ）
- `team2.nats.service.ResultService`（保存・検証ロジック）
- `team2.nats.controller.GameController` に `/game/start` を追加
- `team2.nats.controller.MessageController` の保存処理で ResultService を呼び出す
- `src/main/resources/schema.sql` に `results` テーブル（既に追加済み）
- `src/main/resources/templates/ranking.html` と `RankingController`（GET `/ranking.html`）

SQL（ランキング取得）
```sql
SELECT participant_name, MIN(elapsed_ms) AS best_time
FROM results
WHERE correct = TRUE
GROUP BY participant_name
ORDER BY best_time ASC;
```

テストケース（必須）
- セッションに start を格納してから解答送信 → `results` に期待通りの `elapsed_ms` が保存されるか
- セッションが無い場合の挙動（保存しない or 特別扱い）
- 複数セッション／複数ユーザの同時実行
- 極端な値（0ms 等）のフィルタリング

次のアクション提案
- まずは「セッションに start 保存 + MessageController で elapsed を計算して Result 保存」を実装します。差分を作りますか？
