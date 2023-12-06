import { utilities } from 'nest-winston';
import * as winston from 'winston';

export const dailyOption = (level: string) => {
  return {
    level,
    datePattern: 'YYYY-MM-DD',
    dirname: `./logs/${level}`,
    filename: `%DATE%.${level}.log`,
    maxFiles: 30,
    zippedArchive: true,
    format: winston.format.combine(
      winston.format.timestamp(),
      utilities.format.nestLike(process.env.NODE_ENV, {
        colors: false,
        prettyPrint: true,
      }),
    ),
  };
};
