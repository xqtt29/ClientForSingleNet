package com.chinacreator.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.chinacreator.common.Global;
import com.chinacreator.entity.ReceiveTableModel;
import com.chinacreator.service.DataOprService;
import com.chinacreator.service.SocketService;
import com.chinacreator.service.ThreadWorking;

/**
 * @Description
 * 下载文件按钮动作类
 * 
 * @Author qiang.zhu
 * @Datetime 2016年5月10日 上午10:16:29
 * @Version
 * @Copyright (c) 2013 湖南科创信息技术股份有限公司
 */
public class ReceiveFileAction  implements ActionListener{

	private JFrame frame;
	
	private JTextField ip;

	private JTextField port;
	
	private JTextField savePath;
	
	private DefaultTableModel model;
	
	public ReceiveFileAction(JFrame frame,JTextField ip,JTextField port,JTextField savePath,DefaultTableModel model){
		this.frame=frame;
		this.ip=ip;
		this.port=port;
		this.savePath=savePath;
		this.model=model;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(!DataOprService.getInstance().checkIpAndPort(frame, ip.getText(), port.getText())){
			return;
		}
		if(savePath.getText().length()==0){
			JOptionPane.showMessageDialog(frame, "保存路径为空！", "警告",JOptionPane.WARNING_MESSAGE);
			return;
		}
		int totalFile=model.getRowCount();
		for(int i=0;i<totalFile;i++){
			DataOprService.getInstance().freshData(model, i,Global.receiveTabFresh, "");
		}
		SocketService service=new SocketService(frame,ip.getText(),Integer.parseInt(port.getText()));
		LinkedBlockingQueue<Map<String,Object>> queue= new LinkedBlockingQueue<Map<String,Object>>(1024);
		List<ReceiveTableModel> list=DataOprService.getInstance().bindReceiveData(model);
		for(int i=0;i<totalFile;i++){
			ReceiveTableModel rtm = list.get(i);
			if(!rtm.getFileBox()){
				continue;
			}
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("runType","receive");
			map.put("fileNo",rtm.getFileNo());
			map.put("filePath",rtm.getFilePath());
			map.put("fileName",rtm.getFileName());
			map.put("fileType",rtm.getFileType());
			map.put("savePath", savePath.getText());
			queue.add(map);
		}
		if(queue.size()==0){
			JOptionPane.showMessageDialog(frame, "请选择要下载的文件！", "警告",JOptionPane.WARNING_MESSAGE);
			return;
		}
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("FLAG","END");
		queue.add(map);
		for(int i=0;i<Global.receiveFileThreadCounts;i++){
			ThreadWorking process = new ThreadWorking(service,model,queue);
			Thread t = new Thread(process);
			t.start();
		}
	}

}
