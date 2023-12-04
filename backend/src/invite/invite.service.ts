import { Injectable } from '@nestjs/common';
import * as admin from 'firebase-admin';
import { User } from '../user/entities/user.entity';
@Injectable()
export class InviteService {
  constructor() {
    admin.initializeApp({
      credential: admin.credential.applicationDefault(),
    });
  }
  // async sendInvite(user: User, word: string) {
  //   const fcmToken = await this.userService.getFcmToken(user);
  //   if (!fcmToken) {
  //     throw new Error('fcmToken이 없습니다.');
  //   }
  //   const message = {
  //     data: {
  //       title: `${word}초대장이 도착했어요!`,
  //       body: `${JSON.stringify(user)} 초대장이 도착했어요!`,
  //     },
  //     token: fcmToken,
  //   };
  //   await admin
  //     .messaging()
  //     .send(message)
  //     .then((response) => {
  //       console.log('Successfully sent message:', response);
  //     })
  //     .catch((error) => {
  //       console.log('Error sending message:', error);
  //     });
  // }

  async sendFollowMessage(user: User, fcmToken: string) {
    const message: admin.messaging.Message = {
      data: {
        type: 'FOLLOW',
        body: JSON.stringify({
          id: user.id,
          nickname: user.nickname,
          profile: user.profile?.path ?? null,
        }),
      },
      token: fcmToken,
    };
    await this.sendFireBaseMessage(message);
  }

  async sendFireBaseMessage(message: admin.messaging.Message) {
    try {
      await admin
        .messaging()
        .send(message)
        .then((response) => {
          console.log('Successfully sent message:', response);
        });
    } catch (error) {
      console.log('Error sending message:', error);
    }
  }
}
