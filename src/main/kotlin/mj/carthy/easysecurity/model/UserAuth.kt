package mj.carthy.easysecurity.model

import mj.carthy.easyutils.enums.Sex
import org.apache.commons.lang3.StringUtils.EMPTY
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

data class UserAuth(
  val id: UUID,
  val sex: Sex?,
  private val username: String,
  private val authorities: MutableSet<out GrantedAuthority> = HashSet(),
  private val accountNonExpired: Boolean = false,
  private val accountNonLocked: Boolean = false,
  private val credentialsNonExpired: Boolean = false,
  private val enabled: Boolean = false
) : UserDetails {
    override fun getUsername(): String = username
    override fun getPassword(): String = EMPTY
    override fun isAccountNonExpired(): Boolean = accountNonExpired
    override fun isAccountNonLocked(): Boolean = accountNonLocked
    override fun isCredentialsNonExpired(): Boolean = credentialsNonExpired
    override fun isEnabled(): Boolean = enabled
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = authorities
}