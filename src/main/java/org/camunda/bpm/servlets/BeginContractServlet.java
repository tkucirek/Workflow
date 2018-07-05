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

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		PrintWriter out = response.getWriter();

		Map<String, Object> map = new HashMap<String, Object>();

		JSONObject jsonObj = new JSONObject(request.getParameter("jsonObj"));

		String CustomerType = (String) jsonObj.get("customertype");
		String phone = (String) jsonObj.get("phone");
		String bvisProcessId = (String) jsonObj.get("process_id");
		String endDate = (String) jsonObj.get("enddate");
		String startDate = (String) jsonObj.get("startdate");
		String carType = (String) jsonObj.get("vehicleclass");
		String contract = (String) jsonObj.get("insurance");

		map.put("CustomerType", CustomerType);
		map.put("phone", phone);
		map.put("bvisProcessId", bvisProcessId);
		map.put("endDate", endDate);
		map.put("startDate", startDate);
		map.put("carType", carType);
		map.put("contract", contract);

		if (CustomerType.equals("private")) {

			String firstName = (String) jsonObj.get("firstname");
			String lastName = (String) jsonObj.get("lastname");
			String birthdate = (String) jsonObj.get("birthdate");
			String driverLicenseId = (String) jsonObj.get("licenceid");
			String driverLicenseDate = (String) jsonObj.get("licenceexp");

			map.put("firstName", firstName);
			map.put("lastName", lastName);
			map.put("birthdate", birthdate);
			map.put("driverLicenseId", driverLicenseId);
			map.put("driverLicenseDate", driverLicenseDate);

		} else {
			String name = (String) jsonObj.get("companyname");
			map.put("name", name);
		}
		out.close();
		ProcessInstance processInstance;
		System.out.println("bvisProcessId in CreateContractServlet: " + bvisProcessId);

		processInstance = runtimeService.startProcessInstanceByMessage("instantiationMessageContract", map);
	}

}