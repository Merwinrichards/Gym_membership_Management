package com.gym.gym_membership_system.service;

import com.gym.gym_membership_system.model.Member;
import com.gym.gym_membership_system.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    public Member addMember(Member member) {
        return memberRepository.save(member);
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));
    }

    public Member updateMember(Long id, Member updatedMember) {

        Member member = getMemberById(id);

        member.setName(updatedMember.getName());
        member.setContactNumber(updatedMember.getContactNumber());
        member.setMembershipPlan(updatedMember.getMembershipPlan());
        member.setPaymentStatus(updatedMember.getPaymentStatus());

        return memberRepository.save(member);
    }

    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    public List<Member> searchMembersByName(String name) {
        return memberRepository.findAll()
                .stream()
                .filter(m -> m.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }

    public List<Member> filterMembersByPaymentStatus(String status) {
        return memberRepository.findAll()
                .stream()
                .filter(m -> m.getPaymentStatus().equalsIgnoreCase(status))
                .toList();
    }

    public long getTotalMembersCount() {
        return memberRepository.count();
    }

    public List<Member> getMembersByMembershipPlan(String plan) {
        return memberRepository.findAll()
                .stream()
                .filter(m -> m.getMembershipPlan().equalsIgnoreCase(plan))
                .toList();
    }
}