export namespace Chatting {
  export enum RoomType {
    GROUP = 'GROUP',
    PRIVATE = 'PRIVATE'
  }
  export interface ChattingRoom {

  }

  export interface ChattingMessage {
    senderId: string;
    message: string;
    createdDt: string;
  }
}
