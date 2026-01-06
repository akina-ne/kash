package team2.nats.service;

import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 現在出題中の画像IDを管理するサービス.
 * メモリ上に1つだけ保持する簡易実装.
 * （拡張）全員共通の出題順（画像IDリスト）も保持する。
 */
@Service
public class CurrentQuestionService {

  /** 現在出題中の画像ID（未設定なら null） */
  private Long currentImageId = null;

  /** ★全員共通の出題順（画像IDリスト） */
  private List<Long> questionImageIds = List.of();

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

  // ===== ★追加：出題順リスト =====

  public synchronized boolean hasQuestionSet() {
    return questionImageIds != null && !questionImageIds.isEmpty();
  }

  public synchronized void setQuestionSet(List<Long> imageIds) {
    this.questionImageIds = (imageIds == null) ? List.of() : List.copyOf(imageIds);
  }

  public synchronized int getQuestionSetSize() {
    return questionImageIds == null ? 0 : questionImageIds.size();
  }

  public synchronized Long getQuestionImageIdAt(int index) {
    if (questionImageIds == null)
      return null;
    if (index < 0 || index >= questionImageIds.size())
      return null;
    return questionImageIds.get(index);
  }

  public synchronized void resetAll() {
    this.currentImageId = null;
    this.questionImageIds = List.of();
  }
}
