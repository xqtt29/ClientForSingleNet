package com.chinacreator.controller;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import com.chinacreator.common.Global;
import com.chinacreator.entity.SendTableModel;
import com.chinacreator.service.DataOprService;

/**
 * @Description
 * 粘贴板实时监听服务类
 * 
 * @Author qiang.zhu
 * @Datetime 2016年5月12日 上午10:51:50
 * @Version
 * @Copyright (c) 2013 湖南科创信息技术股份有限公司
 */
public class SystemClipboardMonitor implements ClipboardOwner{
	
    private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    private final DefaultTableModel model;
    private final String ip;
    private final int port;
    private boolean going; //控制开关
     
    public SystemClipboardMonitor(DefaultTableModel model,String ip,int port){
    	this.model=model;
    	this.ip=ip;
    	this.port=port;
    }
    /**
	 * @Description
	 * 开始监视剪贴板
	 * @Author qiang.zhu
	 */
    public void begin(){
        going = true;
        //将剪贴板中内容的ClipboardOwner设置为自己
        //这样当其中内容变化时，就会触发lostOwnership事件
        clipboard.setContents(clipboard.getContents(null), this);
    }
     
    /**
	 * @Description
	 * 停止监视剪贴板
	 * @Author qiang.zhu
	 */
    public void stop(){
        going = false;
    }
 
    /**
	 * @Description
	 * 如果剪贴板的内容改变，则系统自动调用此方法
	 * @Author qiang.zhu
	 */
    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        if (going){ //如果是进行中状态，则操作
            // 如果不暂停一下，经常会抛出IllegalStateException
            // 猜测是操作系统正在使用系统剪切板，故暂时无法访问
            try {
            	Thread.sleep(100);
	            // 取出文本并进行一次文本处理            
	            // 如果剪贴板中有文本:
	            if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)){
                    String text = (String)clipboard.getData(DataFlavor.stringFlavor);
                    String len = DataOprService.getInstance().initData(text.getBytes(Global.charFormat).length, 10);
                    String head=Global.sendString+len+text;
    				DataOprService.getInstance().sendSocketString(ip,port,head);
	            }else if (clipboard.isDataFlavorAvailable(DataFlavor.javaFileListFlavor)){
                    Object text = clipboard.getData(DataFlavor.javaFileListFlavor);
                    final List<File> list=(List)text;
    				for(File f : list){
    					if(DataOprService.getInstance().findSameFile(model, f.getName())){
    						continue;
    					}
    					SendTableModel stm=new SendTableModel();
    					stm.setFilePath(f.getAbsolutePath());
    					stm.setFileType(f.isDirectory()?Global.isDirectory:Global.isFile);
    					String[] temp=f.getAbsolutePath().split("\\".equals(File.separator)?"\\\\":File.separator);
    					stm.setFileName(temp[temp.length-1]);
    					DataOprService.getInstance().insertRowForSendTable(model, stm);
    				}
	            }

            }catch(Exception e){
            	e.printStackTrace();
            }finally{
                // 存入剪贴板，并注册自己为所有者
                // 这样下次剪贴板内容改变时，仍然可以触发此事件
                clipboard.setContents(clipboard.getContents(null), this);
            }
             
        }
    }
 
}