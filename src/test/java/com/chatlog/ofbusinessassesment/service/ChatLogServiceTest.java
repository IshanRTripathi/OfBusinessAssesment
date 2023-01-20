package com.chatlog.ofbusinessassesment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import com.chatlog.ofbusinessassesment.entity.ChatLogEntity;
import com.chatlog.ofbusinessassesment.entity.CreateChatLogRequest;
import com.chatlog.ofbusinessassesment.exception.EmptyMessageException;
import com.chatlog.ofbusinessassesment.exception.MessageNotFoundException;
import com.chatlog.ofbusinessassesment.exception.ValidationException;
import com.chatlog.ofbusinessassesment.repository.ChatLogRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
class ChatLogServiceTest {

    @InjectMocks
    private ChatLogService chatLogService;

    @Mock
    private ChatLogRepository chatLogRepository;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(chatLogService, "chatLogRepository", chatLogRepository);
    }

    @Test
    void createChatLogTestShouldThrowValidationException() {
        var message = CreateChatLogRequest.builder()
            .isSent(10)
            .message("message")
            .timestamp(1672288894477L)
            .build();
        assertThrows(ValidationException.class, () -> chatLogService.createChatLog("user", message));
    }

    @Test
    void createChatLogTestShouldThrowEmptyMessageException() {
        var message = CreateChatLogRequest.builder()
            .isSent(0)
            .message("")
            .timestamp(1672288894477L)
            .build();
        assertThrows(EmptyMessageException.class, () -> chatLogService.createChatLog("user", message));
    }

    @Test
    void getAllChatLogsTest() {
        when(chatLogRepository.findAllByUserAndMessageIdGreaterThanEqualOrderByTimestampDesc(any(), any(), List.of(0), 1))
            .thenReturn(Optional.of(List.of(ChatLogEntity.builder().build())));
        assertNotNull(chatLogService.getAllChatLogs("user", 1L, 1, 1));
    }

    @Test
    void deleteUserChat() {
        when(chatLogRepository.deleteAllByUser(any())).thenReturn(1L);
        assertEquals(chatLogService.deleteUserChat("user"), 1);
    }

    @Test
    void deleteUserChatThrowMessageNotFoundException() {
        when(chatLogRepository.deleteAllByUser(any())).thenReturn(0L);
        assertThrows(MessageNotFoundException.class, () -> chatLogService.deleteUserChat("user"));
    }

    @Test
    void deleteUserChatByMessageIdThrowMessageNotFoundException() {
        when(chatLogRepository.deleteByUserAndMessageIdEquals(any(), any())).thenReturn(0L);
        assertThrows(MessageNotFoundException.class, () -> chatLogService.deleteUserChatByMessageId("user", 1L));
    }
}