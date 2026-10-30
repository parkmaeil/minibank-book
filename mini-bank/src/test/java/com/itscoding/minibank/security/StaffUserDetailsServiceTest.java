package com.itscoding.minibank.security;

import com.itscoding.minibank.domain.Staff;
import com.itscoding.minibank.domain.StaffRole;
import com.itscoding.minibank.repository.StaffRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

// 14.9절 DB의 직원 정보가 Spring Security의 UserDetails로 제대로 바뀌는지 (10.2절의 Mockito 단위 테스트)
@ExtendWith(MockitoExtension.class)
class StaffUserDetailsServiceTest {

    @Mock private StaffRepository staffRepository;
    @InjectMocks private StaffUserDetailsService service;

    @Test
    void 관리자는_TELLER와_ADMIN_역할을_모두_갖는다() {
        given(staffRepository.findByUsername("admin"))
                .willReturn(Optional.of(new Staff("admin", "bcrypt-hash", "관리자", StaffRole.ADMIN)));

        UserDetails user = service.loadUserByUsername("admin");

        assertThat(user.getAuthorities()).extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_TELLER", "ROLE_ADMIN");
        assertThat(user.getPassword()).isEqualTo("bcrypt-hash");   // 해시를 그대로 넘긴다 — 비교는 PasswordEncoder 몫
    }

    @Test
    void 비활성_직원은_enabled가_false다() {
        Staff staff = new Staff("old", "bcrypt-hash", "퇴사자", StaffRole.TELLER);
        staff.disable();
        given(staffRepository.findByUsername("old")).willReturn(Optional.of(staff));

        assertThat(service.loadUserByUsername("old").isEnabled()).isFalse();
    }

    @Test
    void 없는_아이디면_UsernameNotFoundException() {
        given(staffRepository.findByUsername("nobody")).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("nobody"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
