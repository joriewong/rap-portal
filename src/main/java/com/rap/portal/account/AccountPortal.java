package com.rap.portal.account;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taobao.rigel.rap.account.bo.Role;
import com.taobao.rigel.rap.account.bo.User;
import com.taobao.rigel.rap.account.service.AccountMgr;

@Path("/account")
@Produces("application/json;charset=UTF-8")
public class AccountPortal {
	
	private ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"classpath*:hibernate.cfg.xml"});
	private AccountMgr accountMgr = context.getBean("accountMgr", AccountMgr.class);
//	Gson gson = new Gson();
	private String error = "{\"msg\":\"error\"}";
	private String success = "{\"msg\":\"success\"}";
    /**
	 * µÇÂ¼
	 * @param account
	 * @param password
	 * @return
	 */
	@Path("/login")
	@GET
	public String login(@Context HttpServletRequest request,
			@QueryParam("account") String account,
			@QueryParam("password") String password) {
		HttpSession session = request.getSession(true);
		if (accountMgr.validate(account, password)) {
			User user = accountMgr.getUser(account);
			if (user != null && user.getId() > 0) {
				session.setAttribute("KEY_ACCOUNT", user.getAccount());
				session.setAttribute("KEY_USER_ID", user.getAccount());
				session.setAttribute("KEY_NAME", user.getAccount());
				Set<Role> roleList = new HashSet<Role>();
				for (Role role : user.getRoleList()) {
					Role copied = new Role();
                    copied.setId(role.getId());
                    copied.setName(role.getName());
                    roleList.add(copied);
				}
				session.setAttribute("KEY_ROLE_LIST", roleList);
			} else {
				return error;
			}
//			return gson.toJson(session);
			return success;
		} else {
			return error;
		}
	}
}
