import { INestApplication } from '@nestjs/common';
import { SwaggerModule, DocumentBuilder } from '@nestjs/swagger';
import * as expressBasicAuth from 'express-basic-auth';
export function setupSwagger(app: INestApplication): void {
  const SWAGGER_ID = process.env.SWAGGER_ID;
  const SWAGGER_PASSWORD = process.env.SWAGGER_PASSWORD;

  if (!SWAGGER_ID || !SWAGGER_PASSWORD) {
    return;
  }
  console.log(SWAGGER_ID, SWAGGER_PASSWORD);
  app.use(
    ['/docs'],
    expressBasicAuth({
      challenge: true,
      users: {
        [SWAGGER_ID]: SWAGGER_PASSWORD,
      },
      unauthorizedResponse: {
        message: '허가되지 않는 사용자입니다.',
      },
    }),
  );
  const config = new DocumentBuilder()
    .setTitle(`MeetMeet's backend api`)
    .setDescription(`The MeetMeet's API description`)
    .setVersion('1.0')
    .addBearerAuth({
      type: 'http',
      scheme: 'bearer',
      name: 'JWT',
      in: 'header',
    })
    .build();
  const document = SwaggerModule.createDocument(app, config);
  SwaggerModule.setup('docs', app, document, {
    swaggerOptions: {
      persistAuthorization: true,
    },
  });
}
