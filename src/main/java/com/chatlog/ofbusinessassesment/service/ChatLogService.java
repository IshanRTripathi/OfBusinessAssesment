package com.chatlog.ofbusinessassesment.service;

import java.util.List;

import com.chatlog.ofbusinessassesment.entity.ChatLogEntity;
import com.chatlog.ofbusinessassesment.entity.CreateChatLogRequest;
import com.chatlog.ofbusinessassesment.exception.EmptyMessageException;
import com.chatlog.ofbusinessassesment.exception.MessageNotFoundException;
import com.chatlog.ofbusinessassesment.exception.ValidationException;
import com.chatlog.ofbusinessassesment.repository.ChatLogRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ChatLogService {

    @Autowired
    private ChatLogRepository chatLogRepository;

    public String createChatLog(String user, CreateChatLogRequest request) {
        log.info("Creating new chat log for user: {}, chat log: {}", user, request);
        validateRequest(request);
        ChatLogEntity chatLogEntity = ChatLogEntity.builder()
            .isSent(request.getIsSent())
            .user(user)
            .timestamp(request.getTimestamp() == null ? System.currentTimeMillis() : request.getTimestamp())
            .message(request.getMessage())
            .build();

        var response = chatLogRepository.save(chatLogEntity);
        return response.getMessageId().toString();
    }

    private void validateRequest(CreateChatLogRequest request) {
        if (request.getIsSent() == null || request.getIsSent() != 0 && request.getIsSent() != 1) {
            throw new ValidationException("isSent should be either 0 or 1");
        }
        if(request.getMessage().isBlank()) {
            throw new EmptyMessageException("message should not be empty");
        }
    }

    public List<ChatLogEntity> getAllChatLogs(String user, Long start, Integer limit) {
        log.info("Fetching all chat logs for user: {} with starting message id: {} and limit: {}", user, start, limit);
        if (start == null) {
            return List.of(chatLogRepository.findFirstByUserOrderByTimestampDesc(user).get());
        }
        var response = chatLogRepository.findAllByUserAndMessageIdGreaterThanEqualOrderByTimestampDesc(user, start).get();
        return response.subList(Math.max(response.size() - limit, 0), response.size());
    }

    @Transactional
    public Long deleteUserChat(String user) {
        var countOfDeletedChats = chatLogRepository.deleteAllByUser(user);
        log.info("Deleted user: {}'s all chats, count of deleted chats: {}", user, countOfDeletedChats);
        if (countOfDeletedChats == 0) {
            throw new MessageNotFoundException("No messages found for user " + user);
        }
        return countOfDeletedChats;
    }

    @Transactional
    public void deleteUserChatByMessageId(String user, Long messageId) {
        var countOfDeletedChats = chatLogRepository.deleteByUserAndMessageIdEquals(user, messageId);
        log.info("Deleted user: {}'s chat with messageId: {}, count: {}", user, messageId, countOfDeletedChats);
        if (countOfDeletedChats == 0) {
            throw new MessageNotFoundException("No messages found for user " + user + " for messageId " + messageId);
        }
    }
}
