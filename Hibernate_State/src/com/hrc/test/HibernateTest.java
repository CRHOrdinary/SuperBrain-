package com.hrc.test;


import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hrc.entity.News;


/**
 * 测试类
 * @author XC
 *
 */
public class HibernateTest {
	
	private SessionFactory sessionFactory = null;
	
	//在生产环境不能用作成员变量，会有并发问题
	private Session session =null;
	
	//在生产环境不能用作成员变量，会有并发问题
	private Transaction transaction = null;
	
	@Before
	public void init(){
		
		Configuration configuration = new Configuration().configure();
		
		ServiceRegistry serviceRegistry =
				new ServiceRegistryBuilder().applySettings(configuration.getProperties())
											.buildServiceRegistry();
		
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		
		session = sessionFactory.openSession();
		
		transaction = session.beginTransaction();
		
	}
	
	@After
	public void destroy(){
		
		transaction.commit();
		
		session.close();
		
		sessionFactory.close();
		
	}
	
	/**
	 * 1.save() 方法
	 * 使一个临时对象变为持久化对象
	 * 为对象分配ID
	 * 在 flush 缓存时会发送一条 INSERT 语句
	 * 在 save() 方法之前设置ID是无效的
	 * 持久化对象的ID是不能被修改的！
	 */
	@Test
	public void testSave(){
		
		News news = new News();
		
		news.setTitle(".NET");
		
		news.setAuthor("crh");
		
		news.setDate(new Date());
		
		//news.setId(10);
		
		System.out.println(news);
		
		session.save(news);
		
		System.out.println(news);
		
		//news.setId(8);
		
	}
	
	/**
	 * 2.Persist()
	 * 也会执行 INSERT 语句，与 Save() 方法类似
	 * 
	 * 与 Save() 区别：
	 * 在调用 persist() 方法之前，若对象已经有ID了，则不会执行 INSERT 语句，并且抛出异常
	 */
	@Test
	public void testPersist(){
		
		News news = new News();
		
		news.setTitle("BB");
		
		news.setAuthor("crh");
		
		news.setDate(new Date());
		
		news.setId(4);
		
		session.persist(news);
		
	}
	
	/**
	 * get VS load:
	 * 
	 * 1.执行get()方法：会立即加载对象；
	 * 	  执行load()方法：若不使用该对象的除ID之外的属性，则不会立即执行查询操作，而是返回
	 * 	  一个代理对象。
	 * 		
	 * 	 get 是立即检索，load 是延迟检索。
	 * 
	 * 2.若数据表中没有对应的记录，Session 也没有被关闭，同时符合1的描述时，
	 * 	 get 返回 NULL，如果调用属性，就会抛出 NullPointerException
	 * 	 load 抛出异常 ：ObjectNotFoundException
	 * 
	 * 3.load() 方法可能会抛出 LazyInitializationException 异常：在需要初始化
	 * 	  代理对象之前 Session 已经关闭。
	 * 
	 */
	@Test
	public void testGet(){
		
		//自动修正快捷键 Ctrl+1
		News news = (News) session.get(News.class, 1);
		
		session.close();
		
		System.out.println(news);
		
	}
	
	@Test
	public void testLoad(){
		
		News news = (News) session.load(News.class, 1);
		
		session.close();
		
		System.out.println(news.getClass().getName());
		
		System.out.println(news);
	}
	
	/**
	 * update()方法：
	 * 
	 * 1.若更新一个持久化对象，不需要显式的调用 update()方法，因为在调用 Transaction
	 * 	  的 commit()方法时，会先执行 Session 的 flush()方法。
	 * 
	 * 2.若更新一个游离对象，需要显式的调用 Session 的 update()方法;还可以把一个游离对象
	 * 	  变为持久化对象。
	 * 
	 * 需要注意：
	 * 
	 * 1.无论要更新的游离对象和数据表中的记录是否一致，都会强制发送 UPDATE 语句。
	 * 	  不过，这样有可能与数据库的触发器有冲突，所以，如何能让 update 方法不再
	 *   盲目的的发送 update 语句呢？在 .hbm.xml 文件中的 class 节点设置
	 *   select-before-update = true 就可以，但通常不需要设置该属性，除非
	 *   与触发器一起工作。
	 *   
	 * 2.若数据表中没有对应的记录，但还调用了 update()方法，会抛出异常
	 * 
	 * 3.当 update()方法关联一个游离对象时，
	 * 	  如果在 Session 的缓存中，已经存在相同的 OID 的持久化对象，会抛出异常，
	 * 	  因为在 Session 缓存中不能有两个 OID 相同的对象！
	 */
	@Test
	public void tetsUpdate(){
		
		News news = (News) session.get(News.class, 2);
		
		//transaction.commit();
		
		//session.close();
		
		//news.setId(100);
		
		//新对象
		//session = sessionFactory.openSession();
		
		//transaction = session.beginTransaction();
		
		//游离对象
		//news.setTitle("AA");
		
		session.update(news);
		
	}
	
	/**
	 * 注意：
	 * 1.若 OID 不为 NULL ，但数据表还没有与其对应的记录时，会抛出异常。
	 * 2.了解：OID 的值等于 ID 的 unsave-value 属性值的对象，也被认为
	 * 	  是一个游离对象。
	 */
	@Test
	public void testSaveOrUpdate(){
		
		News news = new News("BB", "crh", new Date());
		
		//news.setId(20);
		
		session.saveOrUpdate(news);
		
	}
	
	/**
	 * delete：执行删除操作，只要 OID 和数据表中一条记录对应，就会准备执行 delete()操作，
	 * 若 OID 在数据表中没有对应的记录，则抛出异常。
	 * 
	 * 可以通过设置 Hibernate 配置文件的 hibernate.use_identifier_rollback 为 true,
	 * 使删除对象后，把其 OID 置为 NULL。
	 */
	@Test
	public void testDelete(){
		
		//游离对象
//		News news = new News();
//		
//		news.setId(6);
		
		//持久化对象
		News news2 = (News) session.get(News.class, 5);
		
		session.delete(news2);
		
		System.out.println(news2);
		
	}
	
	/**
	 * evict:从 Session 缓存中把指定的持久化对象移除
	 */
	@Test
	public void testEvict(){
		
		News news = (News) session.get(News.class, 2);
		
		News news2 = (News) session.get(News.class, 3);
		
		news.setTitle("AA");
		
		news2.setTitle("BB");
		
		session.evict(news2);
	}
	
	
	
}
