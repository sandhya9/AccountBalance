package myjpa;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Basic;




@Entity
@Table(name="CUSTOMERACCT")
@NamedQuery(
		name="listCustomers",
		query="select c from CustomerAcct c"
		) 
public class CustomerAcct implements Serializable {
	
	private static final long serialVersionUID= 2716055670746597905L;
	
		
	//	 persistent attributes which maps to the columns of the district table
	
	@Id 
	@Column(name="C_NUM")
	private short customerAcct; // this variable belongs to the primary key

	
	@Basic
	@Column(name="C_NAME")
	private String customerName;

	@Basic
	@Column(name="C_MONEY")
	private BigDecimal customerMoney;

	public short getCustomerAcct() {
		return customerAcct;
	}

	public void setCustomerAcct(short customerAcct) {
		this.customerAcct = customerAcct;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public BigDecimal getCustomerMoney() {
		return customerMoney;
	}

	public void setCustomerMoney(BigDecimal customerMoney) {
		this.customerMoney = customerMoney;
	}

	

	


}
