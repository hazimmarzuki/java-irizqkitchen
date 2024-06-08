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

import com.heroku.java.MODEL.Cakes;
import com.heroku.java.MODEL.Cupcakes;
import com.heroku.java.MODEL.Orders;
import com.heroku.java.MODEL.orderstaff;

import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
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

public class OrderController {
  private final DataSource dataSource;

  @Autowired
  public OrderController(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @GetMapping("/catalogue")
  public String catalogue(@RequestParam(name = "success", required = false) Boolean success, HttpSession session,
      Model model, Cakes cake, Cupcakes cupcake) {
    session.getAttribute("custid");
    List<Cakes> cakes = new ArrayList<>();
    List<Cupcakes> cupcakes = new ArrayList<>();

    try {
      System.out.println("pass connection first");
      Connection connection = dataSource.getConnection();
      String sql = "SELECT proid,proname,protype,proprice,proimg FROM products ORDER BY proid";
      final var statement = connection.createStatement();
      final var resultSet = statement.executeQuery(sql);

      System.out.println("pass connection 2");

      while (resultSet.next()) {
        int proid = resultSet.getInt("proid");
        String proname = resultSet.getString("proname");
        String protype = resultSet.getString("protype");
        int proprice = resultSet.getInt("proprice");

        System.out.println("product name : " + proname);

        byte[] proimgBytes = resultSet.getBytes("proimg");
        String proimgBase64 = Base64.getEncoder().encodeToString(proimgBytes);
        String proimage = "data:image/jpeg;base64," + proimgBase64;

        // debug
        System.out.println("product name : " + proname);
        System.out.println("product id from db : " + proid);

        if (protype.equals("cake")) {
          String sql2 = "SELECT cakesize FROM cakes WHERE proid=?";
          final var statement2 = connection.prepareStatement(sql2);
          statement2.setInt(1, proid);
          final var resultSet2 = statement2.executeQuery();
          System.out.println("cake here <<<<<<");

          if (resultSet2.next()) {
            int cakesize = resultSet2.getInt("cakesize");

            Cakes Cake = new Cakes(proid, proname, protype, proprice, null, null, proimage, cakesize);
            cakes.add(Cake);
            System.out.println("cakesize here>>>>>");
            System.out.println("cake id  2 : " + proid);
          }
        }
        if (protype.equals("cupcake")) {
          String sql3 = "SELECT cuptoppings FROM cupcakes WHERE proid=?";
          final var statement3 = connection.prepareStatement(sql3);
          statement3.setInt(1, proid);
          final var resultSet3 = statement3.executeQuery();
          System.out.println("cupcake here<<<<<<");

          if (resultSet3.next()) {
            String cuptoppings = resultSet3.getString("cuptoppings");
            Cupcakes Cupcake = new Cupcakes(proid, proname, protype, proprice, null, null, proimage, cuptoppings);
            cupcakes.add(Cupcake);
            System.out.println("cuptopping here>>>>>>");
          }
        }
      }

      model.addAttribute("cakes", cakes);
      model.addAttribute("cupcakes", cupcakes);

      connection.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return "user/catalogue";
    // } else {
    // System.out.println("Session expired or invalid");
    // return "login";
    // }
  }

  @PostMapping("/orderproduct")
  public String orderproduct(HttpSession session, Model model, @RequestParam("proprice") int proprice,
      @RequestParam("quantity") int orderdetailqty, @RequestParam("proid") int productid,
      @RequestParam("proname") String proname) {
    int custid = (int) session.getAttribute("custid");
    try {
      Connection connection = dataSource.getConnection();
      Date orderdate = new Date(System.currentTimeMillis());
      int orderprice = proprice * orderdetailqty;
      String orderstatus = "unpaid";

      System.out.println("order date : " + orderdate);
      System.out.println("order price : " + orderprice);
      System.out.println("order status : " + orderstatus);

      String sql = "INSERT INTO orders (orderdate, orderprice, orderstatus,custid) VALUES (?, ?, ?,?) RETURNING orderid";
      final var statement = connection.prepareStatement(sql);
      statement.setDate(1, orderdate);
      statement.setInt(2, orderprice);
      statement.setString(3, orderstatus);
      statement.setInt(4, custid);
      final var resultSet = statement.executeQuery();

      // Retrieve the auto-generated order ID
      int orderid = 0;
      if (resultSet.next()) {
        orderid = resultSet.getInt("orderid");
      }
      System.out.println("id from db order : " + orderid);

      // Prepare the SQL query
      String query = "INSERT INTO orderdetails (proid, orderid, orderdetailsqty) VALUES (?, ?, ?)";
      final var statement2 = connection.prepareStatement(query);
      statement2.setInt(1, productid);
      statement2.setInt(2, orderid);
      statement2.setInt(3, orderdetailqty);

      // Execute the query
      statement2.executeUpdate();

      session.setAttribute("orderprice", orderprice);
      session.setAttribute("orderdetailsqty", orderdetailqty);
      session.setAttribute("proname", proname);

      connection.close();

    } catch (Exception e) {
      e.printStackTrace();
    }

    return "redirect:/payment";
  }

  @GetMapping("/deleteorder")
  public String deleteorder(HttpSession session, Model model, @RequestParam("orderid") int orderid) {
    int userid = (int) session.getAttribute("custid");
    System.out.println("session for this : " + userid);

    try (Connection connection = dataSource.getConnection()) {
      // Delete order record
      final var deleteorderdetailsStatement = connection.prepareStatement("DELETE FROM orderdetails WHERE orderid=?");
      deleteorderdetailsStatement.setInt(1, orderid);
      int orderdetailsRowsAffected = deleteorderdetailsStatement.executeUpdate();

      // debug delete
      System.out.println("done delete order details");

      if (orderdetailsRowsAffected > 0) {
        final var deleteorderStatement = connection.prepareStatement("DELETE FROM orders WHERE orderid=?");
        deleteorderStatement.setInt(1, orderid);
        int orderRowsAffected = deleteorderStatement.executeUpdate();

        // debug
        System.out.println("done delete from order");

        if (orderRowsAffected > 0) {
          // deletion from bridge and main table success
          connection.close();
          return "redirect:/catalogue";
        }
      }
      connection.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "user/catalogue";
  }

  @GetMapping("/orderHistory")
  public String orderHistory(HttpSession session, Model model) {
    String fullname = (String) session.getAttribute("custname");
    int userid = (int) session.getAttribute("custid");
    List<Orders> orders = new ArrayList<>();

    if (fullname != null) {
      try (Connection connection = dataSource.getConnection()) {
        String sql = "SELECT orderid,orderdate,orderprice,orderstatus FROM orders WHERE custid = ?";
        final var statement = connection.prepareStatement(sql);
        statement.setInt(1, userid);
        final var resultSet = statement.executeQuery();
        System.out.println("pass try orderlist >>>>>");

        while (resultSet.next()) {
          int orderid = resultSet.getInt("orderid");
          Date orderdate = resultSet.getDate("orderdate");
          int orderprice = resultSet.getInt("orderprice");
          String orderstatus = resultSet.getString("orderstatus");

          // debug
          System.out.println("orderid : " + orderid);
          System.out.println("order date : " + orderdate);
          System.out.println("order price : " + orderprice);
          System.out.println("orderstatus : " + orderstatus);

          orders.add(new Orders(orderid, orderdate, orderprice, orderstatus));
          model.addAttribute("orders", orders);
        }
        connection.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return "user/orderHistory";
  }

  @GetMapping("/stafforder")
  public String stafforder(@RequestParam(name = "success", required = false) Boolean success, HttpSession session,
      Model model, orderstaff orderstaff) {
    if (session.getAttribute("staffsid") != null) {
      List<orderstaff> orderstaffss = new ArrayList<>();
      try {
        Connection connection = dataSource.getConnection();
        String sql = "SELECT p.orderid, c.custname, c.custaddress, pr.proname, od.orderdetailsqty, o.orderprice, p.paymentamt, p.paymentdate, p.paymentproof, o.orderstatus "
            +
            "FROM payments p " +
            "JOIN orders o ON p.orderid = o.orderid " +
            "JOIN orderdetails od ON o.orderid = od.orderid " +
            "JOIN products pr ON od.proid = pr.proid " +
            "JOIN customers c ON o.custid = c.custid " +
            "WHERE o.orderstatus = 'pending';";
        final var statement = connection.prepareStatement(sql);
        final var resultSet = statement.executeQuery();

        while (resultSet.next()) {
          int orderid = resultSet.getInt("orderid");
          String fullname = resultSet.getString("custname");
          String custaddress = resultSet.getString("custaddress");
          String proname = resultSet.getString("proname");
          int orderdetailsqty = resultSet.getInt("orderdetailsqty");
          int orderprice = resultSet.getInt("orderprice");
          int paymentamt = resultSet.getInt("paymentamt");
          Date paymentdate = resultSet.getDate("paymentdate");

          byte[] paymentBytes = resultSet.getBytes("paymentproof");
          String paymentBase64 = Base64.getEncoder().encodeToString(paymentBytes);
          String paymentprf = "data:image/jpeg;base64," + paymentBase64;

          String orderstatus = resultSet.getString("orderstatus");

          orderstaff orderstaffs = new orderstaff(orderid, fullname, custaddress, proname, orderdetailsqty, orderprice,
              paymentamt, paymentdate, null, null, paymentprf, orderstatus);
          orderstaffss.add(orderstaffs);

        }

        model.addAttribute("orderstaffs", orderstaffss);

        connection.close();
      } catch (Exception e) {
        e.printStackTrace();
      }

      return "admin/stafforder";
    } else {
      System.out.println("Session expired or invalid");
      return "login";
    }

  }

  @GetMapping("/stafforderhistory")
  public String orderhistory(HttpSession session, Model model, orderstaff orderstaff) {
    List<orderstaff> orderstaffss = new ArrayList<>();
    try {
      Connection connection = dataSource.getConnection();
      String sql = "SELECT o.orderid, p.proname, od.orderdetailsqty, o.orderprice, o.orderstatus " +
          "FROM orders o " +
          "JOIN orderdetails od ON o.orderid = od.orderid " +
          "JOIN products p ON od.proid = p.proid " +
          "WHERE o.orderstatus IN ('accepted', 'rejected')";
      final var statement = connection.prepareStatement(sql);
      final var resultSet = statement.executeQuery();

      while (resultSet.next()) {
        int orderid = resultSet.getInt("orderid");
        String proname = resultSet.getString("proname");
        int orderdetailsqty = resultSet.getInt("orderdetailsqty");
        int orderprice = resultSet.getInt("orderprice");
        String orderstatus = resultSet.getString("orderstatus");

        orderstaff orderstaffs = new orderstaff(orderid, proname, orderdetailsqty, orderprice, orderstatus);
        orderstaffss.add(orderstaffs);
      }

      model.addAttribute("orderstaffs", orderstaffss);

      connection.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return "admin/stafforderhistory";
  }
}
