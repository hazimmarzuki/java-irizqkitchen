package com.heroku.java.CONTROLLER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

//import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.heroku.java.MODEL.Orderdetails;
import com.heroku.java.MODEL.Orders;
import com.heroku.java.MODEL.Products;
import com.heroku.java.MODEL.customer;

import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.Map;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;

// import org.jscience.physics.amount.Amount;
// import org.jscience.physics.model.RelativisticModel;
// import javax.measure.unit.SI;
@SpringBootApplication
@Controller

public class paymentController {
  private final DataSource dataSource;

  @Autowired
  public paymentController(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @GetMapping("/payment")
  public String getpayment(HttpSession session, Model model) {
    int custid = (int) session.getAttribute("custid");

    try {
      Connection connection = dataSource.getConnection();
      String sql = "SELECT c.custname, c.custaddress, o.orderid, o.orderprice, o.orderstatus, od.orderdetailsqty, p.proname FROM customers c "
          +
          "JOIN orders o ON c.custid = o.custid " +
          "JOIN orderdetails od ON o.orderid = od.orderid " +
          "JOIN products p ON od.proid = p.proid " +
          "WHERE o.orderstatus = 'unpaid' AND c.custid=?";
      final var statement = connection.prepareStatement(sql);
      statement.setInt(1, custid);
      final var resultSet = statement.executeQuery();

      customer customers = new customer();
      Orders orders = new Orders();
      Orderdetails orderdetails = new Orderdetails();
      Products products = new Products();

      if (resultSet.next()) {
        String fullname = resultSet.getString("custname");
        String custaddress = resultSet.getString("custaddress");
        int orderid = resultSet.getInt("orderid");
        int orderprice = resultSet.getInt("orderprice");
        String orderstatus = resultSet.getString("orderstatus");
        int orderdetailsqty = resultSet.getInt("orderdetailsqty");
        String proname = resultSet.getString("proname");

        System.out.println("cust name : " + fullname);
        System.out.println("orderid : " + custaddress);
        System.out.println("order id : " + orderid);
        System.out.println("orderprice : " + orderprice);
        System.out.println("orderstatus : " + orderstatus);
        System.out.println("quantity : " + orderdetailsqty);
        System.out.println("product name : " + proname);

        customers = new customer();
        customers.setFullname(fullname);
        customers.setCustaddress(custaddress);

        orders = new Orders();
        orders.setOrderid(orderid);
        orders.setOrderprice(orderprice);
        orders.setOrderstatus(orderstatus);

        orderdetails = new Orderdetails();
        orderdetails.setOrderdetailsqty(orderdetailsqty);

        products = new Products();
        products.setProname(proname);

      }
      model.addAttribute("customers", customers);
      model.addAttribute("orders", orders);
      model.addAttribute("orderdetails", orderdetails);
      model.addAttribute("products", products);

      connection.close();

    } catch (Exception e) {
      e.printStackTrace();
    }

    return "user/payment";
  }

  @PostMapping("/payment")
  public String payment(HttpSession session, Model model,
      @RequestParam("paymentproofs") MultipartFile paymentproofs, @RequestParam("orderprice") int orderprice,
      @RequestParam("orderid") int orderid) {
    int custid = (int) session.getAttribute("custid");

    System.out.println("customer id : " + custid);

    try {
      Connection connection = dataSource.getConnection();
      String sql = "INSERT INTO payments (paymentamt,paymentdate,paymentproof,orderid) VALUES(?,?,?,?)";
      final var statement = connection.prepareStatement(sql);

      Date paymentdate = new Date(System.currentTimeMillis());

      statement.setInt(1, orderprice);
      statement.setDate(2, paymentdate);
      statement.setBytes(3, paymentproofs.getBytes());
      statement.setInt(4, orderid);
      statement.executeUpdate();

      // debug
      System.out.println("payment : " + orderprice);
      System.out.println("payment date : " + paymentdate);
      System.out.println("payment proof : " + paymentproofs.getBytes());
      System.out.println("order id : " + orderid);

      String sql2 = "UPDATE orders SET orderstatus=? WHERE orderid=?";
      final var statement2 = connection.prepareStatement(sql2);

      statement2.setString(1, "pending");
      statement2.setInt(2, orderid);
      statement2.executeUpdate();

      System.out.println("order id update :" + orderid);
      connection.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return "redirect:/orderHistory";
  }

  @GetMapping("/acceptorder")
  public String acceptpayment(HttpSession session, @RequestParam("orderid") int orderid) {
    try {
      Connection connection = dataSource.getConnection();
      String sql = "UPDATE orders SET orderstatus = 'accepted' WHERE orderid=?";
      final var statement = connection.prepareStatement(sql);
      statement.setInt(1, orderid);
      statement.executeUpdate();

      connection.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return "redirect:/stafforder";
  }

  @GetMapping("/rejectorder")
  public String rejectpayment(HttpSession session, @RequestParam("orderid") int orderid) {
    try {
      Connection connection = dataSource.getConnection();
      String sql = "UPDATE orders SET orderstatus = 'rejected' WHERE orderid=?";
      final var statement = connection.prepareStatement(sql);
      statement.setInt(1, orderid);
      statement.executeUpdate();

      connection.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return "redirect:/stafforder";
  }

}
