package com.rap.portal.organization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.taobao.rigel.rap.organization.bo.Group;
import com.taobao.rigel.rap.organization.service.OrganizationMgr;

@Path("/group")
@Produces("application/json;charset=UTF-8")
public class GroupPortal {
	
	private ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"classpath*:hibernate.cfg.xml"});
	private OrganizationMgr organizationMgr = context.getBean("organizationMgr",OrganizationMgr.class);
	private Gson gson = new Gson();
	private String error = "{\"msg\":\"error\"}";
	private String success = "{\"msg\":\"success\"}";
	/**
	 * 分组列表
	 * @param curUserId
	 * @param productlineId
	 * @return
	 */
	@Path("/groups")
	@GET
	public String getGroups(@QueryParam("curuserid") int curUserId,
			@QueryParam("productlineid") int productlineId) {
		if (!organizationMgr.canUserAccessProductionLine(curUserId, productlineId)) {
            return error;
        }
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
		List<Group> groupModels = organizationMgr.getGroupList(productlineId);
		for (Group groupModel : groupModels) {
			Map<String, Object> group = new HashMap<String, Object>();
			group.put("id", groupModel.getId());
			group.put("name", groupModel.getName());
			groups.add(group);
		}
		
		result.put("groups", groups);
		return gson.toJson(result);
	}
	/**
	 * 创建分组
	 * @param curUserId
	 * @param productlineId
	 * @param name
	 * @return
	 */
	@Path("/create")
	@POST
	public String createGroup(@QueryParam("curuserid") int curUserId,
			@QueryParam("productlineid") int productlineId,
			@QueryParam("name") String name) {
		if (!organizationMgr.canUserManageProductionLine(curUserId, productlineId)) {
			return error;
		}
		Group group = new Group();
		group.setName(name);
		group.setUserId(curUserId);
		group.setProductionLineId(productlineId);
		int id = organizationMgr.addGroup(group);
		Map<String, Object> g = new HashMap<String, Object>();
		g.put("id", id);
		g.put("name", name);
		return "{\"groups\":[" + gson.toJson(g) + "]}";
	}
	/**
	 * 删除分组
	 * @param curUserId
	 * @param id
	 * @return
	 */
	@Path("/delete")
	@DELETE
	public String deleteGroup(@QueryParam("curuserid") int curUserId,
			@QueryParam("id") int id) {
		if (!organizationMgr.canUserManageGroup(curUserId, id)) {
			return error;
		}
		return organizationMgr.removeGroup(id).toString();
	}
	/**
	 * 修改分组
	 * @param curUserId
	 * @param id
	 * @param name
	 * @return
	 */
	@Path("/update")
	@PUT
	public String updateGroup(@QueryParam("curuserid") int curUserId,
			@QueryParam("id") int id,
			@QueryParam("name") String name) {
		if (!organizationMgr.canUserManageGroup(curUserId, id)) {
            return "error";
        }
        Group group = new Group();
        group.setId(id);
        group.setName(name);
        organizationMgr.updateGroup(group);
        return success;
	}
}
