package com.chinacreator.service;

import java.io.DataOutputStream;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chinacreator.common.Global;

public class MutilUploadService {

	public void uploadFile(String filePath,String fileName,int threadCounts,List<Map<String,Object>> list){
		File file=new File(filePath);
		long totalSize=file.length();
		long stockNums=(long)Math.ceil((double)totalSize/(double)threadCounts);
		long sendedNums=0;
		long sendStockNums=stockNums;
		for(int i=0;i<threadCounts;i++){
			Map<String,Object> map = list.get(i);
			if(sendedNums+stockNums>totalSize){
				sendStockNums=totalSize-sendedNums;
			}
			map.put("startNums", i*stockNums);
			map.put("final_startNums", i*stockNums);
			map.put("stockNums", sendStockNums);
			map.put("final_stockNums", sendStockNums);
			new Thread(new MutilThreadService(filePath,fileName,i*stockNums,sendStockNums, map, map.get("ip").toString(), Integer.parseInt(map.get("port").toString()))).start();
			sendedNums+=stockNums;
		}
	}
	
	public void uploadFileResend(String filePath,String fileName,List<Map<String,Object>> list,Map<String,Object> reSendMap){
		if(list.size()!=0){
			Map map=list.get(0);
			reSendMap.put("status","0");
			reSendMap.put("targetIp",map.get("targetIp"));
			reSendMap.put("ip",map.get("ip"));
			reSendMap.put("port",map.get("port"));
			reSendMap.put("name",map.get("name"));
			reSendMap.put("type","resend");
			new Thread(new MutilThreadService(filePath,fileName,Long.parseLong(reSendMap.get("startNums").toString()),Long.parseLong(reSendMap.get("stockNums").toString()), reSendMap, reSendMap.get("ip").toString(), Integer.parseInt(reSendMap.get("port").toString()))).start();
		}
	}
	
	public void noticeFile(String fileName,String ip,int port){
		try{
			Socket socket=null;
			DataOutputStream dos = null;
			try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(ip,port),
                               3 * 1000);
                dos = new DataOutputStream(socket.getOutputStream());
                //头部
                dos.writeInt(11);
                dos.writeInt(fileName.getBytes(Global.charFormat).length);
                dos.write(fileName.getBytes(Global.charFormat));
                dos.flush();
            } finally {
                if (dos != null)
                    dos.close();
                if (socket != null)
                    socket.close();
            }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws Exception{
		long startTime=new Date().getTime();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("ip", "127.0.0.1");
		map.put("port", 8000);
		list.add(map);
		map=new HashMap<String,Object>();
		map.put("ip", "127.0.0.1");
		map.put("port", 8000);
		list.add(map);
		map=new HashMap<String,Object>();
		map.put("ip", "127.0.0.1");
		map.put("port", 8000);
		list.add(map);
		map=new HashMap<String,Object>();
		map.put("ip", "127.0.0.1");
		map.put("port", 8000);
		list.add(map);
		map=new HashMap<String,Object>();
		map.put("ip", "127.0.0.1");
		map.put("port", 8000);
		list.add(map);
		new MutilUploadService().uploadFile("C:\\Users\\xqtt29\\Desktop\\传送门v1.2.rar","传送门v1.2.rar", list.size(),list);
		boolean flag=true;
		while(flag){
			int counts=0;
			for(Map temp : list){
				if("200".equals(temp.get("status"))){
					counts++;
				}
			}
			if(counts==list.size()){
				flag=false;
			}
			Thread.currentThread().sleep(500);
		}
		long endTime=new Date().getTime();
		System.out.println(endTime-startTime);
		new MutilUploadService().noticeFile("传送门v1.2.rar","127.0.0.1",8000);
	}

}
