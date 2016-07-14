package com.chinacreator.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;

import com.chinacreator.common.Global;

public class MutilThreadService implements Runnable{

	private String filePath;
	private String fileName;
	private long startNum;
	private long stockNum;
	private Map<String,Object> result;
	private String ip;
	private int port;
	public MutilThreadService(String filePath,String fileName,long startNum,long stockNum,Map<String,Object> result,String ip,int port){
		this.filePath=filePath;
		this.fileName=fileName;
		this.startNum=startNum;
		this.stockNum=stockNum;
		this.result=result;
		this.ip=ip;
		this.port=port;
	}
	@Override
	public void run() {
		int length = 0;
		try{
			RandomAccessFile raf=new RandomAccessFile(new File(filePath), "r");
			raf.seek(startNum);
			Socket socket=null;
			DataInputStream dis = null;
			DataOutputStream dos = null;
	        double sended=0;
	        byte[] sendBytes = new byte[102400];
			try {
				double send_=0;
				if(result.get("process")==null){
					result.put("process", "0");
				}else{
					send_=Double.parseDouble(result.get("process").toString());
				}
                result.put("status", "0");
                socket = new Socket();
                socket.connect(new InetSocketAddress(ip,port),
                               3 * 1000);
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());
                //头部
                if(!ip.equals(result.get("targetIp").toString())){
                	dos.write(Global.reSendFileMutil.getBytes(Global.charFormat),0,1);
                	dos.writeInt(result.get("targetIp").toString().getBytes(Global.charFormat).length);
                	dos.write(result.get("targetIp").toString().getBytes(Global.charFormat));
                	dos.writeInt(port);
                }
            	dos.write(Global.sendFileMutil.getBytes(Global.charFormat),0,1);
                dos.writeLong(Long.parseLong(result.get("final_startNums").toString()));
                dos.writeLong(startNum);
                dos.writeInt(fileName.getBytes(Global.charFormat).length);
                dos.write(fileName.getBytes(Global.charFormat));
                dos.flush();
                //文件信息流
                int len=sendBytes.length>stockNum?(int)stockNum:sendBytes.length;
                while ((length = raf.read(sendBytes, 0, len)) > 0) {
                    dos.write(sendBytes, 0, length);
                    dos.flush();
                    sended+=length;
                    result.put("startNums", Long.parseLong(result.get("startNums").toString())+length);
                    result.put("stockNums", Long.parseLong(result.get("stockNums").toString())-length);
                    result.put("process", String.valueOf(send_+sended));
                    if(sended>=stockNum){
                    	break;
                    }
                    if(length==len&&sended+102400>=stockNum){
                    	len=(int)(stockNum-sended);
                    }
                }
                result.put("status", "200");
            } finally {
                if (dis != null)
                    dis.close();
                if (dos != null)
                    dos.close();
                if (raf != null)
                	raf.close();
                if (socket != null)
                    socket.close();
            }
		}catch(Exception e){
			e.printStackTrace();
			result.put("status", "-1");
            result.put("startNums", Long.parseLong(result.get("startNums").toString())-length);
            result.put("stockNums", Long.parseLong(result.get("stockNums").toString())+length);
            result.put("process", Double.parseDouble(result.get("process").toString())-length);
		}
		
	}

}
