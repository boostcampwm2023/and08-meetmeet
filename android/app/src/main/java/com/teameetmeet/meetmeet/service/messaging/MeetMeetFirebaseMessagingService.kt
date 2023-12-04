package com.teameetmeet.meetmeet.service.messaging

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.squareup.moshi.Moshi
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.model.EventMember
import com.teameetmeet.meetmeet.service.INTENT_REQUEST_ID_EVENT_INVITATION_NOTIFICATION
import com.teameetmeet.meetmeet.service.INTENT_REQUEST_ID_FOLLOW_NOTIFICATION
import com.teameetmeet.meetmeet.service.messaging.model.EventInvitationMessage
import com.teameetmeet.meetmeet.service.notification.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MeetMeetFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var moshi: Moshi

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        //todo: 서버에 토큰 보내기
    }

    override fun onMessageReceived(message: RemoteMessage) {
        when (message.data[MESSAGE_KEY_TYPE]) {
            MESSAGE_TYPE_FOLLOW -> {
                val follower =
                    moshi.adapter(EventMember::class.java)
                        .fromJson(message.data[MESSAGE_KEY_BODY])
                        ?: return
                notificationHelper.createNotification(
                    channelId = NotificationHelper.CHANNEL_ID_FOLLOW_NOTIFICATION,
                    requestCode = INTENT_REQUEST_ID_FOLLOW_NOTIFICATION,
                    title = getString(R.string.notification_title_follow_notification),
                    content = getString(R.string.notification_follow_message, follower.nickname),
                    id = follower.id,
                    icon = follower.profile
                )
            }

            MESSAGE_TYPE_EVENT_INVITATION -> {
                val eventInvitationMessage =
                    moshi.adapter(EventInvitationMessage::class.java)
                        .fromJson(message.data[MESSAGE_KEY_BODY])
                        ?: return
                notificationHelper.createNotification(
                    channelId = NotificationHelper.CHANNEL_ID_EVENT_INVITATION_NOTIFICATION,
                    requestCode = INTENT_REQUEST_ID_EVENT_INVITATION_NOTIFICATION,
                    title = getString(R.string.notification_title_event_invitation_notification),
                    content = getString(
                        R.string.notification_event_invitation_message,
                        eventInvitationMessage.eventOwner.nickname,
                        eventInvitationMessage.title
                    ),
                    id = eventInvitationMessage.eventId,
                    icon = eventInvitationMessage.eventOwner.profile
                )
            }
        }
    }

    companion object {
        const val MESSAGE_KEY_TYPE = "type"
        const val MESSAGE_KEY_BODY = "body"

        const val MESSAGE_TYPE_FOLLOW = "FOLLOW"
        const val MESSAGE_TYPE_EVENT_INVITATION = "EVENT_INVITATION"
    }
}