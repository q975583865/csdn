package TEST;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.filechooser.FileSystemView;

public class CSDNimg {
	public int i=0;


	public String a(String imgUrl) throws Exception {
		
		FileSystemView fsv = FileSystemView.getFileSystemView();
		File com=fsv.getHomeDirectory();    //这便是读取桌面路径的方法了
//		System.out.println(com.getPath());
		
		String imgId = null ;
		//截取规则1，直接粘贴的
		if(imgUrl.startsWith("https://img-blog.csdn.net")){
			if(imgUrl.contains("?")) {
				imgId = imgUrl.substring(26, imgUrl.indexOf("?"));
			}else {
				imgId = imgUrl.substring(26);
			}
			 
		}
		//截取规则2，QQ截屏
		else if(imgUrl.startsWith("https://img-blog.csdnimg.cn")) {
			 imgId = imgUrl.substring(28, imgUrl.indexOf("?")-4);
		}
		//截取规则3，CSDN的问题
		else if(imgUrl.startsWith("//img-blog.csdn.net")) {
			imgUrl="https:"+imgUrl;
			imgId = imgUrl.substring(28, imgUrl.indexOf("?")-4);
		}
		//引入的第三方图片
		else {
			imgId="img"+(i++);
		}
		

		File file = new File(com.getPath() +"\\CSDN博客\\images\\"+ imgId + ".png");
				file.createNewFile();
		if (file.exists()) {
			file.delete();
				file.createNewFile();
		}

		try {
			URL url = null;
//			System.out.println(imgUrl);
			url = new URL(imgUrl);
			DataInputStream dataInputStream = new DataInputStream(url.openStream());

			FileOutputStream fileOutputStream = new FileOutputStream(file);
			ByteArrayOutputStream output = new ByteArrayOutputStream();

			byte[] buffer = new byte[1024];
			int length;

			while ((length = dataInputStream.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
			fileOutputStream.write(output.toByteArray());
			dataInputStream.close();
			fileOutputStream.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imgId;
	}

}
