/*
 * Copyright CIB software GmbH and/or licensed to CIB software GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. CIB software licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.cibseven.webapp.rest;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import org.cibseven.webapp.auth.CIBUser;
import org.cibseven.webapp.auth.SevenResourceType;
import org.cibseven.webapp.providers.PermissionConstants;
import org.cibseven.webapp.rest.model.TaskHistory;
import org.cibseven.webapp.rest.model.VariableHistory;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@ApiResponses({
	@ApiResponse(responseCode= "500", description = "An unexpected system error occured"),
	@ApiResponse(responseCode= "401", description = "Unauthorized")
})
@RestController @RequestMapping("${cibseven.webclient.services.basePath:/services/v1}")
public class HistoryTaskService extends BaseService {
	
	@Operation(
			summary = "Get variables from a specific activity",
			description = "The variables found belongs to the history, they have other attributes and variables from finished activities are also fetched" + "<br>"
			+ "<strong>Return: Collection of fetched variables")
	@ApiResponse(responseCode= "404", description = "Activity instance not found")
	@GetMapping("/task-history/{activityInstanceId}/variables")
	public Collection<VariableHistory> fetchActivityVariablesHistory(
			@Parameter(description = "Activity instance Id") @PathVariable String activityInstanceId,
			Locale loc, CIBUser user) {
        checkPermission(user, SevenResourceType.HISTORIC_TASK, PermissionConstants.READ_ALL);
		return bpmProvider.fetchActivityVariablesHistory(activityInstanceId, user);
	}
	
	@Operation(
			summary = "Get tasks which belongs to a specific process instance and filtered by a definition key",
			description = "The tasks found belongs to the history, they have other attributes and finished tasks are also fetched" + "<br>"
			+ "<strong>Return: Collection of fetched tasks")
	@ApiResponse(responseCode= "404", description = "Task/s not found")
	@GetMapping("/task-history/by-process-key")
	public Collection<TaskHistory> findTasksByDefinitionKeyHistory(
			@Parameter(description = "Restrict to tasks that have the given key") @RequestParam String taskDefinitionKey,
			@Parameter(description = "Process instance Id") @RequestParam String processInstanceId,
			Locale loc, CIBUser user) {
		checkPermission(user, SevenResourceType.TASK, PermissionConstants.READ_ALL);
		return bpmProvider.findTasksByDefinitionKeyHistory(taskDefinitionKey, processInstanceId, user);
	}

	@Operation(
			summary = "Get tasks which belongs to a specific process instance",
			description = "The tasks found belongs to the history, they have other attributes and finished tasks are also fetched" + "<br>"
			+ "<strong>Return: Collection of fetched tasks")
	@ApiResponse(responseCode= "404", description = "Task/s not found")
	@GetMapping("/task-history/by-process-instance/{processInstanceId}")
	public Collection<TaskHistory> findTasksByProcessInstanceHistory(
			@Parameter(description = "Process instance Id") @PathVariable String processInstanceId,
			Locale loc, CIBUser user) {
		checkPermission(user, SevenResourceType.TASK, PermissionConstants.READ_ALL);
		return bpmProvider.findTasksByProcessInstanceHistory(processInstanceId, user);
	}
	
	@Operation(
			summary = "Get tasks which belongs to a specific task id",
			description = "The tasks found belongs to the history, they have other attributes and finished tasks are also fetched" + "<br>"
			+ "<strong>Return: Collection of fetched tasks")
	@ApiResponse(responseCode= "404", description = "Task/s not found")
	@GetMapping("/task-history/by-task-id/{taskId}")
	public Collection<TaskHistory> findTasksByTaskIdHistory(
			@Parameter(description = "Task Id") @PathVariable String taskId,
			Locale loc, CIBUser user) {
		checkPermission(user, SevenResourceType.TASK, PermissionConstants.READ_ALL);
		return bpmProvider.findTasksByTaskIdHistory(taskId, user);
	}
	
	@Operation(
			summary = "Get number of tasks based on filters")
	@PostMapping("/task-history/count")
	public Integer findHistoryTasksCount(
			@RequestBody Map<String, Object> filters,
			Locale loc, CIBUser user) {
		return bpmProvider.findHistoryTasksCount(filters, user);
	}

}
