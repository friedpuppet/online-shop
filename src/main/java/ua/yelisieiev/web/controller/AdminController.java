package ua.yelisieiev.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;
import ua.yelisieiev.entity.Product;
import ua.yelisieiev.service.ProductServiceException;
import ua.yelisieiev.service.ProductsService;

import java.util.List;

@RequestMapping("/admin")
@Controller()
public class AdminController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private ProductsService productsService;

    public AdminController(ProductsService productsService) {
        this.productsService = productsService;
    }

    @RequestMapping("/")
    protected String defaultPage() {
        return "redirect:products";
    }

    @RequestMapping("/products")
    protected String getALl(Model model) {
        log.info("Simple getall method entered");
        try {
            List<Product> products;
            products = productsService.getAll();
            model.addAttribute("products", products);
            return "editable_products";
        } catch (ProductServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Backend error", e);
        }
    }

    @GetMapping(value = "/products", params = "search")
    protected String getSearch(@RequestParam("search") String searchExpression, Model model) {
        log.info("Search method entered");
        try {
            List<Product> products;
            products = productsService.getAllFiltered(searchExpression);
            model.addAttribute("products", products);
            return "editable_products";
        } catch (ProductServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Backend error", e);
        }
    }

    @GetMapping("/product/new")
    protected String addProductPage() {
        return "new_product";
    }

    @PostMapping("/product/new")
    protected String addProduct(@RequestParam("name") String name,
                                @RequestParam("description") String description,
                                @RequestParam("price") Double price) throws ProductServiceException {
        Product product = new Product(name, price, description);
        productsService.add(product);
//        return new RedirectView("/products", true);
        return "redirect:/admin/products";
    }


    @GetMapping("/product/{id}/edit")
    protected String editProductPage(@PathVariable("id") Integer id, Model model) throws ProductServiceException {
        final Product.Id productId = new Product.Id(id);
        Product product = productsService.get(productId);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No product with id " + productId);
        }
        model.addAttribute("product", product);
        return "edit_product";
    }

    @PostMapping("/product/{id}/edit")
    protected String editProduct(@PathVariable("id") Integer id,
                                 @RequestParam("name") String name,
                                 @RequestParam("description") String description,
                                 @RequestParam("price") Double price) throws ProductServiceException {
        Product product = new Product(new Product.Id(id), name, price, description);
        productsService.update(product);
        return "redirect:/admin/products";
    }

    @PostMapping("/product/{id}/delete")
    protected String deleteProduct(@PathVariable("id") Integer id) throws ProductServiceException {
        Product.Id productId = new Product.Id(id);
        productsService.delete(productId);
        return "redirect:/admin/products";
    }

}
