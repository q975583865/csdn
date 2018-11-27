package TEST;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import utils.HttpClientUtil;

//使用前注意：   1.分类条数多余20个，只会取前20个
//			  2.在桌面新建CSDN博客文件夹，并在里面新建images文件夹

public class CSDN {
	
	public static void main(String[] args) throws Exception {
		CSDNimg csdNimg=new CSDNimg();
		
		FileSystemView fsv = FileSystemView.getFileSystemView();
		File com=fsv.getHomeDirectory();    //这便是读取桌面路径的方法了
		
		
		//获取文章列表List,填入参数为分类的id
		CSDNIDList c=new CSDNIDList("7941143");
		List<String> idList = c.getIdList();
//		System.out.println(idList);
		
		//便利该分类下的所有文章
		for(String id:idList) {
			String QueryStr =id;
			String url = "https://blog.csdn.net/q975583865/article/details/" + QueryStr;
			String html = HttpClientUtil.doGet(url);
			Document doc = Jsoup.parse(html);
			Elements bodytTitle = doc.getElementsByClass("title-article");
			
			String title=bodytTitle.get(0).text();
			Elements result = doc.getElementsByClass("htmledit_views");
			html=result.outerHtml();
			html="<html><head></head><body>"+bodytTitle.get(0).outerHtml()+html+"</body></html>";
			
			//code红色边框
			Document h = Jsoup.parse(html);
			Elements hh = h.getElementsByTag("code");
			for(Element e:hh) {
				e.html("<div style='border:2px red solid'>"+e.outerHtml()+"</div>");
			}
			html=h.outerHtml();
			
			//不能创建带/的文件，替换
			if(title.contains("/")) {
				title=title.replace("/", "-");
			}
			//不能创建带:的，替换为：
			if(title.contains(":")) {
				title=title.replace(":", "：");
			}
			
			//
			File file = new File(com.getPath()+"\\CSDN博客\\"+title+".html");
			if (!file.exists()) {
					file.createNewFile();
			}
			if(file.exists()) {
				file.delete();
				file.createNewFile();
			}
			
			System.out.println(title);
			
			//保存图片，及修改图片为本地地址
			Document d = Jsoup.parse(html);
			Elements elementsByTag = d.getElementsByTag("img");
			for(Element e:elementsByTag) {
				String imgUrl=e.attr("src");
				String imgId = csdNimg.a(imgUrl);
				e.attr("src","images\\"+ imgId + ".png");
			}
			html=d.outerHtml();
			
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			//把输出字节流转换成字符流并且指定编码表。
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "utf-8");
			outputStreamWriter.write(html);
			outputStreamWriter.close();
			fileOutputStream.close();
		}
		
		
	}
}
