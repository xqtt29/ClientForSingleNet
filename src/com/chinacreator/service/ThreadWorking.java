package com.chinacreator.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import javax.swing.table.DefaultTableModel;

import com.chinacreator.common.Global;

/**
 * @Description
 * 作业线程
 * step1:启动1个数据读取线程循环读取TD_PURCHASE_GETFLOW表中SEND_FLAG=0的目标客户size(变量)条压入队列中
 * step2:并将本次读取的数据更新目标客户表SEND_FLAG=1(目标客户表中的MOBILENUM主键，根据该值更新状态)
 * step3:启动10个作业线程处理队列中的数据,处理成功更新状态state=2处理失败更新状态state=3
 * @Author qiang.zhu
 * @Datetime 2015年11月12日 上午11:54:11
 * @Version
 * @Copyright (c) 2013 湖南科创信息技术股份有限公司

 */
public class ThreadWorking  implements Runnable {
	//待作业数据队列	private BlockingQueue<Map<String, Object>> queue;

	private DefaultTableModel model;
	private SocketService service;
	/**
	 * @Description
	 * 数据作业线程
	 * @Author qiang.zhu
	 * @param queue 存放数据的队列

	 */
	public ThreadWorking(SocketService service,DefaultTableModel model,BlockingQueue<Map<String, Object>> queue){
		this.service=service;
		this.model=model;
		this.queue=queue;
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				//log.debug(threadName+"当前队列大小:"+queue.size());
				Map<String,Object> map = queue.take();
				//如果是结束标志，则工作结束				String flag=map==null||map.get("FLAG")==null?"":map.get("FLAG").toString();
				if("END".equals(flag)){
					//该作业线程将结束标志取出队列了，故在这里重新加入队列告诉其他作业线程工作结束
					Map<String,Object> endMap=new HashMap<String,Object>();
					endMap.put("FLAG", "END");
					queue.put(endMap);
					return;
				}
				//开始作业
				if("send".equals(map.get("runType"))){
					service.sendFile(model, Integer.parseInt(map.get("fileNo").toString()), map.get("filePath").toString(),map.get("fileName").toString(),map.get("fileType").toString());
				}else if("receive".equals(map.get("runType"))){
					service.receiveFile(model, Integer.parseInt(map.get("fileNo").toString()), map.get("filePath").toString(),map.get("fileName").toString(),map.get("savePath").toString(),map.get("fileType").toString());
				}else if("check".equals(map.get("runType"))){
					service.checkFile(model);
				}else if("sendFast".equals(map.get("runType"))){
					if(Global.isDirectory.equals(map.get("fileType").toString())){
						service.sendFile(model, Integer.parseInt(map.get("fileNo").toString()), map.get("filePath").toString(),map.get("fileName").toString(),map.get("fileType").toString());
					}else{
						service.sendFileFast(model, Integer.parseInt(map.get("fileNo").toString()), map.get("filePath").toString(),map.get("fileName").toString(),map.get("fileType").toString());
					}
				}
			}	
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
