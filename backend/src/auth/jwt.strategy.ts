import { Injectable, UnauthorizedException } from '@nestjs/common';
import { PassportStrategy } from '@nestjs/passport';
import { ExtractJwt, Strategy } from 'passport-jwt';
import { ConfigService } from '@nestjs/config';
import { User } from 'src/user/entities/user.entity';
import { UserService } from 'src/user/user.service';

@Injectable()
export class JwtStrategy extends PassportStrategy(Strategy) {
  constructor(
    private userService: UserService,
    private configService: ConfigService,
  ) {
    super({
      // todo : ignoreExpiration true로 바꿀지 고민
      ignoreExpiration: false,
      secretOrKey: configService.get('JWT_SECRET_KEY'),
      jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
    });
  }

  async validate(payload: { nickname: string }): Promise<User> {
    const { nickname } = payload;

    if (!nickname) {
      throw new UnauthorizedException();
    }

    const user: User | null =
      await this.userService.findUserByNickname(nickname);

    if (!user) {
      throw new UnauthorizedException();
    }

    return user;
  }
}
