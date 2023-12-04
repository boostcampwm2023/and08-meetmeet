package com.teameetmeet.meetmeet.presentation.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentFollowListBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FollowSearchFragment :
    BaseFragment<FragmentFollowListBinding>(R.layout.fragment_follow_list) {

    private val followViewModel: FollowViewModel by viewModels({ requireParentFragment() })
    private lateinit var followState: FollowState
    private var id: Int? = null
    private lateinit var actionType: FollowActionType
    private lateinit var followAdapter: FollowAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        arguments?.let {
            followState = FollowState.fromBundle(it)
            actionType = when (it.getString(ACTION_TYPE_KEY)) {
                FollowActionType.EVENT.name -> FollowActionType.EVENT
                FollowActionType.GROUP.name -> FollowActionType.GROUP
                else -> FollowActionType.FOLLOW
            }
            id = it.getInt(ID_KEY)
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = followViewModel
        binding.state = followState

        followAdapter = FollowAdapter(
            actionType = actionType, userClickListener = followViewModel, id = id
        )
        binding.followListRv.adapter = followAdapter
    }

    override fun onResume() {
        super.onResume()
        followViewModel.updateFollowing(actionType, id)
        followViewModel.updateFollower(actionType, id)
    }

    companion object {

        private const val ACTION_TYPE_KEY = "actionType"
        private const val ID_KEY = "id"

        fun create(
            followState: FollowState, actionType: FollowActionType, id: Int
        ): FollowSearchFragment {
            val fragment = FollowSearchFragment()
            fragment.arguments = followState.toBundle().apply {
                putString(ACTION_TYPE_KEY, actionType.name)
                putInt(ID_KEY, id)
            }
            return fragment
        }
    }
}