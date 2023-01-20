package com.chatlog.ofbusinessassesment.repository;

import java.util.List;
import java.util.Optional;

import com.chatlog.ofbusinessassesment.entity.ChatLogEntity;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatLogRepository extends CrudRepository<ChatLogEntity, Long> {

    @Query(nativeQuery = true, value = "select * from chat_log ce where ce.user = ?1 and ce.message_id > ?2 and ce.isSent in ?3 order by ce.timestamp limit ?4")
    Optional<List<ChatLogEntity>> findAllByUserAndMessageIdGreaterThanEqualOrderByTimestampDesc(String user, Long timestamp,
                                                                                                List<Integer> isSent, Integer limit);

    Optional<ChatLogEntity> findFirstByUserAndIsSentInOrderByTimestampDesc(String user, List<Integer> isSent);

    Long deleteAllByUser(String user);

    Long deleteByUserAndMessageIdEquals(String user, Long messageId);
}
