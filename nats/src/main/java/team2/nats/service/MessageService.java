package team2.nats.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.nats.model.Message;
import team2.nats.repository.MessageRepository;

@Service
@Transactional
public class MessageService {

  private final MessageRepository messageRepository;

  public MessageService(MessageRepository messageRepository) {
    this.messageRepository = messageRepository;
  }

  /**
   * メッセージを保存する
   *
   * @param content 保存するテキスト
   * @return 保存された Message
   */
  public Message saveMessage(String content) {
    Message message = new Message(content);
    return messageRepository.save(message);
  }
}
