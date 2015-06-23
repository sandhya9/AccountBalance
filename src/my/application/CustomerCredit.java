package my.application;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;

import javax.annotation.Resource;
import javax.annotation.Resource.AuthenticationType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnit;

import javax.persistence.EntityManagerFactory;

import myjpa.CustomerAcct;

/**
 * Servlet implementation class CustomerCredit
 */
@WebServlet("/CustomerCredit")
@Resource(name = "jdbc/MyDataSource", type = javax.sql.DataSource.class, shareable = true, authenticationType = AuthenticationType.CONTAINER)
public class CustomerCredit extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@PersistenceUnit(unitName = "CustomerQuery")
	EntityManagerFactory emf;

	// @Resource (mappedName="java:comp/UserTransaction") UserTransaction
	// userTran;

	// EntityManagerFactory emf=null; if you want to do explicit lookup
	CustomerAcct customer = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CustomerCredit() {
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
		short customerNumber = Short.valueOf(request
				.getParameter("customerNumber"));
		double moneyToCredit = Double.valueOf(request
				.getParameter("customerAmount"));

		try {

			UserTransaction userTran = getUserTrans();

			userTran.begin();
			em.joinTransaction();

			CustomerAcct customerAcct = em.find(CustomerAcct.class,
					customerNumber);
			if (customerAcct == null) {
				userTran.rollback();
				throw new ServletException(
						"customer not found, customer account number="
								+ customerNumber);
			}
			double money = customerAcct.getCustomerMoney().doubleValue()
					+ moneyToCredit; // add credit to existing balance
			customerAcct.setCustomerMoney(new BigDecimal(money)); // set new
																	// existing
																	// balance
			printResults(out, moneyToCredit, customerAcct);

			userTran.commit();
			em.close();

		} catch (PersistenceException t) {
			t.printStackTrace();
			System.out.println(t.getCause().getMessage());
			String causeMsg = t.getCause().getMessage();
			if (causeMsg.contains("-204"))
				throw new ServletException(
						"table does not exist please run populate DB function from web page: "
								+ t.getCause().getMessage(), t.getCause());
			else
				throw new ServletException(
						"persistence exception obtaining customer acct: "
								+ t.getCause().getMessage(), t.getCause());
		} // end try

		catch (Exception t) {
			t.printStackTrace();
			throw new ServletException("error locating customer account", t);
		} // end try

	}

	private void printResults(PrintWriter out, double moneyToCredit,
			CustomerAcct customerAcct) {
		String title = "Customer Credit Results";

		out.println("<HTML><HEAD><TITLE>");
		out.println(title);
		out.println("</TITLE></HEAD><body bgcolor='#f8f7cd'>");
		out.println("<H1 align=\"center\">" + title + "</H1>");
		out.println("<BR><BR><BR>");

		out.println("<TABLE align='center'	border='8' bgcolor='#cccccc' bordercolor='#FFCC99'>");
		out.println("<TBODY>");
		out.println("<TR>");
		out.println("<TH>Customer:</TH>");
		out.println("<TH>Has been credited: </TH>");
		out.println("<TH>Account balance is now: </TH>");

		out.println("</TR>");
		out.println("<TR>");
		out.println("<TH>" + customerAcct.getCustomerName() + "</TH>");
		out.println("<TH>$"
				+ BigDecimal.valueOf(moneyToCredit).setScale(2,
						BigDecimal.ROUND_HALF_UP) + "</TH>");
		out.println("<TH>$"
				+ customerAcct.getCustomerMoney().setScale(2,
						BigDecimal.ROUND_HALF_UP) + "</TH>");

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
