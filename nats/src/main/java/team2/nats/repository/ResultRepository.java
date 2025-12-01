package team2.nats.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import team2.nats.dto.RankingDto;
import team2.nats.entity.Result;

public interface ResultRepository extends JpaRepository<Result, Long> {

  @Query("SELECT new team2.nats.dto.RankingDto(r.participantName, MIN(r.elapsedMs)) " +
      "FROM Result r WHERE r.correct = true GROUP BY r.participantName ORDER BY MIN(r.elapsedMs) ASC")
  List<RankingDto> findBestTimes();
}
