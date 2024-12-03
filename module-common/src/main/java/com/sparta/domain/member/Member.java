package com.sparta.domain.member;


import com.sparta.domain.entity.AuthMember;
import com.sparta.domain.entity.Timestamped;
import com.sparta.domain.ticket.Ticket;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "members")
public class Member extends Timestamped {
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String email;

	@Column
	private String name;

	@Column
	private String password;

	@Enumerated(EnumType.STRING)
	private MemberRole role;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	private List<Ticket> tickets = new ArrayList<>();

	public Member(String email, String name, String password, MemberRole role) {
		this.email = email;
		this.name = name;
		this.password = password;
		this.role = role;
	}

	private Member(Long id, String email, MemberRole memberRole) {
		this.id = id;
		this.email = email;
		this.role = memberRole;
	}

	public static Member fromAuthMember(AuthMember authUser) {
		return new Member(authUser.getId(), authUser.getEmail(), authUser.getUserRole());
	}

	public void changePassword(String password) {
		this.password = password;
	}

	public void updateRole(MemberRole memberRole) {
		this.role = memberRole;
	}
}
