package com.rap.portal.organization;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.gson.Gson;
import com.taobao.rigel.rap.account.bo.User;
import com.taobao.rigel.rap.account.service.AccountMgr;
import com.taobao.rigel.rap.organization.bo.Corporation;
import com.taobao.rigel.rap.organization.service.OrganizationMgr;

@Path("/team")
@Produces("application/json;charset=UTF-8")
public class TeamPortal {
	
	private ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"classpath*:hibernate.cfg.xml"});
	private OrganizationMgr organizationMgr = context.getBean("organizationMgr", OrganizationMgr.class);
    private AccountMgr accountMgr = context.getBean("accountMgr",AccountMgr.class);
	private Gson gson = new Gson();
	private String error = "{\"msg\":\"error\"}";
	private String success = "{\"msg\":\"success\"}";
	/**
	 * �Ŷ��б�
	 * @return
	 */
	@Path("/teams")
	@GET
	public String getTeams(){
		return gson.toJson(organizationMgr.getCorporationList());
	}
	/**
	 * �����Ŷ�
	 * @param name
	 * @param userid
	 * @param desc
	 * @param accountList
	 * @param accessType
	 * @param logourl
	 * @return
	 */
	@Path("/create")
	@POST
	public String createTeam(@QueryParam("name") String name,
			@QueryParam("userid") int userid,
			@QueryParam("desc") String desc,
			@QueryParam("accountlist") String accountList,
			@QueryParam("accesstype") short accessType,
			@QueryParam("logourl") String logourl) {
		Corporation team = new Corporation();
        team.setName(name);
        team.setUserId(userid);
        //��Ա�б�
        team.setAccountList(Arrays.asList(accountList));
        team.setDesc(desc);
        //10,20
        team.setAccessType(accessType);
        team.setLogoUrl("");
        int id = organizationMgr.addTeam(team);
		return "{\"id\":" + id + "}";
	}
	/**
	 * �޸��Ŷ�
	 * @param id
	 * @param name
	 * @param desc
	 * @param accessType
	 * @return
	 */
	@Path("/update")
	@PUT
	public String updateGroup(@Context HttpServletRequest request,
			@QueryParam("id") int id,
			@QueryParam("name") String name,
			@QueryParam("desc") String desc,
			@QueryParam("accesstype") short accessType) {
		HttpSession session = request.getSession();
		int curUserId = (int) session.getAttribute("KEY_USER_ID");
        User curUser = accountMgr.getUser(curUserId);
        if (curUser == null) {
            return error;
        }

        if (!organizationMgr.canUserManageCorp(curUser.getId(), id)) {
            return error;
        }

        Corporation c = new Corporation();
        c.setId(id);
        c.setName(name);
        c.setDesc(desc);
        c.setAccessType(accessType);
        organizationMgr.updateCorporation(c);
        
        return success;
	} 
	/**
	 * ��ӳ�Ա
	 * @param userId
	 * @param corpId
	 * @param roleId
	 * @return
	 */
	@Path("/addmembers")
	@POST
	public String addMembers(@Context HttpServletRequest request,
			@QueryParam("userid") int userId,
			@QueryParam("corpid") int corpId,
			@QueryParam("roleid") int roleId) {
		HttpSession session = request.getSession();
		int curUserId = (int) session.getAttribute("KEY_USER_ID");
		User curUser = accountMgr.getUser(curUserId);
		if (curUser == null) {
			return error;
		}
		if (organizationMgr.setUserRoleInCorp(curUserId, userId, corpId, roleId)) {
			return success;
		}else{
			return error;
		}
	}
	/**
	 * ɾ����Ա
	 * @param userId
	 * @param corpId
	 * @param roleId
	 * @return
	 */
	@Path("/deletemember")
	@DELETE
	public String deletemember(@Context HttpServletRequest request,
			@QueryParam("curuserid") int userId,
			@QueryParam("curuserid") int corpId,
			@QueryParam("curuserid") int roleId) {
		HttpSession session = request.getSession();
		int curUserId = (int) session.getAttribute("KEY_USER_ID");
		User curUser = accountMgr.getUser(curUserId);
		if (curUser == null) {
			return error;
		}
		if (organizationMgr.removeMemberFromCorp(curUserId, userId, corpId)) {
			return success;
		} else {
			return error;
		}
	}
}
