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
import com.heroku.java.MODEL.Products;

import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.sql.Connection;
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

public class productsController {
  private final DataSource dataSource;

  @Autowired
  public productsController(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @GetMapping("/cakeregister")
  public String cakeregister() {
    return "admin/cakeregister";
  }

  @PostMapping("/cakeregister")
  public String AddCake(HttpSession session, Model model, @ModelAttribute("cakeregister") Products products,
      @RequestParam("proimgs") MultipartFile proimgs, Cakes cake, Cupcakes cupcake) {

    try {
      Connection connection = dataSource.getConnection();
      String sql = "INSERT INTO products(proname,protype,proprice,proimg) VALUES(?,?,?,?)";
      final var statement = connection.prepareStatement(sql);

      String proname = products.getProname();
      String protype = products.getProtype();
      int proprice = products.getProprice();

      System.out.println(cake.getCakesize());
      System.out.println(cupcake.getCuptoppings());

      statement.setString(1, proname);
      statement.setString(2, protype);
      statement.setInt(3, proprice);
      statement.setBytes(4, proimgs.getBytes());
      statement.executeUpdate();

      System.out.println("product name : " + proname);
      System.out.println("type : " + protype);
      System.out.println("product price : RM" + proprice);
      System.out.println("proimg: " + proimgs.getBytes());

      // Get id from database for sql 2 from sql 1
      String sql1 = "SELECT * FROM products where proname=?";
      final var stmt = connection.prepareStatement(sql1);
      stmt.setString(1, products.getProname());
      final var resultSet = stmt.executeQuery();
      int id_pro = 0;
      while (resultSet.next()) {
        id_pro = resultSet.getInt("proid");
      }
      System.out.println("id product from database : " + id_pro);
      System.out.println("protype : " + protype);
      if (protype.equals("cake")) {
        String sql2 = "INSERT INTO cakes (proid,cakesize) VALUES (?,?)";
        final var statement2 = connection.prepareStatement(sql2);

        int cakesize = cake.getCakesize();

        statement2.setInt(1, id_pro);
        statement2.setInt(2, cakesize);
        statement2.executeUpdate();
        System.out.println("cake size : " + cakesize);
      }

      if (protype.equals("cupcake")) {
        System.out.println("cupcake here");

        System.out.println("Cupcake here 2");
        String sql3 = "INSERT INTO cupcakes (proid,cuptoppings) VALUES (?,?)";
        final var statement3 = connection.prepareStatement(sql3);

        String cuptopping = cupcake.getCuptoppings();
        // String cuptopping = ;
        System.out.println("product id : " + id_pro);
        System.out.println("cupcake topping : " + cuptopping);

        statement3.setInt(1, id_pro);
        statement3.setString(2, cuptopping);
        statement3.executeUpdate();
        System.out.println("cupcake topping 2: " + cuptopping);
      }
      model.addAttribute("success", true);

    } catch (Exception e) {
      e.printStackTrace();
      return "redirect:/cakeregister?success=false";
    }
    return "redirect:/staffmenu?success=true";
  }

  @GetMapping("/staffmenu")
  public String productList(@RequestParam(name = "success", required = false) Boolean success, HttpSession session,
      Model model, Cakes cake, Cupcakes cupcake) {
    // String staffsrole = (String) session.getAttribute("staffsid");
    List<Cakes> cakes = new ArrayList<>();
    List<Cupcakes> cupcakes = new ArrayList<>();
    try {
      System.out.println("pass connection first");
      Connection connection = dataSource.getConnection();
      String sql = "SELECT proid,proname,protype,proprice,proimg FROM products ORDER BY proname";
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
      if (success != null && success) {
        model.addAttribute("success", true);
      }

      model.addAttribute("cakes", cakes);
      model.addAttribute("cupcakes", cupcakes);

      connection.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return "admin/staffmenu";
  }

  @GetMapping("/productdetail")
  public String productdetail(HttpSession session, @RequestParam("proid") int proid, Model model) {
    System.out.println("product id : " + proid);
    try {
      Connection connection = dataSource.getConnection();
      String sql = "SELECT products.proid, products.proname, products.protype, products.proprice, products.proimg, cakes.cakesize, cupcakes.cuptoppings "
          +
          "FROM products " +
          "LEFT JOIN cakes ON products.proid = cakes.proid " +
          "LEFT JOIN cupcakes ON products.proid = cupcakes.proid " +
          "WHERE products.proid = ?";
      final var statement = connection.prepareStatement(sql);
      statement.setInt(1, proid);
      final var resultSet = statement.executeQuery();

      if (resultSet.next()) {
        String proname = resultSet.getString("proname");
        String protype = resultSet.getString("protype");
        int proprice = resultSet.getInt("proprice");

        Products product;
        if (protype.equalsIgnoreCase("cake")) {
          int cakesize = resultSet.getInt("cakesize");
          product = new Cakes(proid, proname, protype, proprice, null, null, null, cakesize);
        } else if (protype.equalsIgnoreCase("cupcake")) {
          String cuptoppings = resultSet.getString("cuptoppings");
          product = new Cupcakes(proid, proname, protype, proprice, null, null, null, cuptoppings);
        } else {
          // Handle the case when protype is neither "cake" nor "cupcake"
          product = new Products(proid, proname, protype, proprice, null, null, null);
        }

        model.addAttribute("product", product); // Use "product" as the model attribute name

        connection.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return "admin/productdetail";
  }

  @PostMapping("/updateproduct")
  public String UpdateProduct(@ModelAttribute("product") Products product, Cakes cake, Cupcakes cupcake) {
    System.out.println("pass here <<<<<<<");
    try {
      Connection connection = dataSource.getConnection();
      String sql = "UPDATE products SET proname=?, proprice=? WHERE proid=?";
      final var statement = connection.prepareStatement(sql);
      statement.setString(1, product.getProname());
      statement.setInt(2, product.getProprice());
      statement.setInt(3, product.getProid());

      // Debug
      System.out.println("pro name update: " + product.getProname());
      System.out.println("pro price update: " + product.getProprice());

      statement.executeUpdate();

      // Update fields specific to "cake" or "cupcake" based on the product type
      if ("cake".equalsIgnoreCase(product.getProtype())) {
        String cakeSql = "UPDATE cakes SET cakesize=? WHERE proid=?";
        final var cakeStatement = connection.prepareStatement(cakeSql);
        cakeStatement.setInt(1, cake.getCakesize());
        cakeStatement.setInt(2, product.getProid());

        // Debug
        System.out.println("cake size update: " + cake.getCakesize());

        cakeStatement.executeUpdate();
      } else if ("cupcake".equalsIgnoreCase(product.getProtype())) {
        String cupcakeSql = "UPDATE cupcakes SET cuptoppings=? WHERE proid=?";
        final var cupcakeStatement = connection.prepareStatement(cupcakeSql);
        cupcakeStatement.setString(1, cupcake.getCuptoppings());
        cupcakeStatement.setInt(2, product.getProid());

        // Debug
        System.out.println("cupcake toping update: " + cupcake.getCuptoppings());

        cupcakeStatement.executeUpdate();
      }

      connection.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return "redirect:/staffmenu?success=true";
  }

  @GetMapping("/deletemenu")
  public String deletemenu(HttpSession session, @RequestParam(name = "proid") int proid, Model model) {
    System.out.println("product id : " + proid);
    try {
      Connection connection = dataSource.getConnection();
      String sql = "SELECT protype FROM products WHERE proid = ?";
      final var statement = connection.prepareStatement(sql);
      statement.setInt(1, proid);
      final var resultSet = statement.executeQuery();

      if (resultSet.next()) {
        String protype = resultSet.getString("protype");

        if (protype.equals("cake")) {
          String sql1 = "DELETE FROM cakes WHERE proid =?";
          final var deletestatement = connection.prepareStatement(sql1);
          deletestatement.setInt(1, proid);
          deletestatement.executeUpdate();
        }
        if (protype.equals("cupcake")) {
          String sql2 = "DELETE FROM cupcakes WHERE proid =?";
          final var deletestatement = connection.prepareStatement(sql2);
          deletestatement.setInt(1, proid);
          deletestatement.executeUpdate();
        }
        String sql3 = "DELETE FROM products WHERE proid =?";
        final var deletestatement = connection.prepareStatement(sql3);
        deletestatement.setInt(1, proid);
        deletestatement.executeUpdate();
      }
      connection.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "redirect:/staffmenu?success=true";
  }

}
