package team2.nats.service;

import org.springframework.stereotype.Service;

/**
 * ゲーム開始状態を管理するサービスクラス.
 * メモリ上にゲーム開始フラグを1つだけ持つ簡易実装.
 */
@Service
public class GameStatusService {

  /** ゲームが開始されたかどうかを表すフラグ */
  private boolean started = false;

  /** ゲーム開始時刻（ミリ秒） */
  private long startedAtMillis = 0L;

  /** ゲーム開始状態を有効とみなす時間（ミリ秒）: 例として10秒 */
  private static final long STARTED_DURATION_MILLIS = 10_000L;

  /**
   * ゲームを開始状態に設定する.
   */
  public synchronized void startGame() {
    this.started = true;
    this.startedAtMillis = System.currentTimeMillis();
  }

  /**
   * ゲームが開始されているかどうかを取得する.
   * 一定時間（STARTED_DURATION_MILLIS）を過ぎると自動的に false を返す.
   *
   * @return true: 開始状態期間内 / false: 未開始 または 有効期限切れ
   */
  public synchronized boolean isGameStarted() {
    if (!this.started) {
      return false;
    }

    long now = System.currentTimeMillis();
    if (now - this.startedAtMillis <= STARTED_DURATION_MILLIS) {
      return true;
    }

    // 有効期限切れになった場合はフラグを自動的にリセット
    this.started = false;
    return false;
  }

  /**
   * ゲーム開始状態をリセットする.
   */
  public synchronized void resetGame() {
    this.started = false;
    this.startedAtMillis = 0L;
  }
}
