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

  /** ゲーム開始状態を有効とみなす時間（ミリ秒） */
  private static final long STARTED_DURATION_MILLIS = 10_000L;

  /** カウントダウン開始時刻（ミリ秒） */
  private long countdownStartMillis = 0L;

  /** カウントダウン継続時間（ミリ秒） */
  private long countdownDurationMillis = 0L;

  /**
   * ★「もう一度遊ぶ」通知の有効期限（この時刻まで true 扱い）
   * 誰かが消費して消す方式だと取りこぼしが出るので、時間で消す。
   */
  private long resetRequestedUntilMillis = 0L;

  /** 通知を保持する時間（ミリ秒）: 余裕を持って2秒 */
  private static final long RESET_BROADCAST_MILLIS = 2000L;

  public synchronized void startCountdown(long durationMillis) {
    this.countdownStartMillis = System.currentTimeMillis();
    this.countdownDurationMillis = durationMillis;
    this.started = false;
  }

  public synchronized long getCountdownRemainingMillis() {
    if (this.countdownStartMillis == 0L || this.countdownDurationMillis <= 0L) {
      return 0L;
    }
    long now = System.currentTimeMillis();
    long elapsed = now - this.countdownStartMillis;
    long remaining = this.countdownDurationMillis - elapsed;
    if (remaining <= 0L) {
      this.countdownStartMillis = 0L;
      this.countdownDurationMillis = 0L;
      this.started = true;
      this.startedAtMillis = now;
      return 0L;
    }
    return remaining;
  }

  public synchronized void startGame() {
    this.started = true;
    this.startedAtMillis = System.currentTimeMillis();
  }

  public synchronized boolean isGameStarted() {
    if (this.countdownStartMillis != 0L && this.countdownDurationMillis > 0L) {
      long now = System.currentTimeMillis();
      long elapsed = now - this.countdownStartMillis;
      if (elapsed >= this.countdownDurationMillis) {
        this.countdownStartMillis = 0L;
        this.countdownDurationMillis = 0L;
        this.started = true;
        this.startedAtMillis = now;
      } else {
        return false;
      }
    }

    if (!this.started)
      return false;

    long now = System.currentTimeMillis();
    if (now - this.startedAtMillis <= STARTED_DURATION_MILLIS) {
      return true;
    }

    this.started = false;
    return false;
  }

  /** ★「もう一度遊ぶ」を全員に通知する（一定時間 true を維持） */
  public synchronized void requestReset() {
    long now = System.currentTimeMillis();
    this.resetRequestedUntilMillis = now + RESET_BROADCAST_MILLIS;
  }

  /** ★resetRequested の参照（期限内だけ true） */
  public synchronized boolean isResetRequested() {
    return System.currentTimeMillis() <= this.resetRequestedUntilMillis;
  }

  /** ゲーム状態リセット（通知は requestReset() 側の期限で自然に消える） */
  public synchronized void resetGame() {
    this.started = false;
    this.startedAtMillis = 0L;
    this.countdownStartMillis = 0L;
    this.countdownDurationMillis = 0L;
  }
}
