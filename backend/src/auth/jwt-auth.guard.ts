import { Injectable } from '@nestjs/common';
import { TokenExpiredError } from '@nestjs/jwt';
import { AuthGuard } from '@nestjs/passport';
import {
  ExpiredTokenException,
  InvalidPayloadException,
  InvalidTokenException,
} from './exception/auth.exception';

@Injectable()
export class JwtAuthGuard extends AuthGuard('jwt') {
  handleRequest<User>(err: Error, user: User): User {
    if (err instanceof TokenExpiredError) {
      throw new ExpiredTokenException();
    }
    if (!user) {
      throw new InvalidPayloadException();
    }
    if (err instanceof Error) {
      throw new InvalidTokenException();
    }
    return user;
  }
}
