import { Injectable, NestMiddleware } from '@nestjs/common';
import { Request, Response, NextFunction } from 'express';
import { instance } from './winston.logger';

@Injectable()
export class LoggerMiddleware implements NestMiddleware {
  constructor() {}

  use(req: Request, res: Response, next: NextFunction) {
    const { ip, method, originalUrl } = req;
    const userAgent = req.get('user-agent');

    res.on('finish', () => {
      const { statusCode } = res;

      if (statusCode >= 400 && statusCode < 500)
        instance.warn(
          `[${method}]${originalUrl}(${statusCode}) ${ip} ${userAgent}`,
        );
      else if (statusCode >= 500)
        instance.error(
          `[${method}]${originalUrl}(${statusCode}) ${ip} ${userAgent}`,
        );
      else
        instance.info(
          `[${method}]${originalUrl}(${statusCode}) ${ip} ${userAgent}`,
        );
    });

    next();
  }
}
