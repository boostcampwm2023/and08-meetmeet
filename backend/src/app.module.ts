import { Module } from '@nestjs/common';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { TypeOrmModule } from '@nestjs/typeorm';
import { SnakeNamingStrategy } from 'typeorm-naming-strategies';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { CalendarModule } from './calendar/calendar.module';
import { EventModule } from './event/event.module';
import { DetailModule } from './detail/detail.module';
import { EventMemberModule } from './event-member/event-member.module';
import { InviteModule } from './invite/invite.module';
import { CommentModule } from './comment/comment.module';
import { GroupModule } from './group/group.module';
import { FollowModule } from './follow/follow.module';
import { AuthModule } from './auth/auth.module';
import { UserModule } from './user/user.module';

@Module({
  imports: [
    ConfigModule.forRoot({ isGlobal: true }),
    TypeOrmModule.forRootAsync({
      imports: [ConfigModule],
      inject: [ConfigService],
      useFactory: (configService: ConfigService) => ({
        type: 'mysql',
        host: configService.get<string>('DB_HOST'),
        port: configService.get<number>('DB_PORT'),
        username: configService.get<string>('DB_USER'),
        password: configService.get<string>('DB_PASSWORD'),
        database: configService.get<string>('DB_DATABASE'),
        entities: ['dist/**/*.entity{.ts,.js}'],
        synchronize: false,
        logging: true,
        namingStrategy: new SnakeNamingStrategy(),
      }),
    }),
    CalendarModule,
    EventModule,
    DetailModule,
    EventMemberModule,
    InviteModule,
    CommentModule,
    GroupModule,
    FollowModule,
    UserModule,
    AuthModule,
  ],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
