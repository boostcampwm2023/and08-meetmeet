package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.model.EventMember
import com.teameetmeet.meetmeet.data.model.EventStory
import com.teameetmeet.meetmeet.data.model.Feed

class FakeEventStoryApi : EventStoryApi {
    override fun getStory(id: String): EventStory {
        return EventStory(
            id = 11,
            title = "밋밋 3차 오프라인 미팅",
            startDate = "2023-11-27 11:30",
            endDate = "2023-11-27 21:30",
            eventMembers = listOf(
                EventMember(1, "chani", "/user/profile/1"),
                EventMember(2, "dajung", "/user/profile/2"),
                EventMember(3, "hailim", ""),
                EventMember(4, "chanmin", "")
            ),
            announcement = "다들 11시 반에 광명역에서 집합입니다~",
            authority = "owner",
            repeatPolicyId = null,
            isJoin = true,
            isVisible = true,
            memo = "9시 반 기차로 서대구역에서 출발해야함.",
            feeds = listOf(
                Feed(1, "url", "this is memo"),
                Feed(5, "url", "this is memo"),
                Feed(2, null, "this is memo")
            )
        )
    }
}