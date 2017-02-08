package com.rap.portal.workspace;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taobao.rigel.rap.organization.service.OrganizationMgr;
import com.taobao.rigel.rap.project.bo.Project;
import com.taobao.rigel.rap.project.service.ProjectMgr;
import com.taobao.rigel.rap.workspace.bo.Workspace;

@Path("/workspace")
@Produces("application/json;charset=UTF-8")
public class WorkspacePortal {
	
	private ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"classpath*:hibernate.cfg.xml"});
	private ProjectMgr projectMgr = context.getBean("projectMgr",ProjectMgr.class);
	private OrganizationMgr organizationMgr = context.getBean("organizationMgr",OrganizationMgr.class);
	/**
	 * 加载工作空间
	 * @param projectId
	 * @return
	 */
	@Path("/load")
	@GET
	public String loadWorkspace(@Context HttpServletRequest request,
			@QueryParam("projectid") int projectId) {
		HttpSession session = request.getSession();
		int curUserId = (int) session.getAttribute("KEY_USER_ID");
		Project p = projectMgr.getProject(projectId);

        if (p == null || p.getId() <= 0) {
            return "{\"msg\":\"该项目不存在或已被删除，会不会是亲这个链接保存的太久了呢？0  .0\"}";
        }

        if (!organizationMgr.canUserAccessProject(curUserId, projectId)) {
            return "{\"msg\":\"error\"}";
        }

        Workspace workspace = new Workspace();
        workspace.setProject(p);

        return workspace.toString();
	}
}
