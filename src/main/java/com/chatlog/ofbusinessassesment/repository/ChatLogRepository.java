package com.chatlog.ofbusinessassesment.repository;

import java.util.List;
import java.util.Optional;

import com.chatlog.ofbusinessassesment.entity.ChatLogEntity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatLogRepository extends CrudRepository<ChatLogEntity, Long> {

    Optional<List<ChatLogEntity>> findAllByUserAndMessageIdGreaterThanEqualOrderByTimestampDesc(String user, Long timestamp);

    Optional<ChatLogEntity> findFirstByUserOrderByTimestampDesc(String user);

    Long deleteAllByUser(String user);

    Long deleteByUserAndMessageIdEquals(String user, Long messageId);
}
