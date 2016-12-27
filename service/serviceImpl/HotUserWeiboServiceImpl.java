package cn.edu.bjtu.weibo.immplement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;


import cn.edu.bjtu.weibo.dao.UserDAO;
import cn.edu.bjtu.weibo.dao.WeiboDAO;
import cn.edu.bjtu.weibo.model.Weibo;
import cn.edu.bjtu.weibo.service.*;

public class HotUserWeiboServiceImpl implements HotUserWeiboService{
	@Autowired
	private WeiboDAO weiboDAO;
	@Autowired
	private UserDAO userDAO;
	@Override
	public List<Weibo> HotUserWeiboList(String userId, int pageIndex, int numberPerPage) {
		// TODO Auto-generated method stub
		List<Weibo> weibolist = new ArrayList <Weibo> ();
		weibolist=getallhotnum(userId, numberPerPage, numberPerPage);
		if((pageIndex*numberPerPage+numberPerPage)<weibolist.size())
			return weibolist.subList(pageIndex*numberPerPage-1, pageIndex*numberPerPage+numberPerPage-1);
		else
			return weibolist.subList(pageIndex*numberPerPage-1, weibolist.size()-1);
	}
	public List<Weibo> getallhotnum(String userId,int pageIndex, int numberPerPage)
	{
		Map<Weibo, Double> map = new TreeMap<Weibo, Double>();

		List<Weibo> weibolist = new ArrayList <Weibo> ();
		List<Weibo> resultweibolist = new ArrayList <Weibo> ();
		weibolist= readALLweibo(userId, numberPerPage, numberPerPage);
		for(int i=0;i<weibolist.size();i++)
		{
			map.put(weibolist.get(i), gethotNum(weibolist.get(i)));
		}


		Map<Weibo, Double> resultMap = sortMapByValue(map);
		@SuppressWarnings("rawtypes")
		Iterator it = resultMap.entrySet().iterator();  
        while(it.hasNext()){  
            @SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) it.next();  
            resultweibolist.add((Weibo) entry.getKey());
        }
		return resultweibolist;  
	}

	public List<Weibo> readALLweibo(String userId,int pageIndex, int numberPerPage)
	{
		Date now = new Date(); 
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式
		String Starttime=dateFormat.format( now );

		String oneHoursAgoTime =  "" ;
		Calendar cal = Calendar. getInstance ();
		cal.set(Calendar. HOUR , Calendar. HOUR -1 ) ; //把时间设置为当前时间-1小时，同理，也可以设置其他时间
		cal.set(Calendar. MONTH , Calendar. MONTH -1); //当前月前一月
		oneHoursAgoTime =  new  SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).format(cal.getTime());//获取到完整的时间

		//there, I want to get the weibo who can display according to the userId ,but the Dao layer didn't provied the method
		//GetWeiboAccordingTimeImpl gw = new GetWeiboAccordingTimeImpl();
		//userDAO.getWeibo(userId, pageIndex, pagePerNumber);
		List<Weibo> list = new ArrayList <Weibo> ();
		//list = gw.getWeiboList(userId,Starttime, oneHoursAgoTime);
		list = userDAO.getWeibo(userId, pageIndex, numberPerPage);
		return list;
	}
	
	
	public static Map<Weibo, Double> sortMapByValue(Map<Weibo, Double> oriMap) {
		if (oriMap == null || oriMap.isEmpty()) {
			return null;
		}
		Map<Weibo, Double> sortedMap = new LinkedHashMap<Weibo, Double>();
		List<Map.Entry<Weibo, Double>> entryList = new ArrayList<Map.Entry<Weibo, Double>>(
				oriMap.entrySet());
		Collections.sort(entryList, new MapValueComparator());

		Iterator<Map.Entry<Weibo, Double>> iter = entryList.iterator();
		Map.Entry<Weibo, Double> tmpEntry = null;
		while (iter.hasNext()) {
			tmpEntry = iter.next();
			sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
		}
		return sortedMap;
	}
	public double gethotNum(Weibo weibo)
	{
		
		//GetUserAccordingWeiboImpl gu = new GetUserAccordingWeiboImpl();
		//double hotnum = weibo.getCommentNumber()+weibo.getLike()+Math.sqrt(weibo.getForwardNumber())+Math.log10(gu.getuser(weibo).getFollow()+1);
		//there, I want to get the UserId who own the weibo ,but the Dao layer didn't provied the method
		double hotnum = weibo.getCommentNumber()+weibo.getLike()+Math.sqrt(weibo.getForwardNumber());
		return hotnum;
	}

}


class MapValueComparator implements Comparator<Map.Entry<Weibo, Double>> {

	@Override
	public int compare(Entry<Weibo, Double> o1, Entry<Weibo, Double> o2) {
		// TODO Auto-generated method stub
		if(o1.getValue()>o2.getValue())
			return 1;
		else if(o1.getValue()<o2.getValue())
			return -1;
		else
			return 0;
	}
}
