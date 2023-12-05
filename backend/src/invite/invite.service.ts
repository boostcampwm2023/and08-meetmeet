import { HttpException, Injectable } from '@nestjs/common';
import * as admin from 'firebase-admin';
import { User } from '../user/entities/user.entity';
import { Invite } from './entities/invite.entity';
import { InjectRepository } from '@nestjs/typeorm';
import { Equal, Repository } from 'typeorm';
import { StatusEnum } from './entities/status.enum';
import { Status } from './entities/status.entity';
import { Event } from '../event/entities/event.entity';
@Injectable()
export class InviteService {
  constructor(
    @InjectRepository(Invite)
    private readonly inviteRepository: Repository<Invite>,
    @InjectRepository(Status)
    private readonly statusRepository: Repository<Status>,
  ) {
    admin.initializeApp({
      credential: admin.credential.applicationDefault(),
    });
  }
  async sendFollowMessage(user: User, follower: User, fcmToken: string) {
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
    await this.createInvite('FOLLOW', null, user, follower);
  }

  async sendInviteEventMessage(
    user: User,
    event: Event,
    invitedUser: User,
    fcmToken: string,
  ) {
    const eventOwner = event.eventMembers.find(
      (eventMember) => eventMember.user.id === user.id,
    )?.user;
    const invite = await this.createInvite(
      'INVITE',
      event.id,
      user,
      invitedUser,
    );
    const message: admin.messaging.Message = {
      data: {
        type: 'EVENT_INVITATION',
        body: JSON.stringify({
          inviteId: invite.id,
          eventId: event.id,
          title: event.title,
          startDate: event.startDate,
          endDate: event.endDate,
          eventOwner: {
            id: eventOwner?.id,
            nickname: eventOwner?.nickname,
            profile: eventOwner?.profile?.path ?? null,
          },
        }),
      },
      token: fcmToken,
    };
    if (!(await this.sendFireBaseMessage(message))) {
      await this.updateInvite(invite.id, StatusEnum.Error);
    }
  }

  async sendFireBaseMessage(message: admin.messaging.Message) {
    try {
      await admin
        .messaging()
        .send(message)
        .then((response) => {
          console.log('Successfully sent message:', response);
        });
      return true;
    } catch (error) {
      console.log('Error sending message:', error);
      return false;
    }
  }

  async createInvite(
    type: string,
    eventId: number | null,
    sender: User,
    receiver: User,
  ) {
    const invite = new Invite();
    invite.sender = sender;
    invite.receiver = receiver;
    if (eventId) {
      invite.event = { id: eventId } as any;
    }

    if (type === 'FOLLOW') {
      const status = await this.statusRepository.findOne({
        where: { displayName: StatusEnum.Accepted },
      });
      if (!status) {
        throw new HttpException('Status not found', 404);
      }
      invite.status = status;
    } else if (type === 'INVITE') {
      const status = await this.statusRepository.findOne({
        where: { displayName: StatusEnum.Pending },
      });
      if (!status) {
        throw new HttpException('Status not found', 404);
      }
      invite.status = status;
    }

    return await this.inviteRepository.save(invite);
  }

  async updateInvite(inviteId: number, status: StatusEnum) {
    const invite = await this.inviteRepository.findOne({
      where: { id: inviteId },
    });
    if (!invite) {
      throw new HttpException('Invite not found', 404);
    }
    const statusEntity = await this.statusRepository.findOne({
      where: { displayName: status },
    });
    if (!statusEntity) {
      throw new HttpException('Status not found', 404);
    }
    invite.status = statusEntity;
    await this.inviteRepository.save(invite);
  }

  async getInviteById(inviteId: number) {
    return await this.inviteRepository.findOne({
      where: { id: inviteId },
      relations: ['sender', 'receiver'],
    });
  }

  async getInvitesByUser(user: User) {
    return await this.inviteRepository.find({
      where: { receiver: Equal(user.id) },
      relations: ['sender', 'event', 'status'],
    });
  }

  async getInvitesByEvent(event: Event) {
    return await this.inviteRepository.find({
      where: { event: Equal(event.id) },
      relations: ['sender', 'receiver'],
    });
  }

  transformStatusEnumResponse(status: StatusEnum) {
    if (status === StatusEnum.Accepted) {
      return StatusEnum.Accepted;
    } else if (status === StatusEnum.Pending) {
      return StatusEnum.Pending;
    } else {
      return StatusEnum.Joinable;
    }
  }
}
