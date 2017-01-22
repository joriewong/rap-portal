package com.rap.portal.api;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.gson.Gson;
import com.taobao.rigel.rap.api.service.OpenAPIMgr;
import com.taobao.rigel.rap.mock.service.MockMgr;
import com.taobao.rigel.rap.project.bo.Action;
import com.taobao.rigel.rap.project.bo.Project;
import com.taobao.rigel.rap.project.service.ProjectMgr;

@Path("/openapi")
@Produces("application/json;charset=UTF-8")
public class OpenAPIPortal {
	
	private ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"classpath*:hibernate.cfg.xml"});
	private OpenAPIMgr openAPIMgr = context.getBean("openAPIMgr", OpenAPIMgr.class);
	private ProjectMgr projectMgr = context.getBean("projectMgr", ProjectMgr.class);
	private MockMgr mockMgr = context.getBean("mockMgr", MockMgr.class);
	private Gson g = new Gson();
	/**
	 * 获取某版本项目信息
	 * @param projectId
	 * @param version
	 * @param callback
	 * @param _c
	 * @return
	 * @throws Exception
	 */
	@Path("/querymodel")
	@GET
	public String queryModel(@QueryParam("projectid") int projectId,
			@QueryParam("version") String version,
			@QueryParam("callback") String callback,
			@QueryParam("_c") String _c) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
        
        resultMap.put("model", openAPIMgr.getModel(projectId, version));
        resultMap.put("code", 200);
        resultMap.put("msg", "");
        String resultJson = g.toJson(resultMap);

        // JSONP SUPPORTED
        if (callback != null && !callback.isEmpty()) {
            resultJson = (callback + "(" + resultJson + ")");
        } else if (_c != null && !_c.isEmpty()) {
            resultJson = (_c + "(" + resultJson + ")");
        }
        
        return resultJson;
	}
	/**
	 * 获取某版本项目某个接口详情
	 * @param actionId
	 * @param type
	 * @param projectId
	 * @param version
	 * @param callback
	 * @param _c
	 * @return
	 */
	@Path("/queryschema")
	@GET
	public String querySchema(@QueryParam("actionid") int actionId,
			@QueryParam("type") String type,
			@QueryParam("projectid") int projectId,
			@QueryParam("version") String version,
			@QueryParam("callback") String callback,
			@QueryParam("_c") String _c) {
		 Map<String, Object> resultMap = new HashMap<String, Object>();
		 resultMap.put("schema", openAPIMgr.getSchema(actionId, (type != null && type.equals("request") ? Action.TYPE.REQUEST : Action.TYPE.RESPONSE), version, projectId));
		 resultMap.put("code", 200);
	     resultMap.put("msg", "");
	     String resultJson = g.toJson(resultMap);
	     
	     // JSONP SUPPORTED
	     if (callback != null && !callback.isEmpty()) {
	    	 resultJson = (callback + "(" + resultJson + ")");
	     } else if (_c != null && !_c.isEmpty()) {
	    	 resultJson = (_c + "(" + resultJson + ")");
	     }
	     
	     return resultJson;
	}
	/**
	 * 获取某项目的所有接口详情和mock数据
	 * @param projectId
	 * @param callback
	 * @param _c
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@Path("/queryrapmodel")
	@GET
	public String queryRAPModel(@QueryParam("projectid") int projectId,
			@QueryParam("callback") String callback,
			@QueryParam("_c") String _c) throws UnsupportedEncodingException {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Project p = projectMgr.getProject(projectId);
        List<Action> aList = p.getAllAction();
        Map<Integer, Object> mockDataMap = new HashMap<Integer, Object>();

        for (Action a : aList) {
            mockDataMap.put(a.getId(), mockMgr.generateRule(a.getId(), null, null));
        }

        resultMap.put("modelJSON", p.toString(Project.TO_STRING_TYPE.TO_PARAMETER));
        resultMap.put("mockjsMap", mockDataMap);
        resultMap.put("code", 200);
        resultMap.put("msg", 0);
        String resultJson = g.toJson(resultMap);

        // JSONP SUPPORTED
        if (callback != null && !callback.isEmpty()) {
            resultJson = (callback + "(" + resultJson + ")");
        } else if (_c != null && !_c.isEmpty()) {
            resultJson = (_c + "(" + resultJson + ")");
        }
        
        return resultJson;
	}
	/**
	 * 修改mock规则
	 * @param rules
	 * @param actionId
	 * @return
	 */
	@Path("/modifymockrules")
	@PUT
	public String modifyMockRules(@QueryParam("rules")  String rules,
			@QueryParam("actionid") int actionId) {
		return openAPIMgr.modifyMockRules(rules, actionId);
	}
	/**
	 * 删除mock规则
	 * @param actionId
	 * @return
	 */
	@Path("/resetmockrules")
	@PUT
	public String resetMockRules(@QueryParam("actionid") int actionId) {
		return openAPIMgr.resetMockRules(actionId);
	}
}
