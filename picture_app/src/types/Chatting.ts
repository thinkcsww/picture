import { User } from "./User";
import { PageResult } from "./Page";

export namespace Chatting {
  export enum RoomType {
    GROUP = "GROUP",
    PRIVATE = "PRIVATE"
  }

  export enum MessageType {
    MESSAGE = "MESSAGE",
    IMAGE = "IMAGE",
    ENTER = "ENTER",
    RECEIVE = "RECEIVE",
    REQUEST_MATCHING = "REQUEST_MATCHING",
    ACCEPT_MATCHING = "ACCEPT_MATCHING",
    DECLINE_MATCHING = "DECLINE_MATCHING",
    COMPLETE_MATCHING = "COMPLETE_MATCHING"
  }

  export interface ChattingRoom {
    id: string;
    opponent: User.VM;
    lastMessageDt: string;
    lastMessage: ChattingMessage;
    unreadCount: number;
    messages: PageResult<ChattingMessage>;
    newRoom: boolean;
  }

  export interface ChattingMessage {
    id: string;
    senderId: string;
    message: string;
    createdDt: string;
    readBy: string;
    messageType: MessageType;
    fileName: string;
  }
}
