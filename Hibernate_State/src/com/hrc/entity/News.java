package com.hrc.entity;

import java.util.Date;

/**
 * 新闻实体类
 * @author XC
 *
 */
public class News {
	
	/**
	 * 无参构造函数
	 */
	public News() {
		
	}
	
	/**
	 * 带三个参数的构造函数
	 * @param title 标题
	 * @param author 作者
	 * @param date 日期
	 */
	public News(String title, String author, Date date) {
		super();
		this.title = title;
		this.author = author;
		this.date = date;
	}
	
	@Override
	public String toString() {
		return "News [id=" + id + ", title=" + title + ", author=" + author
				+ ", date=" + date + "]";
	}

	private Integer id;
	private String title;
	private String author;
	private Date date;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
}
