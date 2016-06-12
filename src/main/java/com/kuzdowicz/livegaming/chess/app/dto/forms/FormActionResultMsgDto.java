package com.kuzdowicz.livegaming.chess.app.dto.forms;

import com.kuzdowicz.livegaming.chess.app.constants.FormActionMessageType;

public class FormActionResultMsgDto {

	private FormActionMessageType type;
	private String body;

	public FormActionResultMsgDto() {
	}

	public FormActionResultMsgDto(FormActionMessageType type, String body) {
		this.type = type;
		this.body = body;
	}

	public FormActionMessageType getType() {
		return type;
	}

	public String getBody() {
		return body;
	}

	public void setType(FormActionMessageType type) {
		this.type = type;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public static FormActionResultMsgDto createSuccessMsg(String msg) {
		return new FormActionResultMsgDto(FormActionMessageType.SUCCESS, msg);
	}

	public static FormActionResultMsgDto createErrorMsg(String msg) {
		return new FormActionResultMsgDto(FormActionMessageType.ERROR, msg);
	}
	
	public static FormActionResultMsgDto empty() {
		return new FormActionResultMsgDto();
	}

}
