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
                EventMember(1, "chani", "https://github.com/chani1209.png"),
                EventMember(2, "dajung", "https://github.com/cdj2073.png"),
                EventMember(3, "hailim", "https://github.com/LeeHaiLim.png"),
                EventMember(4, "chanmin", "https://github.com/p-chanmin.png")
            ),
            announcement = "다들 11시 반에 광명역에서 집합입니다~",
            authority = "owner",
            repeatPolicyId = null,
            isJoin = true,
            isVisible = true,
            memo = "9시 반 기차로 서대구역에서 출발해야함.",
            feeds = listOf(
                Feed(1, "https://github.com/agfalcon.png", "this is memo"),
                Feed(5, null, "this is memo"),
                Feed(2, null, null)
            )
        )
    }
}