package team2.nats.service;

import org.springframework.stereotype.Service;

/**
 * 現在出題中の画像IDを管理するサービス.
 * メモリ上に1つだけ保持する簡易実装.
 */
@Service
public class CurrentQuestionService {

  /** 現在出題中の画像ID（未設定なら null） */
  private Long currentImageId = null;

  /**
   * 出題中の画像IDを設定する.
   *
   * @param imageId 出題する画像のID
   */
  public synchronized void setCurrentImageId(Long imageId) {
    this.currentImageId = imageId;
  }

  /**
   * 現在出題中の画像IDを取得する.
   *
   * @return 出題中の画像ID / 未設定なら null
   */
  public synchronized Long getCurrentImageId() {
    return this.currentImageId;
  }

  /**
   * 出題中の画像IDをリセットする.
   */
  public synchronized void resetCurrentImageId() {
    this.currentImageId = null;
  }
}
