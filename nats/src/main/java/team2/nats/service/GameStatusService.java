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

  /** カウントダウン開始時刻（ミリ秒） */
  private long countdownStartMillis = 0L;

  /** カウントダウン継続時間（ミリ秒） */
  private long countdownDurationMillis = 0L;

  /**
   * カウントダウンを開始する（指定ミリ秒後にゲーム開始扱いにする）.
   */
  public synchronized void startCountdown(long durationMillis) {
    this.countdownStartMillis = System.currentTimeMillis();
    this.countdownDurationMillis = durationMillis;
    // カウントダウン中はまだ started をクリアしておく
    this.started = false;
  }

  /**
   * カウントダウンが残っている場合、その残りミリ秒を返す。
   * カウントダウンが完了していたらゲームを開始状態に移行させる。
   * カウントダウン未開始の場合は 0 を返す。
   */
  public synchronized long getCountdownRemainingMillis() {
    if (this.countdownStartMillis == 0L || this.countdownDurationMillis <= 0L) {
      return 0L;
    }
    long now = System.currentTimeMillis();
    long elapsed = now - this.countdownStartMillis;
    long remaining = this.countdownDurationMillis - elapsed;
    if (remaining <= 0L) {
      // カウントダウン完了 -> ゲームを正式に開始状態に移行
      this.countdownStartMillis = 0L;
      this.countdownDurationMillis = 0L;
      this.started = true;
      this.startedAtMillis = now;
      return 0L;
    }
    return remaining;
  }

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
   * カウントダウン中は false を返す（ただし内部でカウントダウンが完了していれば自動的に started に遷移する）
   *
   * @return true: 開始状態期間内 / false: 未開始 または 有効期限切れ
   */
  public synchronized boolean isGameStarted() {
    // まずカウントダウンがあればその進行を確認し、必要なら started に移行する
    if (this.countdownStartMillis != 0L && this.countdownDurationMillis > 0L) {
      long now = System.currentTimeMillis();
      long elapsed = now - this.countdownStartMillis;
      if (elapsed >= this.countdownDurationMillis) {
        // カウントダウン完了
        this.countdownStartMillis = 0L;
        this.countdownDurationMillis = 0L;
        this.started = true;
        this.startedAtMillis = now;
      } else {
        // カウントダウン中はまだ開始扱いにしない
        return false;
      }
    }

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
    this.countdownStartMillis = 0L;
    this.countdownDurationMillis = 0L;
  }
}
