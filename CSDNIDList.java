package TEST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import utils.HttpClientUtil;

public class CSDNIDList {
	public String QueryStr;
	
	//填入分类的ID
	public  CSDNIDList(String QueryStr) {
		this.QueryStr = QueryStr;
	}

	public List<String> getIdList() {
		List<String> idList=new ArrayList<>(34);
		
		String url = "https://blog.csdn.net/q975583865/article/category/" + QueryStr;
		String html = HttpClientUtil.doGet(url);
		Document doc = Jsoup.parse(html);
		Elements tt = doc.getElementsByTag("h4");
		for (Element e : tt) {
			Elements elementsByTag = e.getElementsByTag("a");
			for (Element e2 : elementsByTag) {
				if (e2.attr("href").contains("q975583865")) {
				
					idList.add(e2.attr("href").substring(49));

				}

			}
		}
		return idList;
	}
}
