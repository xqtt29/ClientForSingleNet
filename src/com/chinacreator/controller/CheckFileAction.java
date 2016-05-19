package com.chinacreator.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.chinacreator.service.DataOprService;
import com.chinacreator.service.SocketService;
import com.chinacreator.service.ThreadWorking;

/**
 * @Description
 * 查询文件按钮动作类
 * 
 * @Author qiang.zhu
 * @Datetime 2016年5月10日 上午10:16:29
 * @Version
 * @Copyright (c) 2013 湖南科创信息技术股份有限公司
 */
public class CheckFileAction  implements ActionListener{

	private JFrame frame;
	
	private JTextField ip;
	
	private JTextField port;
	
	private DefaultTableModel model;
	
	public CheckFileAction(JFrame frame,JTextField ip,JTextField port,DefaultTableModel model){
		this.frame=frame;
		this.ip=ip;
		this.port=port;
		this.model=model;
	}
		
	@Override
	public void actionPerformed(ActionEvent e) {
		if(!DataOprService.getInstance().checkIpAndPort(frame, ip.getText(), port.getText())){
			return;
		}
		
		model.setRowCount(0);
		SocketService service=new SocketService(frame,ip.getText(),Integer.parseInt(port.getText()));
		LinkedBlockingQueue<Map<String,Object>> queue= new LinkedBlockingQueue<Map<String,Object>>(1024);
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("runType","check");
		queue.add(map);

		Map<String,Object> mapEnd=new HashMap<String,Object>();
		mapEnd.put("FLAG","END");
		queue.add(mapEnd);
		ThreadWorking process = new ThreadWorking(service,model,queue);
		Thread t = new Thread(process);
		t.start();
	}

}
