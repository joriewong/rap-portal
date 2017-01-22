package com.rap.portal.mock;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.taobao.rigel.rap.common.utils.StringUtils;
import com.taobao.rigel.rap.common.utils.SystemVisitorLog;
import com.taobao.rigel.rap.mock.service.MockMgr;
import com.taobao.rigel.rap.project.bo.Action;
import com.taobao.rigel.rap.project.bo.Module;
import com.taobao.rigel.rap.project.bo.Page;
import com.taobao.rigel.rap.project.bo.Project;
import com.taobao.rigel.rap.project.service.ProjectMgr;

@Path("/mock")
@Produces("application/json;charset=UTF-8")
public class MockPortal {
	
	private ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"classpath*:hibernate.cfg.xml"});
	private MockMgr mockMgr = context.getBean("mockMgr",MockMgr.class);
	private AccountMgr accountMgr = context.getBean("accountMgr",AccountMgr.class);
	private ProjectMgr projectMgr = context.getBean("projectMgr",ProjectMgr.class);
	private Gson gson = new Gson();
	private String success = "{\"msg\":\"success\"}";
	/**
	 * 创建mock数据
	 * @param curUserId
	 * @param pattern
	 * @param projectId
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@Path("/create")
	@POST
	public String createData(@QueryParam("curuserid") int curUserId,
			@QueryParam("pattern") String pattern,
			@QueryParam("projectid") int projectId,
			@QueryParam("callback") String cb,
			@QueryParam("_c" ) String c) throws UnsupportedEncodingException {
		User curUser = accountMgr.getUser(curUserId);
        String callback = StringUtils.escapeInHJ(cb);
        String content = "";
        boolean isJSON = false;
        updateProjectListMockNum(SystemVisitorLog.mock(projectId, "createData", pattern, curUser.getAccount()));
        Map<String, Object> options = new HashMap<String, Object>();
        String _c = StringUtils.escapeInHJ(c);
        String result = mockMgr.generateData(projectId, pattern, options);
        if (options.get("callback") != null) {
            _c = (String) options.get("callback");
            callback = (String) options.get("callback");
        }
        if (callback != null && !callback.isEmpty()) {
            content = callback + "(" + result + ")";
        } else if (_c != null && !_c.isEmpty()) {
            content = _c + "(" + result + ")";
        } else {
            isJSON = true;
            content = result;
        }
        if (isJSON) {
            return content;
        } else {
            return result;
        }
    }
	/**
	 * updateProjectListMockNum
	 * @param list
	 */
	private void updateProjectListMockNum(List<Project> list) {
        for (Project p : list) {
            Project project = projectMgr.getProject(p.getId());
            if (project == null) continue;
            project.setMockNum(p.getMockNum() + project.getMockNum());
            projectMgr.updateProjectNum(project);
        }
    }
	/**
	 * 修改mock数据
	 * @param actionId
	 * @param mockData
	 * @return
	 */
	@Path("/modify")
	@PUT
	public String modify(@QueryParam("actionid") int actionId,
			@QueryParam("mackdata") String mockData) {
		mockMgr.modify(actionId, mockData);
		return success;
	}
	/**
	 * 删除mock数据
	 * @param projectId
	 * @return
	 */
	@Path("/reset")
	@PUT
	public String reset(@QueryParam("projectid") int projectId) {
		mockMgr.reset(projectId);
		return success;
	}
	/**
	 * 查询mock数据
	 * @param actionData
	 * @return
	 */
	@Path("/query")
	@GET
	public String queryMockData(@QueryParam("actiondata") String actionData) {
		Action action = gson.fromJson(actionData, Action.class);
		return mockMgr.getMockRuleFromActionAndRule(null, action);
	}
	/**
	 * 创建规则
	 * @param cb
	 * @param projectId
	 * @param pattern
	 * @param method
	 * @param c
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@Path("/rule/create")
	@POST
	public String createRule(@QueryParam("curuserid") int curUserId,
			@QueryParam("callback") String cb,
			@QueryParam("projectid") int projectId,
			@QueryParam("pattern") String pattern,
			@QueryParam("method") String method,@QueryParam("_c") String c) throws UnsupportedEncodingException {
		User curUser = accountMgr.getUser(curUserId);
		String callback = cb;
		String content = "";
        boolean isJSON = false;
        updateProjectListMockNum(SystemVisitorLog.mock(projectId, "createRule", pattern, curUser.getAccount()));
        Map<String, Object> options = new HashMap<String, Object>();
        String _c = c;
        options.put("method", method);
        String result = mockMgr.generateRule(projectId, pattern, options);
        if (options.get("callback") != null) {
            _c = (String) options.get("callback");
            callback = (String) options.get("callback");
        }
        if (callback != null && !callback.isEmpty()) {
            content = callback + "(" + result + ")";
        } else if (_c != null && !_c.isEmpty()) {
        	content = _c + "(" + result + ")";
        } else {
            isJSON = true;
            content = result;
        }
        if (isJSON) {
            return content;
        } else {
            return result;
        }
	}
	/**
	 * 自动创建规则
	 * @param curUserId
	 * @param cb
	 * @param projectId
	 * @param pattern
	 * @param method
	 * @param c
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@Path("/rule/autocreate")
	@POST
	public String createRuleAuto(@QueryParam("curuserid") int curUserId,
			@QueryParam("callback") String cb,
			@QueryParam("projectid") int projectId,
			@QueryParam("pattern") String pattern,
			@QueryParam("method") String method,
			@QueryParam("_c") String c) throws UnsupportedEncodingException {
		User curUser = accountMgr.getUser(curUserId);
		String callback = cb;
		String content = "";
        boolean isJSON = false;
        updateProjectListMockNum(SystemVisitorLog.mock(projectId, "createRule", pattern, curUser.getAccount()));
        Map<String, Object> options = new HashMap<String, Object>();
        String _c = c;
        options.put("method", method);
        options.put("loadRule", true);
        
        String result = mockMgr.generateRule(projectId, pattern, options);
        if (options.get("callback") != null) {
            _c = (String) options.get("callback");
            callback = (String) options.get("callback");
        }
        if (callback != null && !callback.isEmpty()) {
            content = callback + "(" + result + ")";
        } else if (_c != null && !_c.isEmpty()) {
        	content = _c + "(" + result + ")";
        } else {
            isJSON = true;
            content = result;
        }
        if (isJSON) {
            return content;
        } else {
            return result;
        }
	}
	/**
	 * 通过请求创建规则
	 * @param curUserId
	 * @param cb
	 * @param projectId
	 * @param pattern
	 * @param c
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@Path("/rule/createbyaction")
	@POST
	public String createRuleByAction(@QueryParam("curuserid") int curUserId,
			@QueryParam("callback") String cb,
			@QueryParam("projectid") int projectId,
			@QueryParam("pattern") String pattern,
			@QueryParam("_c") String c) throws UnsupportedEncodingException {
		User curUser = accountMgr.getUser(curUserId);
		String callback = cb;
		String content = "";
        boolean isJSON = false;
        updateProjectListMockNum(SystemVisitorLog.mock(projectId, "createRuleByActionData", pattern, curUser.getAccount()));
        Map<String, Object> options = new HashMap<String, Object>();
        String _c = c;
        String result = mockMgr.generateRule(projectId, pattern, options);
        if (options.get("callback") != null) {
            _c = (String) options.get("callback");
            callback = (String) options.get("callback");
        }
        if (callback != null && !callback.isEmpty()) {
            content = callback + "(" + result + ")";
        } else if (_c != null && !_c.isEmpty()) {
        	content = _c + "(" + result + ")";
        } else {
            isJSON = true;
            content = result;
        }
        if (isJSON) {
            return content;
        } else {
            return result;
        }
	}
	/**
	 * 创建mockjs数据
	 * @param curUserId
	 * @param cb
	 * @param projectId
	 * @param pattern
	 * @param method
	 * @param c
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@Path("/mockjs/create")
	@POST
	public String createMockjsData(@QueryParam("curuserid") int curUserId,
			@QueryParam("callback") String cb,
			@QueryParam("projectid") int projectId,
			@QueryParam("pattern") String pattern,
			@QueryParam("method") String method,
			@QueryParam("_c") String c) throws UnsupportedEncodingException {
		User curUser = accountMgr.getUser(curUserId);
		String callback = cb;
		String content = "";
        boolean isJSON = false;
        updateProjectListMockNum(SystemVisitorLog.mock(projectId, "createRule", pattern, curUser.getAccount()));
        Map<String, Object> options = new HashMap<String, Object>();
        String _c = c;
        options.put("method", method);
        String result = mockMgr.generateRule(projectId, pattern, options);
        if (options.get("callback") != null) {
            _c = (String) options.get("callback");
            callback = (String) options.get("callback");
        }
        if (callback != null && !callback.isEmpty()) {
            content = callback + "(" + result + ")";
        } else if (_c != null && !_c.isEmpty()) {
        	content = _c + "(" + result + ")";
        } else {
            isJSON = true;
            content = result;
        }
        if (isJSON) {
            return content;
        } else {
            return result;
        }
	}
	/**
	 * 自动创建mockjs数据
	 * @param curUserId
	 * @param cb
	 * @param projectId
	 * @param pattern
	 * @param method
	 * @param c
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@Path("/mockjs/autocreate")
	@POST
	public String createMockjsDataAuto(@QueryParam("curuserid") int curUserId,
			@QueryParam("callback") String cb,
			@QueryParam("projectid") int projectId,
			@QueryParam("pattern") String pattern,
			@QueryParam("method") String method,
			@QueryParam("_c") String c) throws UnsupportedEncodingException {
		User curUser = accountMgr.getUser(curUserId);
		String callback = cb;
		String content = "";
        boolean isJSON = false;
        updateProjectListMockNum(SystemVisitorLog.mock(projectId, "createRule", pattern, curUser.getAccount()));
        Map<String, Object> options = new HashMap<String, Object>();
        String _c = c;
        options.put("method", method);
        options.put("loadRule", true);
        String result = mockMgr.generateRule(projectId, pattern, options);
        if (options.get("callback") != null) {
            _c = (String) options.get("callback");
            callback = (String) options.get("callback");
        }
        if (callback != null && !callback.isEmpty()) {
            content = callback + "(" + result + ")";
        } else if (_c != null && !_c.isEmpty()) {
        	content = _c + "(" + result + ")";
        } else {
            isJSON = true;
            content = result;
        }
        if (isJSON) {
            return content;
        } else {
            return result;
        }
	}
	/**
	 * 请求url列表
	 * @param projectId
	 * @return
	 */
	@Path("/urllist")
	@GET
	public String getWhiteList(@QueryParam("projectid") int projectId){
		Map<String, Boolean> _circleRefProtector = new HashMap<String, Boolean>();
        List<String> list = new ArrayList<String>();
        Project p = projectMgr.getProject(projectId);
        
        loadWhiteList(p, list, _circleRefProtector);
        return gson.toJson(list);
	}
	/**
	 * 项目请求url
	 * @param p
	 * @param list
	 * @param map
	 */
	private void loadWhiteList(Project p, List<String> list, Map<String, Boolean> map) {
		if (p == null || map.get(p.getId() + "") != null) {
			return;
		} else {
			map.put(p.getId() + "", true);
		}
		if (p != null) {
			for (Module m : p.getModuleList()) {
				for (Page page : m.getPageList()) {
					for (Action a : page.getActionList()) {
						list.add(a.getRequestUrlRel());
					}
				}
			}
		}
		String relatedIds = p.getRelatedIds();
		if (relatedIds != null && !relatedIds.isEmpty()) {
			String[] relatedIdsArr = relatedIds.split(",");
			for (String relatedId : relatedIdsArr) {
				int rId = Integer.parseInt(relatedId);
				Project rP = projectMgr.getProject(rId);
				if (rP != null && rP.getId() > 0)
					loadWhiteList(rP, list, map);
			}
		}
	}
}