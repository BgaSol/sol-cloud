//package com.bgasol.plugin.websocket.handler;
//
//import com.bgasol.plugin.websocket.dto.WsSendMessageDto;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.redisson.api.RedissonClient;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//@EnableScheduling
//public class SendTest {
//    private final RedissonClient redissonClient;
//
//    @Scheduled(cron = "0/1 * * * * ?")
//    public void sendMessageToAllSessions() {
//        log.info("sendMessageToAllSessions,  {}", "topic");
//        redissonClient.getTopic("ws").publish(WsSendMessageDto.builder()
//                .json("hb").build());
//    }
//}
