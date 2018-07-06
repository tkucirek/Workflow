package org.camunda.bpm.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.impl.util.json.JSONObject;
import org.camunda.bpm.engine.runtime.ProcessInstance;

public class BeginContractServlet extends HttpServlet {
	

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		PrintWriter out = response.getWriter();

		Map<String, Object> map = new HashMap<String, Object>();

		JSONObject jsonObj = new JSONObject(request.getParameter("jsonObj"));

		String name = (String) jsonObj.get("name");
		

		map.put("name", name);
		
		System.out.println(map.get(name));
		
		out.close();
		ProcessInstance processInstance;
		

		processInstance = runtimeService.startProcessInstanceByMessage("instantiationMessageContract", map);
	}

}