package scratch.model.entity;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name="user")
public class User implements Serializable {

	private static final long serialVersionUID = -3192842924146787219L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="user_id")
	private Long userId;
	
	@Column(name="username", length=30)
	@NotBlank(message="用户名不能为空")
	@Pattern(regexp="^[a-zA-Z0-9_]{6,18}$", message="用户名只能以数字或字母组合，且长度不能少于6位超过18位")
	private String username;

	//@Pattern(regexp="^[a-zA-Z0-9_]{6,18}$", message="密码只能以数字或字母组合，且长度不能少于6位超过18位")
	@Transient
	private String password;
	
	@Column(name="password")
	private String hashedPassword;

	@Column(name="email", length=100)
	@Pattern(regexp="^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", message="请输入正确的邮箱")
	private String email;

	/**
	 * 用户状态
	 */
	@Column(name="status", length=1)
	private String status;

	/**
	 * 用户角色
	 */
	@Column(name="role")
	private Integer role;

	/**
	 * 加密的盐
	 */
	@Column(name="salt")
	private String salt;
	
	public User() {}

	public User(String username) {
		this.username = username;
	}
	
	public User(Long userId) {
		this.userId = userId;
	}

	public User(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public Integer getRole() {
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
	}
	
	public String getHashedPassword() {
		return hashedPassword;
	}

	public void setHashedPassword(String hashedPassword) {
		this.hashedPassword = hashedPassword;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	@Override
	public String toString() {
		return "User{" +
				"userId=" + userId +
				", username='" + username + '\'' +
				", email='" + email + '\'' +
				", status='" + status + '\'' +
				", role=" + role +
				'}';
	}
}
