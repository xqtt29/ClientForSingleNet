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
import com.chinacreator.entity.SendTableModel;
import com.chinacreator.service.DataOprService;
import com.chinacreator.service.SocketService;
import com.chinacreator.service.ThreadWorking;

/**
 * @Description
 * 上传文件按钮动作类
 * 
 * @Author qiang.zhu
 * @Datetime 2016年5月10日 上午10:16:29
 * @Version
 * @Copyright (c) 2013 湖南科创信息技术股份有限公司
 */
public class SendFileFastAction implements ActionListener{

	private JFrame frame;
	
	private JTextField ip;
	
	private JTextField port;
	
	private DefaultTableModel model;
	
	public SendFileFastAction(JFrame frame,JTextField ip,JTextField port,DefaultTableModel model){
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
		
		int totalFile=model.getRowCount();
		for(int i=0;i<totalFile;i++){
			DataOprService.getInstance().clearData(model, i,Global.sendTabFresh, "");
		}
		
		SocketService service=new SocketService(frame,ip.getText(),Integer.parseInt(port.getText()));
		LinkedBlockingQueue<Map<String,Object>> queue= new LinkedBlockingQueue<Map<String,Object>>(1024);
		List<SendTableModel> list=DataOprService.getInstance().bindSendData(model);
		for(int i=0;i<totalFile;i++){
			SendTableModel stm=list.get(i);
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("runType","sendFast");
			map.put("fileNo",stm.getFileNo());
			map.put("filePath",stm.getFilePath());
			map.put("fileName",stm.getFileName());
			map.put("fileType",stm.getFileType());
			queue.add(map);
		}
		if(queue.size()==0){
			JOptionPane.showMessageDialog(frame, "请选择要上传的文件！", "警告",JOptionPane.WARNING_MESSAGE);
			return;
		}
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("FLAG","END");
		queue.add(map);
		for(int i=0;i<Global.sendFileThreadCounts;i++){
			ThreadWorking process = new ThreadWorking(service,model,queue);
			Thread t = new Thread(process);
			t.start();
		}
	}

}
