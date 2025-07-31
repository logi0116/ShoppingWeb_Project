package com.busanit501.shoppingweb_project.service;

import com.busanit501.shoppingweb_project.dto.MemberDTO;
import com.busanit501.shoppingweb_project.domain.Address;
import com.busanit501.shoppingweb_project.domain.Member;
import com.busanit501.shoppingweb_project.dto.UserinfoDTO;
import com.busanit501.shoppingweb_project.mapper.MemberMapper;
import com.busanit501.shoppingweb_project.repository.AddressRepository;
import com.busanit501.shoppingweb_project.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public void register(MemberDTO dto) {
        // 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        // DTO → Member 변환
        Member member = MemberMapper.toMemberEntity(dto, encodedPassword);

        // 기본 권한 세팅
        boolean isAdmin = false;

        if (isAdmin) {
            member.settingRole("ROLE_ADMIN");
        } else {
            member.settingRole("ROLE_USER");
        }

        // 주소가 있다면 Address도 생성 후 관계 연결
        if (dto.isRegisterAddress()) {
            Address address = MemberMapper.toAddressEntity(dto, member);
            member.getAddresses().add(address); // 양방향일 경우
        }

        memberRepository.save(member);
    }

    @Override
    public boolean isMemberIdDuplicated(String memberId) {
        return memberRepository.existsByMemberId(memberId);
    }

    @Override
    public UserinfoDTO getUserinfoDTOById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("Member not found with memberId: " + memberId));
        Address address = addressRepository.findByMemberAndIsDefaultTrue(member);
        UserinfoDTO dto = UserinfoDTO.toUserinfoDTO(member, address);
        return dto;
    }
}