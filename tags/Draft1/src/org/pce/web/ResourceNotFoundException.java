package org.pce.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException{
private String resourceId;
	
	public ResourceNotFoundException(String resourceId) {
		this.resourceId = resourceId;
	}
	
	public String getResourceId() {
		return resourceId;
	}
}
