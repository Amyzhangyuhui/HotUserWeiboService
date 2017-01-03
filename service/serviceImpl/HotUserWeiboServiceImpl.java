package cn.edu.bjtu.weibo.service.serviceImpl ;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import org.springframework.beans.factory.annotation.Autowired;


import cn.edu.bjtu.weibo.dao.UserDAO;
import cn.edu.bjtu.weibo.dao.WeiboDAO;
//import cn.edu.bjtu.weibo.dao.userDaoImpl;
//import cn.edu.bjtu.weibo.dao.weiboDaoImpl;
import cn.edu.bjtu.weibo.model.Weibo;
import cn.edu.bjtu.weibo.service.*;

public class HotUserWeiboServiceImpl implements HotUserWeiboService{
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private WeiboDAO weiboDAO ;

	
	@Override
	public List<Weibo> HotUserWeiboList(String userId, int pageIndex, int numberPerPage) {
		// TODO Auto-generated method stub
		List<Weibo> weibolist = new ArrayList <Weibo> ();
		weibolist=getallhotnum(userId, numberPerPage, numberPerPage);
		if((pageIndex*numberPerPage+numberPerPage)<weibolist.size())
		{
			try{
				return weibolist.subList(pageIndex*numberPerPage, pageIndex*numberPerPage+numberPerPage-1);//页数从0开始
			}catch(Exception e)
			{
				return null;
			}
		}
		else
		{
			try{
			return weibolist.subList(pageIndex*numberPerPage-1, weibolist.size()-1);
			}catch(Exception e)
			{
				return null;
			}
		}
	}
	public List<Weibo> getallhotnum(String userId,int pageIndex, int numberPerPage)
	{
		Map<Weibo, Double> map = new HashMap<Weibo, Double>();

		List<Weibo> weibolist = new ArrayList <Weibo> ();
		List<Weibo> resultweibolist = new ArrayList <Weibo> ();
		weibolist= readALLweibo(userId, numberPerPage, numberPerPage);
//		for(int i=0;i<weibolist.size();i++)
//		{
//			System.out.println(gethotNum(weibolist.get(i)));
//		}
		for(int i=0;i<weibolist.size();i++)
		{
			map.put(weibolist.get(i), gethotNum(weibolist.get(i)));
		}


		ArrayList<Entry<Weibo, Double>> resultMap = sortMap(map);

		

		@SuppressWarnings("rawtypes")
		Iterator it = resultMap.iterator();  
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


		List<String> list = new ArrayList <String> ();
		
		list = userDAO.getWeibo(userId, pageIndex, numberPerPage);

		List<Weibo> weibolist = new ArrayList <Weibo> ();

		for(int i=0;i<list.size();i++)
		{
			weibolist.add(weiboDAO.getWeibo(list.get(i)));
		}

		return weibolist;
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

		double hotnum = weibo.getCommentNumber()+weibo.getLike()+Math.sqrt(weibo.getForwardNumber())+Math.log10(userDAO.getFollowerNumber(weibo.getUserId())+1);
		return hotnum;
	}


	public static ArrayList<Map.Entry<Weibo, Double>> sortMap(Map map){
		List<Map.Entry<Weibo, Double>> entries = new ArrayList<Map.Entry<Weibo, Double>>(map.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<Weibo, Double>>() {


			@Override
			public int compare(Entry<Weibo, Double> o1, Entry<Weibo, Double> o2) {
				// TODO Auto-generated method stub
				if((o2.getValue()-o1.getValue())>0)
					return -1;
				if(o2.getValue()==o1.getValue())
					return 0;
				else
					return 1;
			}
		});
		return (ArrayList<Entry<Weibo, Double>>) entries;
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



