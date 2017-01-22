package com.rap.portal.organization;

import java.util.Arrays;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

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
	 * 团队列表
	 * @return
	 */
	@Path("/teams")
	@GET
	public String getTeams(){
		return gson.toJson(organizationMgr.getCorporationList());
	}
	/**
	 * 创建团队
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
        //成员列表
        team.setAccountList(Arrays.asList(accountList));
        team.setDesc(desc);
        //10,20
        team.setAccessType(accessType);
        team.setLogoUrl("");
        int id = organizationMgr.addTeam(team);
		return "{\"id\":" + id + "}";
	}
	/**
	 * 修改团队
	 * @param curUserId
	 * @param id
	 * @param name
	 * @param desc
	 * @param accessType
	 * @return
	 */
	@Path("/update")
	@PUT
	public String updateGroup(@QueryParam("curuserid") int curUserId,
			@QueryParam("id") int id,
			@QueryParam("name") String name,
			@QueryParam("desc") String desc,
			@QueryParam("accesstype") short accessType) {
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
	 * 添加成员
	 * @param curUserId
	 * @param userId
	 * @param corpId
	 * @param roleId
	 * @return
	 */
	@Path("/addmembers")
	@POST
	public String addMembers(@QueryParam("curuserid") int curUserId,
			@QueryParam("curuserid") int userId,
			@QueryParam("curuserid") int corpId,
			@QueryParam("curuserid") int roleId) {
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
	 * 删除成员
	 * @param curUserId
	 * @param userId
	 * @param corpId
	 * @param roleId
	 * @return
	 */
	@Path("/deletemember")
	@DELETE
	public String deletemember(@QueryParam("curuserid") int curUserId,
			@QueryParam("curuserid") int userId,
			@QueryParam("curuserid") int corpId,
			@QueryParam("curuserid") int roleId) {
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
