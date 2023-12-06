import { createLogger, format, transports } from 'winston';
import * as winstonDaily from 'winston-daily-rotate-file';
import { dailyOption } from './winston.daily';

const customFormat = format.printf(({ timestamp, level, stack, message }) => {
  return `[MeetMeet] ${timestamp} - [${level
    .toUpperCase()
    .padEnd(2)
    .padStart(2)}] - ${stack || message}`;
});

const options = {
  file: {
    filename: 'error.log',
    level: 'error',
  },
  console: {
    level: 'silly',
  },
};

const devLogger = {
  format: format.combine(
    format.timestamp({ format: 'YYYY-MM-DD HH:mm:ss' }),
    format.errors({ stack: true }),
    customFormat,
    format.colorize({ all: true }),
  ),
  transports: [new transports.Console(options.console)],
};

const prodLogger = {
  format: format.combine(
    format.timestamp({ format: 'YYYY-MM-DD HH:mm:ss' }),
    format.errors({ stack: true }),
    format.json(),
    customFormat,
    format.colorize({ all: true }),
  ),
  transports: [
    new winstonDaily(dailyOption('warn')),
    new winstonDaily(dailyOption('error')),
    new winstonDaily(dailyOption('info')),
    new transports.Console(options.console),
  ],
};

const instanceLogger = process.env.NODE_ENV === 'prod' ? prodLogger : devLogger;

export const logger = createLogger(instanceLogger);
