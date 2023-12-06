import {
  Injectable,
  NestInterceptor,
  ExecutionContext,
  CallHandler,
  Logger,
} from '@nestjs/common';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import axios from 'axios';
import * as process from 'process';

@Injectable()
export class SlackInterceptor implements NestInterceptor {
  private readonly logger = new Logger(SlackInterceptor.name);

  intercept(context: ExecutionContext, next: CallHandler): Observable<any> {
    const request = context.switchToHttp().getRequest();
    const method = request.method;
    const url = request.url;

    return next.handle().pipe(
      catchError((error) => {
        let statusCode = error?.response?.statusCode || 500;
        if (error?.status !== undefined) {
          statusCode = error.status;
        }
        if (statusCode !== 418 && statusCode >= 500) {
          this.sendSlackNotification(error, method, url);
        }
        throw error;
      }),
    );
  }

  private sendSlackNotification(error: any, method: string, url: string) {
    const slackWebhookUrl = process.env.SLACK_WEBHOOK_URL;
    if (!slackWebhookUrl) {
      return;
    }
    const errorName = error.name;
    const errorMessage = error.message;
    const stackTrace = error.stack;

    const message = {
      text: `:rotating_light: *Error Notification - ${
        process.env.NODE_ENV || 'local'
      }* :rotating_light:`,
      attachments: [
        {
          color: 'danger',
          title: `Error Details - ${process.env.SLACK_USERNAME}`,
          fields: [
            {
              title: 'Error Name',
              value: errorName,
              short: true,
            },
            {
              title: 'Error Message',
              value: errorMessage,
            },
            {
              title: 'Request',
              value: method + ' ' + url,
            },
            {
              title: 'Stack Trace',
              value: '```' + stackTrace + '```',
            },
          ],
        },
      ],
    };

    axios
      .post(slackWebhookUrl, message)
      .catch((err) => this.logger.error('Slack notification sent failed', err));
  }
}
