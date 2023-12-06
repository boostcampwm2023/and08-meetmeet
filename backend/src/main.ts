import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { ValidationPipe } from '@nestjs/common';
import { SlackInterceptor } from './common/slack.interceptor';
import { MeetMeetExceptionFilter } from './common/exception/exception.filter';
import { setupSwagger } from './common/swagger';
import { WinstonModule } from 'nest-winston';
import { instance } from './common/log/winston.logger';

async function bootstrap() {
  const app = await NestFactory.create(AppModule, {
    logger: WinstonModule.createLogger({
      instance: instance,
    }),
  });

  app.useGlobalInterceptors(new SlackInterceptor());
  setupSwagger(app);

  app.useGlobalPipes(
    new ValidationPipe({
      whitelist: true,
      transform: true,
    }),
  );

  app.useGlobalFilters(new MeetMeetExceptionFilter());

  await app.listen(3000);
}
bootstrap();
