package br.ufc.great.tsd.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ManipuladorDatas {	
	/**
	 * Retorna a data e tempo corrente
	 * @param format formato da data exemplo "yyyy/MM/dd HH:mm:ss"
	 * @return String contento da data corrente exemplo 2016/11/16 12:08:43
	 */
	public static String getCurrentDataTime(String padrao) {
		DateFormat dateFormat = new SimpleDateFormat(padrao);
		Date date = new Date();
		
		return (dateFormat.format(date)); 
	}

	public static String getFormattedDataTime(String padrao, Date date) {
		DateFormat dateFormat = new SimpleDateFormat(padrao);
		
		return (dateFormat.format(date)); 
	}

	
	public static Date getDate() {
		Date date = new Date();
		return date;
	}
	
	public boolean dateIsBetweenDates(Date d, Date min, Date max) {
		return d.compareTo(min) >= 0 && d.compareTo(max) <= 0;
	}
	
}
