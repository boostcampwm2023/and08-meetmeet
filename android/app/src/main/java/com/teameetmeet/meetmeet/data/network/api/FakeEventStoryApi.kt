package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.model.EventMember
import com.teameetmeet.meetmeet.data.model.EventStory
import com.teameetmeet.meetmeet.data.model.Feed
import com.teameetmeet.meetmeet.data.network.entity.SingleStringRequest

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
                EventMember(4, "chanmin", "https://github.com/p-chanmin.png"),
                EventMember(5, "hailim", "https://github.com/LeeHaiLim.png"),
                EventMember(6, "hailim", "https://github.com/LeeHaiLim.png"),
                EventMember(7, "hailim", "https://github.com/LeeHaiLim.png"),
                EventMember(8, "hailim", "https://github.com/LeeHaiLim.png"),
                EventMember(9, "hailim", "https://github.com/LeeHaiLim.png"),
                EventMember(10, "hailim", "https://github.com/LeeHaiLim.png"),
                EventMember(11, "hailim", "https://github.com/LeeHaiLim.png"),
                EventMember(12, "hailim", "https://github.com/LeeHaiLim.png"),

            ),
            announcement = "다들 11시 반에 광명역에서 집합입니다~",
            authority = "",
            repeatPolicyId = null,
            isJoin = true,
            isVisible = true,
            memo = "9시 반 기차로 서대구역에서 출발해야함.",
            feeds = listOf(
                Feed(1, "https://github.com/agfalcon.png", "this is memo"),
                Feed(2, null, "this is memo"),
                Feed(3, null, null),
                Feed(4, "https://github.com/agfalcon.png", "this is memo"),
                Feed(5, null, "this is memo"),
                Feed(6, null, null),
                Feed(7, "https://github.com/agfalcon.png", "this is memo"),
                Feed(8, null, "this is memo"),
                Feed(9, null, null),
                Feed(10, "https://github.com/agfalcon.png", "this is memo"),
                Feed(11, null, "this is memo"),
                Feed(12, null, null),
                Feed(13, "https://github.com/agfalcon.png", "this is memo"),
                Feed(14, null, "this is memo"),
                Feed(15, null, null),
                Feed(16, "https://github.com/agfalcon.png", "this is memo"),
                Feed(17, null, "this is memo"),
                Feed(18, null, null)
            )
        )
    }

    override fun editNotification(singleStringRequest: SingleStringRequest) {

    }
}