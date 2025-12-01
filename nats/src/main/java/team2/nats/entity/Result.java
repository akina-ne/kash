package team2.nats.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "results")
public class Result {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "participant_name", nullable = false)
  private String participantName;

  @Column(name = "elapsed_ms", nullable = false)
  private Long elapsedMs;

  @Column(name = "correct", nullable = false)
  private Boolean correct;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  public Result() {
  }

  public Result(String participantName, Long elapsedMs, Boolean correct) {
    this.participantName = participantName;
    this.elapsedMs = elapsedMs;
    this.correct = correct;
  }

  @PrePersist
  protected void onCreate() {
    if (this.createdAt == null) {
      this.createdAt = LocalDateTime.now();
    }
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getParticipantName() {
    return participantName;
  }

  public void setParticipantName(String participantName) {
    this.participantName = participantName;
  }

  public Long getElapsedMs() {
    return elapsedMs;
  }

  public void setElapsedMs(Long elapsedMs) {
    this.elapsedMs = elapsedMs;
  }

  public Boolean getCorrect() {
    return correct;
  }

  public void setCorrect(Boolean correct) {
    this.correct = correct;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
