import { Injectable } from '@nestjs/common';
import { PassportStrategy } from '@nestjs/passport';
import { ExtractJwt, Strategy } from 'passport-jwt';
import { ConfigService } from '@nestjs/config';
import { User } from 'src/user/entities/user.entity';
import { UserService } from 'src/user/user.service';
import {
  ExpiredTokenException,
  InvalidPayloadException,
} from './exception/auth.exception';

@Injectable()
export class JwtStrategy extends PassportStrategy(Strategy) {
  constructor(
    private userService: UserService,
    private configService: ConfigService,
  ) {
    super({
      ignoreExpiration: true,
      secretOrKey: configService.get('JWT_SECRET_KEY'),
      jwtFromRequest: ExtractJwt.fromAuthHeaderAsBearerToken(),
    });
  }

  async validate(payload: { email: string; exp: number }): Promise<User> {
    const { email, exp } = payload;

    if (!email) {
      throw new InvalidPayloadException();
    }

    if (exp < new Date().getTime() / 1000) {
      throw new ExpiredTokenException();
    }

    const user: User | null = await this.userService.findUserByEmail(email);
    if (!user) {
      throw new InvalidPayloadException();
    }
    return user;
  }
}
