# fk 키
-- User table
ALTER TABLE user
ADD CONSTRAINT FK_User_OAuthProvider
FOREIGN KEY (oauth_provider_id)
REFERENCES oauth_provider(id);

-- Calendar table --> 미정

-- Event table
ALTER TABLE event
ADD CONSTRAINT FK_Event_RepeatPolicy
FOREIGN KEY (repeatPolicy_id)
REFERENCES repeat_policy(id);

-- EventMember table
ALTER TABLE event_member
ADD CONSTRAINT FK_EventMember_Event
FOREIGN KEY (event_id)
REFERENCES event(id),
ADD CONSTRAINT FK_EventMember_User
FOREIGN KEY (user_id)
REFERENCES user(id),
ADD CONSTRAINT FK_EventMember_Detail
FOREIGN KEY (detail_id)
REFERENCES detail(id),
ADD CONSTRAINT FK_EventMember_Authority
FOREIGN KEY (authority_id)
REFERENCES authority(id);

-- Feed table
ALTER TABLE feed
ADD CONSTRAINT FK_Feed_User
FOREIGN KEY (author)
REFERENCES user(id),
ADD CONSTRAINT FK_Feed_Event
FOREIGN KEY (event_id)
REFERENCES event(id);

-- Comment table
ALTER TABLE comment
ADD CONSTRAINT FK_Comment_Feed
FOREIGN KEY (feed_id)
REFERENCES feed(id),
ADD CONSTRAINT FK_Comment_User
FOREIGN KEY (author)
REFERENCES user(id);

-- FeedContent table
ALTER TABLE feed_content
ADD CONSTRAINT FK_FeedContent_Feed
FOREIGN KEY (feed_id)
REFERENCES feed(id),
ADD CONSTRAINT FK_FeedContent_Content
FOREIGN KEY (content_id)
REFERENCES content(id);

-- Invite table
ALTER TABLE invite
ADD CONSTRAINT FK_Invite_InviteType
FOREIGN KEY (invite_type_id)
REFERENCES invite_type(id),
ADD CONSTRAINT FK_Invite_User_Sender
FOREIGN KEY (sender)
REFERENCES user(id),
ADD CONSTRAINT FK_Invite_User_Receiver
FOREIGN KEY (receiver)
REFERENCES user(id),
ADD CONSTRAINT FK_Invite_Event
FOREIGN KEY (event_id)
REFERENCES event(id);

-- Follow table
ALTER TABLE follow
ADD CONSTRAINT FK_Follow_User
FOREIGN KEY (user_id)
REFERENCES user(id),
ADD CONSTRAINT FK_Follow_Follower
FOREIGN KEY (follower_id)
REFERENCES user(id);

-- UserGroup table
ALTER TABLE user_group
ADD CONSTRAINT FK_UserGroup_User
FOREIGN KEY (user_id)
REFERENCES user(id),
ADD CONSTRAINT FK_UserGroup_Group
FOREIGN KEY (group_id)
REFERENCES `group`(id),
ADD CONSTRAINT FK_UserGroup_Authority
FOREIGN KEY (authority_id)
REFERENCES authority(id);
