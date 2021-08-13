package mj.carthy.easysecurity.authentication

import mj.carthy.easysecurity.model.UserAuth
import org.springframework.security.authentication.AbstractAuthenticationToken
import java.util.*

data class UserTokenAuth(private val userAuth: UserAuth, private val token: String, val sessionId: UUID) : AbstractAuthenticationToken(userAuth.authorities) {
    override fun getCredentials(): String = token
    override fun getPrincipal(): UserAuth = userAuth
    override fun isAuthenticated(): Boolean = userAuth.isEnabled && userAuth.authorities.isNotEmpty()
}