import { ImATeapotException, Injectable } from '@nestjs/common';
import { TokenExpiredError } from '@nestjs/jwt';
import { AuthGuard } from '@nestjs/passport';

@Injectable()
export class JwtAuthGuard extends AuthGuard('jwt') {
  handleRequest<User>(err: Error, user: User): User {
    if (err instanceof TokenExpiredError) {
      throw new ImATeapotException('Access token is expired.');
    }
    if (!user) {
      throw new ImATeapotException('Invalid token.');
    }
    if (err instanceof Error) {
      throw new ImATeapotException(err.message);
    }
    return user;
  }
}
