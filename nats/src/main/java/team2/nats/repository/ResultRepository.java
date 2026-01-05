package team2.nats.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import team2.nats.dto.RankingDto;
import team2.nats.entity.Result;

public interface ResultRepository extends JpaRepository<Result, Long> {

  // ★合計時間でランキング
  @Query("SELECT new team2.nats.dto.RankingDto(r.participantName, SUM(r.elapsedMs)) " +
      "FROM Result r WHERE r.correct = true GROUP BY r.participantName ORDER BY SUM(r.elapsedMs) ASC")
  List<RankingDto> findBestTimes();

  /** ★ランキングリセット用：results を全削除 */
  @Modifying
  @Query("DELETE FROM Result r")
  void deleteAllResults();
}
