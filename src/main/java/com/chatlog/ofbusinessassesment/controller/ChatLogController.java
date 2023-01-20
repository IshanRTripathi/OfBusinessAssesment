package com.chatlog.ofbusinessassesment.controller;

import java.util.Arrays;

import com.chatlog.ofbusinessassesment.entity.CreateChatLogRequest;
import com.chatlog.ofbusinessassesment.exception.MessageNotFoundException;
import com.chatlog.ofbusinessassesment.exception.ValidationException;
import com.chatlog.ofbusinessassesment.service.ChatLogService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chatlogs")
@ControllerAdvice
@Slf4j
public class ChatLogController {

    @Autowired
    private ChatLogService chatLogService;

    @PostMapping(value = "/{user}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createChatLog(@PathVariable String user, @RequestBody CreateChatLogRequest request) {
        try {
            return ResponseEntity.ok().body(chatLogService.createChatLog(user, request));
        } catch (Exception e) {
            log.error("Exception in post: {}, {}", e.getMessage(), Arrays.toString(e.getStackTrace()));
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(path = "/{user}")
    public ResponseEntity<?> getUserChatLogs(@PathVariable String user,
                                             @RequestParam(name = "sent", required = false) Integer sent,
                                             @RequestParam(name = "start", required = false) Long start,
                                             @RequestParam(name = "limit", required = false, defaultValue = "10") Integer limit) {
        try {
            return ResponseEntity.ok().body(chatLogService.getAllChatLogs(user, start, limit, sent));
        } catch (Exception e) {
            log.error("Exception in get: {}, {}", e.getMessage(), Arrays.toString(e.getStackTrace()));
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping(path = "{user}")
    public ResponseEntity<?> deleteUserChat(@PathVariable String user) {
        try {
            var response = chatLogService.deleteUserChat(user);
            return ResponseEntity.ok().body(response + " number of records deleted");
        } catch (MessageNotFoundException e) {
            return ResponseEntity.noContent().build();
        }
    }

    @DeleteMapping(path = "{user}/{messageId}")
    public ResponseEntity<?> deleteUserChat(@PathVariable String user, @PathVariable Long messageId) {
        try {
            chatLogService.deleteUserChatByMessageId(user, messageId);
            return ResponseEntity.ok().build();
        } catch (MessageNotFoundException e) {
            return ResponseEntity.noContent().build();
        }
    }
}
