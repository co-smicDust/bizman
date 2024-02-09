package org.bizman.models.member;

import lombok.RequiredArgsConstructor;
import org.bizman.commons.constants.MemberType;
import org.bizman.entities.Member;
import org.bizman.repositories.MemberRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MemberInfoService implements UserDetailsService {

    private final MemberRepository repository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Optional : 조건식 필요 없이 던지면 되기 때문에 간편
        Member member = repository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));

        MemberType type = Objects.requireNonNullElse(member.getType(), MemberType.USER);
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority(type.name()));

        return MemberInfo.builder()
                .email(member.getEmail())
                .password(member.getPassword())
                .authorities(authorities)
                .member(member)
                .build();
    }
}
