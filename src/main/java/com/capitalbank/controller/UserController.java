package com.capitalbank.controller;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

import com.capitalbank.dbconfig.DBConnection;

public class UserController extends SelectorComposer<Window> {
	private static final long serialVersionUID = 5244872340242808060L;
	
	@Wire
	private Button submit;
	
	@Listen("onClick=#submit")
	public void onClickBtn() {
		
		alert("Connection: " + DBConnection.getConnection());
	}
}
