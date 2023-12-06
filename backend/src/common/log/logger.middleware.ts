import { Injectable, NestMiddleware } from '@nestjs/common';
import { Request, Response, NextFunction } from 'express';
import { logger } from './winston.logger';

@Injectable()
export class LoggerMiddleware implements NestMiddleware {
  constructor() {}

  use(req: Request, res: Response, next: NextFunction) {
    const { ip, method, originalUrl } = req;
    const userAgent = req.get('user-agent');

    res.on('finish', () => {
      const { statusCode } = res;

      if (statusCode >= 400 && statusCode < 500)
        logger.warn(
          `[${method}]${originalUrl}(${statusCode}) ${ip} ${userAgent}`,
        );
      else if (statusCode >= 500)
        logger.error(
          `[${method}]${originalUrl}(${statusCode}) ${ip} ${userAgent}`,
        );
      else
        logger.info(
          `[${method}]${originalUrl}(${statusCode}) ${ip} ${userAgent}`,
        );
    });

    next();
  }
}
