package com.rap.portal.organization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.gson.Gson;
import com.taobao.rigel.rap.account.bo.User;
import com.taobao.rigel.rap.account.service.AccountMgr;
import com.taobao.rigel.rap.project.bo.Project;
import com.taobao.rigel.rap.project.service.ProjectMgr;

@Path("/org")
@Produces("application/json;charset=UTF-8")
public class OrganizationPortal {
	
	private ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"classpath*:hibernate.cfg.xml"});
	private AccountMgr accountMgr = context.getBean("accountMgr",AccountMgr.class);
	private ProjectMgr projectMgr = context.getBean("projectMgr",ProjectMgr.class);
	private Gson gson = new Gson();
	/**
	 * 项目列表
	 * @param curUserId
	 * @return
	 */
	@Path("/projects")
	@GET
	public String getProjects(@QueryParam("curuserid") int curUserId){
		List<Map<String, Object>> projects = new ArrayList<Map<String, Object>>();
		User curUser = accountMgr.getUser(curUserId);
		List<Project> projectList = projectMgr.getProjectList(curUser, 1, Integer.MAX_VALUE);
		
		for (Project p : projectList) {
			Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", p.getId());
            map.put("name", p.getName());
            map.put("desc", p.getIntroduction());
            map.put("status", p.getLastUpdateStr());
            map.put("accounts", p.getMemberAccountListStr());
            map.put("isManagable", p.isManagable());
            map.put("isDeletable", p.isDeletable());
            map.put("creator", p.getUser().getUserBaseInfo());
            map.put("related", p.getUser().getId() != curUserId);
            map.put("teamId", p.getTeamId());
            projects.add(map);
		}
		StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("	\"groups\" : [{");
        json.append("		\"type\" : \"user\",");
        json.append("		\"projects\" :");
        json.append(gson.toJson(projects));
        json.append("	}]");
        json.append("}");
		return json.toString();
	}
}
