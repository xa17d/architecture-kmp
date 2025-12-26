package at.xa1.architecture.kmp.sample.shared.integration.user

import at.xa1.architecture.kmp.navigation.ScopeContext
import at.xa1.architecture.kmp.sample.shared.common.user.UserInfo

data class UserScopeContext(
    val userInfo: UserInfo
) : ScopeContext
