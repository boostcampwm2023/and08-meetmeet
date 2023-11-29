import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { S3 } from 'aws-sdk';

@Injectable()
export class ObjectStorage {
  private readonly s3: S3;
  private readonly s3Bucket: string;

  constructor(configService: ConfigService) {
    this.s3 = new S3({
      region: configService.get<string>('AWS_REGION'),
      credentials: {
        // TODO: config validation
        accessKeyId: configService.get('AWS_ACCESS_KEY', ''),
        secretAccessKey: configService.get('AWS_SECRET_KEY', ''),
      },
      endpoint: configService.get('S3_ENDPOINT'),
    });

    this.s3Bucket = configService.get('S3_BUCKET', 'meetmeet');
  }

  async upload(file: Express.Multer.File) {
    await this.s3
      .upload({
        Bucket: this.s3Bucket,
        Key: file.path,
        ACL: 'public-read',
        Body: file.buffer,
      })
      .promise();
  }

  async delete(filePath: string) {
    await this.s3
      .deleteObject({ Bucket: this.s3Bucket, Key: filePath })
      .promise();
  }

  async deleteBulk(files: string[]) {
    await this.s3
      .deleteObjects({
        Bucket: this.s3Bucket,
        Delete: {
          Objects: files.map((file) => {
            return { Key: file };
          }),
        },
      })
      .promise();
  }
}
