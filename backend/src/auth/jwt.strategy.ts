import { Injectable, UnauthorizedException } from '@nestjs/common';
import { PassportStrategy } from '@nestjs/passport';
import { ExtractJwt, Strategy } from 'passport-jwt';
import { ConfigService } from '@nestjs/config';
import { User } from 'src/user/entities/user.entity';
import { UserService } from 'src/user/user.service';
import { TokenExpiredError } from '@nestjs/jwt';

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

  async validate(payload: unknown): Promise<User> {
    const { email, exp } = payload as any;

    if (!email) {
      throw new UnauthorizedException();
    }

    if (exp < new Date().getTime() / 1000) {
      throw new TokenExpiredError('토큰이 만료되었습니다.', exp);
    }

    const user: User | null = await this.userService.findUserByEmail(email);

    if (!user) {
      throw new UnauthorizedException();
    }
    return user;
  }
}
