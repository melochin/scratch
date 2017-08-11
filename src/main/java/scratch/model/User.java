package scratch.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name="user")
public class User implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="user_id")
	private Long userId;
	
	@Column(name="username", length=30)
	@NotBlank(message="用户名不能为空")
	@Pattern(regexp="^[a-zA-Z0-9_]{6,18}$", message="用户名只能以数字或字母组合，且长度不能少于6位超过18位")
	private String username;
	
	@Column(name="email", length=100)
	@Pattern(regexp="^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", message="请输入正确的邮箱")
	private String email;
	
	@NotBlank(message="密码不能为空")
	@Pattern(regexp="^[a-zA-Z0-9_]{6,18}$", message="密码只能以数字或字母组合，且长度不能少于6位超过18位")
	@Column(insertable=false, updatable=false)
	private String password;
	
	@Column(name="password")
	private String hashedPassword;
	
	@Column(name="status", length=1)
	private String status;
	
	@Column(name="salt")
	private String salt;
	
	@Column(name="role")
	private Integer role;
	
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
		return "User [userId=" + userId + ", username=" + username + ", email=" + email + ", password=" + password
				+ ", status=" + status + ", role=" + role + "]";
	}

}
