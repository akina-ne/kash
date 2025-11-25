package team2.nats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team2.nats.model.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

}
