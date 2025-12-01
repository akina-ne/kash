package team2.nats.dto;

public class RankingDto {
  private String name;
  private Long bestTimeMs;

  public RankingDto(String name, Long bestTimeMs) {
    this.name = name;
    this.bestTimeMs = bestTimeMs;
  }

  public String getName() {
    return name;
  }

  public Long getBestTimeMs() {
    return bestTimeMs;
  }

  public String getFormattedTime() {
    if (bestTimeMs == null)
      return "-";
    long ms = bestTimeMs;
    long s = ms / 1000;
    long mm = s / 60;
    long ss = s % 60;
    long ms3 = ms % 1000;
    return String.format("%02d:%02d.%03d", mm, ss, ms3);
  }
}
