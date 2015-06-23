package my.application;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Collection;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.annotation.Resource;
import javax.annotation.Resource.AuthenticationType;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnit;

import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.openjpa.persistence.OpenJPAQuery;

import myjpa.CustomerAcct;

/**
 * Servlet implementation class CustomerCredit
 */
@WebServlet("/ListDB")
@Resource(name = "jdbc/MyDataSource", type = javax.sql.DataSource.class, shareable = true, authenticationType = AuthenticationType.CONTAINER)
public class ListDB extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@PersistenceUnit(unitName = "CustomerQuery")
	EntityManagerFactory emf;

	CustomerAcct customer = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ListDB() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();

		printResultsStart(out);

		try {
			System.out.println("test");
			
			EntityManager em = emf.createEntityManager();

			OpenJPAQuery q = OpenJPAPersistence.cast(em
					.createNamedQuery("listCustomers"));
			System.out.println("test1");
			Collection coll = null;
			coll = q.getResultList();

			if (coll != null) {
				Iterator it = coll.iterator();

				while (it.hasNext()) {
					customer = (CustomerAcct) it.next();
					printResultsMiddle(out, customer);
				}

			}

			printResultsEnd(out);

		} catch (PersistenceException t) {
			t.printStackTrace();
			Throwable c = t.getCause();
			if (c != null) {
				System.out.println(c.getMessage());
				String causeMsg = t.getCause().getMessage();
				if (causeMsg.contains("-204"))
					throw new ServletException(
							"table does not exist please run populate DB function from web page: "
									+ t.getCause().getMessage(), t.getCause());
				else
					throw new ServletException(
							"persistence exception obtaining customer acct: "
									+ t.getCause().getMessage(), t.getCause());
			}
			throw new ServletException(
					"persistence exception obtaining customer acct: "
							+ t.getMessage(), t);

		} // end try

		catch (Exception t) {
			t.printStackTrace();
			throw new ServletException("error locating customer account", t);
		} // end try

	}

	private void printResultsStart(PrintWriter out) {
		String title = "Contents of Database";

		out.println("<HTML><HEAD><TITLE>");
		out.println(title);
		out.println("</TITLE></HEAD><body bgcolor='#f8f7cd'>");
		out.println("<H1 align=\"center\">" + title + "</H1>");
		out.println("<BR><BR><BR>");

		out.println("<TABLE align='center'	border='8' bgcolor='#cccccc' bordercolor='#FFCC99'>");
		out.println("<TBODY>");
		out.println("<TR>");
		out.println("<TH>Customer ID:</TH>");
		out.println("<TH>Name: </TH>");
		out.println("<TH>Account balance: </TH>");
		out.println("</TR>");
	}

	private void printResultsMiddle(PrintWriter out, CustomerAcct ca) {
		out.println("<TR>");
		out.println("<TH>" + ca.getCustomerAcct() + "</TH>");
		out.println("<TH>" + ca.getCustomerName() + "</TH>");
		out.println("<TH>$" + ca.getCustomerMoney() + "</TH>");
		out.println("</TR>");
	}

	private void printResultsEnd(PrintWriter out) {
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
