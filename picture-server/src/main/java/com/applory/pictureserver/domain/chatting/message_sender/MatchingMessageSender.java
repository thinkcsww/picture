package com.applory.pictureserver.domain.chatting.message_sender;

import com.applory.pictureserver.domain.chatting.*;
import com.applory.pictureserver.domain.matching.Matching;
import com.applory.pictureserver.domain.matching.MatchingRepository;
import com.applory.pictureserver.domain.user.User;
import com.applory.pictureserver.domain.user.UserRepository;
import com.applory.pictureserver.exception.NotFoundException;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
@Component
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

        User client = userRepository.getById(sendMessageParams.getClientId());
        User seller = userRepository.getById(sendMessageParams.getSellerId());

        Matching matchingInDB = matchingRepository.findBySellerAndClientAndCompleteYN(seller, client, "N");
        if (sendMessageParams.getMessageType().equals(ChattingMessage.Type.REQUEST_MATCHING)) {

            if (matchingInDB != null) {
                throw new IllegalStateException("Seller: " + seller.getId() + " and Client: " + client.getId() + " already have a Matching");
            }

            Matching matching = Matching.builder()
                    .client(client)
                    .seller(seller)
                    .completeYN("N")
                    .dueDate(sendMessageParams.getDueDate())
                    .price(sendMessageParams.getPrice())
                    .specialty(sendMessageParams.getSpecialty())
                    .status(Matching.Status.REQUEST)
                    .comment(sendMessageParams.getRequestComment())
                    .build();
            matchingRepository.save(matching);

            JSONObject messageJson = new JSONObject();
            messageJson.put("completeYN", "N");
            messageJson.put("dueDate", sendMessageParams.getDueDate().toString());
            messageJson.put("price", sendMessageParams.getPrice());
            messageJson.put("specialty", sendMessageParams.getSpecialty());

            sendMessageParams.setMessage(messageJson.toJSONString());
        } else if (sendMessageParams.getMessageType().equals(ChattingMessage.Type.ACCEPT_MATCHING)) {
            if (!matchingInDB.getStatus().equals(Matching.Status.REQUEST)) {
                throw new IllegalStateException("Matching: " + matchingInDB.getId() + " status is not REQUEST");
            }

            matchingInDB.setStatus(Matching.Status.ACCEPT);
            matchingRepository.save(matchingInDB);

            JSONObject messageJson = new JSONObject();
            messageJson.put("completeYN", "Y");
            messageJson.put("dueDate", matchingInDB.getDueDate().toString());
            messageJson.put("price", matchingInDB.getPrice());
            messageJson.put("specialty", matchingInDB.getSpecialty());

            sendMessageParams.setMessage(messageJson.toJSONString());

            ChattingMessage requestMatchingMessage = chattingMessageRepository.findTopByChattingRoomIdAndMessageTypeOrderByCreatedDtDesc(sendMessageParams.getRoomId(), ChattingMessage.Type.REQUEST_MATCHING);
            requestMatchingMessage.setMessage(requestMatchingMessage.getMessage().replace("\"completeYN\":\"N\"", "\"completeYN\":\"Y\""));
            chattingMessageRepository.save(requestMatchingMessage);

        } else if (sendMessageParams.getMessageType().equals(ChattingMessage.Type.DECLINE_MATCHING)) {
            if (!matchingInDB.getStatus().equals(Matching.Status.REQUEST)) {
                throw new IllegalStateException("Matching: " + matchingInDB.getId() + " status is not REQUEST");
            }

            matchingInDB.setCompleteYN("Y");
            matchingInDB.setStatus(Matching.Status.DECLINE);
            matchingRepository.save(matchingInDB);

            ChattingMessage requestMatchingMessage = chattingMessageRepository.findTopByChattingRoomIdAndMessageTypeOrderByCreatedDtDesc(sendMessageParams.getRoomId(), ChattingMessage.Type.REQUEST_MATCHING);
            requestMatchingMessage.setMessage(requestMatchingMessage.getMessage().replace("\"completeYN\":\"N\"", "\"completeYN\":\"Y\""));
            chattingMessageRepository.save(requestMatchingMessage);
        }



        ChattingMessage chattingMessage = saveMessage(sendMessageParams, targetChattingRoom);
        sendMessageParams.setMessageId(chattingMessage.getId());

        ChattingDto.StompMessageVM stompMessageVM = ChattingDto.StompMessageVM.builder()
                .senderId(sendMessageParams.getSenderId())
                .roomType(sendMessageParams.getRoomType())
                .messageType(chattingMessage.getMessageType())
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
        chattingMessage.setMessageType(sendMessage.getMessageType());
        chattingMessage.setSender(userRepository.getById(sendMessage.getSenderId()));
        chattingMessage.setVisibleTo("ALL");
        return chattingMessageRepository.save(chattingMessage);
    }
}
