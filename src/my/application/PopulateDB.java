package my.application;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import javax.annotation.Resource;
import javax.annotation.Resource.AuthenticationType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;


import myjpa.CustomerAcct;

/**
 * Servlet implementation class CustomerCredit
 */
@WebServlet("/PopulateDB")
@Resource(name = "jdbc/MyDataSource", type = javax.sql.DataSource.class, shareable = true, authenticationType = AuthenticationType.CONTAINER)
public class PopulateDB extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@PersistenceUnit(unitName = "CustomerQuery")
	EntityManagerFactory emf;	
	
	private String[] name = {"Ben Franklin","Albert Einstein","Groucho Marx","Harpo Marx","Chico Marx","Zeppo Marx","Eleanor Roosevelt","Jean-Paul Sartre"};
	private double[] money = {100.00,180000.00,1.00,2.00,3.00,4.00,101118.84,0.00};

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PopulateDB() {
		super();
		// TODO Auto-generated constructor stub
	}

	private UserTransaction getUserTrans() throws NamingException {
		InitialContext ctx = new InitialContext();
		UserTransaction tx = (UserTransaction) ctx
				.lookup("java:comp/UserTransaction");
		return tx;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		
		EntityManager em = emf.createEntityManager();
		String db2NoTableMsg = "-204";
		String oracle2NoTableMsg = "table or view does not exist";
		String derbyNoTableMsg1 = "cannot be performed";
		String derbyNoTableMsg2 = "because it does not exist";
		
		
		
		
		CustomerAcct ca = null;

		try {
			
			UserTransaction userTran = getUserTrans();

			userTran.begin();
			em.joinTransaction();
			Query q = null;
			try {
				q = em.createNativeQuery("DROP TABLE CUSTOMERACCT");
				q.executeUpdate();
			} catch(PersistenceException p) {
				String causeMsg = p.getCause().getMessage();
				if (causeMsg.contains(db2NoTableMsg)||causeMsg.contains(oracle2NoTableMsg)||(causeMsg.contains(derbyNoTableMsg1)&& causeMsg.contains(derbyNoTableMsg2)));
				else throw new ServletException("there was a problem dropping the table "+p.getCause().getMessage(),p.getCause());
				if (userTran.getStatus() == Status.STATUS_MARKED_ROLLBACK) {
					userTran.rollback();
					userTran.begin();
				}
				
			}
			
			
			em.joinTransaction();
			
			
			q = em.createNativeQuery("CREATE TABLE  CUSTOMERACCT ( C_NUM  SMALLINT NOT NULL  ,  C_NAME  CHARACTER(30) NOT NULL  ,  C_MONEY DECIMAL(12,2) NOT NULL,PRIMARY KEY ( C_NUM ) )");
			q.executeUpdate();
			
			

			
			for (short j=0;j<8;j++) {
				ca = new CustomerAcct();
				ca.setCustomerAcct((short) (j+1));
				ca.setCustomerMoney(BigDecimal.valueOf(money [j]));
				ca.setCustomerName(name[j]);	
				em.persist(ca);				
			}
			
			
					
						
			printResults(out);
			
			userTran.commit();
			
			em.close();
			
			

		} catch (PersistenceException t) {
			t.printStackTrace();
			System.out.println(t.getCause().getMessage());
			throw new ServletException(
					"persistence exception creating customer table: "
							+ t.getCause().getMessage(), t.getCause());
		} // end try

		catch (Exception t) {
			t.printStackTrace();
			throw new ServletException("exception creating customer table", t);
		} // end try

	}
	
	private void printResults(PrintWriter out) {
		String title = "DataBase Population results";

		out.println("<HTML><HEAD><TITLE>");
		out.println(title);
		out.println("</TITLE></HEAD><body bgcolor='#f8f7cd'>");
		out.println("<H1 align=\"center\">" + title + "</H1>");
		out.println("<BR><BR><BR>");

		out.println("<TABLE align='center'	border='8' bgcolor='#cccccc' bordercolor='#FFCC99'>");
		out.println("<TBODY>");
		out.println("<TR>");
		out.println("<TH>Database Population:</TH>");
		out.println("</TR>");
		out.println("<TR>");
		out.println("<TH>successful</TH>");
		out.println("</TR>");
		
		out.println("</TBODY>");
		out.println("</TABLE>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
