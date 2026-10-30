package com.itscoding.minibank.security;

import com.itscoding.minibank.domain.Staff;
import com.itscoding.minibank.domain.StaffRole;
import com.itscoding.minibank.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 14.4절 로그인할 때 Spring Security가 "이 아이디의 직원 정보"를 묻는 곳
// 메모리가 아니라 staff 테이블에서 찾아 UserDetails로 바꿔 돌려준다. 비밀번호 비교는 Spring Security가 한다
@Service
@RequiredArgsConstructor
public class StaffUserDetailsService implements UserDetailsService {

    private final StaffRepository staffRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Staff staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("직원을 찾을 수 없습니다: " + username));

        return User.withUsername(staff.getUsername())
                .password(staff.getPassword())        // DB에 저장된 BCrypt 해시
                .roles(rolesOf(staff.getRole()))      // roles()가 "ROLE_" 접두어를 붙여 준다
                .disabled(!staff.isEnabled())         // 비활성 직원은 비밀번호가 맞아도 로그인 실패
                .build();
    }

    // 관리자는 창구 업무도 할 수 있으므로 TELLER 역할을 함께 준다
    private String[] rolesOf(StaffRole role) {
        return role == StaffRole.ADMIN
                ? new String[]{"TELLER", "ADMIN"}
                : new String[]{"TELLER"};
    }
}
