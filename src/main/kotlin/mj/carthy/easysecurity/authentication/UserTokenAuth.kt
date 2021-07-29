package mj.carthy.easysecurity.authentication

import mj.carthy.easysecurity.model.UserAuth
import org.springframework.security.authentication.AbstractAuthenticationToken

class UserTokenAuth(private val userAuth: UserAuth, private val token: String) : AbstractAuthenticationToken(userAuth.authorities) {
    override fun getCredentials(): String = token
    override fun getPrincipal(): UserAuth = userAuth
    override fun isAuthenticated(): Boolean = userAuth.isEnabled && userAuth.authorities.isNotEmpty()
}