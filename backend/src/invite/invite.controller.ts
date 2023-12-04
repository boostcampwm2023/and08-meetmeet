import { ApiBearerAuth, ApiTags } from '@nestjs/swagger';
import { Controller } from '@nestjs/common';
import { InviteService } from './invite.service';

@ApiBearerAuth()
@ApiTags('invite')
@Controller('invite')
export class InviteController {
  constructor(private readonly inviteService: InviteService) {}
}
