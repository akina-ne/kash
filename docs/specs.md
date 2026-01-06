実装内容（やったこと）

- 画像によって回答が異なるようにし、画像ごとに正解のひらがなを判定するようにした
  - 例：金閣寺の画像なら「きんかくじ」で正解となるように判定

- 1P がゲーム開始すると、そのほかに参加しているユーザーもゲーム開始し、
  参加している人全員が /game にアクセスする仕様を前提として画面を設計した

- 画像の拡大倍率を大きくしても画像データが壊れて見えないように、表示側で縮小するよう調整した

- 送信ボタン押下直後に画像縮小アニメーションの更新が同時に行われないようにする仕様を検討し、
  コンフリクト解消後に main にマージする前提で実装を進めた
  （この制御は現在コンフリクト中のため main には未マージ）

- ランキング機能を追加し，/game 開始から正解するまでの時間を計測して，
  その時間の短い順に /ranking ページで順位付けして表示するようにした
  - ブランチ名：add-Ranking（main から作成され，PR #45 でマージ済み）
  - AI 利用：調査・計画・実装 AI を利用し，各フェーズの報告書が作成されていることを確認済み

- ゲーム画面のデザインを豪華にし，以下の仕様を満たすようにした
  - /game の開始ボタンを画面中央に表示する
  - 自分のプレイヤー名に印（マーカー）が付くようにした
  - 1P が開始ボタンを押すと，全プレイヤーの画面に 3, 2, 1 のカウントダウンが表示される
  - /game ページで経過時間を ss.fff 形式で表示する
  - ブランチ名：add-design1（main から作成され，PR #47 でマージ済み）
  - AI 利用：調査・計画・実装 AI を利用し，各フェーズの報告書が作成されていることを確認済み

- 送信ボタン押下後に画像縮小アニメーションの更新を止める機能を追加し，以下の仕様を満たすようにした
  - /game で正解のとき：画面に「〇」を表示し，その後 /ranking に遷移する
  - /game で不正解のとき：画面に「✕」を表示し，画像縮小をリセットせず，
    メッセージ送信時点の縮小状態からアニメーションを再開する
  - ブランチ名：addAnimetion（main から作成され，PR #46 でマージ済み）
  - AI 利用：実装・質問・デバッグに AI（ASK モード）を利用

- 複数問連続で出題されるクイズ機能を実装し，1ゲームあたり 5 問を共通の出題順で解答するようにした
  - 問題セットはサーバ側で共有され，全プレイヤーが同じ順番で同じ画像問題に挑戦する
    - 実装：`team2.nats.controller.GameController` の `ensureSharedQuestionSetInitialized` で 5 問の問題セットを作成
    - 出題と進行管理：`/game` ハンドラおよび `/game/answer` ハンドラでセッションごとの進行状況を管理
  - 各プレイヤーは自分のセッションごとに現在の問題番号と合計経過時間を保持する
    - セッションキー：`questionIndex`, `questionStartMs`, `totalElapsedMs`
    - 実装：[`team2.nats.controller.GameController`](nats/src/main/java/team2/nats/controller/GameController.java)

- RANKING は，複数問の解答にかかった時間の「合計」で計算されるようにした
  - 各問題の経過時間をミリ秒単位で計測し，正解またはタイムアップで次の問題へ進むときに合計時間に加算する
    - 実装：`/game/answer` 内で `questionStartMs` と現在時刻の差分を `elapsedMs` として算出し，`totalElapsedMs` に加算
  - 全問に到達したタイミングで，プレイヤーごとの合計時間を結果テーブルに保存する
    - 実装：`GameController` 内でゲーム終了判定後に [`ResultService.saveResult`](nats/src/main/java/team2/nats/service/ResultService.java) を呼び出し，`results` テーブルに保存
    - エンティティ：[`team2.nats.entity.Result`](nats/src/main/java/team2/nats/entity/Result.java)
  - ランキングは，正解したプレイヤーの合計時間（`elapsed_ms` の SUM）を用いて昇順に並べる
    - 実装：[`ResultRepository.findBestTimes`](nats/src/main/java/team2/nats/repository/ResultRepository.java) で `SUM(elapsedMs)` による集計を行い，[`RankingDto`](nats/src/main/java/team2/nats/dto/RankingDto.java) にマッピング
    - 表示：[`ranking.html`](nats/src/main/resources/templates/ranking.html) で `mm:ss.mmm` 形式に整形して表示

- メッセージ送信時にページリロードせず，エンターキーで送信できるようにした
  - `/game` の回答フォーム送信を Fetch API による非同期送信へ変更し，送信後は JSON で返ってきた判定結果に応じて 〇/✕ のオーバーレイを表示する
    - 実装：[`game.html`](nats/src/main/resources/templates/game.html) 内 `document.addEventListener('DOMContentLoaded', ...)` で `answerForm` の `submit` イベントをフックし，`fetch('/game/answer')` で送信
    - サーバ応答処理：同ファイル内 `handleAnswerResponse` で 〇/✕ の表示と /ranking への遷移を制御
  - テキストエリアで Enter キーを押すとメッセージ送信されるようにし，Shift+Enter などで改行できるような入力制御を追加した（※既存実装に合わせ，仕様として明記）
    - 実装箇所：`game.html` 内の送信フォーム用 JavaScript（`keydown` ハンドラ）

- ログイン人数とロビー（/）の収容人数に応じた UI を実装した
  - ロビー画面で現在の参加人数と，ログイン中ユーザー一覧を表示する
    - 実装：[`index.html`](nats/src/main/resources/static/index.html) で `/api/online` の結果（`{ users: [...], currentUser: "..." }`）を 10 秒ごとに取得し，参加人数と入室順のリストを描画
  - 1P（最初に入室したユーザー）のみが「開始」ボタンを押してゲームを開始できるようにした
    - 実装：`index.html` 内の `render` 関数で `users[0] === currentUser` のときだけ開始ボタン（`#openImagesBtn`）を表示
  - 参加人数の増加を前提に，ロビー画面ではカウントダウンオーバーレイと参加者表示が見やすいようデザインを調整
    - カウントダウン：「3, 2, 1」を全プレイヤーの画面に表示し，カウント終了後に `/game` へ遷移
    - 実装：`index.html` の JavaScript にて `/api/game/start-countdown` を呼び出し，共通カウントダウンを開始

- BGM 機能を追加し，ロビー・ゲーム・ランキングそれぞれで異なる BGM を再生するようにした
  - 各ページで共通の `playBgm` 関数を用意し，ページ遷移時に BGM をループ再生／停止する
    - ロビー（/）: [`index.html`](nats/src/main/resources/static/index.html) で `playBgm('Demo1.mp3')` を呼び出し
    - ゲーム（/game）: [`game.html`](nats/src/main/resources/templates/game.html) で `playBgm('Demo2.mp3')` を呼び出し
    - ランキング（/ranking）: [`ranking.html`](nats/src/main/resources/templates/ranking.html) で `playBgm('Demo1.mp3')` を呼び出し
  - ページごとに 1 つの `Audio` インスタンス（`bgmAudio`）を使い回し，BGM 再生中に別ページへ遷移した場合でも，新しいページ側で前の BGM を停止してから再生を開始する
    - 実装：各 HTML の `<script>` 内で `bgmAudio` 変数と `playBgm` 関数を定義

未実装・今後の課題
