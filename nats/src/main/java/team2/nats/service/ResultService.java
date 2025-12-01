package team2.nats.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.nats.entity.Result;
import team2.nats.repository.ResultRepository;

import java.util.List;
import team2.nats.dto.RankingDto;

@Service
@Transactional
public class ResultService {

  private final ResultRepository resultRepository;

  public ResultService(ResultRepository resultRepository) {
    this.resultRepository = resultRepository;
  }

  public Result saveResult(String participantName, Long elapsedMs, boolean correct) {
    Result r = new Result(participantName, elapsedMs, correct);
    return resultRepository.save(r);
  }

  public List<RankingDto> getRankings() {
    return resultRepository.findBestTimes();
  }
}
