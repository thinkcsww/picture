package com.applory.pictureserver.domain.chatting.message_sender;

import com.applory.pictureserver.domain.matching.Matching;
import com.applory.pictureserver.domain.chatting.*;
import com.applory.pictureserver.domain.matching.MatchingRepository;
import com.applory.pictureserver.domain.user.UserRepository;
import com.applory.pictureserver.exception.NotFoundException;
import com.nimbusds.jose.shaded.json.JSONObject;
import jdk.nashorn.api.scripting.JSObject;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import javax.transaction.Transactional;

public class MatchingMessageSender implements MessageSender {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final ChattingRoomRepository chattingRoomRepository;

    private final ChattingMessageRepository chattingMessageRepository;

    private final UserRepository userRepository;

    private final MatchingRepository matchingRepository;

    public MatchingMessageSender(SimpMessagingTemplate simpMessagingTemplate, ChattingRoomRepository chattingRoomRepository, ChattingMessageRepository chattingMessageRepository, UserRepository userRepository, MatchingRepository matchingRepository) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chattingRoomRepository = chattingRoomRepository;
        this.chattingMessageRepository = chattingMessageRepository;
        this.userRepository = userRepository;
        this.matchingRepository = matchingRepository;
    }

    @Override
    @Transactional
    public void sendMessage(ChattingDto.SendMessageParams sendMessageParams) {
        ChattingRoom targetChattingRoom = chattingRoomRepository.findById(sendMessageParams.getRoomId()).orElseThrow(() -> new NotFoundException("Rood does not exist: " + sendMessageParams.getRoomId()));

        if (sendMessageParams.getMessageType().equals(ChattingMessage.Type.REQUEST_MATCHING)) {
            Matching matching = Matching.builder()
                    .client(userRepository.getById(sendMessageParams.getClientId()))
                    .seller(userRepository.getById(sendMessageParams.getSellerId()))
                    .completeYN("N")
                    .dueDate(sendMessageParams.getDueDate())
                    .price(sendMessageParams.getPrice())
                    .specialty(sendMessageParams.getSpecialty())
                    .status(Matching.Status.REQUEST)
                    .build();
            matchingRepository.save(matching);

            JSONObject messageJson = new JSONObject();
            messageJson.put("completeYN", "N");
            messageJson.put("dueDate", sendMessageParams.getDueDate());
            messageJson.put("price", sendMessageParams.getPrice());
            messageJson.put("specialty", sendMessageParams.getSpecialty());

            sendMessageParams.setMessage(messageJson.toJSONString());
        } else if (sendMessageParams.getMessageType().equals(ChattingMessage.Type.ACCEPT_MATCHING)) {
            Matching matchingInDB = matchingRepository.findBySellerAndClientAndCompleteYN(userRepository.getById(sendMessageParams.getSellerId()), userRepository.getById(sendMessageParams.getClientId()), "N");
            matchingInDB.setCompleteYN("Y");
            matchingInDB.setStatus(Matching.Status.ACCEPT);

            matchingRepository.save(matchingInDB);
        } else if (sendMessageParams.getMessageType().equals(ChattingMessage.Type.DECLINE_MATCHING)) {
            Matching matchingInDB = matchingRepository.findBySellerAndClientAndCompleteYN(userRepository.getById(sendMessageParams.getSellerId()), userRepository.getById(sendMessageParams.getClientId()), "N");
            matchingInDB.setCompleteYN("Y");
            matchingInDB.setStatus(Matching.Status.DECLINE);

            matchingRepository.save(matchingInDB);
        }



        ChattingMessage chattingMessage = saveMessage(sendMessageParams, targetChattingRoom);
        sendMessageParams.setMessageId(chattingMessage.getId());

        ChattingDto.StompMessageVM stompMessageVM = ChattingDto.StompMessageVM.builder()
                .senderId(sendMessageParams.getSenderId())
                .roomType(sendMessageParams.getRoomType())
                .messageType(chattingMessage.getType())
                .message(chattingMessage.getMessage())
                .id(chattingMessage.getId())
                .build();


        simpMessagingTemplate.convertAndSend("/room/" + sendMessageParams.getRoomId(), stompMessageVM);
    }

    @Transactional
    ChattingMessage saveMessage(ChattingDto.SendMessageParams sendMessage, ChattingRoom chattingRoom) {
        ChattingMessage chattingMessage = new ChattingMessage();
        chattingMessage.setChattingRoom(chattingRoom);
        chattingMessage.setMessage(sendMessage.getMessage());
        chattingMessage.setType(sendMessage.getMessageType());
        chattingMessage.setSender(userRepository.getById(sendMessage.getSenderId()));
        chattingMessage.setVisibleTo("ALL");
        return chattingMessageRepository.save(chattingMessage);
    }
}
