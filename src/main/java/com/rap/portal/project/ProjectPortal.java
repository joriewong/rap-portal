package com.rap.portal.project;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DELETE;
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
import com.taobao.rigel.rap.organization.service.OrganizationMgr;
import com.taobao.rigel.rap.project.bo.Project;
import com.taobao.rigel.rap.project.service.ProjectMgr;

@Path("/project")
@Produces("application/json;charset=UTF-8")
public class ProjectPortal {
	
	private ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"classpath*:hibernate.cfg.xml"});
	private OrganizationMgr organizationMgr = context.getBean("organizationMgr",OrganizationMgr.class);
	private ProjectMgr projectMgr = context.getBean("projectMgr",ProjectMgr.class);
	private AccountMgr accountMgr = context.getBean("accountMgr",AccountMgr.class);
	private Gson gson = new Gson();
	private String error = "{\"msg\":\"error\"}";
	private String success = "{\"msg\":\"success\"}";
	/**
	 * 创建项目
	 * @param curUserId
	 * @param desc
	 * @param name
	 * @param groupId
	 * @param accountList
	 * @return
	 */
	@Path("/create")
	@POST
	public String createProject(@QueryParam("curuserid") int curUserId,
			@QueryParam("desc") String desc,
			@QueryParam("name") String name,
			@QueryParam("groupid") int groupId,
			@QueryParam("accountlist") String accountList) {
		Project project = new Project();
		project.setCreateDate(new Date());
		User curUser = accountMgr.getUser(curUserId);
	    project.setUser(curUser);
	    project.setUserId(curUserId);
	    project.setIntroduction(desc);
	    project.setName(name);
	    project.setGroupId(groupId);
	    List<String> memberAccountList = new ArrayList<String>();
	    String[] list = accountList.split(",");
	    for (String item : list) {
	    	String account = item.contains("(") ? item.substring(0, item.indexOf("(")).trim() : item.trim();
	    	if (!account.equals("")) {
	    		memberAccountList.add(account);
	    	}
	    }
	    project.setMemberAccountList(memberAccountList);
	    int projectId = projectMgr.addProject(project);
	    project = projectMgr.getProject(projectId);
	    for (String account : memberAccountList) {
	    	organizationMgr.addTeamMembers(curUserId, organizationMgr.getTeamIdByProjectId(project.getId()), account);
	    }
	    Map<String, Object> result = new HashMap<String, Object>();
	    result.put("id", project.getId());
	    result.put("name", project.getName());
	    result.put("desc", project.getIntroduction());
	    result.put("accounts", project.getMemberAccountListStr());
	    result.put("groupId", project.getGroupId());
	    result.put("isManagable", "true");
	    result.put("creator", project.getUser().getUserBaseInfo());
	    return gson.toJson(result);
	}
	/**
	 * 删除项目
	 * @param curUserId
	 * @param id
	 * @return
	 */
	@Path("/delete")
	@DELETE
	public String deleteProject(@QueryParam("curuserid") int curUserId,
			@QueryParam("id") int id) {
		if (!organizationMgr.canUserDeleteProject(curUserId, id)) {
			return error;
        }
        projectMgr.removeProject(id);
		return success;
	}
	/**
	 * 修改项目
	 * @param curUserId
	 * @param id
	 * @param desc
	 * @param name
	 * @param accountList
	 * @return
	 */
	@Path("/update")
	@PUT
	public String updateProject(@QueryParam("curuserid") int curUserId,
			@QueryParam("id") int id,
			@QueryParam("desc") String desc,
			@QueryParam("name") String name,
			@QueryParam("accountlist") String accountList) {
		if (!organizationMgr.canUserManageProject(curUserId, id)) {
            return error;
        }
		Project project = new Project();
		User curUser = accountMgr.getUser(curUserId);
		project.setId(id);
        project.setIntroduction(desc);
        project.setName(name);
        project.setUser(curUser);
        List<String> memberAccountList = new ArrayList<String>();
        String[] list = accountList.split(",");
        for (String item : list) {
            String account = item.contains("(") ? item.substring(0,
                    item.indexOf("(")).trim() : item.trim();
            if (!account.equals("")) {
                memberAccountList.add(account);
                organizationMgr.addTeamMembers(curUserId, organizationMgr.getTeamIdByProjectId(project.getId()), account);
            }
        }
        project.setMemberAccountList(memberAccountList);
        projectMgr.updateProject(project);

        project = projectMgr.getProject(project.getId());

        if (curUser.isUserInRole("admin")
                || curUser.getId() == project.getUser().getId()) {
            project.setIsManagable(true);
        }

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("id", project.getId());
        result.put("name", project.getName());
        result.put("desc", project.getIntroduction());
        result.put("accounts", project.getMemberAccountListStr());
        result.put("groupId", project.getGroupId());
        result.put("isManagable", project.isManagable());
		return gson.toJson(result);
	}
}
